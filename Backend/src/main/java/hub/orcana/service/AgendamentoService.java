package hub.orcana.service;

import hub.orcana.dto.agendamento.DetalhesAgendamentoOutput;
import hub.orcana.dto.agendamento.AgendamentoMapper;
import hub.orcana.dto.agendamento.CadastroAgendamentoInput;
import hub.orcana.observer.AgendamentoObserver;
import hub.orcana.observer.AgendamentoSubject;
import hub.orcana.tables.Agendamento;
import hub.orcana.tables.Orcamento;
import hub.orcana.tables.StatusAgendamento;
import hub.orcana.tables.Usuario;
import hub.orcana.tables.repository.AgendamentoRepository;
import hub.orcana.tables.repository.OrcamentoRepository;
import hub.orcana.tables.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class AgendamentoService implements AgendamentoSubject {

    private static final Logger log = LoggerFactory.getLogger(AgendamentoService.class);

    private final AgendamentoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final List<AgendamentoObserver> observers = new ArrayList<>();

    public AgendamentoService(
            AgendamentoRepository repository,
            UsuarioRepository usuarioRepository,
            OrcamentoRepository orcamentoRepository,
            EmailService emailService) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.orcamentoRepository = orcamentoRepository;

        if (emailService != null) {
            this.attach(emailService);
        }
    }

    @Override
    public void attach(AgendamentoObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
            log.info("Observer {} registrado com sucesso", observer.getClass().getSimpleName());
        }
    }

    @Override
    public void detach(AgendamentoObserver observer) {
        observers.remove(observer);
        log.info("Observer {} removido com sucesso", observer.getClass().getSimpleName());
    }

    @Override
    public void notifyObservers(Agendamento agendamento, String acao) {
        log.info("Notificando {} observers sobre ação: {}", observers.size(), acao);
        for (AgendamentoObserver observer : observers) {
            try {
                observer.updateAgendamento(agendamento, acao);
            } catch (Exception e) {
                log.error("Erro ao notificar observer {}: {}",
                        observer.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
    // ------------------ CRUD BÁSICO ------------------

    public List<DetalhesAgendamentoOutput> getAgendamentos() {
        return repository.findAll().stream().map(AgendamentoMapper::of).toList();
    }

    public DetalhesAgendamentoOutput getAgendamentoPorId(Long id) {
        Agendamento agendamento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));
        return AgendamentoMapper.of(agendamento);
    }

    public List<DetalhesAgendamentoOutput> getAgendamentosByStatus(String status) {
        return repository.findAll()
                .stream()
                .filter(atual -> atual.getStatus().name().equalsIgnoreCase(status))
                .map(AgendamentoMapper::of)
                .toList();
    }

    public DetalhesAgendamentoOutput postAgendamento(CadastroAgendamentoInput agendamento) {
        Usuario usuario = usuarioRepository.findByEmail(agendamento.emailUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuário é obrigatório."));

        Orcamento orcamento = orcamentoRepository.findByCodigoOrcamento(agendamento.codigoOrcamento())
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado."));

        Optional<Agendamento> agendamentoExistente = repository.findByOrcamentoCodigoOrcamento(agendamento.codigoOrcamento());
        if (agendamentoExistente.isPresent()) {
            throw new IllegalArgumentException("Já existe um agendamento para este código de orçamento.");
        }

        Agendamento novoAgendamento = AgendamentoMapper.of(agendamento, usuario, orcamento);
        novoAgendamento.setStatus(StatusAgendamento.AGUARDANDO);
        Agendamento salvo = repository.save(novoAgendamento);

        try {
            notifyObservers(salvo, "CRIADO");
        } catch (Exception e) {
            log.error("Falha ao notificar observers de novo agendamento ID {}: {}",
                    salvo.getId(), e.getMessage());
        }

        return AgendamentoMapper.of(salvo);
    }

    public DetalhesAgendamentoOutput putAgendamentoById(Long id, CadastroAgendamentoInput agendamento) {
        Agendamento existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));

        StatusAgendamento statusAnterior = existente.getStatus();

        existente.setDataHora(agendamento.dataHora());
        existente.setStatus(agendamento.status());

        Usuario usuario = usuarioRepository.findByEmail(agendamento.emailUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuário é obrigatório."));
        existente.setUsuario(usuario);

        Orcamento orcamento = orcamentoRepository.findByCodigoOrcamento(agendamento.codigoOrcamento())
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado."));
        existente.setOrcamento(orcamento);

        Agendamento salvo = repository.save(existente);

        try {
            String acao = "ATUALIZADO";

            if (statusAnterior != agendamento.status()) {
                acao = "STATUS_ALTERADO_" + statusAnterior + "_PARA_" + agendamento.status();
            }

            notifyObservers(salvo, acao);
        } catch (Exception e) {
            log.error("Falha ao notificar observers de atualização do agendamento ID {}: {}",
                    id, e.getMessage());
        }

        return AgendamentoMapper.of(salvo);
    }

    public void deleteAgendamentoById(Long id) {
        Agendamento agendamento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));

        try {
            notifyObservers(agendamento, "CANCELADO");
        } catch (Exception e) {
            log.error("Falha ao notificar observers de cancelamento do agendamento ID {}: {}",
                    id, e.getMessage());
        }

        repository.deleteById(id);

        if (repository.existsById(id)) {
            throw new IllegalArgumentException("Erro ao excluir agendamento.");
        }
    }

    public boolean verificarCodigoOrcamento(String codigoOrcamento) {
        Optional<Orcamento> orcamento = orcamentoRepository.findByCodigoOrcamento(codigoOrcamento);
        if (orcamento.isEmpty()) {
            return false;
        }

        Optional<Agendamento> agendamento = repository.findByOrcamentoCodigoOrcamento(codigoOrcamento);
        return agendamento.isEmpty();
    }

    public List<String> getDatasOcupadas() {
        LocalDateTime hoje = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<Agendamento> agendamentos = repository.findAll().stream()
                .filter(a -> a.getDataHora().isAfter(hoje) || a.getDataHora().isEqual(hoje))
                .toList();

        return agendamentos.stream()
                .map(a -> a.getDataHora().toLocalDate().toString())
                .distinct()
                .toList();
    }

    public DetalhesAgendamentoOutput getAgendamentoCompleto(Long id) {
        Agendamento agendamento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));
        return AgendamentoMapper.of(agendamento);
    }

    public List<DetalhesAgendamentoOutput> getAgendamentosPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream().map(AgendamentoMapper::of).toList();
    }

    public DetalhesAgendamentoOutput atualizarOrcamento(Long agendamentoId, String codigoOrcamento) {
        Agendamento agendamento = repository.findById(agendamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));
        Orcamento orcamento = orcamentoRepository.findByCodigoOrcamento(codigoOrcamento)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado."));

        agendamento.setOrcamento(orcamento);
        Agendamento salvo = repository.save(agendamento);

        try {
            notifyObservers(salvo, "ORCAMENTO_ALTERADO");
        } catch (Exception e) {
            log.error("Falha ao notificar observers de alteração de orçamento do agendamento ID {}: {}",
                    agendamentoId, e.getMessage());
        }

        return AgendamentoMapper.of(salvo);
    }
}
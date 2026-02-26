package hub.orcana.service;

import hub.orcana.dto.agendamento.*;
import hub.orcana.observer.AgendamentoObserver;
import hub.orcana.observer.AgendamentoSubject;
import hub.orcana.tables.*;
import hub.orcana.tables.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final EmailService emailService;
    private final List<AgendamentoObserver> observers = new ArrayList<>();
    private final RelatorioRepository relatorioRepository;
    private final EquipamentoUsoRepository equipamentoUsoRepository;
    private final EstoqueRepository estoqueRepository;

    public AgendamentoService(
            AgendamentoRepository repository,
            UsuarioRepository usuarioRepository,
            OrcamentoRepository orcamentoRepository,
            EmailService emailService,
            RelatorioRepository relatorioRepository,
            EquipamentoUsoRepository equipamentoUsoRepository,
            EstoqueRepository estoqueRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.emailService = emailService;
        this.relatorioRepository = relatorioRepository;
        this.equipamentoUsoRepository = equipamentoUsoRepository;
        this.estoqueRepository = estoqueRepository;
        this.attach(emailService);
    }

    @Override
    public void attach(AgendamentoObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void detach(AgendamentoObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Agendamento agendamento) {
        for (AgendamentoObserver observer : observers) {
            try {
                observer.updateAgendamento(agendamento);
            } catch (Exception e) {
                log.error("Falha ao notificar observer: {}", e.getMessage());
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
        novoAgendamento.setStatus(StatusAgendamento.PENDENTE);
        Agendamento salvo = repository.save(novoAgendamento);

        try {
            notifyObservers(salvo);
        } catch (Exception e) {
            log.error("Falha ao notificar Observers de novo agendamento ID {}: {}", salvo.getId(), e.getMessage());
        }

        return AgendamentoMapper.of(salvo);
    }

    @Transactional
    public DetalhesAgendamentoOutput putAgendamentoById(Long id, CadastroAgendamentoInput agendamento) {
        Agendamento existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));

        existente.setDataHora(agendamento.dataHora());
        existente.setStatus(agendamento.status());

        Usuario usuario = usuarioRepository.findByEmail(agendamento.emailUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuário é obrigatório."));
        existente.setUsuario(usuario);

        Orcamento orcamento = orcamentoRepository.findByCodigoOrcamento(agendamento.codigoOrcamento())
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado."));
        existente.setOrcamento(orcamento);

        // ATUALIZAR CAMPOS DE PAGAMENTO E TEMPO
        if (agendamento.tempoDuracao() != null) {
            existente.setTempoDuracao(agendamento.tempoDuracao());
        }
        if (agendamento.pagamentoFeito() != null) {
            existente.setPagamentoFeito(agendamento.pagamentoFeito());
        }
        if (agendamento.formaPagamento() != null) {
            existente.setFormaPagamento(agendamento.formaPagamento());
        }

        Agendamento salvo = repository.save(existente);
        return AgendamentoMapper.of(salvo);
    }

    public void deleteAgendamentoById(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Agendamento não encontrado.");
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
        return AgendamentoMapper.of(salvo);
    }

    @Transactional
    public void adicionarMateriaisUsados(Long agendamentoId, AdicionarMateriaisRequest request) {
        log.info("Adicionando materiais usados ao agendamento ID: {}", agendamentoId);

        // 1. Buscar o agendamento
        Agendamento agendamento = repository.findById(agendamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));

        // 2. Buscar ou criar o relatório
        Relatorio relatorio = relatorioRepository.findByAgendamentoId(agendamentoId)
                .orElseGet(() -> {
                    log.info("Criando novo relatório para agendamento ID: {}", agendamentoId);
                    Relatorio novoRelatorio = new Relatorio();
                    novoRelatorio.setAgendamento(agendamento);
                    novoRelatorio.setUsuario(agendamento.getUsuario());
                    return relatorioRepository.save(novoRelatorio);
                });

        // 3. Remover materiais antigos (se existirem)
        equipamentoUsoRepository.deleteByRelatorioId(relatorio.getId());
        log.info("Materiais antigos removidos do relatório ID: {}", relatorio.getId());

        // 4. Adicionar novos materiais e atualizar estoque
        for (MaterialUsadoRequest material : request.materiais()) {
            Estoque equipamento = estoqueRepository.findById(material.materialId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Material não encontrado: " + material.materialId()));

            // Verificar se há estoque suficiente
            if (equipamento.getQuantidade() < material.quantidade()) {
                throw new IllegalArgumentException(
                        String.format("Estoque insuficiente para %s. Disponível: %.1f, Solicitado: %d",
                                equipamento.getNome(), equipamento.getQuantidade(), material.quantidade()));
            }

            // Criar registro de uso
            EquipamentoUso equipamentoUso = new EquipamentoUso();
            equipamentoUso.setEquipamento(equipamento);
            equipamentoUso.setQuantidade(material.quantidade());
            equipamentoUso.setRelatorio(relatorio);
            equipamentoUsoRepository.save(equipamentoUso);

            // Atualizar estoque (diminuir quantidade)
            equipamento.setQuantidade(equipamento.getQuantidade() - material.quantidade());
            estoqueRepository.save(equipamento);

            log.info("Material '{}' (qtd: {}) adicionado ao relatório ID: {}. Estoque atualizado: {}",
                    equipamento.getNome(), material.quantidade(), relatorio.getId(),
                    equipamento.getQuantidade());
        }
    }
}
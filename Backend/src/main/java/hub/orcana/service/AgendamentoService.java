package hub.orcana.service;

import hub.orcana.dto.agendamento.DetalhesAgendamentoOutput;
import hub.orcana.dto.agendamento.AgendamentoMapper;
import hub.orcana.dto.agendamento.CadastroAgendamentoInput;
import hub.orcana.tables.Agendamento;
import hub.orcana.tables.Orcamento;
import hub.orcana.tables.StatusAgendamento;
import hub.orcana.tables.Usuario;
import hub.orcana.tables.repository.AgendamentoRepository;
import hub.orcana.tables.repository.OrcamentoRepository;
import hub.orcana.tables.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AgendamentoService {

    private final AgendamentoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final OrcamentoRepository orcamentoRepository;

    public AgendamentoService(
            AgendamentoRepository repository,
            UsuarioRepository usuarioRepository,
            OrcamentoRepository orcamentoRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.orcamentoRepository = orcamentoRepository;
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

        // Verifica se já existe agendamento para este orçamento
        Optional<Agendamento> agendamentoExistente = repository.findByOrcamentoCodigoOrcamento(agendamento.codigoOrcamento());
        if (agendamentoExistente.isPresent()) {
            throw new IllegalArgumentException("Já existe um agendamento para este código de orçamento.");
        }

        Agendamento novoAgendamento = AgendamentoMapper.of(agendamento, usuario, orcamento);
        novoAgendamento.setStatus(StatusAgendamento.AGUARDANDO);
        Agendamento salvo = repository.save(novoAgendamento);

        return AgendamentoMapper.of(salvo);
    }

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

    // ------------------ VALIDAÇÕES E BUSCAS ------------------

    // Verifica se um código de orçamento já possui agendamento
    public boolean verificarCodigoOrcamento(String codigoOrcamento) {
        // Primeiro verifica se o orçamento existe
        Optional<Orcamento> orcamento = orcamentoRepository.findByCodigoOrcamento(codigoOrcamento);
        if (orcamento.isEmpty()) {
            return false; // Orçamento não existe
        }
        
        // Verifica se já existe agendamento para este orçamento
        Optional<Agendamento> agendamento = repository.findByOrcamentoCodigoOrcamento(codigoOrcamento);
        return agendamento.isEmpty(); // Retorna true se NÃO existe agendamento (código disponível)
    }

    // Retorna as datas que possuem agendamentos
    public List<String> getDatasOcupadas() {
        LocalDateTime hoje = LocalDateTime.now();
        List<LocalDateTime> datasComAgendamento = repository.findDatasComAgendamento(hoje);
        
        return datasComAgendamento.stream()
                .map(data -> LocalDate.from(data).toString())
                .distinct()
                .toList();
    }

    // ------------------ RELACIONAMENTOS ------------------

    // 🔹 1. Agendamento detalhado com usuário e orçamento
    public DetalhesAgendamentoOutput getAgendamentoCompleto(Long id) {
        Agendamento agendamento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));
        return AgendamentoMapper.of(agendamento);
    }

    // 🔹 2. Listar agendamentos por usuário
    public List<DetalhesAgendamentoOutput> getAgendamentosPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream().map(AgendamentoMapper::of).toList();
    }

    // 🔹 3. Atualizar o orçamento de um agendamento
    public DetalhesAgendamentoOutput atualizarOrcamento(Long agendamentoId, String codigoOrcamento) {
        Agendamento agendamento = repository.findById(agendamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));
        Orcamento orcamento = orcamentoRepository.findByCodigoOrcamento(codigoOrcamento)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado."));
        agendamento.setOrcamento(orcamento);
        Agendamento salvo = repository.save(agendamento);
        return AgendamentoMapper.of(salvo);
    }
}

package hub.orcana.service;

import hub.orcana.dto.agendamento.AgendamentoDetalhadoDTO;
import hub.orcana.dto.agendamento.AgendamentoMapper;
import hub.orcana.dto.agendamento.CadastroAgendamento;
import hub.orcana.tables.Agendamento;
import hub.orcana.tables.Orcamento;
import hub.orcana.tables.StatusAgendamento;
import hub.orcana.tables.Usuario;
import hub.orcana.tables.repository.AgendamentoRepository;
import hub.orcana.tables.repository.OrcamentoRepository;
import hub.orcana.tables.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;

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

    public List<AgendamentoDetalhadoDTO> getAgendamentos() {
        return repository.findAll().stream().map(AgendamentoMapper::of).toList();
    }

    public AgendamentoDetalhadoDTO getAgendamentoPorId(Long id) {
         Agendamento agendamento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));
            return AgendamentoMapper.of(agendamento);
    }

    public List<AgendamentoDetalhadoDTO> getAgendamentosByStatus(String status) {
        return repository.findAll()
                .stream()
                .filter(atual -> atual.getStatus().name().equalsIgnoreCase(status))
                .map(AgendamentoMapper::of)
                .toList();
    }

    public AgendamentoDetalhadoDTO postAgendamento(CadastroAgendamento agendamento) {
        Usuario usuario = usuarioRepository.findByEmail(agendamento.emailUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuário é obrigatório."));

        Orcamento orcamento = orcamentoRepository.findByCodigoOrcamento(agendamento.codigoOrcamento())
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado."));

        Agendamento novoAgendamento = AgendamentoMapper.of(agendamento, usuario, orcamento);
        novoAgendamento.setStatus(StatusAgendamento.AGUARDANDO);
        Agendamento salvo = repository.save(novoAgendamento);

        return AgendamentoMapper.of(salvo);
    }

    public AgendamentoDetalhadoDTO putAgendamentoById(Long id, CadastroAgendamento agendamento) {
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

    // ------------------ RELACIONAMENTOS ------------------

    // 🔹 1. Agendamento detalhado com usuário e orçamento
    public AgendamentoDetalhadoDTO getAgendamentoCompleto(Long id) {
        Agendamento agendamento = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));
        return AgendamentoMapper.of(agendamento);
    }

    // 🔹 2. Listar agendamentos por usuário
    public List<AgendamentoDetalhadoDTO> getAgendamentosPorUsuario(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream().map(AgendamentoMapper::of).toList();
    }

    // 🔹 3. Atualizar o orçamento de um agendamento
    public AgendamentoDetalhadoDTO atualizarOrcamento(Long agendamentoId, String codigoOrcamento) {
        Agendamento agendamento = repository.findById(agendamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Agendamento não encontrado."));
        Orcamento orcamento = orcamentoRepository.findByCodigoOrcamento(codigoOrcamento)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado."));
        agendamento.setOrcamento(orcamento);
        Agendamento salvo = repository.save(agendamento);
        return AgendamentoMapper.of(salvo);
    }
}

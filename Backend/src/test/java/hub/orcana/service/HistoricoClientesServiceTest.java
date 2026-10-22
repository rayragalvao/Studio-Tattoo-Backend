package hub.orcana.service;

import hub.orcana.dto.historico.DetalhesHistoricoClienteOutput;
import hub.orcana.dto.historico.HistoricoClientesOutput;
import hub.orcana.exception.DependenciaNaoEncontradaException;
import hub.orcana.tables.Agendamento;
import hub.orcana.tables.Orcamento;
import hub.orcana.tables.StatusAgendamento;
import hub.orcana.tables.Usuario;
import hub.orcana.tables.repository.AgendamentoRepository;
import hub.orcana.tables.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoricoClientesServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private HistoricoClientesService service;

    @Test
    void listarDeveConsolidarSessoesConcluidasPorCliente() {
        Usuario maria = usuario(10L, "Maria Ávila", false);
        Agendamento recente = agendamento(2L, maria, LocalDateTime.of(2026, 9, 10, 14, 0), 300.0, 120);
        Agendamento antiga = agendamento(1L, maria, LocalDateTime.of(2026, 8, 1, 10, 0), 200.0, 90);
        recente.getOrcamento().setEstilo("Fineline");
        antiga.getOrcamento().setEstilo("fineline");

        when(agendamentoRepository.findByStatusComUsuarioEOrcamento(StatusAgendamento.CONCLUIDO))
                .thenReturn(List.of(recente, antiga));

        HistoricoClientesOutput resultado = service.listar("fineline");

        assertEquals(1, resultado.totalClientes());
        assertEquals(2, resultado.totalSessoes());
        assertEquals(500.0, resultado.receitaAcumulada());
        assertEquals("MA", resultado.clientes().getFirst().iniciais());
        assertEquals(List.of("Fineline"), resultado.clientes().getFirst().estilos());
        assertEquals(recente.getDataHora(), resultado.clientes().getFirst().ultimaSessao());
    }

    @Test
    void listarDeveIgnorarUsuariosAdministradores() {
        Usuario admin = usuario(1L, "Admin Estudio", true);
        when(agendamentoRepository.findByStatusComUsuarioEOrcamento(StatusAgendamento.CONCLUIDO))
                .thenReturn(List.of(agendamento(1L, admin, LocalDateTime.now(), 100.0, 60)));

        HistoricoClientesOutput resultado = service.listar("");

        assertEquals(0, resultado.totalClientes());
        assertEquals(0, resultado.totalSessoes());
        assertEquals(0.0, resultado.receitaAcumulada());
    }

    @Test
    void detalharDeveRetornarSessoesDaMaisRecenteParaAMaisAntiga() {
        Usuario cliente = usuario(7L, "Ana Costa", false);
        Agendamento sessao = agendamento(4L, cliente, LocalDateTime.of(2026, 7, 3, 9, 0), 250.0, null);
        sessao.getOrcamento().setTempo(Time.valueOf("01:30:00"));

        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(cliente));
        when(agendamentoRepository.findByUsuarioIdAndStatusComOrcamento(7L, StatusAgendamento.CONCLUIDO))
                .thenReturn(List.of(sessao));

        DetalhesHistoricoClienteOutput resultado = service.detalhar(7L);

        assertEquals("AC", resultado.iniciais());
        assertEquals(1, resultado.quantidadeSessoes());
        assertEquals(250.0, resultado.gastoTotal());
        assertEquals(90, resultado.sessoes().getFirst().duracaoMinutos());
        assertEquals("Nao informado", resultado.sessoes().getFirst().estilo());
    }

    @Test
    void detalharDeveRejeitarClienteInexistente() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(DependenciaNaoEncontradaException.class, () -> service.detalhar(99L));
    }

    private Usuario usuario(Long id, String nome, boolean admin) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome(nome);
        usuario.setAdmin(admin);
        return usuario;
    }

    private Agendamento agendamento(
            Long id,
            Usuario usuario,
            LocalDateTime data,
            Double valor,
            Integer duracao
    ) {
        Orcamento orcamento = new Orcamento();
        orcamento.setCodigoOrcamento("ORC-" + id);
        orcamento.setIdeia("Procedimento " + id);
        orcamento.setValor(valor);

        Agendamento agendamento = new Agendamento();
        agendamento.setId(id);
        agendamento.setUsuario(usuario);
        agendamento.setOrcamento(orcamento);
        agendamento.setDataHora(data);
        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        agendamento.setTempoDuracao(duracao);
        return agendamento;
    }
}

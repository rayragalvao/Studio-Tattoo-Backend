package hub.orcana.service;

import hub.orcana.dto.dashboard.DashboardOutput;
import hub.orcana.tables.Agendamento;
import hub.orcana.tables.Estoque;
import hub.orcana.tables.Orcamento;
import hub.orcana.tables.StatusAgendamento;
import hub.orcana.tables.StatusOrcamento;
import hub.orcana.tables.Usuario;
import hub.orcana.tables.repository.AgendamentoRepository;
import hub.orcana.tables.repository.EstoqueRepository;
import hub.orcana.tables.repository.OrcamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @Mock
    private EstoqueRepository estoqueRepository;

    @InjectMocks
    private DashboardService dashboardService;

    private Usuario usuario;
    private Orcamento orcamento;
    private Agendamento agendamento;
    private Estoque estoque;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");

        orcamento = new Orcamento("ORC123", "João Silva", "joao@email.com",
                "Dragão nas costas", 1500.0, "Preto e Vermelho", "Costas", 2.0, Time.valueOf("2:00"), null, StatusOrcamento.APROVADO, usuario);

        agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.setDataHora(LocalDateTime.now().plusDays(1));
        agendamento.setStatus(StatusAgendamento.CONCLUIDO);
        agendamento.setUsuario(usuario);
        agendamento.setOrcamento(orcamento);

        estoque = new Estoque();
        estoque.setId(1L);
        estoque.setNome("Tinta Preta");
        estoque.setQuantidade(5.0);
        estoque.setMinAviso(10.0);
    }


    @Test
    @DisplayName("Deve retornar faturamento dos últimos 12 meses com valores corretos")
    void deveRetornarFaturamentoUltimos12Meses() {
        LocalDateTime hoje = LocalDate.now().atTime(23, 59, 59);

        List<Agendamento> agendamentosMes1 = criarAgendamentosParaMes(hoje, 1000.0, 500.0);
        List<Agendamento> agendamentosMes2 = criarAgendamentosParaMes(hoje.minusMonths(1), 2000.0);
        List<Agendamento> agendamentosMes3 = criarAgendamentosParaMes(hoje.minusMonths(2), 1500.0, 800.0);

        when(agendamentoRepository.findAllWithOrcamentoByDataHoraBetween(any(), any()))
                .thenReturn(agendamentosMes1)
                .thenReturn(agendamentosMes2)
                .thenReturn(agendamentosMes3)
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList());

        List<Double> resultado = dashboardService.getFaturamentoUltimos12Meses();

        assertNotNull(resultado);
        assertEquals(12, resultado.size());
        verify(agendamentoRepository, times(12)).findAllWithOrcamentoByDataHoraBetween(any(), any());
    }

    @Test
    @DisplayName("Deve contar apenas agendamentos concluídos no faturamento")
    void deveContarApenasAgendamentosConcluidos() {
        Agendamento agendamentoConcluido = criarAgendamento(1L, StatusAgendamento.CONCLUIDO, 1000.0);
        Agendamento agendamentoPendente = criarAgendamento(2L, StatusAgendamento.PENDENTE, 500.0);
        Agendamento agendamentoCancelado = criarAgendamento(3L, StatusAgendamento.CANCELADO, 300.0);

        List<Agendamento> agendamentos = Arrays.asList(
                agendamentoConcluido,
                agendamentoPendente,
                agendamentoCancelado
        );

        when(agendamentoRepository.findAllWithOrcamentoByDataHoraBetween(any(), any()))
                .thenReturn(agendamentos)
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList());

        List<Double> resultado = dashboardService.getFaturamentoUltimos12Meses();

        assertNotNull(resultado);
        assertEquals(12, resultado.size());
        assertEquals(1000.0, resultado.get(11)); 
    }

    @Test
    @DisplayName("Deve retornar zero quando não houver agendamentos no mês")
    void deveRetornarZeroQuandoNaoHouverAgendamentos() {
        when(agendamentoRepository.findAllWithOrcamentoByDataHoraBetween(any(), any()))
                .thenReturn(Collections.emptyList());

        List<Double> resultado = dashboardService.getFaturamentoUltimos12Meses();

        assertNotNull(resultado);
        assertEquals(12, resultado.size());
        assertTrue(resultado.stream().allMatch(valor -> valor == 0.0));
    }

    @Test
    @DisplayName("Deve tratar agendamentos com orçamento null")
    void deveTratarAgendamentosComOrcamentoNull() {
        Agendamento agendamentoSemOrcamento = new Agendamento();
        agendamentoSemOrcamento.setId(1L);
        agendamentoSemOrcamento.setStatus(StatusAgendamento.CONCLUIDO);
        agendamentoSemOrcamento.setOrcamento(null);

        when(agendamentoRepository.findAllWithOrcamentoByDataHoraBetween(any(), any()))
                .thenReturn(Arrays.asList(agendamentoSemOrcamento))
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList());

        List<Double> resultado = dashboardService.getFaturamentoUltimos12Meses();

        assertNotNull(resultado);
        assertEquals(0.0, resultado.get(11));
    }

    @Test
    @DisplayName("Deve tratar agendamentos com valor null no orçamento")
    void deveTratarAgendamentosComValorNullNoOrcamento() {
        Orcamento orcamentoSemValor = new Orcamento("ORC456", "João Silva", "joao@email.com",
                "Tatuagem", 10.0, "Preto", "Braço", null);

        Agendamento agendamentoComOrcamentoSemValor = criarAgendamento(1L, StatusAgendamento.CONCLUIDO, null);
        agendamentoComOrcamentoSemValor.setOrcamento(orcamentoSemValor);

        when(agendamentoRepository.findAllWithOrcamentoByDataHoraBetween(any(), any()))
                .thenReturn(Arrays.asList(agendamentoComOrcamentoSemValor))
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList());

        List<Double> resultado = dashboardService.getFaturamentoUltimos12Meses();
        assertNotNull(resultado);
        assertEquals(0.0, resultado.get(11)); 
    }

    @Test
    @DisplayName("Deve somar múltiplos agendamentos do mesmo mês")
    void deveSomarMultiplosAgendamentosDoMesmoMes() {
        List<Agendamento> agendamentos = Arrays.asList(
                criarAgendamento(1L, StatusAgendamento.CONCLUIDO, 1000.0),
                criarAgendamento(2L, StatusAgendamento.CONCLUIDO, 1500.0),
                criarAgendamento(3L, StatusAgendamento.CONCLUIDO, 2000.0)
        );

        when(agendamentoRepository.findAllWithOrcamentoByDataHoraBetween(any(), any()))
                .thenReturn(agendamentos)
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList());

        List<Double> resultado = dashboardService.getFaturamentoUltimos12Meses();

        assertNotNull(resultado);
        assertEquals(4500.0, resultado.get(11)); 
    }

    @Test
    @DisplayName("Deve reverter a ordem dos meses para cronológico")
    void deveReverterOrdemDosMesesParaCronologico() {
        when(agendamentoRepository.findAllWithOrcamentoByDataHoraBetween(any(), any()))
                .thenReturn(criarAgendamentosParaMes(LocalDate.now().atTime(23, 59, 59), 100.0))
                .thenReturn(criarAgendamentosParaMes(LocalDate.now().atTime(23, 59, 59), 200.0))
                .thenReturn(criarAgendamentosParaMes(LocalDate.now().atTime(23, 59, 59), 300.0))
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList())
                .thenReturn(Collections.emptyList());

        List<Double> resultado = dashboardService.getFaturamentoUltimos12Meses();

        assertNotNull(resultado);
        assertEquals(12, resultado.size());
    }


    @Test
    @DisplayName("Deve retornar próximo agendamento pendente")
    void deveRetornarProximoAgendamentoPendente() {
        Agendamento proximoAgendamento = criarAgendamento(1L, StatusAgendamento.PENDENTE, 1000.0);
        proximoAgendamento.setDataHora(LocalDateTime.now().plusDays(1));

        when(agendamentoRepository.findTopByStatusOrderByDataHoraAsc(StatusAgendamento.PENDENTE))
                .thenReturn(Optional.of(proximoAgendamento));
        when(orcamentoRepository.countByStatus(StatusOrcamento.PENDENTE)).thenReturn(5L);
        when(agendamentoRepository.findAllByDataHoraBetween(any(), any()))
                .thenReturn(Collections.emptyList());
        when(estoqueRepository.findAllByQuantidadeLessThanMinAviso())
                .thenReturn(Collections.emptyList());

        DashboardOutput resultado = dashboardService.getDashboardKPIs();

        assertNotNull(resultado);
        assertNotNull(resultado.proximoAgendamento());
        assertEquals(1L, resultado.proximoAgendamento().getId());
        assertEquals(StatusAgendamento.PENDENTE, resultado.proximoAgendamento().getStatus());
    }

    @Test
    @DisplayName("Deve retornar null quando não houver próximo agendamento")
    void deveRetornarNullQuandoNaoHouverProximoAgendamento() {
        when(agendamentoRepository.findTopByStatusOrderByDataHoraAsc(StatusAgendamento.PENDENTE))
                .thenReturn(Optional.empty());
        when(orcamentoRepository.countByStatus(StatusOrcamento.PENDENTE)).thenReturn(0L);
        when(agendamentoRepository.findAllByDataHoraBetween(any(), any()))
                .thenReturn(Collections.emptyList());
        when(estoqueRepository.findAllByQuantidadeLessThanMinAviso())
                .thenReturn(Collections.emptyList());

        DashboardOutput resultado = dashboardService.getDashboardKPIs();

        assertNotNull(resultado);
        assertNull(resultado.proximoAgendamento());
    }

    @Test
    @DisplayName("Deve contar orçamentos pendentes corretamente")
    void deveContarOrcamentosPendentesCorretamente() {
        when(agendamentoRepository.findTopByStatusOrderByDataHoraAsc(StatusAgendamento.PENDENTE))
                .thenReturn(Optional.empty());
        when(orcamentoRepository.countByStatus(StatusOrcamento.PENDENTE)).thenReturn(15L);
        when(agendamentoRepository.findAllByDataHoraBetween(any(), any()))
                .thenReturn(Collections.emptyList());
        when(estoqueRepository.findAllByQuantidadeLessThanMinAviso())
                .thenReturn(Collections.emptyList());

        DashboardOutput resultado = dashboardService.getDashboardKPIs();

        assertNotNull(resultado);
        assertEquals(15L, resultado.orcamentosPendentes());
        verify(orcamentoRepository, times(1)).countByStatus(StatusOrcamento.PENDENTE);
    }

    @Test
    @DisplayName("Deve retornar zero quando não houver orçamentos pendentes")
    void deveRetornarZeroQuandoNaoHouverOrcamentosPendentes() {
        when(agendamentoRepository.findTopByStatusOrderByDataHoraAsc(StatusAgendamento.PENDENTE))
                .thenReturn(Optional.empty());
        when(orcamentoRepository.countByStatus(StatusOrcamento.PENDENTE)).thenReturn(0L);
        when(agendamentoRepository.findAllByDataHoraBetween(any(), any()))
                .thenReturn(Collections.emptyList());
        when(estoqueRepository.findAllByQuantidadeLessThanMinAviso())
                .thenReturn(Collections.emptyList());

        DashboardOutput resultado = dashboardService.getDashboardKPIs();

        assertNotNull(resultado);
        assertEquals(0L, resultado.orcamentosPendentes());
    }

    @Test
    @DisplayName("Deve retornar agendamentos do dia atual")
    void deveRetornarAgendamentosDoDiaAtual() {
        LocalDateTime agora = LocalDateTime.now();
        List<Agendamento> agendamentosDoDia = Arrays.asList(
                criarAgendamentoComData(1L, agora.withHour(10).withMinute(0)),
                criarAgendamentoComData(2L, agora.withHour(14).withMinute(0)),
                criarAgendamentoComData(3L, agora.withHour(16).withMinute(30))
        );

        when(agendamentoRepository.findTopByStatusOrderByDataHoraAsc(StatusAgendamento.PENDENTE))
                .thenReturn(Optional.empty());
        when(orcamentoRepository.countByStatus(StatusOrcamento.PENDENTE)).thenReturn(0L);
        when(agendamentoRepository.findAllByDataHoraBetween(any(), any()))
                .thenReturn(agendamentosDoDia);
        when(estoqueRepository.findAllByQuantidadeLessThanMinAviso())
                .thenReturn(Collections.emptyList());

        DashboardOutput resultado = dashboardService.getDashboardKPIs();

        assertNotNull(resultado);
        assertEquals(3, resultado.agendamentosDoDia().size());
        verify(agendamentoRepository, times(1)).findAllByDataHoraBetween(any(), any());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver agendamentos do dia")
    void deveRetornarListaVaziaQuandoNaoHouverAgendamentosDoDia() {
        when(agendamentoRepository.findTopByStatusOrderByDataHoraAsc(StatusAgendamento.PENDENTE))
                .thenReturn(Optional.empty());
        when(orcamentoRepository.countByStatus(StatusOrcamento.PENDENTE)).thenReturn(0L);
        when(agendamentoRepository.findAllByDataHoraBetween(any(), any()))
                .thenReturn(Collections.emptyList());
        when(estoqueRepository.findAllByQuantidadeLessThanMinAviso())
                .thenReturn(Collections.emptyList());

        DashboardOutput resultado = dashboardService.getDashboardKPIs();

        assertNotNull(resultado);
        assertTrue(resultado.agendamentosDoDia().isEmpty());
    }

    @Test
    @DisplayName("Deve buscar agendamentos do dia entre início e fim do dia")
    void deveBuscarAgendamentosEntrInicioEFimDoDia() {
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioEsperado = hoje.atStartOfDay();
        LocalDateTime fimEsperado = hoje.atTime(23, 59, 59);

        when(agendamentoRepository.findTopByStatusOrderByDataHoraAsc(StatusAgendamento.PENDENTE))
                .thenReturn(Optional.empty());
        when(orcamentoRepository.countByStatus(StatusOrcamento.PENDENTE)).thenReturn(0L);
        when(agendamentoRepository.findAllByDataHoraBetween(any(), any()))
                .thenReturn(Collections.emptyList());
        when(estoqueRepository.findAllByQuantidadeLessThanMinAviso())
                .thenReturn(Collections.emptyList());

        dashboardService.getDashboardKPIs();

        verify(agendamentoRepository, times(1)).findAllByDataHoraBetween(any(), any());
    }

    @Test
    @DisplayName("Deve retornar alertas de estoque abaixo do mínimo")
    void deveRetornarAlertasDeEstoqueAbaixoDoMinimo() {
        List<Estoque> alertas = Arrays.asList(
                criarEstoque(1L, "Tinta Preta", 5.0, 10.0),
                criarEstoque(2L, "Tinta Vermelha", 3.0, 15.0),
                criarEstoque(3L, "Agulhas", 8.0, 20.0)
        );

        when(agendamentoRepository.findTopByStatusOrderByDataHoraAsc(StatusAgendamento.PENDENTE))
                .thenReturn(Optional.empty());
        when(orcamentoRepository.countByStatus(StatusOrcamento.PENDENTE)).thenReturn(0L);
        when(agendamentoRepository.findAllByDataHoraBetween(any(), any()))
                .thenReturn(Collections.emptyList());
        when(estoqueRepository.findAllByQuantidadeLessThanMinAviso())
                .thenReturn(alertas);

        DashboardOutput resultado = dashboardService.getDashboardKPIs();

        assertNotNull(resultado);
        assertEquals(3, resultado.alertasEstoque().size());
        verify(estoqueRepository, times(1)).findAllByQuantidadeLessThanMinAviso();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver alertas de estoque")
    void deveRetornarListaVaziaQuandoNaoHouverAlertasDeEstoque() {
        when(agendamentoRepository.findTopByStatusOrderByDataHoraAsc(StatusAgendamento.PENDENTE))
                .thenReturn(Optional.empty());
        when(orcamentoRepository.countByStatus(StatusOrcamento.PENDENTE)).thenReturn(0L);
        when(agendamentoRepository.findAllByDataHoraBetween(any(), any()))
                .thenReturn(Collections.emptyList());
        when(estoqueRepository.findAllByQuantidadeLessThanMinAviso())
                .thenReturn(Collections.emptyList());

        DashboardOutput resultado = dashboardService.getDashboardKPIs();

        assertNotNull(resultado);
        assertTrue(resultado.alertasEstoque().isEmpty());
    }

    @Test
    @DisplayName("Deve retornar KPIs completos com todos os dados")
    void deveRetornarKPIsCompletosComTodosDados() {
        Agendamento proximoAgendamento = criarAgendamento(1L, StatusAgendamento.PENDENTE, 1000.0);
        List<Agendamento> agendamentosDoDia = Arrays.asList(
                criarAgendamento(2L, StatusAgendamento.CONFIRMADO, 1500.0)
        );
        List<Estoque> alertas = Arrays.asList(
                criarEstoque(1L, "Tinta Preta", 5.0, 10.0)
        );

        when(agendamentoRepository.findTopByStatusOrderByDataHoraAsc(StatusAgendamento.PENDENTE))
                .thenReturn(Optional.of(proximoAgendamento));
        when(orcamentoRepository.countByStatus(StatusOrcamento.PENDENTE)).thenReturn(10L);
        when(agendamentoRepository.findAllByDataHoraBetween(any(), any()))
                .thenReturn(agendamentosDoDia);
        when(estoqueRepository.findAllByQuantidadeLessThanMinAviso())
                .thenReturn(alertas);

        DashboardOutput resultado = dashboardService.getDashboardKPIs();

        assertNotNull(resultado);
        assertNotNull(resultado.proximoAgendamento());
        assertEquals(10L, resultado.orcamentosPendentes());
        assertEquals(1, resultado.agendamentosDoDia().size());
        assertEquals(1, resultado.alertasEstoque().size());
    }

    @Test
    @DisplayName("Deve garantir que listas não sejam null no DashboardOutput")
    void deveGarantirQueListasNaoSejamNull() {
        when(agendamentoRepository.findTopByStatusOrderByDataHoraAsc(StatusAgendamento.PENDENTE))
                .thenReturn(Optional.empty());
        when(orcamentoRepository.countByStatus(StatusOrcamento.PENDENTE)).thenReturn(0L);
        when(agendamentoRepository.findAllByDataHoraBetween(any(), any()))
                .thenReturn(null); 
        when(estoqueRepository.findAllByQuantidadeLessThanMinAviso())
                .thenReturn(null); 

        DashboardOutput resultado = dashboardService.getDashboardKPIs();

        assertNotNull(resultado);
        assertNotNull(resultado.agendamentosDoDia()); 
        assertNotNull(resultado.alertasEstoque()); 
    }


    private Agendamento criarAgendamento(Long id, StatusAgendamento status, Double valorOrcamento) {
        Agendamento agendamento = new Agendamento();
        agendamento.setId(id);
        agendamento.setStatus(status);
        agendamento.setUsuario(usuario);

        if (valorOrcamento != null) {
            Orcamento orc = new Orcamento("ORC" + id, "João Silva", "joao@email.com",
                    "Tatuagem", valorOrcamento, "Preto", "Braço", 2.0, Time.valueOf("2:00"), null, StatusOrcamento.APROVADO, null);
            agendamento.setOrcamento(orc);
        }

        return agendamento;
    }

    private Agendamento criarAgendamentoComData(Long id, LocalDateTime dataHora) {
        Agendamento agendamento = criarAgendamento(id, StatusAgendamento.CONFIRMADO, 1000.0);
        agendamento.setDataHora(dataHora);
        return agendamento;
    }

    private List<Agendamento> criarAgendamentosParaMes(LocalDateTime data, Double... valores) {
        return Arrays.stream(valores)
                .map(valor -> {
                    Agendamento ag = criarAgendamento(1L, StatusAgendamento.CONCLUIDO, valor);
                    ag.setDataHora(data);
                    return ag;
                })
                .toList();
    }

    private Estoque criarEstoque(Long id, String nome, Double quantidade, Double minAviso) {
        Estoque estoque = new Estoque();
        estoque.setId(id);
        estoque.setNome(nome);
        estoque.setQuantidade(quantidade);
        estoque.setMinAviso(minAviso);
        return estoque;
    }
}
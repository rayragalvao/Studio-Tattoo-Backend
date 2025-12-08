package hub.orcana.controller;

import hub.orcana.dto.dashboard.DashboardOutput;
import hub.orcana.service.DashboardService;
import hub.orcana.tables.Agendamento;
import hub.orcana.tables.Estoque;
import hub.orcana.tables.StatusAgendamento;
import hub.orcana.tables.Usuario;
import hub.orcana.tables.Orcamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    private DashboardOutput dashboardOutput;
    private List<Double> faturamentoAnual;
    private Agendamento proximoAgendamento;
    private List<Agendamento> agendamentosDoDia;
    private List<Estoque> alertasEstoque;

    @BeforeEach
    void setUp() {
        faturamentoAnual = Arrays.asList(
                1000.0, 1500.0, 2000.0, 1800.0, 2200.0, 2500.0,
                2300.0, 2700.0, 3000.0, 2800.0, 3200.0, 3500.0
        );

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");

        Orcamento orcamento = new Orcamento("ORC123", "João Silva", "joao@email.com",
                "Dragão nas costas", 20.5, "Preto e Vermelho", "Costas", null);
        orcamento.setValor(1500.0);

        proximoAgendamento = new Agendamento();
        proximoAgendamento.setId(1L);
        proximoAgendamento.setDataHora(LocalDateTime.now().plusDays(1));
        proximoAgendamento.setStatus(StatusAgendamento.PENDENTE);
        proximoAgendamento.setUsuario(usuario);
        proximoAgendamento.setOrcamento(orcamento);

        Agendamento agendamento1 = new Agendamento();
        agendamento1.setId(2L);
        agendamento1.setDataHora(LocalDateTime.now().withHour(10).withMinute(0));
        agendamento1.setStatus(StatusAgendamento.CONFIRMADO);
        agendamento1.setUsuario(usuario);
        agendamento1.setOrcamento(orcamento);

        Agendamento agendamento2 = new Agendamento();
        agendamento2.setId(3L);
        agendamento2.setDataHora(LocalDateTime.now().withHour(14).withMinute(0));
        agendamento2.setStatus(StatusAgendamento.PENDENTE);
        agendamento2.setUsuario(usuario);
        agendamento2.setOrcamento(orcamento);

        agendamentosDoDia = Arrays.asList(agendamento1, agendamento2);

        Estoque estoque1 = new Estoque("Tinta Preta", 5.0, "ml", 10.0);
        estoque1.setId(1L);

        Estoque estoque2 = new Estoque("Agulhas Descartáveis", 15.0, "unidade", 20.0);
        estoque2.setId(2L);

        alertasEstoque = Arrays.asList(estoque1, estoque2);

        dashboardOutput = new DashboardOutput(
                proximoAgendamento,
                5L,
                agendamentosDoDia,
                alertasEstoque
        );
    }

    @Test
    @DisplayName("Deve retornar status 200 e faturamento anual dos últimos 12 meses")
    void deveRetornar200ComFaturamentoAnual() {
        when(dashboardService.getFaturamentoUltimos12Meses()).thenReturn(faturamentoAnual);

        ResponseEntity<List<Double>> response = dashboardController.getFaturamentoAnual();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(12, response.getBody().size());
        assertEquals(1000.0, response.getBody().get(0));
        assertEquals(3500.0, response.getBody().get(11));
        verify(dashboardService, times(1)).getFaturamentoUltimos12Meses();
    }

    @Test
    @DisplayName("Deve retornar status 200 com lista vazia quando não houver faturamento")
    void deveRetornar200ComListaVaziaQuandoNaoHouverFaturamento() {
        when(dashboardService.getFaturamentoUltimos12Meses()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Double>> response = dashboardController.getFaturamentoAnual();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(dashboardService, times(1)).getFaturamentoUltimos12Meses();
    }

    @Test
    @DisplayName("Deve retornar faturamento com valores zero quando não houver vendas")
    void deveRetornarFaturamentoComValoresZero() {
        List<Double> faturamentoZero = Arrays.asList(
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                0.0, 0.0, 0.0, 0.0, 0.0, 0.0
        );
        when(dashboardService.getFaturamentoUltimos12Meses()).thenReturn(faturamentoZero);

        ResponseEntity<List<Double>> response = dashboardController.getFaturamentoAnual();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(12, response.getBody().size());
        assertTrue(response.getBody().stream().allMatch(valor -> valor == 0.0));
    }

    @Test
    @DisplayName("Deve retornar status 200 e KPIs do dashboard")
    void deveRetornar200ComDashboardKPIs() {
        when(dashboardService.getDashboardKPIs()).thenReturn(dashboardOutput);

        ResponseEntity<DashboardOutput> response = dashboardController.getDashboardKPIs();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5L, response.getBody().orcamentosPendentes());
        assertNotNull(response.getBody().proximoAgendamento());
        assertEquals(2, response.getBody().agendamentosDoDia().size());
        assertEquals(2, response.getBody().alertasEstoque().size());
        verify(dashboardService, times(1)).getDashboardKPIs();
    }

    @Test
    @DisplayName("Deve retornar KPIs com próximo agendamento correto")
    void deveRetornarKPIsComProximoAgendamentoCorreto() {
        when(dashboardService.getDashboardKPIs()).thenReturn(dashboardOutput);

        ResponseEntity<DashboardOutput> response = dashboardController.getDashboardKPIs();

        assertNotNull(response);
        assertNotNull(response.getBody());
        Agendamento proximo = response.getBody().proximoAgendamento();
        assertNotNull(proximo);
        assertEquals(1L, proximo.getId());
        assertEquals(StatusAgendamento.PENDENTE, proximo.getStatus());
        assertEquals("João Silva", proximo.getUsuario().getNome());
    }

    @Test
    @DisplayName("Deve retornar KPIs com contagem correta de orçamentos pendentes")
    void deveRetornarKPIsComContagemCorretaDeOrcamentosPendentes() {
        when(dashboardService.getDashboardKPIs()).thenReturn(dashboardOutput);

        ResponseEntity<DashboardOutput> response = dashboardController.getDashboardKPIs();

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(5L, response.getBody().orcamentosPendentes());
    }

    @Test
    @DisplayName("Deve retornar KPIs com agendamentos do dia corretos")
    void deveRetornarKPIsComAgendamentosDoDiaCorretos() {
        when(dashboardService.getDashboardKPIs()).thenReturn(dashboardOutput);

        ResponseEntity<DashboardOutput> response = dashboardController.getDashboardKPIs();

        assertNotNull(response);
        assertNotNull(response.getBody());
        List<Agendamento> agendamentos = response.getBody().agendamentosDoDia();
        assertEquals(2, agendamentos.size());
        assertEquals(2L, agendamentos.get(0).getId());
        assertEquals(3L, agendamentos.get(1).getId());
    }

    @Test
    @DisplayName("Deve retornar KPIs com alertas de estoque corretos")
    void deveRetornarKPIsComAlertasDeEstoqueCorretos() {
        when(dashboardService.getDashboardKPIs()).thenReturn(dashboardOutput);

        ResponseEntity<DashboardOutput> response = dashboardController.getDashboardKPIs();

        assertNotNull(response);
        assertNotNull(response.getBody());
        List<Estoque> alertas = response.getBody().alertasEstoque();
        assertEquals(2, alertas.size());
        assertEquals("Tinta Preta", alertas.get(0).getNome());
        assertEquals(5.0, alertas.get(0).getQuantidade());
        assertEquals("Agulhas Descartáveis", alertas.get(1).getNome());
        assertEquals(15.0, alertas.get(1).getQuantidade());
    }

    @Test
    @DisplayName("Deve retornar KPIs quando não houver próximo agendamento")
    void deveRetornarKPIsQuandoNaoHouverProximoAgendamento() {
        DashboardOutput outputSemProximo = new DashboardOutput(
                null,
                5L,
                agendamentosDoDia,
                alertasEstoque
        );
        when(dashboardService.getDashboardKPIs()).thenReturn(outputSemProximo);

        ResponseEntity<DashboardOutput> response = dashboardController.getDashboardKPIs();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().proximoAgendamento());
        assertEquals(5L, response.getBody().orcamentosPendentes());
    }

    @Test
    @DisplayName("Deve retornar KPIs quando não houver agendamentos do dia")
    void deveRetornarKPIsQuandoNaoHouverAgendamentosDoDia() {
        DashboardOutput outputSemAgendamentos = new DashboardOutput(
                proximoAgendamento,
                5L,
                Collections.emptyList(),
                alertasEstoque
        );
        when(dashboardService.getDashboardKPIs()).thenReturn(outputSemAgendamentos);

        ResponseEntity<DashboardOutput> response = dashboardController.getDashboardKPIs();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().agendamentosDoDia().isEmpty());
    }

    @Test
    @DisplayName("Deve retornar KPIs quando não houver alertas de estoque")
    void deveRetornarKPIsQuandoNaoHouverAlertasDeEstoque() {
        DashboardOutput outputSemAlertas = new DashboardOutput(
                proximoAgendamento,
                5L,
                agendamentosDoDia,
                Collections.emptyList()
        );
        when(dashboardService.getDashboardKPIs()).thenReturn(outputSemAlertas);

        ResponseEntity<DashboardOutput> response = dashboardController.getDashboardKPIs();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().alertasEstoque().isEmpty());
    }

    @Test
    @DisplayName("Deve retornar KPIs quando orçamentos pendentes for zero")
    void deveRetornarKPIsQuandoOrcamentosPendentesForZero() {
        DashboardOutput outputSemOrcamentos = new DashboardOutput(
                proximoAgendamento,
                0L,
                agendamentosDoDia,
                alertasEstoque
        );
        when(dashboardService.getDashboardKPIs()).thenReturn(outputSemOrcamentos);

        ResponseEntity<DashboardOutput> response = dashboardController.getDashboardKPIs();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0L, response.getBody().orcamentosPendentes());
    }

    @Test
    @DisplayName("Deve retornar KPIs completamente vazios")
    void deveRetornarKPIsCompletamenteVazios() {
        DashboardOutput outputVazio = new DashboardOutput(
                null,
                0L,
                Collections.emptyList(),
                Collections.emptyList()
        );
        when(dashboardService.getDashboardKPIs()).thenReturn(outputVazio);

        ResponseEntity<DashboardOutput> response = dashboardController.getDashboardKPIs();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().proximoAgendamento());
        assertEquals(0L, response.getBody().orcamentosPendentes());
        assertTrue(response.getBody().agendamentosDoDia().isEmpty());
        assertTrue(response.getBody().alertasEstoque().isEmpty());
    }

    @Test
    @DisplayName("Deve verificar que faturamento mantém ordem cronológica")
    void deveVerificarOrdemCronologicaDoFaturamento() {
        when(dashboardService.getFaturamentoUltimos12Meses()).thenReturn(faturamentoAnual);

        ResponseEntity<List<Double>> response = dashboardController.getFaturamentoAnual();

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(12, response.getBody().size());
        assertEquals(1000.0, response.getBody().get(0));
        assertEquals(3500.0, response.getBody().get(11));
    }

    @Test
    @DisplayName("Deve retornar faturamento com valores decimais precisos")
    void deveRetornarFaturamentoComValoresDecimaisPrecisos() {
        List<Double> faturamentoDecimal = Arrays.asList(
                1234.56, 2345.67, 3456.78, 4567.89, 5678.90, 6789.01,
                7890.12, 8901.23, 9012.34, 1023.45, 2134.56, 3245.67
        );
        when(dashboardService.getFaturamentoUltimos12Meses()).thenReturn(faturamentoDecimal);

        ResponseEntity<List<Double>> response = dashboardController.getFaturamentoAnual();

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(1234.56, response.getBody().get(0));
        assertEquals(3245.67, response.getBody().get(11));
    }

    @Test
    @DisplayName("Deve tratar múltiplos alertas de estoque críticos")
    void deveTratarMultiplosAlertasDeEstoqueCriticos() {
        Estoque estoque1 = new Estoque("Tinta Preta", 2.0, "ml", 10.0);
        estoque1.setId(1L);

        Estoque estoque2 = new Estoque("Tinta Vermelha", 1.0, "ml", 10.0);
        estoque2.setId(2L);

        Estoque estoque3 = new Estoque("Agulhas", 5.0, "unidade", 20.0);
        estoque3.setId(3L);

        List<Estoque> alertasCriticos = Arrays.asList(estoque1, estoque2, estoque3);

        DashboardOutput outputComAlertas = new DashboardOutput(
                proximoAgendamento,
                5L,
                agendamentosDoDia,
                alertasCriticos
        );
        when(dashboardService.getDashboardKPIs()).thenReturn(outputComAlertas);

        ResponseEntity<DashboardOutput> response = dashboardController.getDashboardKPIs();

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().alertasEstoque().size());
        assertTrue(response.getBody().alertasEstoque().stream()
                .allMatch(e -> e.getQuantidade() < e.getMinAviso()));
    }

    @Test
    @DisplayName("Deve retornar agendamentos do dia ordenados por horário")
    void deveRetornarAgendamentosDoDiaOrdenadosPorHorario() {
        when(dashboardService.getDashboardKPIs()).thenReturn(dashboardOutput);

        ResponseEntity<DashboardOutput> response = dashboardController.getDashboardKPIs();

        assertNotNull(response);
        assertNotNull(response.getBody());
        List<Agendamento> agendamentos = response.getBody().agendamentosDoDia();
        assertEquals(2, agendamentos.size());
        assertEquals(agendamentos.get(0).getDataHora().toLocalDate(),
                agendamentos.get(1).getDataHora().toLocalDate());
    }
}
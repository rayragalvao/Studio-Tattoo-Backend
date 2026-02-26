package hub.orcana.controller;

import hub.orcana.dto.dashboard.DashboardKPIsOutput;
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

    // ------------------ TESTES PARA GET /dashboard/estatisticas ------------------

    @Test
    @DisplayName("Deve retornar status 200 e estatísticas completas")
    void deveRetornar200ComEstatisticasCompletas() {
        // Arrange
        DashboardKPIsOutput estatisticas = new DashboardKPIsOutput(
                15L,  // totalAgendamentos
                8L,   // agendamentosConcluidos
                12500.0, // faturamentoTotal
                150   // totalProdutos
        );

        when(dashboardService.getEstatisticas()).thenReturn(estatisticas);

        // Act
        ResponseEntity<DashboardKPIsOutput> response = dashboardController.getEstatisticas();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(15L, response.getBody().totalAgendamentos());
        assertEquals(8L, response.getBody().agendamentosConcluidos());
        assertEquals(12500.0, response.getBody().faturamentoTotal());
        assertEquals(150, response.getBody().totalProdutos());
        verify(dashboardService, times(1)).getEstatisticas();
    }

    @Test
    @DisplayName("Deve retornar estatísticas com valores zerados quando não houver dados")
    void deveRetornarEstatisticasComValoresZerados() {
        // Arrange
        DashboardKPIsOutput estatisticasVazias = new DashboardKPIsOutput(
                0L,    // totalAgendamentos
                0L,    // agendamentosConcluidos
                0.0,   // faturamentoTotal
                0      // totalProdutos
        );

        when(dashboardService.getEstatisticas()).thenReturn(estatisticasVazias);

        // Act
        ResponseEntity<DashboardKPIsOutput> response = dashboardController.getEstatisticas();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0L, response.getBody().totalAgendamentos());
        assertEquals(0L, response.getBody().agendamentosConcluidos());
        assertEquals(0.0, response.getBody().faturamentoTotal());
        assertEquals(0, response.getBody().totalProdutos());
        verify(dashboardService, times(1)).getEstatisticas();
    }

    @Test
    @DisplayName("Deve retornar estatísticas com agendamentos mas sem faturamento")
    void deveRetornarEstatisticasComAgendamentosSemFaturamento() {
        // Arrange
        DashboardKPIsOutput estatisticas = new DashboardKPIsOutput(
                10L,   // totalAgendamentos
                0L,    // agendamentosConcluidos (nenhum concluído)
                0.0,   // faturamentoTotal (sem faturamento)
                75     // totalProdutos
        );

        when(dashboardService.getEstatisticas()).thenReturn(estatisticas);

        // Act
        ResponseEntity<DashboardKPIsOutput> response = dashboardController.getEstatisticas();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().totalAgendamentos());
        assertEquals(0L, response.getBody().agendamentosConcluidos());
        assertEquals(0.0, response.getBody().faturamentoTotal());
        assertEquals(75, response.getBody().totalProdutos());
        verify(dashboardService, times(1)).getEstatisticas();
    }

    @Test
    @DisplayName("Deve retornar estatísticas com números grandes")
    void deveRetornarEstatisticasComNumerosGrandes() {
        // Arrange
        DashboardKPIsOutput estatisticasGrandes = new DashboardKPIsOutput(
                1000L,     // totalAgendamentos
                750L,      // agendamentosConcluidos
                125000.50, // faturamentoTotal
                5000       // totalProdutos
        );

        when(dashboardService.getEstatisticas()).thenReturn(estatisticasGrandes);

        // Act
        ResponseEntity<DashboardKPIsOutput> response = dashboardController.getEstatisticas();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1000L, response.getBody().totalAgendamentos());
        assertEquals(750L, response.getBody().agendamentosConcluidos());
        assertEquals(125000.50, response.getBody().faturamentoTotal());
        assertEquals(5000, response.getBody().totalProdutos());
        verify(dashboardService, times(1)).getEstatisticas();
    }

    @Test
    @DisplayName("Deve propagar exceção quando service lança RuntimeException")
    void devePropagaExcecaoQuandoServiceLancaRuntimeException() {
        // Arrange
        String mensagemErro = "Erro ao calcular estatísticas";
        when(dashboardService.getEstatisticas())
                .thenThrow(new RuntimeException(mensagemErro));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> dashboardController.getEstatisticas());

        assertEquals(mensagemErro, exception.getMessage());
        verify(dashboardService, times(1)).getEstatisticas();
    }

    @Test
    @DisplayName("Deve propagar exceção quando service lança IllegalArgumentException")
    void devePropagaExcecaoQuandoServiceLancaIllegalArgumentException() {
        // Arrange
        String mensagemErro = "Parâmetros inválidos para cálculo";
        when(dashboardService.getEstatisticas())
                .thenThrow(new IllegalArgumentException(mensagemErro));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> dashboardController.getEstatisticas());

        assertEquals(mensagemErro, exception.getMessage());
        verify(dashboardService, times(1)).getEstatisticas();
    }

    @Test
    @DisplayName("Deve validar estrutura das estatísticas")
    void deveValidarEstruturaDasEstatisticas() {
        // Arrange
        DashboardKPIsOutput estatisticas = new DashboardKPIsOutput(
                100L,      // long
                85L,       // long
                15750.75,  // Double
                250        // Integer
        );

        when(dashboardService.getEstatisticas()).thenReturn(estatisticas);

        // Act
        ResponseEntity<DashboardKPIsOutput> response = dashboardController.getEstatisticas();

        // Assert
        assertNotNull(response);
        assertNotNull(response.getBody());

        // Validar que todos os campos são válidos
        assertEquals(100L, response.getBody().totalAgendamentos());
        assertEquals(85L, response.getBody().agendamentosConcluidos());
        assertEquals(15750.75, response.getBody().faturamentoTotal());
        assertEquals(250, response.getBody().totalProdutos());
    }

    @Test
    @DisplayName("Deve validar faturamento com valores decimais")
    void deveValidarFaturamentoComValoresDecimais() {
        // Arrange
        DashboardKPIsOutput estatisticas = new DashboardKPIsOutput(
                50L,
                35L,
                9999.99,  // Valor com centavos
                120
        );

        when(dashboardService.getEstatisticas()).thenReturn(estatisticas);

        // Act
        ResponseEntity<DashboardKPIsOutput> response = dashboardController.getEstatisticas();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().faturamentoTotal());
        assertEquals(9999.99, response.getBody().faturamentoTotal());
    }

    @Test
    @DisplayName("Deve validar que totalProdutos pode ser null")
    void deveValidarQueTotalProdutosPodeSerNull() {
        // Arrange
        DashboardKPIsOutput estatisticas = new DashboardKPIsOutput(
                25L,
                20L,
                5000.0,
                null  // totalProdutos null
        );

        when(dashboardService.getEstatisticas()).thenReturn(estatisticas);

        // Act
        ResponseEntity<DashboardKPIsOutput> response = dashboardController.getEstatisticas();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(25L, response.getBody().totalAgendamentos());
        assertEquals(20L, response.getBody().agendamentosConcluidos());
        assertEquals(5000.0, response.getBody().faturamentoTotal());
        assertNull(response.getBody().totalProdutos());
    }

    @Test
    @DisplayName("Deve validar múltiplas chamadas consecutivas")
    void deveValidarMultiplasChamadasConsecutivas() {
        // Arrange
        DashboardKPIsOutput estatisticas1 = new DashboardKPIsOutput(10L, 8L, 1000.0, 50);
        DashboardKPIsOutput estatisticas2 = new DashboardKPIsOutput(15L, 12L, 1500.0, 75);

        when(dashboardService.getEstatisticas())
                .thenReturn(estatisticas1)
                .thenReturn(estatisticas2);

        // Act
        ResponseEntity<DashboardKPIsOutput> response1 = dashboardController.getEstatisticas();
        ResponseEntity<DashboardKPIsOutput> response2 = dashboardController.getEstatisticas();

        // Assert
        assertNotNull(response1);
        assertNotNull(response2);
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertNotNull(response1.getBody());
        assertNotNull(response2.getBody());

        assertEquals(10L, response1.getBody().totalAgendamentos());
        assertEquals(15L, response2.getBody().totalAgendamentos());

        verify(dashboardService, times(2)).getEstatisticas();
    }

    @Test
    @DisplayName("Deve validar cenário com faturamento null")
    void deveValidarCenarioComFaturamentoNull() {
        // Arrange
        DashboardKPIsOutput estatisticas = new DashboardKPIsOutput(
                5L,
                3L,
                null,  // faturamentoTotal null
                30
        );

        when(dashboardService.getEstatisticas()).thenReturn(estatisticas);

        // Act
        ResponseEntity<DashboardKPIsOutput> response = dashboardController.getEstatisticas();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5L, response.getBody().totalAgendamentos());
        assertEquals(3L, response.getBody().agendamentosConcluidos());
        assertNull(response.getBody().faturamentoTotal());
        assertEquals(30, response.getBody().totalProdutos());
    }
}



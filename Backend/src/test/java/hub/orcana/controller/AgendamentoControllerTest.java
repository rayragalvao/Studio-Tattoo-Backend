package hub.orcana.controller;

import hub.orcana.dto.agendamento.AdicionarMateriaisRequest;
import hub.orcana.dto.agendamento.CadastroAgendamentoInput;
import hub.orcana.dto.agendamento.DetalhesAgendamentoOutput;
import hub.orcana.dto.agendamento.MaterialUsadoRequest;
import hub.orcana.service.AgendamentoService;
import hub.orcana.tables.StatusAgendamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoControllerTest {

    @Mock
    private AgendamentoService agendamentoService;

    @InjectMocks
    private AgendamentoController agendamentoController;

    private DetalhesAgendamentoOutput agendamentoOutput;
    private CadastroAgendamentoInput agendamentoInput;
    private LocalDateTime dataHora;

    @BeforeEach
    void setUp() {
        // Usar data sempre no futuro para evitar problemas de validação
        dataHora = LocalDateTime.now().plusDays(30);

        agendamentoOutput = new DetalhesAgendamentoOutput(
                1L,
                dataHora,
                "PENDENTE",
                "João Silva",
                "joao@email.com",
                "ORC123",
                "Dragão nas costas",
                20.5,
                "Preto e Vermelho",
                "Costas",
                null,
                List.of(),
                null,
                null,
                null
        );

        agendamentoInput = new CadastroAgendamentoInput(
                "joao@email.com",
                "ORC123",
                dataHora,
                StatusAgendamento.PENDENTE,
                null,
                null,
                null
        );
    }

    @Test
    @DisplayName("Deve retornar status 200 e lista de agendamentos")
    void deveRetornar200ComListaDeAgendamentos() {
        // Arrange
        DetalhesAgendamentoOutput agendamento2 = new DetalhesAgendamentoOutput(
                2L,
                dataHora.plusDays(1), // Data futura
                "CONFIRMADO",
                "Maria Santos",
                "maria@email.com",
                "ORC124",
                "Rosa no braço",
                10.0,
                "Rosa e Verde",
                "Braço",
                null,
                List.of(),
                null,
                null,
                null
        );
        List<DetalhesAgendamentoOutput> agendamentos = List.of(agendamentoOutput, agendamento2);
        when(agendamentoService.getAgendamentos()).thenReturn(agendamentos);

        // Act
        ResponseEntity<List<DetalhesAgendamentoOutput>> response = agendamentoController.getAgendamento();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("João Silva", response.getBody().getFirst().nomeUsuario());
        assertEquals("Maria Santos", response.getBody().get(1).nomeUsuario());
        verify(agendamentoService, times(1)).getAgendamentos();
    }

    @Test
    @DisplayName("Deve retornar status 204 quando lista estiver vazia")
    void deveRetornar204QuandoListaVazia() {

        when(agendamentoService.getAgendamentos()).thenReturn(Collections.emptyList());


        ResponseEntity<List<DetalhesAgendamentoOutput>> response = agendamentoController.getAgendamento();


        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(agendamentoService, times(1)).getAgendamentos();
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException quando ocorrer exceção")
    void deveLancarResponseStatusExceptionQuandoOcorrerExcecao() {
        // Arrange
        when(agendamentoService.getAgendamentos()).thenThrow(new RuntimeException("Erro ao buscar agendamentos"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> agendamentoController.getAgendamento());

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Erro ao buscar agendamentos", exception.getReason());
        verify(agendamentoService, times(1)).getAgendamentos();
    }

    @Test
    @DisplayName("Deve retornar status 200 e agendamento por ID")
    void deveRetornar200ComAgendamentoPorId() {

        when(agendamentoService.getAgendamentoPorId(1L)).thenReturn(agendamentoOutput);


        ResponseEntity<DetalhesAgendamentoOutput> response = agendamentoController.getAgendamentoPorId(1L);


        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("João Silva", response.getBody().nomeUsuario());
        verify(agendamentoService, times(1)).getAgendamentoPorId(1L);
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException quando agendamento não for encontrado")
    void deveLancarExcecaoQuandoAgendamentoNaoEncontrado() {
        // Arrange
        when(agendamentoService.getAgendamentoPorId(999L))
                .thenThrow(new IllegalArgumentException("Agendamento não encontrado."));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> agendamentoController.getAgendamentoPorId(999L));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Agendamento não encontrado.", exception.getReason());
        verify(agendamentoService, times(1)).getAgendamentoPorId(999L);
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException quando ID for inválido")
    void deveLancarExcecaoQuandoIdInvalido() {
        // Arrange
        when(agendamentoService.getAgendamentoPorId(anyLong()))
                .thenThrow(new IllegalArgumentException("ID inválido"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> agendamentoController.getAgendamentoPorId(1L));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("ID inválido", exception.getReason());
    }

    @Test
    @DisplayName("Deve retornar status 200 e agendamentos por status")
    void deveRetornar200ComAgendamentosPorStatus() {
        // Arrange
        List<DetalhesAgendamentoOutput> agendamentos = List.of(agendamentoOutput);
        when(agendamentoService.getAgendamentosByStatus("PENDENTE")).thenReturn(agendamentos);

        // Act
        ResponseEntity<List<DetalhesAgendamentoOutput>> response =
                agendamentoController.getAgendamentosByStatus("PENDENTE");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("PENDENTE", response.getBody().getFirst().status());
        verify(agendamentoService, times(1)).getAgendamentosByStatus("PENDENTE");
    }

    @Test
    @DisplayName("Deve retornar status 204 quando não houver agendamentos com o status")
    void deveRetornar204QuandoNaoHouverAgendamentosComStatus() {

        when(agendamentoService.getAgendamentosByStatus("CANCELADO")).thenReturn(Collections.emptyList());


        ResponseEntity<List<DetalhesAgendamentoOutput>> response = 
                agendamentoController.getAgendamentosByStatus("CANCELADO");


        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(agendamentoService, times(1)).getAgendamentosByStatus("CANCELADO");
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException quando status for inválido")
    void deveLancarExcecaoQuandoStatusInvalido() {
        // Arrange
        when(agendamentoService.getAgendamentosByStatus(anyString()))
                .thenThrow(new RuntimeException("Status inválido"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            agendamentoController.getAgendamentosByStatus("INVALIDO");
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Status inválido", exception.getReason());
    }

    @Test
    @DisplayName("Deve retornar status 201 ao criar agendamento com sucesso")
    void deveRetornar201AoCriarAgendamentoComSucesso() {

        when(agendamentoService.postAgendamento(any(CadastroAgendamentoInput.class)))
                .thenReturn(agendamentoOutput);


        ResponseEntity<DetalhesAgendamentoOutput> response = agendamentoController.postAgendamento(agendamentoInput);


        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("João Silva", response.getBody().nomeUsuario());
        verify(agendamentoService, times(1)).postAgendamento(any(CadastroAgendamentoInput.class));
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException quando agendamento já existir")
    void deveLancarExcecaoQuandoAgendamentoJaExistir() {
        // Arrange
        when(agendamentoService.postAgendamento(any(CadastroAgendamentoInput.class)))
                .thenThrow(new IllegalArgumentException("Já existe um agendamento para este código de orçamento."));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            agendamentoController.postAgendamento(agendamentoInput);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Já existe um agendamento para este código de orçamento.", exception.getReason());
        verify(agendamentoService, times(1)).postAgendamento(any(CadastroAgendamentoInput.class));
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException quando dados forem inválidos")
    void deveLancarExcecaoQuandoDadosInvalidos() {
        // Arrange
        when(agendamentoService.postAgendamento(any(CadastroAgendamentoInput.class)))
                .thenThrow(new RuntimeException("Dados inválidos"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            agendamentoController.postAgendamento(agendamentoInput);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Dados inválidos", exception.getReason());
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException quando usuário não for encontrado")
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        // Arrange
        when(agendamentoService.postAgendamento(any(CadastroAgendamentoInput.class)))
                .thenThrow(new IllegalArgumentException("Usuário é obrigatório."));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            agendamentoController.postAgendamento(agendamentoInput);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Usuário é obrigatório.", exception.getReason());
    }

    @Test
    @DisplayName("Deve retornar status 200 ao atualizar agendamento com sucesso")
    void deveRetornar200AoAtualizarAgendamentoComSucesso() {
        // Arrange
        when(agendamentoService.putAgendamentoById(eq(1L), any(CadastroAgendamentoInput.class)))
                .thenReturn(agendamentoOutput);

        // Act
        ResponseEntity<DetalhesAgendamentoOutput> response =
                agendamentoController.putAgendamento(1L, agendamentoInput);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("João Silva", response.getBody().nomeUsuario());
        verify(agendamentoService, times(1)).putAgendamentoById(eq(1L), any(CadastroAgendamentoInput.class));
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException quando agendamento não existir para atualização")
    void deveLancarExcecaoQuandoAgendamentoNaoExistirParaAtualizacao() {
        // Arrange
        when(agendamentoService.putAgendamentoById(eq(999L), any(CadastroAgendamentoInput.class)))
                .thenThrow(new IllegalArgumentException("Agendamento não encontrado."));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            agendamentoController.putAgendamento(999L, agendamentoInput);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Agendamento não encontrado.", exception.getReason());
        verify(agendamentoService, times(1)).putAgendamentoById(eq(999L), any(CadastroAgendamentoInput.class));
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException ao atualizar com dados inválidos")
    void deveLancarExcecaoAoAtualizarComDadosInvalidos() {
        // Arrange
        when(agendamentoService.putAgendamentoById(eq(1L), any(CadastroAgendamentoInput.class)))
                .thenThrow(new RuntimeException("Erro ao processar dados"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            agendamentoController.putAgendamento(1L, agendamentoInput);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Erro ao processar dados", exception.getReason());
    }

    @Test
    @DisplayName("Deve retornar status 204 ao deletar agendamento com sucesso")
    void deveRetornar204AoDeletarAgendamentoComSucesso() {

        doNothing().when(agendamentoService).deleteAgendamentoById(1L);


        ResponseEntity<String> response = agendamentoController.deleteAgendamento(1L);


        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(agendamentoService, times(1)).deleteAgendamentoById(1L);
    }

    @Test
    @DisplayName("Deve retornar status 404 ao deletar agendamento inexistente")
    void deveRetornar404AoDeletarAgendamentoInexistente() {

        doThrow(new IllegalArgumentException("Agendamento não encontrado."))
                .when(agendamentoService).deleteAgendamentoById(999L);


        ResponseEntity<String> response = agendamentoController.deleteAgendamento(999L);


        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Agendamento não encontrado"));
        verify(agendamentoService, times(1)).deleteAgendamentoById(999L);
    }

    @Test
    @DisplayName("Deve retornar status 400 quando ocorrer erro ao deletar")
    void deveRetornar400QuandoOcorrerErroAoDeletar() {

        doThrow(new RuntimeException("Erro ao excluir"))
                .when(agendamentoService).deleteAgendamentoById(1L);


        ResponseEntity<String> response = agendamentoController.deleteAgendamento(1L);


        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Erro ao excluir"));
    }

    @Test
    @DisplayName("Deve tratar corretamente agendamento com status null")
    void deveTratarAgendamentoComStatusNull() {

        CadastroAgendamentoInput inputSemStatus = new CadastroAgendamentoInput(
                "joao@email.com",
                "ORC123",
                dataHora,
                null,
                null,
                null,
                null
        );
        when(agendamentoService.postAgendamento(any(CadastroAgendamentoInput.class)))
                .thenReturn(agendamentoOutput);


        ResponseEntity<DetalhesAgendamentoOutput> response = agendamentoController.postAgendamento(inputSemStatus);


        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve tratar múltiplos agendamentos para o mesmo usuário")
    void deveTratarMultiplosAgendamentosParaMesmoUsuario() {
        // Arrange
        DetalhesAgendamentoOutput agendamento2 = new DetalhesAgendamentoOutput(
                2L,
                dataHora.plusDays(2),
                "CONFIRMADO",
                "João Silva",
                "joao@email.com",
                "ORC125",
                "Leão no braço",
                15.0,
                "Colorido",
                "Braço",
                null,
                List.of(),
                null,
                null,
                null
        );
        List<DetalhesAgendamentoOutput> agendamentos = List.of(agendamentoOutput, agendamento2);
        when(agendamentoService.getAgendamentos()).thenReturn(agendamentos);

        // Act
        ResponseEntity<List<DetalhesAgendamentoOutput>> response = agendamentoController.getAgendamento();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("joao@email.com", response.getBody().getFirst().emailUsuario());
        assertEquals("joao@email.com", response.getBody().get(1).emailUsuario());
    }

    @Test
    @DisplayName("Deve validar busca case-insensitive por status")
    void deveValidarBuscaCaseInsensitivePorStatus() {
        // Arrange
        List<DetalhesAgendamentoOutput> agendamentos = List.of(agendamentoOutput);
        when(agendamentoService.getAgendamentosByStatus("aguardando")).thenReturn(agendamentos);

        // Act
        ResponseEntity<List<DetalhesAgendamentoOutput>> response =
                agendamentoController.getAgendamentosByStatus("aguardando");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(agendamentoService, times(1)).getAgendamentosByStatus("aguardando");
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException ao buscar com ID negativo")
    void deveLancarExcecaoAoBuscarComIdNegativo() {
        // Arrange
        when(agendamentoService.getAgendamentoPorId(-1L))
                .thenThrow(new IllegalArgumentException("ID inválido"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            agendamentoController.getAgendamentoPorId(-1L);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("ID inválido", exception.getReason());
    }

    @Test
    @DisplayName("Deve tratar agendamento com data no limite do futuro")
    void deveTratarAgendamentoComDataNoLimiteFuturo() {

        LocalDateTime dataLimite = LocalDateTime.now().plusYears(1);
        CadastroAgendamentoInput inputFuturo = new CadastroAgendamentoInput(
                "joao@email.com",
                "ORC123",
                dataLimite,
                StatusAgendamento.PENDENTE,
                null,
                null,
                null
        );
        DetalhesAgendamentoOutput outputFuturo = new DetalhesAgendamentoOutput(
                1L, dataLimite, "PENDENTE", "João Silva", "joao@email.com",
                "ORC123", "Dragão nas costas", 20.5, "Preto e Vermelho", "Costas", null, List.of(), null, null, null
        );
        when(agendamentoService.postAgendamento(any(CadastroAgendamentoInput.class)))
                .thenReturn(outputFuturo);


        ResponseEntity<DetalhesAgendamentoOutput> response = agendamentoController.postAgendamento(inputFuturo);


        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve tratar atualização parcial de agendamento")
    void deveTratarAtualizacaoParcialDeAgendamento() {
        // Arrange
        CadastroAgendamentoInput inputParcial = new CadastroAgendamentoInput(
                "joao@email.com",
                "ORC123",
                dataHora.plusHours(2),
                StatusAgendamento.CONFIRMADO,
                null,
                null,
                null
        );
        DetalhesAgendamentoOutput outputAtualizado = new DetalhesAgendamentoOutput(
                1L, dataHora.plusHours(2), "CONFIRMADO", "João Silva", "joao@email.com",
                "ORC123", "Dragão nas costas", 20.5, "Preto e Vermelho", "Costas", null, List.of(), null, null, null
        );
        when(agendamentoService.putAgendamentoById(eq(1L), any(CadastroAgendamentoInput.class)))
                .thenReturn(outputAtualizado);

        // Act
        ResponseEntity<DetalhesAgendamentoOutput> response =
                agendamentoController.putAgendamento(1L, inputParcial);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("CONFIRMADO", response.getBody().status());
    }

    // ------------------ TESTES DOS MÉTODOS RELACIONAIS ------------------

    @Test
    @DisplayName("Deve retornar agendamento completo com sucesso")
    void deveRetornarAgendamentoCompletoComSucesso() {
        // Arrange
        when(agendamentoService.getAgendamentoCompleto(1L)).thenReturn(agendamentoOutput);

        // Act
        ResponseEntity<?> response = agendamentoController.getAgendamentoCompleto(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(agendamentoOutput, response.getBody());
        verify(agendamentoService, times(1)).getAgendamentoCompleto(1L);
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando agendamento completo não for encontrado")
    void deveRetornarErro400QuandoAgendamentoCompletoNaoEncontrado() {
        // Arrange
        when(agendamentoService.getAgendamentoCompleto(999L))
                .thenThrow(new IllegalArgumentException("Agendamento não encontrado"));

        // Act
        ResponseEntity<?> response = agendamentoController.getAgendamentoCompleto(999L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().toString().contains("Erro ao buscar agendamento detalhado"));
        verify(agendamentoService, times(1)).getAgendamentoCompleto(999L);
    }

    @Test
    @DisplayName("Deve retornar agendamentos por usuário com sucesso")
    void deveRetornarAgendamentosPorUsuarioComSucesso() {
        // Arrange
        List<DetalhesAgendamentoOutput> agendamentos = List.of(agendamentoOutput);
        when(agendamentoService.getAgendamentosPorUsuario(1L)).thenReturn(agendamentos);

        // Act
        ResponseEntity<List<DetalhesAgendamentoOutput>> response =
                agendamentoController.getAgendamentosPorUsuario(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(agendamentoOutput, response.getBody().getFirst());
        verify(agendamentoService, times(1)).getAgendamentosPorUsuario(1L);
    }

    @Test
    @DisplayName("Deve retornar 204 quando usuário não tiver agendamentos")
    void deveRetornar204QuandoUsuarioNaoTiverAgendamentos() {
        // Arrange
        when(agendamentoService.getAgendamentosPorUsuario(1L)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<DetalhesAgendamentoOutput>> response =
                agendamentoController.getAgendamentosPorUsuario(1L);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(agendamentoService, times(1)).getAgendamentosPorUsuario(1L);
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException ao buscar agendamentos por usuário inexistente")
    void deveLancarExcecaoAoBuscarAgendamentosPorUsuarioInexistente() {
        // Arrange
        when(agendamentoService.getAgendamentosPorUsuario(999L))
                .thenThrow(new RuntimeException("Usuário não encontrado"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> agendamentoController.getAgendamentosPorUsuario(999L));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Usuário não encontrado", exception.getReason());
        verify(agendamentoService, times(1)).getAgendamentosPorUsuario(999L);
    }

    @Test
    @DisplayName("Deve validar código de orçamento válido")
    void deveValidarCodigoOrcamentoValido() {
        // Arrange
        when(agendamentoService.verificarCodigoOrcamento("ORC123")).thenReturn(true);

        // Act
        ResponseEntity<Boolean> response = agendamentoController.validarCodigoOrcamento("ORC123");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody());
        verify(agendamentoService, times(1)).verificarCodigoOrcamento("ORC123");
    }

    @Test
    @DisplayName("Deve validar código de orçamento inválido")
    void deveValidarCodigoOrcamentoInvalido() {
        // Arrange
        when(agendamentoService.verificarCodigoOrcamento("ORC999")).thenReturn(false);

        // Act
        ResponseEntity<Boolean> response = agendamentoController.validarCodigoOrcamento("ORC999");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody());
        verify(agendamentoService, times(1)).verificarCodigoOrcamento("ORC999");
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException ao validar código com erro")
    void deveLancarExcecaoAoValidarCodigoComErro() {
        // Arrange
        when(agendamentoService.verificarCodigoOrcamento("INVALID"))
                .thenThrow(new RuntimeException("Erro na validação"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> agendamentoController.validarCodigoOrcamento("INVALID"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Erro na validação", exception.getReason());
        verify(agendamentoService, times(1)).verificarCodigoOrcamento("INVALID");
    }

    @Test
    @DisplayName("Deve retornar datas ocupadas com sucesso")
    void deveRetornarDatasOcupadasComSucesso() {
        // Arrange
        List<String> datas = List.of("2026-02-20", "2026-02-21", "2026-02-22");
        when(agendamentoService.getDatasOcupadas()).thenReturn(datas);

        // Act
        ResponseEntity<List<String>> response = agendamentoController.getDatasOcupadas();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3, response.getBody().size());
        assertEquals("2026-02-20", response.getBody().getFirst());
        verify(agendamentoService, times(1)).getDatasOcupadas();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver datas ocupadas")
    void deveRetornarListaVaziaQuandoNaoHouverDatasOcupadas() {
        // Arrange
        when(agendamentoService.getDatasOcupadas()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<String>> response = agendamentoController.getDatasOcupadas();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(agendamentoService, times(1)).getDatasOcupadas();
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException ao buscar datas ocupadas com erro")
    void deveLancarExcecaoAoBuscarDatasOcupadasComErro() {
        // Arrange
        when(agendamentoService.getDatasOcupadas())
                .thenThrow(new RuntimeException("Erro ao buscar datas"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> agendamentoController.getDatasOcupadas());

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Erro ao buscar datas", exception.getReason());
        verify(agendamentoService, times(1)).getDatasOcupadas();
    }

    @Test
    @DisplayName("Deve atualizar orçamento do agendamento com sucesso")
    void deveAtualizarOrcamentoDoAgendamentoComSucesso() {
        // Arrange
        when(agendamentoService.atualizarOrcamento(1L, "ORC456")).thenReturn(agendamentoOutput);

        // Act
        ResponseEntity<DetalhesAgendamentoOutput> response =
                agendamentoController.atualizarOrcamentoDoAgendamento(1L, "ORC456");

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(agendamentoOutput, response.getBody());
        verify(agendamentoService, times(1)).atualizarOrcamento(1L, "ORC456");
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException ao atualizar orçamento com erro")
    void deveLancarExcecaoAoAtualizarOrcamentoComErro() {
        // Arrange
        when(agendamentoService.atualizarOrcamento(999L, "ORC999"))
                .thenThrow(new IllegalArgumentException("Agendamento ou orçamento não encontrado"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> agendamentoController.atualizarOrcamentoDoAgendamento(999L, "ORC999"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Agendamento ou orçamento não encontrado", exception.getReason());
        verify(agendamentoService, times(1)).atualizarOrcamento(999L, "ORC999");
    }

    @Test
    @DisplayName("Deve adicionar materiais usados com sucesso")
    void deveAdicionarMateriaisUsadosComSucesso() {
        // Arrange
        MaterialUsadoRequest material1 = new MaterialUsadoRequest(1L, 2);
        MaterialUsadoRequest material2 = new MaterialUsadoRequest(2L, 1);
        AdicionarMateriaisRequest request = new AdicionarMateriaisRequest(List.of(material1, material2));

        doNothing().when(agendamentoService).adicionarMateriaisUsados(1L, request);

        // Act
        ResponseEntity<String> response = agendamentoController.adicionarMateriaisUsados(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Materiais registrados com sucesso", response.getBody());
        verify(agendamentoService, times(1)).adicionarMateriaisUsados(1L, request);
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException ao adicionar materiais em agendamento inexistente")
    void deveLancarExcecaoAoAdicionarMateriaisEmAgendamentoInexistente() {
        // Arrange
        MaterialUsadoRequest material = new MaterialUsadoRequest(1L, 2);
        AdicionarMateriaisRequest request = new AdicionarMateriaisRequest(List.of(material));

        doThrow(new IllegalArgumentException("Agendamento não encontrado"))
                .when(agendamentoService).adicionarMateriaisUsados(999L, request);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> agendamentoController.adicionarMateriaisUsados(999L, request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Agendamento não encontrado", exception.getReason());
        verify(agendamentoService, times(1)).adicionarMateriaisUsados(999L, request);
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException ao adicionar materiais com estoque insuficiente")
    void deveLancarExcecaoAoAdicionarMateriaisComEstoqueInsuficiente() {
        // Arrange
        MaterialUsadoRequest material = new MaterialUsadoRequest(1L, 100);
        AdicionarMateriaisRequest request = new AdicionarMateriaisRequest(List.of(material));

        doThrow(new IllegalArgumentException("Estoque insuficiente"))
                .when(agendamentoService).adicionarMateriaisUsados(1L, request);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> agendamentoController.adicionarMateriaisUsados(1L, request));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Estoque insuficiente", exception.getReason());
        verify(agendamentoService, times(1)).adicionarMateriaisUsados(1L, request);
    }

    @Test
    @DisplayName("Deve lançar ResponseStatusException com erro interno ao adicionar materiais")
    void deveLancarExcecaoComErroInternoAoAdicionarMateriais() {
        // Arrange
        MaterialUsadoRequest material = new MaterialUsadoRequest(1L, 2);
        AdicionarMateriaisRequest request = new AdicionarMateriaisRequest(List.of(material));

        doThrow(new RuntimeException("Erro interno do sistema"))
                .when(agendamentoService).adicionarMateriaisUsados(1L, request);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
            () -> agendamentoController.adicionarMateriaisUsados(1L, request));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertNotNull(exception.getReason());
        assertTrue(exception.getReason().contains("Erro ao registrar materiais"));
        verify(agendamentoService, times(1)).adicionarMateriaisUsados(1L, request);
    }

    @Test
    @DisplayName("Deve tratar lista vazia de materiais")
    void deveTratarListaVaziaDeMateriais() {
        // Arrange
        AdicionarMateriaisRequest request = new AdicionarMateriaisRequest(Collections.emptyList());

        doNothing().when(agendamentoService).adicionarMateriaisUsados(1L, request);

        // Act
        ResponseEntity<String> response = agendamentoController.adicionarMateriaisUsados(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Materiais registrados com sucesso", response.getBody());
        verify(agendamentoService, times(1)).adicionarMateriaisUsados(1L, request);
    }
}

package hub.orcana.controller;

import hub.orcana.dto.agendamento.CadastroAgendamentoInput;
import hub.orcana.dto.agendamento.DetalhesAgendamentoOutput;
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

import java.time.LocalDateTime;
import java.util.Arrays;
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
        dataHora = LocalDateTime.now().plusDays(1);

        agendamentoOutput = new DetalhesAgendamentoOutput(
                1L,
                dataHora,
                "AGUARDANDO",
                "João Silva",
                "joao@email.com",
                "Dragão nas costas",
                20.5,
                "Preto e Vermelho",
                "Costas"
        );

        agendamentoInput = new CadastroAgendamentoInput(
                "joao@email.com",
                "ORC123",
                dataHora,
                StatusAgendamento.AGUARDANDO
        );
    }

    @Test
    @DisplayName("Deve retornar status 200 e lista de agendamentos")
    void deveRetornar200ComListaDeAgendamentos() {

        DetalhesAgendamentoOutput agendamento2 = new DetalhesAgendamentoOutput(
                2L,
                dataHora.plusDays(1),
                "CONFIRMADO",
                "Maria Santos",
                "maria@email.com",
                "Rosa no braço",
                10.0,
                "Rosa e Verde",
                "Braço"
        );
        List<DetalhesAgendamentoOutput> agendamentos = Arrays.asList(agendamentoOutput, agendamento2);
        when(agendamentoService.getAgendamentos()).thenReturn(agendamentos);


        ResponseEntity<List<DetalhesAgendamentoOutput>> response = agendamentoController.getAgendamento();


        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("João Silva", response.getBody().get(0).nomeUsuario());
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
    @DisplayName("Deve retornar status 400 quando ocorrer exceção")
    void deveRetornar400QuandoOcorrerExcecao() {

        when(agendamentoService.getAgendamentos()).thenThrow(new RuntimeException("Erro ao buscar agendamentos"));


        ResponseEntity<List<DetalhesAgendamentoOutput>> response = agendamentoController.getAgendamento();


        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
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
    @DisplayName("Deve retornar status 404 quando agendamento não for encontrado")
    void deveRetornar404QuandoAgendamentoNaoEncontrado() {

        when(agendamentoService.getAgendamentoPorId(999L))
                .thenThrow(new IllegalArgumentException("Agendamento não encontrado."));


        ResponseEntity<DetalhesAgendamentoOutput> response = agendamentoController.getAgendamentoPorId(999L);


        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(agendamentoService, times(1)).getAgendamentoPorId(999L);
    }

    @Test
    @DisplayName("Deve retornar status 400 quando ID for inválido")
    void deveRetornar400QuandoIdInvalido() {

        when(agendamentoService.getAgendamentoPorId(anyLong()))
                .thenThrow(new RuntimeException("Erro ao processar requisição"));


        ResponseEntity<DetalhesAgendamentoOutput> response = agendamentoController.getAgendamentoPorId(1L);


        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Deve retornar status 200 e agendamentos por status")
    void deveRetornar200ComAgendamentosPorStatus() {

        List<DetalhesAgendamentoOutput> agendamentos = Arrays.asList(agendamentoOutput);
        when(agendamentoService.getAgendamentosByStatus("AGUARDANDO")).thenReturn(agendamentos);


        ResponseEntity<List<DetalhesAgendamentoOutput>> response = 
                agendamentoController.getAgendamentosByStatus("AGUARDANDO");


        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("AGUARDANDO", response.getBody().get(0).status());
        verify(agendamentoService, times(1)).getAgendamentosByStatus("AGUARDANDO");
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
    @DisplayName("Deve retornar status 400 quando status for inválido")
    void deveRetornar400QuandoStatusInvalido() {

        when(agendamentoService.getAgendamentosByStatus(anyString()))
                .thenThrow(new RuntimeException("Status inválido"));


        ResponseEntity<List<DetalhesAgendamentoOutput>> response = 
                agendamentoController.getAgendamentosByStatus("INVALIDO");


        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
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
    @DisplayName("Deve retornar status 409 quando agendamento já existir")
    void deveRetornar409QuandoAgendamentoJaExistir() {

        when(agendamentoService.postAgendamento(any(CadastroAgendamentoInput.class)))
                .thenThrow(new IllegalArgumentException("Já existe um agendamento para este código de orçamento."));


        ResponseEntity<DetalhesAgendamentoOutput> response = agendamentoController.postAgendamento(agendamentoInput);


        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(response.getBody());
        verify(agendamentoService, times(1)).postAgendamento(any(CadastroAgendamentoInput.class));
    }

    @Test
    @DisplayName("Deve retornar status 400 quando dados forem inválidos")
    void deveRetornar400QuandoDadosInvalidos() {

        when(agendamentoService.postAgendamento(any(CadastroAgendamentoInput.class)))
                .thenThrow(new RuntimeException("Dados inválidos"));


        ResponseEntity<DetalhesAgendamentoOutput> response = agendamentoController.postAgendamento(agendamentoInput);


        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Deve retornar status 400 quando usuário não for encontrado")
    void deveRetornar400QuandoUsuarioNaoEncontrado() {

        when(agendamentoService.postAgendamento(any(CadastroAgendamentoInput.class)))
                .thenThrow(new IllegalArgumentException("Usuário é obrigatório."));


        ResponseEntity<DetalhesAgendamentoOutput> response = agendamentoController.postAgendamento(agendamentoInput);


        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Deve retornar status 204 ao atualizar agendamento com sucesso")
    void deveRetornar204AoAtualizarAgendamentoComSucesso() {

        when(agendamentoService.putAgendamentoById(eq(1L), any(CadastroAgendamentoInput.class)))
                .thenReturn(agendamentoOutput);


        ResponseEntity<DetalhesAgendamentoOutput> response = 
                agendamentoController.putAgendamento(1L, agendamentoInput);


        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(agendamentoService, times(1)).putAgendamentoById(eq(1L), any(CadastroAgendamentoInput.class));
    }

    @Test
    @DisplayName("Deve retornar status 404 ao atualizar agendamento inexistente")
    void deveRetornar404AoAtualizarAgendamentoInexistente() {

        when(agendamentoService.putAgendamentoById(eq(999L), any(CadastroAgendamentoInput.class)))
                .thenThrow(new IllegalArgumentException("Agendamento não encontrado."));


        ResponseEntity<DetalhesAgendamentoOutput> response = 
                agendamentoController.putAgendamento(999L, agendamentoInput);


        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(agendamentoService, times(1)).putAgendamentoById(eq(999L), any(CadastroAgendamentoInput.class));
    }

    @Test
    @DisplayName("Deve retornar status 400 ao atualizar com dados inválidos")
    void deveRetornar400AoAtualizarComDadosInvalidos() {

        when(agendamentoService.putAgendamentoById(eq(1L), any(CadastroAgendamentoInput.class)))
                .thenThrow(new RuntimeException("Erro ao processar dados"));


        ResponseEntity<DetalhesAgendamentoOutput> response = 
                agendamentoController.putAgendamento(1L, agendamentoInput);


        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());
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

        DetalhesAgendamentoOutput agendamento2 = new DetalhesAgendamentoOutput(
                2L,
                dataHora.plusDays(2),
                "CONFIRMADO",
                "João Silva",
                "joao@email.com",
                "Leão no braço",
                15.0,
                "Colorido",
                "Braço"
        );
        List<DetalhesAgendamentoOutput> agendamentos = Arrays.asList(agendamentoOutput, agendamento2);
        when(agendamentoService.getAgendamentos()).thenReturn(agendamentos);


        ResponseEntity<List<DetalhesAgendamentoOutput>> response = agendamentoController.getAgendamento();


        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("joao@email.com", response.getBody().get(0).emailUsuario());
        assertEquals("joao@email.com", response.getBody().get(1).emailUsuario());
    }

    @Test
    @DisplayName("Deve validar busca case-insensitive por status")
    void deveValidarBuscaCaseInsensitivePorStatus() {

        List<DetalhesAgendamentoOutput> agendamentos = Arrays.asList(agendamentoOutput);
        when(agendamentoService.getAgendamentosByStatus("aguardando")).thenReturn(agendamentos);


        ResponseEntity<List<DetalhesAgendamentoOutput>> response = 
                agendamentoController.getAgendamentosByStatus("aguardando");


        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(agendamentoService, times(1)).getAgendamentosByStatus("aguardando");
    }

    @Test
    @DisplayName("Deve retornar status correto ao buscar com ID negativo")
    void deveRetornarStatusCorretoAoBuscarComIdNegativo() {

        when(agendamentoService.getAgendamentoPorId(-1L))
                .thenThrow(new IllegalArgumentException("ID inválido"));


        ResponseEntity<DetalhesAgendamentoOutput> response = agendamentoController.getAgendamentoPorId(-1L);


        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve tratar agendamento com data no limite do futuro")
    void deveTratarAgendamentoComDataNoLimiteFuturo() {

        LocalDateTime dataLimite = LocalDateTime.now().plusYears(1);
        CadastroAgendamentoInput inputFuturo = new CadastroAgendamentoInput(
                "joao@email.com",
                "ORC123",
                dataLimite,
                StatusAgendamento.AGUARDANDO
        );
        DetalhesAgendamentoOutput outputFuturo = new DetalhesAgendamentoOutput(
                1L, dataLimite, "AGUARDANDO", "João Silva", "joao@email.com",
                "Dragão nas costas", 20.5, "Preto e Vermelho", "Costas"
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

        CadastroAgendamentoInput inputParcial = new CadastroAgendamentoInput(
                "joao@email.com",
                "ORC123",
                dataHora.plusHours(2),
                StatusAgendamento.CONFIRMADO
        );
        DetalhesAgendamentoOutput outputAtualizado = new DetalhesAgendamentoOutput(
                1L, dataHora.plusHours(2), "CONFIRMADO", "João Silva", "joao@email.com",
                "Dragão nas costas", 20.5, "Preto e Vermelho", "Costas"
        );
        when(agendamentoService.putAgendamentoById(eq(1L), any(CadastroAgendamentoInput.class)))
                .thenReturn(outputAtualizado);


        ResponseEntity<DetalhesAgendamentoOutput> response = 
                agendamentoController.putAgendamento(1L, inputParcial);


        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}

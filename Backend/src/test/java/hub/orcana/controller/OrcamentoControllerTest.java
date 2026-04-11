package hub.orcana.controller;

import hub.orcana.dto.orcamento.CadastroOrcamentoInput;
import hub.orcana.dto.orcamento.DetalhesOrcamentoOutput;
import hub.orcana.service.OrcamentoService;
import hub.orcana.tables.Orcamento;
import hub.orcana.tables.StatusOrcamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrcamentoControllerTest {

    @Mock
    private OrcamentoService servico;

    @InjectMocks
    private OrcamentoController controlador;

    private CadastroOrcamentoInput entradaValida;
    private Orcamento orcamentoSimulado;
    private MockMultipartFile arquivoSimulado;

    @BeforeEach
    void configurar() {
        arquivoSimulado = new MockMultipartFile(
                "arquivo",
                "tatuagem.jpg",
                "image/jpeg",
                "conteudo".getBytes()
        );

        entradaValida = new CadastroOrcamentoInput(
                "João Silva",
                "joao@email.com",
                "Tatuagem de dragão",
                10.0,
                "Preto, Vermelho",
                "Braço direito",
                List.of(arquivoSimulado),
                2L
        );

        orcamentoSimulado = new Orcamento(
                "ORC-A1B2C3D4",
                "João Silva",
                "joao@email.com",
                "Tatuagem de dragão",
                10.0,
                "Preto, Vermelho",
                "Braço direito",
                List.of("url1.jpg"),
                1L,
                StatusOrcamento.PENDENTE
        );
    }

    @Test
    @DisplayName("Deve criar orçamento com sucesso e retornar status 201")
    void deveCriarOrcamentoComSucesso() {
        when(servico.postOrcamento(any(CadastroOrcamentoInput.class)))
                .thenReturn(orcamentoSimulado);

        ResponseEntity<?> resposta = controlador.postOrcamento(entradaValida);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        assertNotNull(resposta.getBody());

        Map<String, Object> corpo = (Map<String, Object>) resposta.getBody();
        assertTrue((Boolean) corpo.get("success"));
        assertEquals("ORC-A1B2C3D4", corpo.get("codigo"));
        assertEquals("Orçamento criado com sucesso", corpo.get("message"));

        verify(servico, times(1)).postOrcamento(any(CadastroOrcamentoInput.class));
    }

    @Test
    @DisplayName("Deve retornar código único gerado para o orçamento")
    void deveRetornarCodigoUnicoGerado() {
        when(servico.postOrcamento(any(CadastroOrcamentoInput.class)))
                .thenReturn(orcamentoSimulado);

        ResponseEntity<?> resposta = controlador.postOrcamento(entradaValida);

        @SuppressWarnings("unchecked")
        Map<String, Object> corpo = (Map<String, Object>) resposta.getBody();
        assertNotNull(corpo);
        String codigo = (String) corpo.get("codigo");

        assertNotNull(codigo);
        assertTrue(codigo.startsWith("ORC-"));
        assertEquals(12
                , codigo.length());
    }

    @Test
    @DisplayName("Deve lançar exceção quando service falhar")
    void deveLancarExcecaoQuandoServicoFalhar() {
        when(servico.postOrcamento(any(CadastroOrcamentoInput.class)))
                .thenThrow(new RuntimeException("Erro ao processar orçamento"));

        assertThrows(RuntimeException.class, () -> controlador.postOrcamento(entradaValida));

        verify(servico, times(1)).postOrcamento(any(CadastroOrcamentoInput.class));
    }

    @Test
    @DisplayName("Deve retornar lista de orçamentos com status 200")
    void deveRetornarListaDeOrcamentos() {
        List<DetalhesOrcamentoOutput> orcamentos = new ArrayList<>();
        orcamentos.add(new DetalhesOrcamentoOutput(
                "ORC-A1B2C3D4",
                "João Silva",
                "joao@email.com",
                "Tatuagem de dragão",
                10.0,
                "Preto e vermelho",
                "Braço direito",
                List.of("url1.jpg"),
                null,
                null,
                StatusOrcamento.PENDENTE
        ));
        orcamentos.add(new DetalhesOrcamentoOutput(
                "ORC-E5F6G7H8",
                "Maria Santos",
                "maria@email.com",
                "Tatuagem floral",
                8.5,
                "Colorido",
                "Tornozelo",
                List.of("url2.jpg"),
                null,
                null,
                StatusOrcamento.PENDENTE
        ));

        when(servico.findAllOrcamentos()).thenReturn(orcamentos);

        ResponseEntity<List<DetalhesOrcamentoOutput>> resposta = controlador.getOrcamentos();

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());
        assertEquals(2, resposta.getBody().size());
        assertEquals("ORC-A1B2C3D4", resposta.getBody().get(0).codigoOrcamento());
        assertEquals("ORC-E5F6G7H8", resposta.getBody().get(1).codigoOrcamento());

        verify(servico, times(1)).findAllOrcamentos();
    }

    @Test
    @DisplayName("Deve retornar status 204 quando não houver orçamentos")
    void deveRetornarSemConteudoQuandoListaVazia() {
        when(servico.findAllOrcamentos()).thenReturn(new ArrayList<>());

        ResponseEntity<List<DetalhesOrcamentoOutput>> resposta = controlador.getOrcamentos();

        assertEquals(HttpStatus.NO_CONTENT, resposta.getStatusCode());
        assertNull(resposta.getBody());

        verify(servico, times(1)).findAllOrcamentos();
    }

    @Test
    @DisplayName("Deve lançar exceção quando falhar ao buscar orçamentos")
    void deveLancarExcecaoAoBuscarOrcamentos() {
        when(servico.findAllOrcamentos())
                .thenThrow(new RuntimeException("Erro ao buscar orçamentos"));

        assertThrows(RuntimeException.class, () -> controlador.getOrcamentos());

        verify(servico, times(1)).findAllOrcamentos();
    }

    @Test
    @DisplayName("Deve processar múltiplos arquivos de imagem")
    void deveProcessarMultiplosArquivos() {
        MockMultipartFile arquivo2 = new MockMultipartFile(
                "arquivo2",
                "tatuagem2.jpg",
                "image/jpeg",
                "conteudo2".getBytes()
        );

        CadastroOrcamentoInput entradaComMultiplosArquivos = new CadastroOrcamentoInput(
                "João Silva",
                "joao@email.com",
                "Tatuagem de dragão",
                12.0,
                "Preto e vermelho",
                "Braço direito",
                List.of(arquivoSimulado, arquivo2),
                1L
        );

        when(servico.postOrcamento(any(CadastroOrcamentoInput.class)))
                .thenReturn(orcamentoSimulado);

        ResponseEntity<?> resposta = controlador.postOrcamento(entradaComMultiplosArquivos);

        assertEquals(HttpStatus.CREATED, resposta.getStatusCode());
        verify(servico, times(1)).postOrcamento(argThat(entrada ->
                entrada.imagemReferencia().size() == 2
        ));
    }

    @Test
    @DisplayName("Deve retornar orçamentos por usuário com sucesso")
    void deveRetornarOrcamentosPorUsuarioComSucesso() {
        // Arrange
        Long usuarioId = 1L;
        DetalhesOrcamentoOutput orcamento1 = new DetalhesOrcamentoOutput(
                "ORC-123", "João Silva", "joao@email.com", "Dragão",
                10.0, "Preto", "Braço", List.of(), 500.0, Time.valueOf("02:00:00"), StatusOrcamento.PENDENTE
        );
        DetalhesOrcamentoOutput orcamento2 = new DetalhesOrcamentoOutput(
                "ORC-124", "João Silva", "joao@email.com", "Rosa",
                5.0, "Rosa", "Perna", List.of(), 300.0, Time.valueOf("01:30:00"), StatusOrcamento.APROVADO
        );
        List<DetalhesOrcamentoOutput> orcamentos = List.of(orcamento1, orcamento2);

        when(servico.findOrcamentosByUsuarioId(usuarioId)).thenReturn(orcamentos);

        // Act
        ResponseEntity<List<DetalhesOrcamentoOutput>> response = controlador.getOrcamentosPorUsuario(usuarioId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("ORC-123", response.getBody().get(0).codigoOrcamento());
        assertEquals("ORC-124", response.getBody().get(1).codigoOrcamento());
        verify(servico, times(1)).findOrcamentosByUsuarioId(usuarioId);
    }

    @Test
    @DisplayName("Deve retornar 204 quando usuário não tiver orçamentos")
    void deveRetornar204QuandoUsuarioNaoTiverOrcamentos() {
        // Arrange
        Long usuarioId = 1L;
        when(servico.findOrcamentosByUsuarioId(usuarioId)).thenReturn(List.of());

        // Act
        ResponseEntity<List<DetalhesOrcamentoOutput>> response = controlador.getOrcamentosPorUsuario(usuarioId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(servico, times(1)).findOrcamentosByUsuarioId(usuarioId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar orçamentos por usuário inexistente")
    void deveLancarExcecaoAoBuscarOrcamentosPorUsuarioInexistente() {
        // Arrange
        Long usuarioId = 999L;
        when(servico.findOrcamentosByUsuarioId(usuarioId))
                .thenThrow(new RuntimeException("Usuário não encontrado"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> controlador.getOrcamentosPorUsuario(usuarioId));
        verify(servico, times(1)).findOrcamentosByUsuarioId(usuarioId);
    }

    @Test
    @DisplayName("Deve retornar orçamento por código com sucesso")
    void deveRetornarOrcamentoPorCodigoComSucesso() {
        // Arrange
        String codigo = "ORC-123";
        DetalhesOrcamentoOutput orcamento = new DetalhesOrcamentoOutput(
                codigo, "João Silva", "joao@email.com", "Dragão",
                10.0, "Preto", "Braço", List.of(), 500.0, Time.valueOf("02:00:00"), StatusOrcamento.PENDENTE
        );

        when(servico.findByCodigo(codigo)).thenReturn(orcamento);

        // Act
        ResponseEntity<DetalhesOrcamentoOutput> response = controlador.getOrcamentoPorCodigo(codigo);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(codigo, response.getBody().codigoOrcamento());
        assertEquals("João Silva", response.getBody().nome());
        verify(servico, times(1)).findByCodigo(codigo);
    }

    @Test
    @DisplayName("Deve retornar 404 quando orçamento não for encontrado por código")
    void deveRetornar404QuandoOrcamentoNaoEncontradoPorCodigo() {
        // Arrange
        String codigo = "ORC-999";
        when(servico.findByCodigo(codigo)).thenThrow(new RuntimeException("Orçamento não encontrado"));

        // Act
        ResponseEntity<DetalhesOrcamentoOutput> response = controlador.getOrcamentoPorCodigo(codigo);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(servico, times(1)).findByCodigo(codigo);
    }

    @Test
    @DisplayName("Deve atualizar orçamento com sucesso")
    void deveAtualizarOrcamentoComSucesso() {
        // Arrange
        String codigo = "ORC-123";
        Map<String, Object> dados = Map.of(
                "valor", 600.0,
                "tempo", "03:00:00",
                "status", "APROVADO"
        );
        DetalhesOrcamentoOutput orcamentoAtualizado = new DetalhesOrcamentoOutput(
                codigo, "João Silva", "joao@email.com", "Dragão",
                10.0, "Preto", "Braço", List.of(), 600.0, Time.valueOf("03:00:00"), StatusOrcamento.APROVADO
        );

        when(servico.atualizarOrcamento(codigo, dados)).thenReturn(orcamentoAtualizado);

        // Act
        ResponseEntity<?> response = controlador.atualizarOrcamento(codigo, dados);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Orçamento atualizado com sucesso", responseBody.get("message"));
        assertEquals(codigo, responseBody.get("codigo"));
        verify(servico, times(1)).atualizarOrcamento(codigo, dados);
    }

    @Test
    @DisplayName("Deve retornar 404 quando orçamento não encontrado para atualização")
    void deveRetornar404QuandoOrcamentoNaoEncontradoParaAtualizacao() {
        // Arrange
        String codigo = "ORC-999";
        Map<String, Object> dados = Map.of("valor", 600.0);
        when(servico.atualizarOrcamento(codigo, dados))
                .thenThrow(new RuntimeException("Orçamento não encontrado"));

        // Act
        ResponseEntity<?> response = controlador.atualizarOrcamento(codigo, dados);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals("Orçamento não encontrado", responseBody.get("message"));
        verify(servico, times(1)).atualizarOrcamento(codigo, dados);
    }

    @Test
    @DisplayName("Deve verificar se orçamento tem agendamento - true")
    void deveVerificarSeOrcamentoTemAgendamentoTrue() {
        // Arrange
        String codigo = "ORC-123";
        when(servico.verificarSeTemAgendamento(codigo)).thenReturn(true);

        // Act
        ResponseEntity<?> response = controlador.verificarSeTemAgendamento(codigo);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("temAgendamento"));
        verify(servico, times(1)).verificarSeTemAgendamento(codigo);
    }

    @Test
    @DisplayName("Deve verificar se orçamento tem agendamento - false")
    void deveVerificarSeOrcamentoTemAgendamentoFalse() {
        // Arrange
        String codigo = "ORC-123";
        when(servico.verificarSeTemAgendamento(codigo)).thenReturn(false);

        // Act
        ResponseEntity<?> response = controlador.verificarSeTemAgendamento(codigo);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("temAgendamento"));
        verify(servico, times(1)).verificarSeTemAgendamento(codigo);
    }

    @Test
    @DisplayName("Deve retornar 500 quando ocorrer erro ao verificar agendamento")
    void deveRetornar500QuandoOcorrerErroAoVerificarAgendamento() {
        // Arrange
        String codigo = "ORC-123";
        when(servico.verificarSeTemAgendamento(codigo))
                .thenThrow(new RuntimeException("Erro de conexão"));

        // Act
        ResponseEntity<?> response = controlador.verificarSeTemAgendamento(codigo);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals("Erro ao verificar agendamento", responseBody.get("message"));
        verify(servico, times(1)).verificarSeTemAgendamento(codigo);
    }

    @Test
    @DisplayName("Deve deletar orçamento com sucesso")
    void deveDeletarOrcamentoComSucesso() {
        // Arrange
        String codigo = "ORC-123";
        doNothing().when(servico).deletarOrcamento(codigo);

        // Act
        ResponseEntity<?> response = controlador.deletarOrcamento(codigo);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));
        assertEquals("Orçamento excluído com sucesso", responseBody.get("message"));
        verify(servico, times(1)).deletarOrcamento(codigo);
    }

    @Test
    @DisplayName("Deve retornar 404 quando orçamento não encontrado para exclusão")
    void deveRetornar404QuandoOrcamentoNaoEncontradoParaExclusao() {
        // Arrange
        String codigo = "ORC-999";
        doThrow(new RuntimeException("Orçamento não encontrado"))
                .when(servico).deletarOrcamento(codigo);

        // Act
        ResponseEntity<?> response = controlador.deletarOrcamento(codigo);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertFalse((Boolean) responseBody.get("success"));
        assertEquals("Orçamento não encontrado", responseBody.get("message"));
        verify(servico, times(1)).deletarOrcamento(codigo);
    }

    @Test
    @DisplayName("Deve tratar dados de atualização com múltiplos campos")
    void deveTratarDadosDeAtualizacaoComMultiplosCampos() {
        // Arrange
        String codigo = "ORC-123";
        Map<String, Object> dados = Map.of(
                "valor", 800.0,
                "tempo", "04:00:00",
                "status", "APROVADO",
                "cores", "Azul e Verde"
        );
        DetalhesOrcamentoOutput orcamentoAtualizado = new DetalhesOrcamentoOutput(
                codigo, "João Silva", "joao@email.com", "Dragão",
                10.0, "Azul e Verde", "Braço", List.of(), 800.0, Time.valueOf("04:00:00"), StatusOrcamento.APROVADO
        );

        when(servico.atualizarOrcamento(codigo, dados)).thenReturn(orcamentoAtualizado);

        // Act
        ResponseEntity<?> response = controlador.atualizarOrcamento(codigo, dados);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody);
        assertTrue((Boolean) responseBody.get("success"));

        @SuppressWarnings("unchecked")
        DetalhesOrcamentoOutput orcamentoResponse = (DetalhesOrcamentoOutput) responseBody.get("orcamento");
        assertNotNull(orcamentoResponse);
        assertEquals(800.0, orcamentoResponse.valor());
        assertEquals("Azul e Verde", orcamentoResponse.cores());
        verify(servico, times(1)).atualizarOrcamento(codigo, dados);
    }

    @Test
    @DisplayName("Deve validar diferentes códigos de orçamento")
    void deveValidarDiferentesCodigosDeOrcamento() {
        // Arrange
        String[] codigos = {"ORC-001", "ORC-999", "ORC-ABC123"};

        for (String codigo : codigos) {
            DetalhesOrcamentoOutput orcamento = new DetalhesOrcamentoOutput(
                    codigo, "Cliente", "email@test.com", "Tatuagem",
                    5.0, "Preto", "Local", List.of(), 100.0, Time.valueOf("01:00:00"), StatusOrcamento.PENDENTE
            );
            when(servico.findByCodigo(codigo)).thenReturn(orcamento);

            // Act
            ResponseEntity<DetalhesOrcamentoOutput> response = controlador.getOrcamentoPorCodigo(codigo);

            // Assert
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(codigo, response.getBody().codigoOrcamento());
        }

        verify(servico, times(codigos.length)).findByCodigo(anyString());
    }
}

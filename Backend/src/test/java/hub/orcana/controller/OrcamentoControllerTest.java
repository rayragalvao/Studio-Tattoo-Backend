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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para OrcamentoController")
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

        Map<String, Object> corpo = (Map<String, Object>) resposta.getBody();
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

        assertThrows(RuntimeException.class, () -> {
            controlador.postOrcamento(entradaValida);
        });

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
                List.of("url1.jpg")
        ));
        orcamentos.add(new DetalhesOrcamentoOutput(
                "ORC-E5F6G7H8",
                "Maria Santos",
                "maria@email.com",
                "Tatuagem floral",
                8.5,
                "Colorido",
                "Tornozelo",
                List.of("url2.jpg")
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

        assertThrows(RuntimeException.class, () -> {
            controlador.getOrcamentos();
        });

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
}

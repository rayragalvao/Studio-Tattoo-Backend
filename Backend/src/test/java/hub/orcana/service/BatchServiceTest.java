package hub.orcana.service;

import hub.orcana.tables.Estoque;
import hub.orcana.tables.repository.EstoqueRepository;
import hub.orcana.tables.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BatchServiceTest {

    @Mock
    private EstoqueRepository estoqueRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private BatchService batchService;

    private List<Estoque> estoquesBaixos;
    private List<Estoque> estoquesVazios;

    @BeforeEach
    void setUp() {
        // Criar estoques com quantidade baixa
        estoquesBaixos = new ArrayList<>();
        Estoque estoque1 = new Estoque("Tinta Preta", 2.0, "ml", 10.0);
        estoque1.setId(1L);
        Estoque estoque2 = new Estoque("Agulha 3RL", 1.0, "unidade", 5.0);
        estoque2.setId(2L);
        Estoque estoque3 = new Estoque("Luva Descartável", 3.0, "par", 20.0);
        estoque3.setId(3L);

        estoquesBaixos.add(estoque1);
        estoquesBaixos.add(estoque2);
        estoquesBaixos.add(estoque3);

        // Lista vazia para quando não há estoques baixos
        estoquesVazios = new ArrayList<>();
    }

    @Test
    @DisplayName("Deve processar corretamente quando há materiais com estoque baixo")
    void deveProcessarCorretamenteQuandoHaMateriaisComEstoqueBaixo() {
        // Arrange
        when(estoqueRepository.findAllByQuantidadeLessThanMinAviso()).thenReturn(estoquesBaixos);

        // Act
        assertDoesNotThrow(() -> batchService.verificarEstoque());

        // Assert
        verify(estoqueRepository, times(1)).findAllByQuantidadeLessThanMinAviso();
        verify(emailService, times(1)).enviarEmailParaTodosAdminsEstoqueBaixo(
                eq("estoque_baixo_observer"),
                any(String.class)
        );
        verify(emailService, never()).enviarEmailParaTodosAdminsEstoqueOk(any(String.class));
    }

    @Test
    @DisplayName("Deve processar corretamente quando não há materiais com estoque baixo")
    void deveProcessarCorretamenteQuandoNaoHaMateriaisComEstoqueBaixo() {
        // Arrange
        when(estoqueRepository.findAllByQuantidadeLessThanMinAviso()).thenReturn(estoquesVazios);

        // Act
        assertDoesNotThrow(() -> batchService.verificarEstoque());

        // Assert
        verify(estoqueRepository, times(1)).findAllByQuantidadeLessThanMinAviso();
        verify(emailService, times(1)).enviarEmailParaTodosAdminsEstoqueOk("estoque_ok");
        verify(emailService, never()).enviarEmailParaTodosAdminsEstoqueBaixo(any(String.class), any(String.class));
    }

    @Test
    @DisplayName("Deve gerar texto correto para materiais com estoque baixo")
    void deveGerarTextoCorretoParaMateriaisComEstoqueBaixo() {
        // Arrange
        when(estoqueRepository.findAllByQuantidadeLessThanMinAviso()).thenReturn(estoquesBaixos);

        // Act
        assertDoesNotThrow(() -> batchService.verificarEstoque());

        // Assert - Verificar se o texto contém informações dos materiais
        verify(emailService).enviarEmailParaTodosAdminsEstoqueBaixo(
                eq("estoque_baixo_observer"),
                argThat(texto ->
                    texto.contains("Tinta Preta: 2.0 ml (Mínimo: 10.0)") &&
                    texto.contains("Agulha 3RL: 1.0 unidade (Mínimo: 5.0)") &&
                    texto.contains("Luva Descartável: 3.0 par (Mínimo: 20.0)")
                )
        );
    }

    @Test
    @DisplayName("Deve continuar execução mesmo se houver erro no envio de email para estoque baixo")
    void deveContinuarExecucaoMesmoSeHouverErroNoEnvioDeEmailParaEstoqueBaixo() {
        // Arrange
        when(estoqueRepository.findAllByQuantidadeLessThanMinAviso()).thenReturn(estoquesBaixos);
        doThrow(new RuntimeException("Erro no envio de email"))
                .when(emailService).enviarEmailParaTodosAdminsEstoqueBaixo(any(String.class), any(String.class));

        // Act & Assert - Não deve lançar exceção
        assertDoesNotThrow(() -> batchService.verificarEstoque());

        verify(estoqueRepository, times(1)).findAllByQuantidadeLessThanMinAviso();
        verify(emailService, times(1)).enviarEmailParaTodosAdminsEstoqueBaixo(any(String.class), any(String.class));
    }

    @Test
    @DisplayName("Deve continuar execução mesmo se houver erro no envio de email para estoque ok")
    void deveContinuarExecucaoMesmoSeHouverErroNoEnvioDeEmailParaEstoqueOk() {
        // Arrange
        when(estoqueRepository.findAllByQuantidadeLessThanMinAviso()).thenReturn(estoquesVazios);
        doThrow(new RuntimeException("Erro no envio de email"))
                .when(emailService).enviarEmailParaTodosAdminsEstoqueOk(any(String.class));

        // Act & Assert - Não deve lançar exceção
        assertDoesNotThrow(() -> batchService.verificarEstoque());

        verify(estoqueRepository, times(1)).findAllByQuantidadeLessThanMinAviso();
        verify(emailService, times(1)).enviarEmailParaTodosAdminsEstoqueOk("estoque_ok");
    }

    @Test
    @DisplayName("Deve continuar execução mesmo se houver erro na consulta do banco")
    void deveContinuarExecucaoMesmoSeHouverErroNaConsultaDoBanco() {
        // Arrange
        when(estoqueRepository.findAllByQuantidadeLessThanMinAviso())
                .thenThrow(new RuntimeException("Erro de conexão com banco"));

        // Act & Assert - Não deve lançar exceção
        assertDoesNotThrow(() -> batchService.verificarEstoque());

        verify(estoqueRepository, times(1)).findAllByQuantidadeLessThanMinAviso();
        verify(emailService, never()).enviarEmailParaTodosAdminsEstoqueBaixo(any(String.class), any(String.class));
        verify(emailService, never()).enviarEmailParaTodosAdminsEstoqueOk(any(String.class));
    }

    @Test
    @DisplayName("Deve processar um único material com estoque baixo")
    void deveProcessarUmUnicoMaterialComEstoqueBaixo() {
        // Arrange
        List<Estoque> umMaterial = List.of(estoquesBaixos.getFirst());
        when(estoqueRepository.findAllByQuantidadeLessThanMinAviso()).thenReturn(umMaterial);

        // Act
        assertDoesNotThrow(() -> batchService.verificarEstoque());

        // Assert
        verify(emailService).enviarEmailParaTodosAdminsEstoqueBaixo(
                eq("estoque_baixo_observer"),
                argThat(texto ->
                    texto.contains("Tinta Preta: 2.0 ml (Mínimo: 10.0)") &&
                    !texto.contains("Agulha 3RL") &&
                    !texto.contains("Luva Descartável")
                )
        );
    }

    @Test
    @DisplayName("Deve processar materiais com valores decimais corretamente")
    void deveProcessarMateriaisComValoresDecimaisCorretamente() {
        // Arrange
        List<Estoque> materiaisDecimais = new ArrayList<>();
        Estoque estoqueDecimal = new Estoque("Tinta Vermelha", 2.5, "ml", 10.7);
        estoqueDecimal.setId(1L);
        materiaisDecimais.add(estoqueDecimal);

        when(estoqueRepository.findAllByQuantidadeLessThanMinAviso()).thenReturn(materiaisDecimais);

        // Act
        assertDoesNotThrow(() -> batchService.verificarEstoque());

        // Assert
        verify(emailService).enviarEmailParaTodosAdminsEstoqueBaixo(
                eq("estoque_baixo_observer"),
                argThat(texto -> texto.contains("Tinta Vermelha: 2.5 ml (Mínimo: 10.7)"))
        );
    }

    @Test
    @DisplayName("Deve processar materiais com minAviso null")
    void deveProcessarMateriaisComMinAvisoNull() {
        // Arrange
        List<Estoque> materiaisComNull = new ArrayList<>();
        Estoque estoqueSemMinimo = new Estoque("Material Teste", 5.0, "unidade", null);
        estoqueSemMinimo.setId(1L);
        materiaisComNull.add(estoqueSemMinimo);

        when(estoqueRepository.findAllByQuantidadeLessThanMinAviso()).thenReturn(materiaisComNull);

        // Act
        assertDoesNotThrow(() -> batchService.verificarEstoque());

        // Assert
        verify(emailService).enviarEmailParaTodosAdminsEstoqueBaixo(
                eq("estoque_baixo_observer"),
                argThat(texto -> texto.contains("Material Teste: 5.0 unidade (Mínimo: null)"))
        );
    }
}



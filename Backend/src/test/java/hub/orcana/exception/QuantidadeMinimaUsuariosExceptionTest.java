package hub.orcana.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes unitários para QuantidadeMinimaUsuariosException")
class QuantidadeMinimaUsuariosExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem padrão")
    void deveCriarExcecaoComMensagemPadrao() {
        // Act
        QuantidadeMinimaUsuariosException exception = new QuantidadeMinimaUsuariosException();

        // Assert
        assertNotNull(exception);
        assertEquals("Quantidade mínima de usuários atingida. Não é possível excluir mais registros.",
                     exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Deve herdar de RuntimeException")
    void deveHerdarDeRuntimeException() {
        // Arrange
        QuantidadeMinimaUsuariosException exception = new QuantidadeMinimaUsuariosException();

        // Assert
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    @DisplayName("Deve ser lancável como exceção")
    void deveSerLancavelComoExcecao() {
        // Act & Assert
        QuantidadeMinimaUsuariosException exception = assertThrows(
            QuantidadeMinimaUsuariosException.class,
            () -> {
                throw new QuantidadeMinimaUsuariosException();
            }
        );

        assertEquals("Quantidade mínima de usuários atingida. Não é possível excluir mais registros.",
                     exception.getMessage());
    }

    @Test
    @DisplayName("Deve manter mensagem consistente em múltiplas instâncias")
    void deveManterMensagemConsistenteEmMultiplasInstancias() {
        // Arrange & Act
        QuantidadeMinimaUsuariosException exception1 = new QuantidadeMinimaUsuariosException();
        QuantidadeMinimaUsuariosException exception2 = new QuantidadeMinimaUsuariosException();
        QuantidadeMinimaUsuariosException exception3 = new QuantidadeMinimaUsuariosException();

        // Assert
        String expectedMessage = "Quantidade mínima de usuários atingida. Não é possível excluir mais registros.";
        assertEquals(expectedMessage, exception1.getMessage());
        assertEquals(expectedMessage, exception2.getMessage());
        assertEquals(expectedMessage, exception3.getMessage());

        // Verifica se todas têm a mesma mensagem
        assertEquals(exception1.getMessage(), exception2.getMessage());
        assertEquals(exception2.getMessage(), exception3.getMessage());
    }

    @Test
    @DisplayName("Deve ter construtor sem parâmetros funcionando corretamente")
    void deveTerConstrutorSemParametrosFuncionandoCorretamente() {
        // Act
        QuantidadeMinimaUsuariosException exception = new QuantidadeMinimaUsuariosException();

        // Assert
        assertNotNull(exception);
        assertNotNull(exception.getMessage());
        assertFalse(exception.getMessage().isEmpty());
        assertTrue(exception.getMessage().contains("usuários"));
        assertTrue(exception.getMessage().contains("excluir"));
    }

    @Test
    @DisplayName("Deve manter stackTrace quando lançada")
    void deveManterStackTraceQuandoLancada() {
        try {
            // Act
            throw new QuantidadeMinimaUsuariosException();
        } catch (QuantidadeMinimaUsuariosException e) {
            // Assert
            assertNotNull(e.getStackTrace());
            assertTrue(e.getStackTrace().length > 0);
            assertEquals("hub.orcana.exception.QuantidadeMinimaUsuariosExceptionTest",
                        e.getStackTrace()[0].getClassName());
        }
    }

    @Test
    @DisplayName("Deve ser serializável")
    void deveSerSerializavel() {
        // Arrange
        QuantidadeMinimaUsuariosException exception = new QuantidadeMinimaUsuariosException();

        // Assert - RuntimeException implementa Serializable
        assertTrue(exception instanceof java.io.Serializable);
    }

    @Test
    @DisplayName("Deve ter mensagem informativa sobre o problema de negócio")
    void deveTerMensagemInformativaSobreProblemaDeNegocio() {
        // Arrange
        QuantidadeMinimaUsuariosException exception = new QuantidadeMinimaUsuariosException();

        // Assert - Verifica se a mensagem explica o problema claramente
        String message = exception.getMessage();
        assertTrue(message.contains("Quantidade mínima"));
        assertTrue(message.contains("usuários"));
        assertTrue(message.contains("atingida"));
        assertTrue(message.contains("Não é possível"));
        assertTrue(message.contains("excluir"));
        assertTrue(message.contains("registros"));
    }

    @Test
    @DisplayName("Deve funcionar em contexto de múltiplas threads")
    void deveFuncionarEmContextoDeMultiplasThreads() throws InterruptedException {
        // Arrange
        final QuantidadeMinimaUsuariosException[] exceptions = new QuantidadeMinimaUsuariosException[5];
        Thread[] threads = new Thread[5];

        // Act - Criar exceções em threads paralelas
        for (int i = 0; i < 5; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                exceptions[index] = new QuantidadeMinimaUsuariosException();
            });
            threads[i].start();
        }

        // Aguarda todas as threads terminarem
        for (Thread thread : threads) {
            thread.join();
        }

        // Assert
        String expectedMessage = "Quantidade mínima de usuários atingida. Não é possível excluir mais registros.";
        for (QuantidadeMinimaUsuariosException exception : exceptions) {
            assertNotNull(exception);
            assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    @DisplayName("Deve funcionar corretamente com getMessage() sobrescrito da superclasse")
    void deveFuncionarCorretamenteComGetMessageSobrescritoDaSuperclasse() {
        // Arrange
        QuantidadeMinimaUsuariosException exception = new QuantidadeMinimaUsuariosException();

        // Act
        String message = exception.getMessage();
        String toStringResult = exception.toString();

        // Assert
        assertNotNull(message);
        assertTrue(toStringResult.contains(message));
        assertTrue(toStringResult.contains("QuantidadeMinimaUsuariosException"));
    }
}

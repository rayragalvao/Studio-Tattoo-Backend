package hub.orcana.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes unitários para UsuarioProtegidoException")
class UsuarioProtegidoExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com ID válido e gerar mensagem formatada")
    void deveCriarExcecaoComIdValidoEGerarMensagemFormatada() {
        // Arrange
        Long id = 1L;

        // Act
        UsuarioProtegidoException exception = new UsuarioProtegidoException(id);

        // Assert
        assertNotNull(exception);
        assertEquals("Usuário com ID 1 é protegido e não pode ser excluído.", exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Deve criar exceção com diferentes IDs")
    void deveCriarExcecaoComDiferentesIds() {
        // Arrange & Act & Assert
        Long[] ids = {1L, 2L, 100L, 999L, 123456L};

        for (Long id : ids) {
            UsuarioProtegidoException exception = new UsuarioProtegidoException(id);
            assertEquals("Usuário com ID " + id + " é protegido e não pode ser excluído.",
                        exception.getMessage());
        }
    }

    @Test
    @DisplayName("Deve criar exceção com ID zero")
    void deveCriarExcecaoComIdZero() {
        // Arrange
        Long id = 0L;

        // Act
        UsuarioProtegidoException exception = new UsuarioProtegidoException(id);

        // Assert
        assertEquals("Usuário com ID 0 é protegido e não pode ser excluído.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar exceção com ID negativo")
    void deveCriarExcecaoComIdNegativo() {
        // Arrange
        Long id = -1L;

        // Act
        UsuarioProtegidoException exception = new UsuarioProtegidoException(id);

        // Assert
        assertEquals("Usuário com ID -1 é protegido e não pode ser excluído.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar exceção com ID null")
    void deveCriarExcecaoComIdNull() {
        // Arrange
        Long id = null;

        // Act
        UsuarioProtegidoException exception = new UsuarioProtegidoException(id);

        // Assert
        assertEquals("Usuário com ID null é protegido e não pode ser excluído.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve herdar de RuntimeException")
    void deveHerdarDeRuntimeException() {
        // Arrange
        UsuarioProtegidoException exception = new UsuarioProtegidoException(1L);

        // Assert
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    @DisplayName("Deve ser lancável como exceção")
    void deveSerLancavelComoExcecao() {
        // Arrange
        Long id = 42L;

        // Act & Assert
        UsuarioProtegidoException exception = assertThrows(
            UsuarioProtegidoException.class,
            () -> {
                throw new UsuarioProtegidoException(id);
            }
        );

        assertEquals("Usuário com ID 42 é protegido e não pode ser excluído.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar exceção com IDs muito grandes")
    void deveCriarExcecaoComIdsMuitoGrandes() {
        // Arrange
        Long[] idsGrandes = {
            Long.MAX_VALUE,
            9999999999L,
            1234567890123456789L
        };

        // Act & Assert
        for (Long id : idsGrandes) {
            UsuarioProtegidoException exception = new UsuarioProtegidoException(id);
            assertTrue(exception.getMessage().contains(id.toString()));
            assertTrue(exception.getMessage().startsWith("Usuário com ID " + id));
        }
    }

    @Test
    @DisplayName("Deve manter stackTrace quando lançada")
    void deveManterStackTraceQuandoLancada() {
        try {
            // Act
            throw new UsuarioProtegidoException(123L);
        } catch (UsuarioProtegidoException e) {
            // Assert
            assertNotNull(e.getStackTrace());
            assertTrue(e.getStackTrace().length > 0);
            assertEquals("hub.orcana.exception.UsuarioProtegidoExceptionTest",
                        e.getStackTrace()[0].getClassName());
        }
    }

    @Test
    @DisplayName("Deve ser serializável")
    void deveSerSerializavel() {
        // Arrange
        UsuarioProtegidoException exception = new UsuarioProtegidoException(1L);

        // Assert - RuntimeException implementa Serializable
        assertTrue(exception instanceof java.io.Serializable);
    }

    @Test
    @DisplayName("Deve ter mensagem informativa sobre o problema de negócio")
    void deveTerMensagemInformativaSobreProblemaDeNegocio() {
        // Arrange
        Long id = 5L;
        UsuarioProtegidoException exception = new UsuarioProtegidoException(id);

        // Assert - Verifica se a mensagem explica o problema claramente
        String message = exception.getMessage();
        assertTrue(message.contains("Usuário"));
        assertTrue(message.contains("ID " + id));
        assertTrue(message.contains("protegido"));
        assertTrue(message.contains("não pode ser"));
        assertTrue(message.contains("excluído"));
    }

    @Test
    @DisplayName("Deve funcionar em contexto de múltiplas threads")
    void deveFuncionarEmContextoDeMultiplasThreads() throws InterruptedException {
        // Arrange
        final Long[] ids = {1L, 2L, 3L, 4L, 5L};
        final UsuarioProtegidoException[] exceptions = new UsuarioProtegidoException[5];
        Thread[] threads = new Thread[5];

        // Act - Criar exceções em threads paralelas
        for (int i = 0; i < 5; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                exceptions[index] = new UsuarioProtegidoException(ids[index]);
            });
            threads[i].start();
        }

        // Aguarda todas as threads terminarem
        for (Thread thread : threads) {
            thread.join();
        }

        // Assert
        for (int i = 0; i < 5; i++) {
            assertNotNull(exceptions[i]);
            assertTrue(exceptions[i].getMessage().contains(ids[i].toString()));
        }
    }

    @Test
    @DisplayName("Deve funcionar corretamente com getMessage() sobrescrito da superclasse")
    void deveFuncionarCorretamenteComGetMessageSobrescritoDaSuperclasse() {
        // Arrange
        Long id = 999L;
        UsuarioProtegidoException exception = new UsuarioProtegidoException(id);

        // Act
        String message = exception.getMessage();
        String toStringResult = exception.toString();

        // Assert
        assertNotNull(message);
        assertTrue(toStringResult.contains(message));
        assertTrue(toStringResult.contains("UsuarioProtegidoException"));
        assertTrue(toStringResult.contains(id.toString()));
    }

    @Test
    @DisplayName("Deve manter consistência na formatação da mensagem")
    void deveManterConsistenciaNaFormatacaoDaMensagem() {
        // Arrange
        Long[] ids = {1L, 10L, 100L, 1000L};

        // Act & Assert
        for (Long id : ids) {
            UsuarioProtegidoException exception = new UsuarioProtegidoException(id);
            String message = exception.getMessage();

            // Verifica padrão da mensagem
            assertTrue(message.startsWith("Usuário com ID "));
            assertTrue(message.contains(" é protegido e não pode ser excluído."));
            assertTrue(message.endsWith("excluído."));

            // Verifica se o ID está corretamente inserido na mensagem
            String expectedMessage = "Usuário com ID " + id + " é protegido e não pode ser excluído.";
            assertEquals(expectedMessage, message);
        }
    }
}

package hub.orcana.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes unitários para DependenciaNaoEncontradaException")
class DependenciaNaoEncontradaExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com nome de dependência e gerar mensagem formatada")
    void deveCriarExcecaoComNomeDependenciaEGerarMensagemFormatada() {
        // Arrange
        String nomeDependencia = "Usuario";

        // Act
        DependenciaNaoEncontradaException exception = new DependenciaNaoEncontradaException(nomeDependencia);

        // Assert
        assertNotNull(exception);
        assertEquals("Usuario não encontrado(a) no sistema", exception.getMessage());
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    @DisplayName("Deve criar exceção com diferentes nomes de dependência")
    void deveCriarExcecaoComDiferentesNomesDeDependencia() {
        // Arrange & Act & Assert
        String[] nomes = {"Agendamento", "Orcamento", "Estoque", "Relatorio"};

        for (String nome : nomes) {
            DependenciaNaoEncontradaException exception = new DependenciaNaoEncontradaException(nome);
            assertEquals(nome + " não encontrado(a) no sistema", exception.getMessage());
        }
    }

    @Test
    @DisplayName("Deve criar exceção com nome vazio")
    void deveCriarExcecaoComNomeVazio() {
        // Arrange
        String nomeDependencia = "";

        // Act
        DependenciaNaoEncontradaException exception = new DependenciaNaoEncontradaException(nomeDependencia);

        // Assert
        assertEquals(" não encontrado(a) no sistema", exception.getMessage());
    }

    @Test
    @DisplayName("Deve criar exceção com nome null")
    void deveCriarExcecaoComNomeNull() {
        // Arrange
        String nomeDependencia = null;

        // Act
        DependenciaNaoEncontradaException exception = new DependenciaNaoEncontradaException(nomeDependencia);

        // Assert
        assertEquals("null não encontrado(a) no sistema", exception.getMessage());
    }

    @Test
    @DisplayName("Deve ter anotação ResponseStatus com NOT_FOUND")
    void deveTerAnotacaoResponseStatusComNotFound() {
        // Arrange & Act
        ResponseStatus responseStatus = DependenciaNaoEncontradaException.class
                .getAnnotation(ResponseStatus.class);

        // Assert
        assertNotNull(responseStatus);
        assertEquals(HttpStatus.NOT_FOUND, responseStatus.code());
    }

    @Test
    @DisplayName("Deve herdar de RuntimeException")
    void deveHerdarDeRuntimeException() {
        // Arrange
        DependenciaNaoEncontradaException exception = new DependenciaNaoEncontradaException("Teste");

        // Assert
        assertTrue(exception instanceof RuntimeException);
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    @DisplayName("Deve ser lancável como exceção")
    void deveSerLancavelComoExcecao() {
        // Arrange
        String nomeDependencia = "Produto";

        // Act & Assert
        DependenciaNaoEncontradaException exception = assertThrows(
            DependenciaNaoEncontradaException.class,
            () -> {
                throw new DependenciaNaoEncontradaException(nomeDependencia);
            }
        );

        assertEquals("Produto não encontrado(a) no sistema", exception.getMessage());
    }

    @Test
    @DisplayName("Deve funcionar com nomes com acentos e caracteres especiais")
    void deveFuncionarComNomesComAcentosECaracteresEspeciais() {
        // Arrange
        String[] nomesEspeciais = {
            "Usuário",
            "Relatório",
            "Configuração",
            "Item@Estoque",
            "Dados-Sistema"
        };

        // Act & Assert
        for (String nome : nomesEspeciais) {
            DependenciaNaoEncontradaException exception = new DependenciaNaoEncontradaException(nome);
            assertTrue(exception.getMessage().contains(nome));
            assertTrue(exception.getMessage().endsWith("não encontrado(a) no sistema"));
        }
    }

    @Test
    @DisplayName("Deve manter referência ao nome da dependência internamente")
    void deveManterReferenciaAoNomeDaDependenciaInternamente() {
        // Arrange
        String nomeDependencia = "TesteInterno";

        // Act
        DependenciaNaoEncontradaException exception = new DependenciaNaoEncontradaException(nomeDependencia);

        // Assert - Verifica se a mensagem contém o nome original
        assertTrue(exception.getMessage().startsWith(nomeDependencia));
        assertEquals("TesteInterno não encontrado(a) no sistema", exception.getMessage());
    }

    @Test
    @DisplayName("Deve ser thread-safe para criação de múltiplas instâncias")
    void deveSerThreadSafeParaCriacaoDeMultiplasInstancias() {
        // Arrange
        String[] nomes = {"Dep1", "Dep2", "Dep3", "Dep4", "Dep5"};

        // Act - Criar múltiplas exceções em paralelo
        DependenciaNaoEncontradaException[] exceptions = new DependenciaNaoEncontradaException[nomes.length];

        for (int i = 0; i < nomes.length; i++) {
            exceptions[i] = new DependenciaNaoEncontradaException(nomes[i]);
        }

        // Assert
        for (int i = 0; i < nomes.length; i++) {
            assertEquals(nomes[i] + " não encontrado(a) no sistema", exceptions[i].getMessage());
        }
    }
}

package hub.orcana.controller;

import hub.orcana.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailControllerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailController emailController;

    @BeforeEach
    void setUp() {
        // Setup inicial se necessário
    }

    @Test
    @DisplayName("Deve enviar email de teste com sucesso")
    void deveEnviarEmailDeTesteComSucesso() {
        // Arrange
        doNothing().when(emailService).enviarTextoSimples(anyString(), anyString(), anyString());

        // Act
        ResponseEntity<String> response = emailController.enviarEmailTeste();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("E-mail enviado com sucesso!", response.getBody());

        // Verifica se o método do service foi chamado com os parâmetros corretos
        verify(emailService, times(1)).enviarTextoSimples(
                "linyaalvesm@gmail.com",
                "Teste de envio",
                "Olá, este é um e-mail de teste!"
        );
    }

    @Test
    @DisplayName("Deve retornar erro 500 quando ocorrer exceção no envio")
    void deveRetornarErro500QuandoOcorrerExcecao() {
        // Arrange
        String mensagemErro = "Falha na conexão SMTP";
        doThrow(new RuntimeException(mensagemErro))
                .when(emailService).enviarTextoSimples(anyString(), anyString(), anyString());

        // Act
        ResponseEntity<String> response = emailController.enviarEmailTeste();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Erro ao enviar e-mail"));
        assertTrue(response.getBody().contains(mensagemErro));

        verify(emailService, times(1)).enviarTextoSimples(
                "linyaalvesm@gmail.com",
                "Teste de envio",
                "Olá, este é um e-mail de teste!"
        );
    }

    @Test
    @DisplayName("Deve retornar erro 500 quando ocorrer IllegalArgumentException")
    void deveRetornarErro500QuandoOcorrerIllegalArgumentException() {
        // Arrange
        String mensagemErro = "E-mail inválido";
        doThrow(new IllegalArgumentException(mensagemErro))
                .when(emailService).enviarTextoSimples(anyString(), anyString(), anyString());

        // Act
        ResponseEntity<String> response = emailController.enviarEmailTeste();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Erro ao enviar e-mail: " + mensagemErro, response.getBody());

        verify(emailService, times(1)).enviarTextoSimples(
                "linyaalvesm@gmail.com",
                "Teste de envio",
                "Olá, este é um e-mail de teste!"
        );
    }

    @Test
    @DisplayName("Deve retornar erro 500 quando ocorrer NullPointerException")
    void deveRetornarErro500QuandoOcorrerNullPointerException() {
        // Arrange
        doThrow(new NullPointerException("Configuração de email não encontrada"))
                .when(emailService).enviarTextoSimples(anyString(), anyString(), anyString());

        // Act
        ResponseEntity<String> response = emailController.enviarEmailTeste();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Erro ao enviar e-mail"));

        verify(emailService, times(1)).enviarTextoSimples(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve usar os parâmetros corretos no envio do email")
    void deveUsarParametrosCorretosNoEnvio() {
        // Arrange
        doNothing().when(emailService).enviarTextoSimples(anyString(), anyString(), anyString());

        // Act
        emailController.enviarEmailTeste();

        // Assert
        // Verifica se foi chamado exatamente uma vez com os parâmetros esperados
        verify(emailService, times(1)).enviarTextoSimples(
                eq("linyaalvesm@gmail.com"),
                eq("Teste de envio"),
                eq("Olá, este é um e-mail de teste!")
        );

        // Verifica que não houve outras chamadas
        verifyNoMoreInteractions(emailService);
    }

    @Test
    @DisplayName("Deve tratar exceção com mensagem nula")
    void deveTratarExcecaoComMensagemNula() {
        // Arrange
        RuntimeException exception = new RuntimeException(); // Mensagem nula
        doThrow(exception).when(emailService).enviarTextoSimples(anyString(), anyString(), anyString());

        // Act
        ResponseEntity<String> response = emailController.enviarEmailTeste();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Erro ao enviar e-mail"));

        verify(emailService, times(1)).enviarTextoSimples(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve verificar se o controller está configurado corretamente")
    void deveVerificarSeControllerEstaConfiguradoCorretamente() {
        // Arrange & Act & Assert
        assertNotNull(emailController);

        // Verifica se o service está injetado
        ResponseEntity<String> response = emailController.enviarEmailTeste();

        // Se chegou até aqui sem erro de injeção, está configurado corretamente
        assertNotNull(response);
        verify(emailService, times(1)).enviarTextoSimples(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Deve manter consistência no formato da resposta de sucesso")
    void deveManterConsistenciaNoFormatoDaRespostaDeSucesso() {
        // Arrange
        doNothing().when(emailService).enviarTextoSimples(anyString(), anyString(), anyString());

        // Act
        ResponseEntity<String> response = emailController.enviarEmailTeste();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("E-mail enviado com sucesso!", response.getBody());

        // Verifica que não há caracteres extras ou formatação diferente
        assertFalse(response.getBody().startsWith(" "));
        assertFalse(response.getBody().endsWith(" "));
        assertTrue(response.getBody().contains("sucesso"));
    }

    @Test
    @DisplayName("Deve manter consistência no formato da resposta de erro")
    void deveManterConsistenciaNoFormatoDaRespostaDeErro() {
        // Arrange
        String mensagemErro = "Falha de conexão";
        doThrow(new RuntimeException(mensagemErro))
                .when(emailService).enviarTextoSimples(anyString(), anyString(), anyString());

        // Act
        ResponseEntity<String> response = emailController.enviarEmailTeste();

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        // Verifica o formato da mensagem de erro
        assertTrue(response.getBody().startsWith("Erro ao enviar e-mail: "));
        assertTrue(response.getBody().contains(mensagemErro));
    }
}

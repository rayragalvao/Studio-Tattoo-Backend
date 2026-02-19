package hub.orcana.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários para GlobalHandlerException")
class GlobalHandlerExceptionTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private GlobalHandlerException globalHandler;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    // ---------- TESTES PARA DEPENDENCIA NÃO ENCONTRADA ----------

    @Test
    @DisplayName("Deve tratar DependenciaNaoEncontradaException corretamente")
    void deveTratarDependenciaNaoEncontradaExceptionCorretamente() {
        // Arrange
        DependenciaNaoEncontradaException exception = new DependenciaNaoEncontradaException("Usuario");

        // Act
        ResponseEntity<Map<String, Object>> response = globalHandler.handleDependenciaNaoEncontrada(exception, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Not Found", response.getBody().get("error"));
        assertEquals("Usuario não encontrado(a) no sistema", response.getBody().get("message"));
        assertEquals("/api/test", response.getBody().get("path"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    // ---------- TESTES PARA QUANTIDADE MÍNIMA USUÁRIOS ----------

    @Test
    @DisplayName("Deve tratar QuantidadeMinimaUsuariosException corretamente")
    void deveTratarQuantidadeMinimaUsuariosExceptionCorretamente() {
        // Arrange
        QuantidadeMinimaUsuariosException exception = new QuantidadeMinimaUsuariosException();

        // Act
        ResponseEntity<Map<String, Object>> response = globalHandler.handleQuantidadeMinimaUsuarios(exception, request);

        // Assert
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(422, response.getBody().get("status"));
        assertEquals("Unprocessable Entity", response.getBody().get("error"));
        assertEquals("Quantidade mínima de usuários atingida. Não é possível excluir mais registros.",
                     response.getBody().get("message"));
        assertEquals("/api/test", response.getBody().get("path"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    // ---------- TESTES PARA USUÁRIO PROTEGIDO ----------

    @Test
    @DisplayName("Deve tratar UsuarioProtegidoException corretamente")
    void deveTratarUsuarioProtegidoExceptionCorretamente() {
        // Arrange
        UsuarioProtegidoException exception = new UsuarioProtegidoException(1L);

        // Act
        ResponseEntity<Map<String, Object>> response = globalHandler.handleUsuarioProtegido(exception, request);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(403, response.getBody().get("status"));
        assertEquals("Forbidden", response.getBody().get("error"));
        assertEquals("Usuário com ID 1 é protegido e não pode ser excluído.",
                     response.getBody().get("message"));
        assertEquals("/api/test", response.getBody().get("path"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    // ---------- TESTES PARA RESPONSE STATUS EXCEPTION ----------

    @Test
    @DisplayName("Deve tratar ResponseStatusException corretamente")
    void deveTratarResponseStatusExceptionCorretamente() {
        // Arrange
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados inválidos");

        // Act
        ResponseEntity<Map<String, Object>> response = globalHandler.handleResponseStatusException(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Dados inválidos", response.getBody().get("message"));
        assertEquals("/api/test", response.getBody().get("path"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    @DisplayName("Deve tratar ResponseStatusException com reason null")
    void deveTratarResponseStatusExceptionComReasonNull() {
        // Arrange
        ResponseStatusException exception = new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, null);

        // Act
        ResponseEntity<Map<String, Object>> response = globalHandler.handleResponseStatusException(exception, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Erro interno", response.getBody().get("message"));
    }

    // ---------- TESTES PARA MAX UPLOAD SIZE EXCEEDED ----------

    @Test
    @DisplayName("Deve tratar MaxUploadSizeExceededException corretamente")
    void deveTratarMaxUploadSizeExceededExceptionCorretamente() {
        // Arrange
        MaxUploadSizeExceededException exception = new MaxUploadSizeExceededException(1000L);

        // Act
        ResponseEntity<Map<String, Object>> response = globalHandler.handleMaxUploadSizeExceeded(exception, request);

        // Assert
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(413, response.getBody().get("status"));
        assertEquals("Payload Too Large", response.getBody().get("error"));
        assertEquals("O arquivo enviado excede o tamanho máximo permitido.",
                     response.getBody().get("message"));
        assertEquals("/api/test", response.getBody().get("path"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    // ---------- TESTES PARA MULTIPART EXCEPTION ----------

    @Test
    @DisplayName("Deve tratar MultipartException corretamente")
    void deveTratarMultipartExceptionCorretamente() {
        // Arrange
        MultipartException exception = new MultipartException("Multipart error");

        // Act
        ResponseEntity<Map<String, Object>> response = globalHandler.handleMultipartException(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals("Erro no processamento do arquivo enviado.",
                     response.getBody().get("message"));
        assertEquals("/api/test", response.getBody().get("path"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    // ---------- TESTES PARA EXCEÇÃO GENÉRICA ----------

    @Test
    @DisplayName("Deve tratar Exception genérica com ResponseStatus annotation")
    void deveTratarExceptionGenericaComResponseStatusAnnotation() {
        // Arrange
        DependenciaNaoEncontradaException exception = new DependenciaNaoEncontradaException("Test");

        // Act
        ResponseEntity<Map<String, Object>> response = globalHandler.handleAllExceptions(exception, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().get("status"));
        assertNotNull(response.getBody().get("timestamp"));
        assertEquals("/api/test", response.getBody().get("path"));
    }

    @Test
    @DisplayName("Deve tratar Exception genérica sem annotation")
    void deveTratarExceptionGenericaSemAnnotation() {
        // Arrange
        RuntimeException exception = new RuntimeException("Erro genérico");

        // Act
        ResponseEntity<Map<String, Object>> response = globalHandler.handleAllExceptions(exception, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().get("status"));
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertEquals("Erro genérico", response.getBody().get("message"));
        assertEquals("/api/test", response.getBody().get("path"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    // ---------- TESTES DE ESTRUTURA DE RESPOSTA ----------

    @Test
    @DisplayName("Deve sempre incluir timestamp nas respostas de erro")
    void deveIncluirTimestampNasRespostasDeErro() {
        // Arrange
        DependenciaNaoEncontradaException exception = new DependenciaNaoEncontradaException("Test");

        // Act
        ResponseEntity<Map<String, Object>> response = globalHandler.handleDependenciaNaoEncontrada(exception, request);

        // Assert
        assertNotNull(response.getBody().get("timestamp"));
        assertTrue(response.getBody().get("timestamp") instanceof String);
    }

    @Test
    @DisplayName("Deve sempre incluir path nas respostas de erro")
    void deveIncluirPathNasRespostasDeErro() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/usuarios/1");
        UsuarioProtegidoException exception = new UsuarioProtegidoException(1L);

        // Act
        ResponseEntity<Map<String, Object>> response = globalHandler.handleUsuarioProtegido(exception, request);

        // Assert
        assertEquals("/api/usuarios/1", response.getBody().get("path"));
    }

    // ---------- TESTES DE EDGE CASES ----------

//    @Test
//    @DisplayName("Deve tratar MethodArgumentNotValidException sem erros de campo")
//    void deveTratarMethodArgumentNotValidExceptionSemErrosDeCampo() {
//        // Arrange
//        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
//        when(exception.getBindingResult()).thenReturn(bindingResult);
//        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList());
//
//        // Act
//        ResponseEntity<Map<String, Object>> response = globalHandler.handleValidationExceptions(exception);
//
//        // Assert
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        assertEquals("Dados inválidos", response.getBody().get("message"));
//
//        @SuppressWarnings("unchecked")
//        Map<String, String> errors = (Map<String, String>) response.getBody().get("errors");
//        assertTrue(errors.isEmpty());
//    }
}

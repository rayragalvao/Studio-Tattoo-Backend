package hub.orcana.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoEntryPointTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AutenticacaoEntryPoint entryPoint;

    @Test
    void commence_badCredentialsException_deveRetornarUnauthorized() throws IOException, ServletException {
        // Arrange
        BadCredentialsException exception = new BadCredentialsException("Credenciais inválidas");

        // Act
        entryPoint.commence(request, response, exception);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void commence_insufficientAuthenticationException_deveRetornarUnauthorized() throws IOException, ServletException {
        // Arrange
        InsufficientAuthenticationException exception = new InsufficientAuthenticationException("Autenticação insuficiente");

        // Act
        entryPoint.commence(request, response, exception);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void commence_outraException_deveRetornarForbidden() throws IOException, ServletException {
        // Arrange
        AuthenticationException exception = mock(AuthenticationException.class);

        // Act
        entryPoint.commence(request, response, exception);

        // Assert
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}



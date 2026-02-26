package hub.orcana.config;

import hub.orcana.service.AutenticacaoService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoFilterTest {

    @Mock
    private AutenticacaoService autenticacaoService;

    @Mock
    private GerenciadorTokenJwt gerenciadorTokenJwt;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AutenticacaoFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_urlPermitida_devePassarDireto() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/swagger-ui/index.html");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(gerenciadorTokenJwt);
    }

    @Test
    void doFilterInternal_urlPermitidaAuth_devePassarDireto() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/auth/login");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(gerenciadorTokenJwt);
    }

    @Test
    void doFilterInternal_urlPermitidaCadastro_devePassarDireto() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/usuario/cadastro");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(gerenciadorTokenJwt);
    }

    @Test
    void doFilterInternal_semAuthorizationHeader_deveContinuar() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/usuario/perfil");
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(gerenciadorTokenJwt);
    }

    @Test
    void doFilterInternal_authorizationSemBearer_deveContinuar() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/usuario/perfil");
        when(request.getHeader("Authorization")).thenReturn("Basic abc123");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(gerenciadorTokenJwt);
    }

    @Test
    void doFilterInternal_tokenValido_deveAutenticar() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/usuario/perfil");
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(gerenciadorTokenJwt.getUsernameFromToken("validToken")).thenReturn("usuario@test.com");
        when(autenticacaoService.loadUserByUsername("usuario@test.com")).thenReturn(userDetails);
        when(gerenciadorTokenJwt.validateToken("validToken", userDetails)).thenReturn(true);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(gerenciadorTokenJwt).getUsernameFromToken("validToken");
        verify(autenticacaoService).loadUserByUsername("usuario@test.com");
        verify(gerenciadorTokenJwt).validateToken("validToken", userDetails);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_tokenInvalido_naoDeveAutenticar() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/usuario/perfil");
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        when(gerenciadorTokenJwt.getUsernameFromToken("invalidToken")).thenReturn("usuario@test.com");
        when(autenticacaoService.loadUserByUsername("usuario@test.com")).thenReturn(userDetails);
        when(gerenciadorTokenJwt.validateToken("invalidToken", userDetails)).thenReturn(false);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(gerenciadorTokenJwt).getUsernameFromToken("invalidToken");
        verify(autenticacaoService).loadUserByUsername("usuario@test.com");
        verify(gerenciadorTokenJwt).validateToken("invalidToken", userDetails);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_tokenExpirado_deveRetornarUnauthorized() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/usuario/perfil");
        when(request.getHeader("Authorization")).thenReturn("Bearer expiredToken");

        ExpiredJwtException expiredException = mock(ExpiredJwtException.class);
        when(expiredException.getClaims()).thenReturn(mock(io.jsonwebtoken.Claims.class));
        when(expiredException.getClaims().getSubject()).thenReturn("usuario@test.com");
        when(expiredException.getMessage()).thenReturn("Token expirado");

        when(gerenciadorTokenJwt.getUsernameFromToken("expiredToken")).thenThrow(expiredException);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_jaAutenticado_naoDeveReautenticar() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/usuario/perfil");
        when(request.getHeader("Authorization")).thenReturn("Bearer validToken");
        when(gerenciadorTokenJwt.getUsernameFromToken("validToken")).thenReturn("usuario@test.com");

        // Simular que já existe autenticação no contexto
        SecurityContextHolder.getContext().setAuthentication(mock(org.springframework.security.core.Authentication.class));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(gerenciadorTokenJwt).getUsernameFromToken("validToken");
        verify(autenticacaoService, never()).loadUserByUsername(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_validarCodigoOrcamento_devePermitir() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/agendamento/validar-codigo/ABC123");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(gerenciadorTokenJwt);
    }

    @Test
    void doFilterInternal_h2Console_devePermitir() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/h2-console/login.do");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(gerenciadorTokenJwt);
    }
}

package hub.orcana.config;

import hub.orcana.service.AutenticacaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoProviderTest {

    @Mock
    private AutenticacaoService autenticacaoService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AutenticacaoProvider provider;

    @Test
    void authenticate_credenciaisValidas_deveRetornarAuthentication() {
        // Arrange
        String username = "usuario@test.com";
        String password = "senha123";
        String encodedPassword = "encodedPassword";

        UsernamePasswordAuthenticationToken inputAuth =
            new UsernamePasswordAuthenticationToken(username, password);

        UserDetails userDetails = new User(username, encodedPassword,
            List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(autenticacaoService.loadUserByUsername(username)).thenReturn(userDetails);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        // Act
        Authentication result = provider.authenticate(inputAuth);

        // Assert
        assertNotNull(result);
        assertInstanceOf(UsernamePasswordAuthenticationToken.class, result);
        assertEquals(userDetails, result.getPrincipal());
        assertNull(result.getCredentials());
    }

    @Test
    void authenticate_credenciaisInvalidas_deveLancarBadCredentialsException() {
        // Arrange
        String username = "usuario@test.com";
        String password = "senhaErrada";
        String encodedPassword = "encodedPassword";

        UsernamePasswordAuthenticationToken inputAuth =
            new UsernamePasswordAuthenticationToken(username, password);

        UserDetails userDetails = new User(username, encodedPassword,
            List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(autenticacaoService.loadUserByUsername(username)).thenReturn(userDetails);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        // Act & Assert
        BadCredentialsException exception = assertThrows(BadCredentialsException.class,
            () -> provider.authenticate(inputAuth));

        assertEquals("Usuário ou senha inválidos", exception.getMessage());
    }

    @Test
    void authenticate_usuarioNaoEncontrado_deveLancarException() {
        // Arrange
        String username = "usuario@inexistente.com";
        String password = "senha123";

        UsernamePasswordAuthenticationToken inputAuth =
            new UsernamePasswordAuthenticationToken(username, password);

        when(autenticacaoService.loadUserByUsername(username))
            .thenThrow(new RuntimeException("Usuário não encontrado"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> provider.authenticate(inputAuth));
    }

    @Test
    void supports_usernamePasswordAuthenticationToken_deveRetornarTrue() {
        // Act
        boolean result = provider.supports(UsernamePasswordAuthenticationToken.class);

        // Assert
        assertTrue(result);
    }

    @Test
    void supports_outroTipoAuthentication_deveRetornarFalse() {
        // Act
        boolean result = provider.supports(Authentication.class);

        // Assert
        assertFalse(result);
    }

    @Test
    void authenticate_senhaNull_deveLancarException() {
        // Arrange
        String username = "usuario@test.com";

        UsernamePasswordAuthenticationToken inputAuth =
            new UsernamePasswordAuthenticationToken(username, null);

        // Act & Assert
        assertThrows(NullPointerException.class, () -> provider.authenticate(inputAuth));
    }

    @Test
    void authenticate_deveVerificarInteracoes() {
        // Arrange
        String username = "usuario@test.com";
        String password = "senha123";
        String encodedPassword = "encodedPassword";

        UsernamePasswordAuthenticationToken inputAuth =
            new UsernamePasswordAuthenticationToken(username, password);

        UserDetails userDetails = new User(username, encodedPassword,
            List.of(new SimpleGrantedAuthority("ROLE_USER")));

        when(autenticacaoService.loadUserByUsername(username)).thenReturn(userDetails);
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        // Act
        provider.authenticate(inputAuth);

        // Assert
        verify(autenticacaoService, times(1)).loadUserByUsername(username);
        verify(passwordEncoder, times(1)).matches(password, encodedPassword);
    }
}


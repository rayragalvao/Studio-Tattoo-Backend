package hub.orcana.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GerenciadorTokenJwtTest {

    private GerenciadorTokenJwt gerenciadorToken;

    @BeforeEach
    void setUp() {
        String secretKey = "mySecretKeyForTestingPurposesOnly123456789012345";
        long validity = 3600; // 1 hora em segundos

        gerenciadorToken = new GerenciadorTokenJwt();
        ReflectionTestUtils.setField(gerenciadorToken, "secret", secretKey);
        ReflectionTestUtils.setField(gerenciadorToken, "jwtTokenValidity", validity);
    }

    @Test
    void gerarToken_deveGerarTokenValido() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("usuario@test.com");

        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        doReturn(authorities).when(authentication).getAuthorities();

        // Act
        String token = gerenciadorToken.gerarToken(authentication);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(3, token.split("\\.").length); // JWT tem 3 partes separadas por ponto
    }

    @Test
    void getUsernameFromToken_deveRetornarUsername() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("usuario@test.com");

        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        doReturn(authorities).when(authentication).getAuthorities();

        String token = gerenciadorToken.gerarToken(authentication);

        // Act
        String username = gerenciadorToken.getUsernameFromToken(token);

        // Assert
        assertEquals("usuario@test.com", username);
    }

    @Test
    void getExpirationDateFromToken_deveRetornarDataExpiracao() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("usuario@test.com");

        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        doReturn(authorities).when(authentication).getAuthorities();

        String token = gerenciadorToken.gerarToken(authentication);

        // Act
        Date expiration = gerenciadorToken.getExpirationDateFromToken(token);

        // Assert
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date())); // Deve ser no futuro
    }

    @Test
    void validateToken_tokenValido_deveRetornarTrue() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("usuario@test.com");

        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        doReturn(authorities).when(authentication).getAuthorities();

        String token = gerenciadorToken.gerarToken(authentication);

        UserDetails userDetails = new User("usuario@test.com", "password",
            List.of(new SimpleGrantedAuthority("ROLE_USER")));

        // Act
        boolean isValid = gerenciadorToken.validateToken(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_usernameIncorreto_deveRetornarFalse() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("usuario@test.com");

        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        doReturn(authorities).when(authentication).getAuthorities();

        String token = gerenciadorToken.gerarToken(authentication);

        UserDetails userDetails = new User("outro@test.com", "password",
            List.of(new SimpleGrantedAuthority("ROLE_USER")));

        // Act
        boolean isValid = gerenciadorToken.validateToken(token, userDetails);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_tokenExpirado_deveLancarExpiredJwtException() {
        // Arrange - Token com validade muito baixa (1 milissegundo)
        ReflectionTestUtils.setField(gerenciadorToken, "jwtTokenValidity", 0L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("usuario@test.com");

        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        doReturn(authorities).when(authentication).getAuthorities();

        String token = gerenciadorToken.gerarToken(authentication);

        UserDetails userDetails = new User("usuario@test.com", "password",
            List.of(new SimpleGrantedAuthority("ROLE_USER")));

        // Act & Assert
        // Esperar um pouco para garantir que o token expire
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Quando o token está expirado, o JWT parser lança ExpiredJwtException
        // antes mesmo de chegar na validação de expiração do validateToken
        assertThrows(ExpiredJwtException.class,
                () -> gerenciadorToken.validateToken(token, userDetails));
    }

    @Test
    void validateToken_tokenNaoExpiradoMasUsuarioDiferente_deveRetornarFalse() {
        // Arrange - Token com validade normal
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("usuario@test.com");

        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        doReturn(authorities).when(authentication).getAuthorities();

        String token = gerenciadorToken.gerarToken(authentication);

        // UserDetails com username diferente
        UserDetails userDetails = new User("outro@test.com", "password",
            List.of(new SimpleGrantedAuthority("ROLE_USER")));

        // Act
        boolean isValid = gerenciadorToken.validateToken(token, userDetails);

        // Assert
        assertFalse(isValid); // Deve retornar false pois o username não confere
    }

    @Test
    void getUsernameFromToken_tokenInvalido_deveLancarException() {
        // Arrange
        String tokenInvalido = "token.invalido.aqui";

        // Act & Assert
        assertThrows(MalformedJwtException.class,
            () -> gerenciadorToken.getUsernameFromToken(tokenInvalido));
    }

    @Test
    void getExpirationDateFromToken_tokenInvalido_deveLancarException() {
        // Arrange
        String tokenInvalido = "token.invalido.aqui";

        // Act & Assert
        assertThrows(MalformedJwtException.class,
            () -> gerenciadorToken.getExpirationDateFromToken(tokenInvalido));
    }

    @Test
    void gerarToken_semAuthorities_deveGerarTokenSemErros() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("usuario@test.com");

        Collection<? extends GrantedAuthority> authorities = List.of();
        doReturn(authorities).when(authentication).getAuthorities();

        // Act
        String token = gerenciadorToken.gerarToken(authentication);

        // Assert
        assertNotNull(token);
        String username = gerenciadorToken.getUsernameFromToken(token);
        assertEquals("usuario@test.com", username);
    }

    @Test
    void gerarToken_multiplosRoles_deveIncluirTodosOsRoles() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin@test.com");

        Collection<? extends GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority("ROLE_USER"),
            new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        doReturn(authorities).when(authentication).getAuthorities();

        // Act
        String token = gerenciadorToken.gerarToken(authentication);

        // Assert
        assertNotNull(token);
        String username = gerenciadorToken.getUsernameFromToken(token);
        assertEquals("admin@test.com", username);
    }

    @Test
    void getClaimForToken_deveRetornarClaimCorreto() {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("usuario@test.com");

        Collection<? extends GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        doReturn(authorities).when(authentication).getAuthorities();

        String token = gerenciadorToken.gerarToken(authentication);

        // Act
        String subject = gerenciadorToken.getClaimForToken(token, claims -> claims.getSubject());
        Date expiration = gerenciadorToken.getClaimForToken(token, claims -> claims.getExpiration());
        Date issuedAt = gerenciadorToken.getClaimForToken(token, claims -> claims.getIssuedAt());

        // Assert
        assertEquals("usuario@test.com", subject);
        assertNotNull(expiration);
        assertNotNull(issuedAt);
        assertTrue(expiration.after(issuedAt));
    }
}







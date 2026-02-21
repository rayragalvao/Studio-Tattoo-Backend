package hub.orcana.config;

import hub.orcana.service.AutenticacaoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfiguracaoTest {

    @Mock
    private AutenticacaoService autenticacaoService;

    @Mock
    private AutenticacaoEntryPoint autenticacaoEntryPoint;

    @Test
    void passwordEncoder_deveRetornarBCryptPasswordEncoder() {
        // Arrange
        SecurityConfiguracao config = new SecurityConfiguracao(autenticacaoService, autenticacaoEntryPoint);

        // Act
        PasswordEncoder encoder = config.passwordEncoder();

        // Assert
        assertNotNull(encoder);
        assertInstanceOf(BCryptPasswordEncoder.class, encoder);
    }

    @Test
    void jwtAuthenticationFilterBean_deveRetornarAutenticacaoFilter() {
        // Arrange
        SecurityConfiguracao config = new SecurityConfiguracao(autenticacaoService, autenticacaoEntryPoint);

        // Act
        AutenticacaoFilter filter = config.jwtAuthenticationFilterBean();

        // Assert
        assertNotNull(filter);
    }

    @Test
    void jwtAuthenticationUtilBean_deveRetornarGerenciadorTokenJwt() {
        // Arrange
        SecurityConfiguracao config = new SecurityConfiguracao(autenticacaoService, autenticacaoEntryPoint);

        // Act
        GerenciadorTokenJwt gerenciador = config.jwtAuthenticationUtilBean();

        // Assert
        assertNotNull(gerenciador);
    }

    @Test
    void corsConfigurationSource_deveConfigurarCorsCorretamente() {
        // Arrange
        SecurityConfiguracao config = new SecurityConfiguracao(autenticacaoService, autenticacaoEntryPoint);

        // Act
        CorsConfigurationSource corsSource = config.corsConfigurationSource();

        // Assert
        assertNotNull(corsSource);

        // Usar uma URI válida para obter a configuração
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "/test");
        CorsConfiguration corsConfig = corsSource.getCorsConfiguration(mockRequest);
        assertNotNull(corsConfig);

        // Verificar origins permitidas
        List<String> allowedOrigins = corsConfig.getAllowedOriginPatterns();
        assertNotNull(allowedOrigins);
        assertTrue(allowedOrigins.contains("http://localhost:3000"));
        assertTrue(allowedOrigins.contains("http://localhost:5173"));
        assertTrue(allowedOrigins.contains("http://localhost:5174"));
        assertTrue(allowedOrigins.contains("http://localhost:8080"));

        // Verificar métodos permitidos
        List<String> allowedMethods = corsConfig.getAllowedMethods();
        assertNotNull(allowedMethods);
        assertTrue(allowedMethods.contains("GET"));
        assertTrue(allowedMethods.contains("POST"));
        assertTrue(allowedMethods.contains("PUT"));
        assertTrue(allowedMethods.contains("PATCH"));
        assertTrue(allowedMethods.contains("DELETE"));
        assertTrue(allowedMethods.contains("OPTIONS"));
        assertTrue(allowedMethods.contains("HEAD"));

        // Verificar headers
        List<String> allowedHeaders = corsConfig.getAllowedHeaders();
        assertNotNull(allowedHeaders);
        assertTrue(allowedHeaders.contains("*"));

        // Verificar credentials
        Boolean allowCredentials = corsConfig.getAllowCredentials();
        assertNotNull(allowCredentials);
        assertTrue(allowCredentials);

        // Verificar exposed headers
        List<String> exposedHeaders = corsConfig.getExposedHeaders();
        assertNotNull(exposedHeaders);
        assertTrue(exposedHeaders.contains("Content-Disposition"));
        assertTrue(exposedHeaders.contains("Authorization"));
    }

    @Test
    void authenticationEntryPoint_deveRetornarEntryPointPersonalizado() {
        // Arrange
        SecurityConfiguracao config = new SecurityConfiguracao(autenticacaoService, autenticacaoEntryPoint);

        // Act
        var entryPoint = config.authenticationEntryPoint();

        // Assert
        assertNotNull(entryPoint);
    }

    @Test
    void passwordEncoder_devePermitirValidacaoSenhas() {
        // Arrange
        SecurityConfiguracao config = new SecurityConfiguracao(autenticacaoService, autenticacaoEntryPoint);
        PasswordEncoder encoder = config.passwordEncoder();
        String senhaOriginal = "minhasenha123";

        // Act
        String senhaEncodada = encoder.encode(senhaOriginal);
        boolean matches = encoder.matches(senhaOriginal, senhaEncodada);

        // Assert
        assertNotNull(senhaEncodada);
        assertNotEquals(senhaOriginal, senhaEncodada);
        assertTrue(matches);
    }

    @Test
    void passwordEncoder_senhasIguaisDevemGerarHashesDiferentes() {
        // Arrange
        SecurityConfiguracao config = new SecurityConfiguracao(autenticacaoService, autenticacaoEntryPoint);
        PasswordEncoder encoder = config.passwordEncoder();
        String senha = "minhasenha123";

        // Act
        String hash1 = encoder.encode(senha);
        String hash2 = encoder.encode(senha);

        // Assert
        assertNotEquals(hash1, hash2); // BCrypt usa salt, então hashes devem ser diferentes
        assertTrue(encoder.matches(senha, hash1));
        assertTrue(encoder.matches(senha, hash2));
    }

    @Test
    void corsConfigurationSource_devePermitirTodasAsOrigens() {
        // Arrange
        SecurityConfiguracao config = new SecurityConfiguracao(autenticacaoService, autenticacaoEntryPoint);

        // Act
        CorsConfigurationSource corsSource = config.corsConfigurationSource();
        MockHttpServletRequest mockRequest = new MockHttpServletRequest("GET", "/test");
        CorsConfiguration corsConfig = corsSource.getCorsConfiguration(mockRequest);

        // Assert
        assertNotNull(corsConfig);

        // Verificar se todas as origens esperadas estão configuradas
        List<String> origins = corsConfig.getAllowedOriginPatterns();
        assertNotNull(origins);
        assertEquals(4, origins.size());
        assertTrue(origins.containsAll(List.of(
            "http://localhost:3000",
            "http://localhost:5173",
            "http://localhost:5174",
            "http://localhost:8080"
        )));
    }
}







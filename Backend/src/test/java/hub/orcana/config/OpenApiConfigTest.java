package hub.orcana.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OpenApiConfigTest {

    private final OpenApiConfig config = new OpenApiConfig();

    @Test
    void customOpenAPI_deveConfigurarOpenAPICorretamente() {
        // Act
        OpenAPI openAPI = config.customOpenAPI();

        // Assert
        assertNotNull(openAPI);
    }

    @Test
    void customOpenAPI_deveConfigurarInfo() {
        // Act
        OpenAPI openAPI = config.customOpenAPI();

        // Assert
        Info info = openAPI.getInfo();
        assertNotNull(info);
        assertEquals("Orcana API", info.getTitle());
        assertEquals("1.0", info.getVersion());
        assertEquals("API para gerenciamento de agendamentos e orçamentos", info.getDescription());
    }

    @Test
    void customOpenAPI_deveConfigurarSecurityScheme() {
        // Act
        OpenAPI openAPI = config.customOpenAPI();

        // Assert
        assertNotNull(openAPI.getComponents());
        assertNotNull(openAPI.getComponents().getSecuritySchemes());

        SecurityScheme securityScheme = openAPI.getComponents().getSecuritySchemes().get("Bearer");
        assertNotNull(securityScheme);
        assertEquals(SecurityScheme.Type.HTTP, securityScheme.getType());
        assertEquals("bearer", securityScheme.getScheme());
        assertEquals("JWT", securityScheme.getBearerFormat());
    }

    @Test
    void customOpenAPI_deveRetornarInstanciaNova() {
        // Act
        OpenAPI openAPI1 = config.customOpenAPI();
        OpenAPI openAPI2 = config.customOpenAPI();

        // Assert
        assertNotNull(openAPI1);
        assertNotNull(openAPI2);
        assertNotSame(openAPI1, openAPI2); // Cada chamada deve retornar uma nova instância
    }

    @Test
    void customOpenAPI_deveConfigurarTodosOsCamposObrigatorios() {
        // Act
        OpenAPI openAPI = config.customOpenAPI();

        // Assert
        // Verificar se a configuração básica está presente
        assertNotNull(openAPI.getInfo());
        assertNotNull(openAPI.getComponents());

        // Verificar se o security scheme está configurado corretamente
        SecurityScheme bearerScheme = openAPI.getComponents().getSecuritySchemes().get("Bearer");
        assertNotNull(bearerScheme);
        assertEquals(SecurityScheme.Type.HTTP, bearerScheme.getType());
        assertEquals("bearer", bearerScheme.getScheme());
        assertEquals("JWT", bearerScheme.getBearerFormat());

        // Verificar informações da API
        Info info = openAPI.getInfo();
        assertEquals("Orcana API", info.getTitle());
        assertEquals("1.0", info.getVersion());
        assertEquals("API para gerenciamento de agendamentos e orçamentos", info.getDescription());
    }
}

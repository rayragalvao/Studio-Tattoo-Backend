package hub.orcana.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import static org.mockito.Mockito.*;

class WebConfigTest {

    private WebConfig webConfig;

    @BeforeEach
    void setUp() {
        webConfig = new WebConfig();
    }

    @Test
    void addResourceHandlers_deveConfigurarRecursosEstaticos() {
        // Arrange
        ResourceHandlerRegistry registry = mock(ResourceHandlerRegistry.class);
        var resourceHandlerRegistration = mock(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration.class);

        when(registry.addResourceHandler("/uploads/**")).thenReturn(resourceHandlerRegistration);
        when(resourceHandlerRegistration.addResourceLocations("file:uploads/")).thenReturn(resourceHandlerRegistration);

        // Act
        webConfig.addResourceHandlers(registry);

        // Assert
        verify(registry).addResourceHandler("/uploads/**");
        verify(resourceHandlerRegistration).addResourceLocations("file:uploads/");
        verify(resourceHandlerRegistration).setCachePeriod(3600);
    }

    @Test
    void addCorsMappings_deveConfigurarCors() {
        // Arrange
        CorsRegistry registry = mock(CorsRegistry.class);
        var corsRegistration = mock(org.springframework.web.servlet.config.annotation.CorsRegistration.class);

        when(registry.addMapping("/**")).thenReturn(corsRegistration);
        when(corsRegistration.allowedOrigins("http://localhost:5173", "http://localhost:3000")).thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders("*")).thenReturn(corsRegistration);

        // Act
        webConfig.addCorsMappings(registry);

        // Assert
        verify(registry).addMapping("/**");
        verify(corsRegistration).allowedOrigins("http://localhost:5173", "http://localhost:3000");
        verify(corsRegistration).allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS");
        verify(corsRegistration).allowedHeaders("*");
        verify(corsRegistration).allowCredentials(true);
    }

    @Test
    void addResourceHandlers_deveConfigurarCacheCorreto() {
        // Arrange
        ResourceHandlerRegistry registry = mock(ResourceHandlerRegistry.class);
        var resourceHandlerRegistration = mock(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration.class);

        when(registry.addResourceHandler("/uploads/**")).thenReturn(resourceHandlerRegistration);
        when(resourceHandlerRegistration.addResourceLocations("file:uploads/")).thenReturn(resourceHandlerRegistration);

        // Act
        webConfig.addResourceHandlers(registry);

        // Assert
        verify(resourceHandlerRegistration).setCachePeriod(3600); // 1 hora em segundos
    }

    @Test
    void addCorsMappings_devePermitirCredenciais() {
        // Arrange
        CorsRegistry registry = mock(CorsRegistry.class);
        var corsRegistration = mock(org.springframework.web.servlet.config.annotation.CorsRegistration.class);

        when(registry.addMapping("/**")).thenReturn(corsRegistration);
        when(corsRegistration.allowedOrigins("http://localhost:5173", "http://localhost:3000")).thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders("*")).thenReturn(corsRegistration);

        // Act
        webConfig.addCorsMappings(registry);

        // Assert
        verify(corsRegistration).allowCredentials(true);
    }

    @Test
    void addResourceHandlers_deveUsarCaminhoCorreto() {
        // Arrange
        ResourceHandlerRegistry registry = mock(ResourceHandlerRegistry.class);
        var resourceHandlerRegistration = mock(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration.class);

        when(registry.addResourceHandler("/uploads/**")).thenReturn(resourceHandlerRegistration);
        when(resourceHandlerRegistration.addResourceLocations("file:uploads/")).thenReturn(resourceHandlerRegistration);

        // Act
        webConfig.addResourceHandlers(registry);

        // Assert
        verify(registry).addResourceHandler("/uploads/**");
        verify(resourceHandlerRegistration).addResourceLocations("file:uploads/");
    }

    @Test
    void addCorsMappings_devePermitirTodosOsHeaders() {
        // Arrange
        CorsRegistry registry = mock(CorsRegistry.class);
        var corsRegistration = mock(org.springframework.web.servlet.config.annotation.CorsRegistration.class);

        when(registry.addMapping("/**")).thenReturn(corsRegistration);
        when(corsRegistration.allowedOrigins("http://localhost:5173", "http://localhost:3000")).thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders("*")).thenReturn(corsRegistration);

        // Act
        webConfig.addCorsMappings(registry);

        // Assert
        verify(corsRegistration).allowedHeaders("*");
    }

    @Test
    void addCorsMappings_deveConfigurarTodosOsMetodos() {
        // Arrange
        CorsRegistry registry = mock(CorsRegistry.class);
        var corsRegistration = mock(org.springframework.web.servlet.config.annotation.CorsRegistration.class);

        when(registry.addMapping("/**")).thenReturn(corsRegistration);
        when(corsRegistration.allowedOrigins("http://localhost:5173", "http://localhost:3000")).thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders("*")).thenReturn(corsRegistration);

        // Act
        webConfig.addCorsMappings(registry);

        // Assert
        verify(corsRegistration).allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS");
    }

    @Test
    void addCorsMappings_deveMapearTodasAsRotas() {
        // Arrange
        CorsRegistry registry = mock(CorsRegistry.class);
        var corsRegistration = mock(org.springframework.web.servlet.config.annotation.CorsRegistration.class);

        when(registry.addMapping("/**")).thenReturn(corsRegistration);
        when(corsRegistration.allowedOrigins("http://localhost:5173", "http://localhost:3000")).thenReturn(corsRegistration);
        when(corsRegistration.allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")).thenReturn(corsRegistration);
        when(corsRegistration.allowedHeaders("*")).thenReturn(corsRegistration);

        // Act
        webConfig.addCorsMappings(registry);

        // Assert
        verify(registry).addMapping("/**");
    }
}


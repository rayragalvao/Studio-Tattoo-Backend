package hub.orcana.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import hub.orcana.service.AuditoriaService;
import hub.orcana.service.AutenticacaoService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguracao {
    private final AutenticacaoService autenticacaoService;
    private final AutenticacaoEntryPoint autenticacaoEntryPoint;
    private final AuditoriaService auditoriaService;

    public SecurityConfiguracao(AutenticacaoService autenticacaoService,
                                AutenticacaoEntryPoint autenticacaoEntryPoint,
                                AuditoriaService auditoriaService) {
        this.autenticacaoService = autenticacaoService;
        this.autenticacaoEntryPoint = autenticacaoEntryPoint;
        this.auditoriaService = auditoriaService;
    }

    private static final String[] URLS_PERMITIDAS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/v3/api-docs.yaml",
            "/auth/**",
            "/usuario/cadastro",
            "/usuario/login",
            "/orcamento/cadastro",
            "/agendamento/datas-ocupadas",
            "/agendamento/validar-codigo/**",
            "/uploads/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny)
                .xssProtection(xss -> xss.headerValue(
                        XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .contentSecurityPolicy(csp -> csp.policyDirectives(
                        "default-src 'self'; script-src 'self'; style-src 'self' 'unsafe-inline'; " +
                        "img-src 'self' data:; font-src 'self'; object-src 'none'; frame-ancestors 'none'")))
                .cors(Customizer.withDefaults())
                .csrf(CsrfConfigurer<HttpSecurity>::disable)
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers(URLS_PERMITIDAS)
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                ).exceptionHandling(handling -> handling
                        .authenticationEntryPoint(autenticacaoEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler())
                ).sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.addFilterBefore(jwtAuthenticationFilterBean(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(
                new AutenticacaoProvider(autenticacaoService, passwordEncoder(), auditoriaService));
        return authenticationManagerBuilder.build();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Token inválido ou expirado");
            errorResponse.put("error", "UNAUTHORIZED");

            ObjectMapper mapper = new ObjectMapper();
            response.getWriter().write(mapper.writeValueAsString(errorResponse));
        };
    }


    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            String usuario = (request.getUserPrincipal() != null)
                    ? request.getUserPrincipal().getName()
                    : "anonimo";
            auditoriaService.registrarAcessoNegado(usuario, request.getRequestURI());
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "Acesso negado");
            errorResponse.put("error", "FORBIDDEN");
            response.getWriter().write(mapper.writeValueAsString(errorResponse));
        };
    }

    @Bean
    public AutenticacaoFilter jwtAuthenticationFilterBean() {
        return new AutenticacaoFilter(autenticacaoService, jwtAuthenticationUtilBean());
    }

    @Bean
    public GerenciadorTokenJwt jwtAuthenticationUtilBean() {
        return new GerenciadorTokenJwt();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuracao = new CorsConfiguration();

        configuracao.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:8080"
        ));

        configuracao.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.PATCH.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.OPTIONS.name(),
                HttpMethod.HEAD.name()
        ));

        configuracao.setAllowedHeaders(Arrays.asList("*"));
        configuracao.setAllowCredentials(true);
        configuracao.setExposedHeaders(Arrays.asList(HttpHeaders.CONTENT_DISPOSITION, HttpHeaders.AUTHORIZATION));

        UrlBasedCorsConfigurationSource origem = new UrlBasedCorsConfigurationSource();
        origem.registerCorsConfiguration("/**", configuracao);

        return origem;
    }


}
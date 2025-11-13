package hub.orcana.config;

import hub.orcana.service.AutenticacaoService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@Component
public class AutenticacaoFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutenticacaoFilter.class);

    private final AutenticacaoService autenticacaoService;

    private final GerenciadorTokenJwt gerenciadorTokenJwt;

    private static final String[] URLS_PERMITIDAS = {
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/v3/api-docs.yaml",
            "/h2-console",
            "/auth",
            "/usuario/cadastro",
            "/usuario/login",
            "/orcamento/cadastro"
    };

    public AutenticacaoFilter(AutenticacaoService autenticacaoService, GerenciadorTokenJwt gerenciadorTokenJwt) {
        this.autenticacaoService = autenticacaoService;
        this.gerenciadorTokenJwt = gerenciadorTokenJwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Pula a validação JWT para URLs permitidas
        String requestPath = request.getRequestURI();
        boolean isPermittedUrl = Arrays.stream(URLS_PERMITIDAS)
                .anyMatch(url -> requestPath.startsWith(url));

        if (isPermittedUrl) {
            filterChain.doFilter(request, response);
            return;
        }
        String username = null;
        String jwtToken = null;

        String requestTokenHeader = request.getHeader("Authorization");
        if (Objects.nonNull(requestTokenHeader) && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);

            try {
                username = gerenciadorTokenJwt.getUsernameFromToken(jwtToken);
            } catch (ExpiredJwtException e) {
                LOGGER.info("[FALHA DE AUTENTICACAO] Token expirado, usuario: {} - {}",
                        e.getClaims().getSubject(), e.getMessage()
                );
                LOGGER.trace("[FALHA DE AUTENTICACAO] Stack trace: ", e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            addUsernameInContext(request, username, jwtToken);
        }

        filterChain.doFilter(request, response);
    }

    private void addUsernameInContext(HttpServletRequest request, String username, String jwtToken) {
        UserDetails userDetails = autenticacaoService.loadUserByUsername(username);

        if (gerenciadorTokenJwt.validateToken(jwtToken, userDetails)) {

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}
package hub.orcana.config;

import hub.orcana.service.AuditoriaService;
import hub.orcana.service.AutenticacaoService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AutenticacaoProvider implements AuthenticationProvider {

    private final AutenticacaoService autenticacaoService;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public AutenticacaoProvider(AutenticacaoService autenticacaoService,
                                PasswordEncoder passwordEncoder,
                                AuditoriaService auditoriaService) {
        this.autenticacaoService = autenticacaoService;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();

        UserDetails userDetails;
        try {
            userDetails = this.autenticacaoService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            auditoriaService.registrarLoginFalha(username, "Usuario nao encontrado");
            throw new BadCredentialsException("Usu\u00e1rio ou senha inv\u00e1lidos");
        }

        if (this.passwordEncoder.matches(password, userDetails.getPassword())) {
            auditoriaService.registrarLoginSucesso(username);
            return new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
        } else {
            auditoriaService.registrarLoginFalha(username, "Senha incorreta");
            throw new BadCredentialsException("Usu\u00e1rio ou senha inv\u00e1lidos");
        }
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
package hub.orcana.service;

import hub.orcana.config.GerenciadorTokenJwt;
import hub.orcana.dto.usuario.AlterarSenhaUsuario;
import hub.orcana.dto.usuario.ListarUsuarios;
import hub.orcana.exception.DependenciaNaoEncontradaException;
import hub.orcana.tables.Usuario;
import hub.orcana.tables.repository.OrcamentoRepository;
import hub.orcana.tables.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioContaServiceTest {

    @Mock
    private UsuarioRepository repository;
    @Mock
    private OrcamentoRepository orcamentoRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private GerenciadorTokenJwt gerenciadorTokenJwt;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    @Test
    void buscarPerfilDeveUsarEmailDoUsuarioAutenticado() {
        Usuario usuario = usuario();
        when(repository.findByEmail("ana@studio.com")).thenReturn(Optional.of(usuario));

        ListarUsuarios resultado = service.buscarPerfil("ana@studio.com");

        assertEquals(8L, resultado.id());
        assertEquals("Ana", resultado.nome());
    }

    @Test
    void alterarSenhaDeveValidarSenhaAtualEGravarHash() {
        Usuario usuario = usuario();
        AlterarSenhaUsuario dados = new AlterarSenhaUsuario("Atual@123", "Nova@1234", "Nova@1234");
        when(repository.findByEmail("ana@studio.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Atual@123", "hash-atual")).thenReturn(true);
        when(passwordEncoder.matches("Nova@1234", "hash-atual")).thenReturn(false);
        when(passwordEncoder.encode("Nova@1234")).thenReturn("hash-novo");

        service.alterarSenha("ana@studio.com", dados);

        assertEquals("hash-novo", usuario.getSenha());
        verify(repository).save(usuario);
    }

    @Test
    void alterarSenhaDeveRejeitarSenhaAtualIncorreta() {
        Usuario usuario = usuario();
        AlterarSenhaUsuario dados = new AlterarSenhaUsuario("Errada@123", "Nova@1234", "Nova@1234");
        when(repository.findByEmail("ana@studio.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("Errada@123", "hash-atual")).thenReturn(false);

        ResponseStatusException erro = assertThrows(
                ResponseStatusException.class,
                () -> service.alterarSenha("ana@studio.com", dados)
        );

        assertEquals(400, erro.getStatusCode().value());
        verify(repository, never()).save(usuario);
    }

    @Test
    void buscarPerfilDeveFalharQuandoUsuarioNaoExiste() {
        when(repository.findByEmail("ausente@studio.com")).thenReturn(Optional.empty());

        assertThrows(DependenciaNaoEncontradaException.class,
                () -> service.buscarPerfil("ausente@studio.com"));
    }

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.setId(8L);
        usuario.setNome("Ana");
        usuario.setEmail("ana@studio.com");
        usuario.setSenha("hash-atual");
        return usuario;
    }
}

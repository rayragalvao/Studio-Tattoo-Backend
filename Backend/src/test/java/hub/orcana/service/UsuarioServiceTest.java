package hub.orcana.service;

import hub.orcana.config.GerenciadorTokenJwt;
import hub.orcana.dto.usuario.*;
import hub.orcana.exception.*;
import hub.orcana.tables.Orcamento;
import hub.orcana.tables.Usuario;
import hub.orcana.tables.repository.OrcamentoRepository;
import hub.orcana.tables.repository.UsuarioRepository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

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

    // -------------------------------------------------------
    // criar(CadastroUsuario)
    // -------------------------------------------------------

    @Test
    @DisplayName("criar(CadastroUsuario) - sucesso")
    void criarCadastro_sucesso() {
        CadastroUsuario dto = new CadastroUsuario(
                "rayra", "email@ex.com", "(11) 91234-1234", "123", new Date(), false);

        Usuario convertido = UsuarioMapper.of(dto);

        when(repository.existsByEmail("email@ex.com")).thenReturn(false);
        when(repository.existsByTelefone("(11) 91234-1234")).thenReturn(false);
        when(passwordEncoder.encode("123")).thenReturn("hash");
        when(repository.save(any())).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(10L);
            return u;
        });

        when(orcamentoRepository.findOrcamentoByEmail("email@ex.com")).thenReturn(List.of());

        Usuario salvo = service.criar(dto);

        assertNotNull(salvo.getId());
        assertEquals("hash", salvo.getSenha());
        verify(repository).save(any());
    }

    @Test
    @DisplayName("criar(CadastroUsuario) - email já cadastrado")
    void criarCadastro_emailDuplicado() {
        CadastroUsuario dto = new CadastroUsuario("a", "x@x.com", "", "1", new Date(), false);

        when(repository.existsByEmail("x@x.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.criar(dto));

        assertEquals(409, ex.getStatusCode().value());
    }

    @Test
    @DisplayName("criar(CadastroUsuario) - telefone já cadastrado")
    void criarCadastro_telefoneDuplicado() {
        CadastroUsuario dto = new CadastroUsuario("a", "x@x.com", "(11) 99999-9999", "1", new Date(), false);

        when(repository.existsByEmail("x@x.com")).thenReturn(false);
        when(repository.existsByTelefone("(11) 99999-9999")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.criar(dto));

        assertEquals(409, ex.getStatusCode().value());
    }

    // -------------------------------------------------------
    // criar(Usuario)
    // -------------------------------------------------------

    @Test
    @DisplayName("criar(Usuario) - id já existe → erro")
    void criarUsuario_idExiste() {
        Usuario usuario = new Usuario();
        usuario.setId(5L);

        when(repository.existsById(5L)).thenReturn(true);

        assertThrows(DependenciaNaoEncontradaException.class, () -> service.criar(usuario));
    }

    @Test
    @DisplayName("criar(Usuario) - sucesso")
    void criarUsuario_sucesso() {
        Usuario u = new Usuario();
        u.setId(100L); // será zerado pelo service

        when(repository.save(any())).thenAnswer(invocation -> {
            Usuario user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        when(orcamentoRepository.findOrcamentoByEmail(any())).thenReturn(List.of());

        Usuario salvo = service.criar(u);

        assertEquals(1L, salvo.getId());
        verify(repository).save(any());
    }

    // -------------------------------------------------------
    // autenticar
    // -------------------------------------------------------

    @Test
    @DisplayName("autenticar - sucesso")
    void autenticar_sucesso() {
        LoginUsuario login = new LoginUsuario("email@x.com", "123");

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

        Usuario encontrado = new Usuario();
        encontrado.setId(10L);
        encontrado.setNome("Rayra");
        encontrado.setEmail("email@x.com");
        encontrado.setAdmin(false);

        when(repository.findByEmail("email@x.com")).thenReturn(Optional.of(encontrado));
        when(gerenciadorTokenJwt.gerarToken(auth)).thenReturn("jwt-token");

        UsuarioToken token = service.autenticar(login);

        assertEquals("jwt-token", token.token());
        assertEquals(10L, token.id());
        verify(authenticationManager).authenticate(any());
    }

    @Test
    @DisplayName("autenticar - email não encontrado")
    void autenticar_emailNaoExiste() {
        LoginUsuario login = new LoginUsuario("inexistente@x.com", "123");

        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);

        when(repository.findByEmail("inexistente@x.com")).thenReturn(Optional.empty());

        ResponseStatusException ex =
                assertThrows(ResponseStatusException.class, () -> service.autenticar(login));

        assertEquals(404, ex.getStatusCode().value());
    }

    // -------------------------------------------------------
    // listar
    // -------------------------------------------------------

    @Test
    @DisplayName("listar - sucesso")
    void listar_sucesso() {
        Usuario u1 = new Usuario();
        u1.setId(1L);
        Usuario u2 = new Usuario();
        u2.setId(2L);

        when(repository.findAll()).thenReturn(List.of(u1, u2));

        List<ListarUsuarios> result = service.listar();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("listar - vazio")
    void listar_vazio() {
        when(repository.findAll()).thenReturn(List.of());

        List<ListarUsuarios> result = service.listar();

        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------
    // buscarById
    // -------------------------------------------------------

    @Test
    @DisplayName("buscarById - sucesso")
    void buscarById_sucesso() {
        Usuario user = new Usuario();
        user.setId(5L);
        user.setNome("Teste");

        when(repository.findById(5L)).thenReturn(Optional.of(user));

        ListarUsuarios dto = service.buscarById(5L);

        assertEquals(5L, dto.id());
    }

    @Test
    @DisplayName("buscarById - id não existe")
    void buscarById_naoExiste() {
        when(repository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(DependenciaNaoEncontradaException.class, () -> service.buscarById(5L));
    }

    // -------------------------------------------------------
    // atualizarById
    // -------------------------------------------------------

    @Test
    @DisplayName("atualizarById - sucesso")
    void atualizar_sucesso() {
        Usuario u = new Usuario();

        when(repository.existsById(10L)).thenReturn(true);
        when(repository.save(any())).thenAnswer(inv -> {
            Usuario x = inv.getArgument(0);
            x.setId(10L);
            return x;
        });

        ListarUsuarios atualizado = service.atualizarById(10L, u);

        assertEquals(10L, atualizado.id());
    }

    @Test
    @DisplayName("atualizarById - id inconsistente → erro")
    void atualizar_idInconsistente() {
        Usuario u = new Usuario();
        u.setId(99L);

        when(repository.existsById(10L)).thenReturn(true);

        assertThrows(DependenciaNaoEncontradaException.class, () ->
                service.atualizarById(10L, u));
    }

    // -------------------------------------------------------
    // deletarById
    // -------------------------------------------------------

    @Test
    @DisplayName("deletarById - sucesso")
    void deletar_sucesso() {
        when(repository.count()).thenReturn(5L);
        when(repository.existsById(10L)).thenReturn(true);

        service.deletarById(10L);

        verify(repository).deleteById(10L);
    }

    @Test
    @DisplayName("deletarById - mínimo de usuários → erro")
    void deletar_minimoErro() {
        when(repository.count()).thenReturn(1L);

        assertThrows(QuantidadeMinimaUsuariosException.class, () ->
                service.deletarById(5L));
    }

    @Test
    @DisplayName("deletarById - usuário protegido id=1")
    void deletar_protegido() {
        when(repository.count()).thenReturn(2L);

        assertThrows(UsuarioProtegidoException.class, () ->
                service.deletarById(1L));
    }

    @Test
    @DisplayName("deletarById - id não existe")
    void deletar_idNaoExiste() {
        when(repository.count()).thenReturn(5L);
        when(repository.existsById(10L)).thenReturn(false);

        assertThrows(DependenciaNaoEncontradaException.class, () ->
                service.deletarById(10L));
    }
}

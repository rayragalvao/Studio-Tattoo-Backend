package hub.orcana.controller;

import hub.orcana.dto.usuario.CadastroUsuario;
import hub.orcana.dto.usuario.ListarUsuarios;
import hub.orcana.dto.usuario.LoginUsuario;
import hub.orcana.dto.usuario.UsuarioToken;
import hub.orcana.service.UsuarioService;
import hub.orcana.tables.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    // ---------------- POST /cadastro ----------------

    @Test
    @DisplayName("criarUsuario - deve criar usuário e retornar 201")
    void criarUsuario_sucesso() {
        CadastroUsuario payload = mock(CadastroUsuario.class);
        Usuario usuario = mock(Usuario.class);

        when(usuario.getId()).thenReturn(1L);
        when(usuario.getEmail()).thenReturn("abc@ex.com");
        when(usuarioService.criar(any(CadastroUsuario.class))).thenReturn(usuario);

        ResponseEntity<Usuario> response = usuarioController.criarUsuario(payload);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
        verify(usuarioService, times(1)).criar(any(CadastroUsuario.class));
    }

    @Test
    @DisplayName("criarUsuario - deve lançar exceção do service")
    void criarUsuario_excecao() {
        CadastroUsuario payload = mock(CadastroUsuario.class);
        when(usuarioService.criar(any(CadastroUsuario.class)))
                .thenThrow(new RuntimeException("Erro"));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> usuarioController.criarUsuario(payload));

        assertEquals("Erro", thrown.getMessage());
        verify(usuarioService, times(1)).criar(any(CadastroUsuario.class));
    }

    // ---------------- POST /login ----------------

    @Test
    @DisplayName("login - autenticação bem-sucedida retorna token e status 200")
    void login_sucesso() {
        LoginUsuario payload = mock(LoginUsuario.class);
        UsuarioToken token = mock(UsuarioToken.class);

        when(token.token()).thenReturn("jwt-123");
        when(usuarioService.autenticar(any(LoginUsuario.class))).thenReturn(token);

        ResponseEntity<UsuarioToken> res = usuarioController.login(payload);

        assertEquals(HttpStatus.OK, res.getStatusCode());
        assertEquals("jwt-123", res.getBody().token());
        verify(usuarioService, times(1)).autenticar(any(LoginUsuario.class));
    }

    @Test
    @DisplayName("login - deve repassar exceção do service")
    void login_excecao() {
        LoginUsuario payload = mock(LoginUsuario.class);
        when(usuarioService.autenticar(any(LoginUsuario.class)))
                .thenThrow(new RuntimeException("Credenciais"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioController.login(payload));

        assertEquals("Credenciais", ex.getMessage());
        verify(usuarioService, times(1)).autenticar(any(LoginUsuario.class));
    }

    // ---------------- GET /usuario ----------------

    @Test
    @DisplayName("listarUsuarios - retorna lista e 200")
    void listarUsuarios_sucesso() {
        ListarUsuarios u1 = mock(ListarUsuarios.class);
        ListarUsuarios u2 = mock(ListarUsuarios.class);
        when(usuarioService.listar()).thenReturn(List.of(u1, u2));

        ResponseEntity<List<ListarUsuarios>> resp = usuarioController.listarUsuarios();

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals(2, resp.getBody().size());
        verify(usuarioService, times(1)).listar();
    }

    @Test
    @DisplayName("listarUsuarios - retorna 204 quando vazio")
    void listarUsuarios_vazio() {
        when(usuarioService.listar()).thenReturn(List.of());

        ResponseEntity<List<ListarUsuarios>> resp = usuarioController.listarUsuarios();

        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(usuarioService, times(1)).listar();
    }

    @Test
    @DisplayName("listarUsuarios - exceção propagada")
    void listarUsuarios_excecao() {
        when(usuarioService.listar()).thenThrow(new RuntimeException("Erro listar"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioController.listarUsuarios());
        assertEquals("Erro listar", ex.getMessage());
        verify(usuarioService, times(1)).listar();
    }

    // ---------------- GET /usuario/{id} ----------------

    @Test
    @DisplayName("buscarUsuarioById - sucesso retorna 200")
    void buscarPorId_sucesso() {
        ListarUsuarios user = mock(ListarUsuarios.class);
        when(usuarioService.buscarById(1L)).thenReturn(user);

        ResponseEntity<ListarUsuarios> resp = usuarioController.buscarUsuarioById(1L);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        verify(usuarioService, times(1)).buscarById(1L);
    }

    @Test
    @DisplayName("buscarUsuarioById - retorna 204 quando nulo")
    void buscarPorId_vazio() {
        when(usuarioService.buscarById(1L)).thenReturn(null);

        ResponseEntity<ListarUsuarios> resp = usuarioController.buscarUsuarioById(1L);

        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(usuarioService, times(1)).buscarById(1L);
    }

    @Test
    @DisplayName("buscarUsuarioById - exceção propagada")
    void buscarPorId_excecao() {
        when(usuarioService.buscarById(1L)).thenThrow(new RuntimeException("Erro"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioController.buscarUsuarioById(1L));
        assertEquals("Erro", ex.getMessage());
        verify(usuarioService, times(1)).buscarById(1L);
    }

    // ---------------- PUT /usuario/{id} ----------------

    @Test
    @DisplayName("atualizarUsuario - retorna 200 com usuário atualizado")
    void atualizar_sucesso() {
        Usuario payload = mock(Usuario.class);
        ListarUsuarios atualizado = mock(ListarUsuarios.class);

        when(usuarioService.atualizarById(eq(1L), any(Usuario.class))).thenReturn(atualizado);

        ResponseEntity<ListarUsuarios> resp = usuarioController.atualizarUsuario(1L, payload);

        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertNotNull(resp.getBody());
        verify(usuarioService, times(1)).atualizarById(eq(1L), any(Usuario.class));
    }

    @Test
    @DisplayName("atualizarUsuario - exceção propagada")
    void atualizar_excecao() {
        Usuario payload = mock(Usuario.class);
        when(usuarioService.atualizarById(eq(1L), any(Usuario.class)))
                .thenThrow(new RuntimeException("Erro update"));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioController.atualizarUsuario(1L, payload));
        assertEquals("Erro update", ex.getMessage());
        verify(usuarioService, times(1)).atualizarById(eq(1L), any(Usuario.class));
    }

    // ---------------- DELETE /usuario/{id} ----------------

    @Test
    @DisplayName("deletarUsuario - sucesso retorna 204")
    void deletar_sucesso() {
        doNothing().when(usuarioService).deletarById(1L);

        ResponseEntity<Void> resp = usuarioController.deletarUsuario(1L);

        assertEquals(HttpStatus.NO_CONTENT, resp.getStatusCode());
        verify(usuarioService, times(1)).deletarById(1L);
    }

    @Test
    @DisplayName("deletarUsuario - exceção propagada")
    void deletar_excecao() {
        doThrow(new RuntimeException("Erro delete")).when(usuarioService).deletarById(1L);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> usuarioController.deletarUsuario(1L));
        assertEquals("Erro delete", ex.getMessage());
        verify(usuarioService, times(1)).deletarById(1L);
    }
}

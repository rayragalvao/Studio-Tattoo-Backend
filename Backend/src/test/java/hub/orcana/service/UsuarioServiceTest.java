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

    @Test
    @DisplayName("criar(Usuario) - deve zerar ID mesmo quando null")
    void criarUsuario_idNullDeveSerZerado() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(null);
        usuario.setEmail("test@null.com");

        when(repository.save(any())).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(100L);
            return u;
        });
        when(orcamentoRepository.findOrcamentoByEmail("test@null.com")).thenReturn(List.of());

        // Act
        Usuario salvo = service.criar(usuario);

        // Assert
        assertEquals(100L, salvo.getId());
        verify(repository, never()).existsById(any()); // Não deve verificar se ID null existe
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

    @Test
    @DisplayName("autenticar - erro de autenticação lançado pelo AuthenticationManager")
    void autenticar_erroAutenticacao() {
        // Arrange
        LoginUsuario login = new LoginUsuario("email@error.com", "senhaErrada");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Credenciais inválidas"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.autenticar(login));

        // Não deve tentar buscar o usuário se a autenticação falhar
        verify(repository, never()).findByEmail(any());
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

    @Test
    @DisplayName("atualizarById - deve permitir ID null no objeto usuario")
    void atualizarById_idNullPermitido() {
        // Arrange
        Usuario u = new Usuario();
        u.setId(null); // ID null deve ser aceito e substituído pelo ID do path
        u.setNome("Nome Atualizado");

        when(repository.existsById(50L)).thenReturn(true);
        when(repository.save(any())).thenAnswer(inv -> {
            Usuario x = inv.getArgument(0);
            assertEquals(50L, x.getId()); // Deve ter sido definido pelo service
            return x;
        });

        // Act
        ListarUsuarios atualizado = service.atualizarById(50L, u);

        // Assert
        assertEquals(50L, atualizado.id());
        verify(repository).save(u);
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

    @Test
    @DisplayName("deletarById - deve verificar ordem correta de validações")
    void deletar_ordemValidacoes() {
        // Arrange - quando só tem 1 usuário, deve falhar antes de verificar se é o protegido
        when(repository.count()).thenReturn(1L);

        // Act & Assert
        assertThrows(QuantidadeMinimaUsuariosException.class,
                () -> service.deletarById(1L));

        // Não deve verificar se existe o usuário pois falha na quantidade mínima primeiro
        verify(repository, never()).existsById(any());
    }

    // -------------------------------------------------------
    // atualizarPerfil
    // -------------------------------------------------------

    @Test
    @DisplayName("atualizarPerfil - sucesso com todos os campos")
    void atualizarPerfil_sucesso() {
        // Arrange
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(10L);
        usuarioExistente.setNome("Nome Antigo");
        usuarioExistente.setEmail("teste@email.com");

        AtualizarPerfilUsuario perfilAtualizado = new AtualizarPerfilUsuario(
                "Novo Nome",
                "(11) 99999-9999",
                "1990-05-15"
        );

        when(repository.findById(10L)).thenReturn(Optional.of(usuarioExistente));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ListarUsuarios resultado = service.atualizarPerfil(10L, perfilAtualizado);

        // Assert
        assertEquals(10L, resultado.id());
        assertEquals("Novo Nome", usuarioExistente.getNome());
        assertEquals("(11) 99999-9999", usuarioExistente.getTelefone());
        assertNotNull(usuarioExistente.getDtNasc());
        verify(repository).save(usuarioExistente);
    }

    @Test
    @DisplayName("atualizarPerfil - sucesso sem data de nascimento")
    void atualizarPerfil_sucessoSemDataNascimento() {
        // Arrange
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(5L);

        AtualizarPerfilUsuario perfilAtualizado = new AtualizarPerfilUsuario(
                "Nome Atualizado",
                "(11) 88888-8888",
                null
        );

        when(repository.findById(5L)).thenReturn(Optional.of(usuarioExistente));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        service.atualizarPerfil(5L, perfilAtualizado);

        // Assert
        assertEquals("Nome Atualizado", usuarioExistente.getNome());
        assertEquals("(11) 88888-8888", usuarioExistente.getTelefone());
        assertNull(usuarioExistente.getDtNasc());
    }

    @Test
    @DisplayName("atualizarPerfil - sucesso com data vazia")
    void atualizarPerfil_sucessoComDataVazia() {
        // Arrange
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(5L);

        AtualizarPerfilUsuario perfilAtualizado = new AtualizarPerfilUsuario(
                "Nome Atualizado",
                "(11) 88888-8888",
                ""
        );

        when(repository.findById(5L)).thenReturn(Optional.of(usuarioExistente));
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        service.atualizarPerfil(5L, perfilAtualizado);

        // Assert
        assertEquals("Nome Atualizado", usuarioExistente.getNome());
        assertEquals("(11) 88888-8888", usuarioExistente.getTelefone());
        assertNull(usuarioExistente.getDtNasc());
    }

    @Test
    @DisplayName("atualizarPerfil - erro formato data inválido")
    void atualizarPerfil_erroFormatoDataInvalido() {
        // Arrange
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(10L);

        AtualizarPerfilUsuario perfilAtualizado = new AtualizarPerfilUsuario(
                "Nome",
                "(11) 99999-9999",
                "15/05/1990" // formato inválido
        );

        when(repository.findById(10L)).thenReturn(Optional.of(usuarioExistente));

        // Act & Assert
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.atualizarPerfil(10L, perfilAtualizado)
        );

        assertEquals("Formato de data inválido. Use yyyy-MM-dd", ex.getMessage());
    }

    @Test
    @DisplayName("atualizarPerfil - usuário não encontrado")
    void atualizarPerfil_usuarioNaoEncontrado() {
        // Arrange
        AtualizarPerfilUsuario perfilAtualizado = new AtualizarPerfilUsuario(
                "Nome",
                "(11) 99999-9999",
                "1990-05-15"
        );

        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DependenciaNaoEncontradaException.class,
                () -> service.atualizarPerfil(999L, perfilAtualizado));
    }

    // -------------------------------------------------------
    // criar - testes adicionais para associarOrcamentosAoUsuario
    // -------------------------------------------------------

    @Test
    @DisplayName("criar(CadastroUsuario) - deve associar orçamentos existentes")
    void criarCadastro_deveAssociarOrcamentos() {
        // Arrange
        CadastroUsuario dto = new CadastroUsuario(
                "teste", "test@email.com", null, "123", new Date(), false);

        Orcamento orcamento1 = new Orcamento();
        orcamento1.setCodigoOrcamento("ORC001");
        orcamento1.setEmail("test@email.com");

        Orcamento orcamento2 = new Orcamento();
        orcamento2.setCodigoOrcamento("ORC002");
        orcamento2.setEmail("test@email.com");

        when(repository.existsByEmail("test@email.com")).thenReturn(false);
        when(passwordEncoder.encode("123")).thenReturn("hash");
        when(repository.save(any())).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(10L);
            return u;
        });
        when(orcamentoRepository.findOrcamentoByEmail("test@email.com"))
                .thenReturn(List.of(orcamento1, orcamento2));

        // Act
        Usuario salvo = service.criar(dto);

        // Assert
        assertNotNull(salvo.getId());
        verify(orcamentoRepository).saveAll(List.of(orcamento1, orcamento2));
        assertEquals(salvo, orcamento1.getUsuario());
        assertEquals(salvo, orcamento2.getUsuario());
    }

    @Test
    @DisplayName("criar(Usuario) - deve associar orçamentos existentes")
    void criarUsuario_deveAssociarOrcamentos() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setEmail("usuario@test.com");

        Orcamento orcamento = new Orcamento();
        orcamento.setCodigoOrcamento("ORC003");
        orcamento.setEmail("usuario@test.com");

        when(repository.save(any())).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(5L);
            return u;
        });
        when(orcamentoRepository.findOrcamentoByEmail("usuario@test.com"))
                .thenReturn(List.of(orcamento));

        // Act
        Usuario salvo = service.criar(usuario);

        // Assert
        assertEquals(5L, salvo.getId());
        verify(orcamentoRepository).saveAll(List.of(orcamento));
        assertEquals(salvo, orcamento.getUsuario());
    }

    @Test
    @DisplayName("criar(CadastroUsuario) - telefone null deve ser ignorado na validação")
    void criarCadastro_telefoneNullIgnorado() {
        // Arrange
        CadastroUsuario dto = new CadastroUsuario(
                "teste", "test@email.com", null, "123", new Date(), false);

        when(repository.existsByEmail("test@email.com")).thenReturn(false);
        // não deve chamar existsByTelefone quando telefone é null
        when(passwordEncoder.encode("123")).thenReturn("hash");
        when(repository.save(any())).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(10L);
            return u;
        });
        when(orcamentoRepository.findOrcamentoByEmail("test@email.com")).thenReturn(List.of());

        // Act
        Usuario salvo = service.criar(dto);

        // Assert
        assertNotNull(salvo);
        verify(repository, never()).existsByTelefone(any());
    }

    @Test
    @DisplayName("criar(CadastroUsuario) - telefone vazio deve ser ignorado na validação")
    void criarCadastro_telefoneVazioIgnorado() {
        // Arrange
        CadastroUsuario dto = new CadastroUsuario(
                "teste", "test@email.com", "   ", "123", new Date(), false);

        when(repository.existsByEmail("test@email.com")).thenReturn(false);
        // não deve chamar existsByTelefone quando telefone é vazio/espaços
        when(passwordEncoder.encode("123")).thenReturn("hash");
        when(repository.save(any())).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(10L);
            return u;
        });
        when(orcamentoRepository.findOrcamentoByEmail("test@email.com")).thenReturn(List.of());

        // Act
        Usuario salvo = service.criar(dto);

        // Assert
        assertNotNull(salvo);
        verify(repository, never()).existsByTelefone(any());
    }

    // -------------------------------------------------------
    // atualizarById - teste adicional
    // -------------------------------------------------------

    @Test
    @DisplayName("atualizarById - usuário não existe")
    void atualizar_usuarioNaoExiste() {
        // Arrange
        Usuario u = new Usuario();
        when(repository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(DependenciaNaoEncontradaException.class,
                () -> service.atualizarById(999L, u));
    }

    @Test
    @DisplayName("associarOrcamentosAoUsuario - deve logar quando não há orçamentos")
    void associarOrcamentosAoUsuario_nenhumOrcamentoEncontrado() {
        // Arrange
        Usuario usuario = new Usuario();
        usuario.setId(20L);
        usuario.setEmail("semorcamento@test.com");

        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(orcamentoRepository.findOrcamentoByEmail("semorcamento@test.com")).thenReturn(List.of());

        // Act
        Usuario salvo = service.criar(usuario);

        // Assert
        assertNotNull(salvo);
        verify(orcamentoRepository).findOrcamentoByEmail("semorcamento@test.com");
        verify(orcamentoRepository, never()).saveAll(any()); // Não deve salvar se não há orçamentos
    }

    @Test
    @DisplayName("listar - deve usar UsuarioMapper.of corretamente")
    void listar_deveUsarMapper() {
        // Arrange
        Usuario u1 = new Usuario();
        u1.setId(1L);
        u1.setNome("Usuario 1");
        u1.setEmail("user1@test.com");

        Usuario u2 = new Usuario();
        u2.setId(2L);
        u2.setNome("Usuario 2");
        u2.setEmail("user2@test.com");

        when(repository.findAll()).thenReturn(List.of(u1, u2));

        // Act
        List<ListarUsuarios> result = service.listar();

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Usuario 1", result.get(0).nome());
        assertEquals("user1@test.com", result.get(0).email());
        assertEquals(2L, result.get(1).id());
        assertEquals("Usuario 2", result.get(1).nome());
        assertEquals("user2@test.com", result.get(1).email());
    }

    @Test
    @DisplayName("criar(CadastroUsuario) - deve funcionar com telefone com espaços em branco")
    void criarCadastro_telefoneComEspacos() {
        // Arrange
        CadastroUsuario dto = new CadastroUsuario(
                "teste", "espacos@email.com", "  (11) 99999-9999  ", "123", new Date(), false);

        when(repository.existsByEmail("espacos@email.com")).thenReturn(false);
        when(repository.existsByTelefone("  (11) 99999-9999  ")).thenReturn(false);
        when(passwordEncoder.encode("123")).thenReturn("hash");
        when(repository.save(any())).thenAnswer(invocation -> {
            Usuario u = invocation.getArgument(0);
            u.setId(15L);
            return u;
        });
        when(orcamentoRepository.findOrcamentoByEmail("espacos@email.com")).thenReturn(List.of());

        // Act
        Usuario salvo = service.criar(dto);

        // Assert
        assertNotNull(salvo.getId());
        verify(repository).existsByTelefone("  (11) 99999-9999  "); // Deve verificar com os espaços
    }
}

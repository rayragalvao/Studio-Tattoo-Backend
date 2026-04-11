package hub.orcana.service;

import hub.orcana.dto.usuario.DetalhesUsuario;
import hub.orcana.tables.Usuario;
import hub.orcana.tables.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutenticacaoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AutenticacaoService autenticacaoService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        usuario.setSenha("senhaHasheada123");
        usuario.setTelefone("(11) 99999-9999");
    }

    @Test
    @DisplayName("Deve carregar usuário por email com sucesso")
    void deveCarregarUsuarioPorEmailComSucesso() {
        // Arrange
        String email = "joao@email.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // Act
        UserDetails userDetails = autenticacaoService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertTrue(userDetails instanceof DetalhesUsuario);
        assertEquals(email, userDetails.getUsername());

        DetalhesUsuario detalhesUsuario = (DetalhesUsuario) userDetails;
        assertEquals(usuario.getNome(), detalhesUsuario.getNome());
        assertEquals(usuario.getEmail(), detalhesUsuario.getEmail());
        assertEquals(usuario.getSenha(), detalhesUsuario.getPassword());

        verify(usuarioRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando usuário não for encontrado")
    void deveLancarUsernameNotFoundExceptionQuandoUsuarioNaoForEncontrado() {
        // Arrange
        String emailInexistente = "inexistente@email.com";
        when(usuarioRepository.findByEmail(emailInexistente)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> autenticacaoService.loadUserByUsername(emailInexistente)
        );

        assertEquals("Usuário inexistente@email.com não encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).findByEmail(emailInexistente);
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException com email null")
    void deveLancarUsernameNotFoundExceptionComEmailNull() {
        // Arrange
        when(usuarioRepository.findByEmail(null)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> autenticacaoService.loadUserByUsername(null)
        );

        assertEquals("Usuário null não encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).findByEmail(null);
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException com email vazio")
    void deveLancarUsernameNotFoundExceptionComEmailVazio() {
        // Arrange
        String emailVazio = "";
        when(usuarioRepository.findByEmail(emailVazio)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> autenticacaoService.loadUserByUsername(emailVazio)
        );

        assertEquals("Usuário  não encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).findByEmail(emailVazio);
    }

    @Test
    @DisplayName("Deve retornar DetalhesUsuario válido com todos os dados do usuário")
    void deveRetornarDetalhesUsuarioValidoComTodosOsDadosDoUsuario() {
        // Arrange
        usuario.setTelefone("(11) 98765-4321");
        usuario.setSenha("outraSenhaHasheada456");

        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.of(usuario));

        // Act
        UserDetails userDetails = autenticacaoService.loadUserByUsername(usuario.getEmail());

        // Assert
        assertNotNull(userDetails);
        DetalhesUsuario detalhesUsuario = (DetalhesUsuario) userDetails;

        assertEquals(usuario.getNome(), detalhesUsuario.getNome());
        assertEquals(usuario.getEmail(), detalhesUsuario.getEmail());
        assertEquals(usuario.getSenha(), detalhesUsuario.getPassword());

        // Verificar propriedades do Spring Security
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isEnabled());

        verify(usuarioRepository, times(1)).findByEmail(usuario.getEmail());
    }

    @Test
    @DisplayName("Deve funcionar com diferentes formatos de email")
    void deveFuncionarComDiferentesFormatosDeEmail() {
        // Arrange
        String[] emailsValidos = {
            "usuario@dominio.com",
            "usuario.teste@dominio.com.br",
            "user+tag@example.org",
            "123456@numbers.co"
        };

        for (String email : emailsValidos) {
            Usuario usuarioTemp = new Usuario();
            usuarioTemp.setId(1L);
            usuarioTemp.setEmail(email);
            usuarioTemp.setNome("Usuário Teste");
            usuarioTemp.setSenha("senha123");

            when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuarioTemp));

            // Act
            UserDetails userDetails = autenticacaoService.loadUserByUsername(email);

            // Assert
            assertNotNull(userDetails);
            assertEquals(email, userDetails.getUsername());
        }

        verify(usuarioRepository, times(emailsValidos.length)).findByEmail(anyString());
    }

    @Test
    @DisplayName("Deve manter case sensitivity do email")
    void deveManterCaseSensitivityDoEmail() {
        // Arrange
        String emailOriginal = "Usuario@Email.COM";
        usuario.setEmail(emailOriginal);

        when(usuarioRepository.findByEmail(emailOriginal)).thenReturn(Optional.of(usuario));

        // Act
        UserDetails userDetails = autenticacaoService.loadUserByUsername(emailOriginal);

        // Assert
        assertNotNull(userDetails);
        assertEquals(emailOriginal, userDetails.getUsername());
        verify(usuarioRepository, times(1)).findByEmail(emailOriginal);
    }

    @Test
    @DisplayName("Deve tratar corretamente quando repository lança exceção")
    void deveTratarCorretamenteQuandoRepositoryLancaExcecao() {
        // Arrange
        String email = "teste@email.com";
        when(usuarioRepository.findByEmail(email))
                .thenThrow(new RuntimeException("Erro de conexão com banco"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> autenticacaoService.loadUserByUsername(email)
        );

        assertEquals("Erro de conexão com banco", exception.getMessage());
        verify(usuarioRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Deve verificar se método implementa corretamente UserDetailsService")
    void deveVerificarSeMetodoImplementaCorretamenteUserDetailsService() {
        // Arrange
        String email = "service@test.com";
        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        // Act
        UserDetails result = autenticacaoService.loadUserByUsername(email);

        // Assert
        assertNotNull(result);
        assertInstanceOf(DetalhesUsuario.class, result);

        // Verifica se todos os métodos obrigatórios do UserDetails estão funcionando
        assertDoesNotThrow(() -> {
            result.getUsername();
            result.getPassword();
            result.getAuthorities();
            result.isAccountNonExpired();
            result.isAccountNonLocked();
            result.isCredentialsNonExpired();
            result.isEnabled();
        });
    }
}



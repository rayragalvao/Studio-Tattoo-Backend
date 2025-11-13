package hub.orcana.controller;

import hub.orcana.dto.usuario.CadastroUsuario;
import hub.orcana.dto.usuario.ListarUsuarios;
import hub.orcana.dto.usuario.LoginUsuario;
import hub.orcana.dto.usuario.UsuarioToken;
import hub.orcana.service.UsuarioService;
import hub.orcana.tables.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuario")
@Tag(name = "Usuários", description = "Gerenciamento de usuários")
public class UsuarioController {
    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // Cria um usuário
    @PostMapping("/cadastro")
    @Operation(summary = "Criar um novo usuário",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos",
                            content = @Content(schema = @Schema(example = "{\"message\": \"Dados inválidos\", \"errors\": {\"email\": \"E-mail é obrigatório\"}}"))),
                    @ApiResponse(responseCode = "409", description = "Conflito - Email ou telefone já cadastrado",
                            content = @Content(schema = @Schema(example = "{\"message\": \"Email de usuário já cadastrado.\", \"status\": 409}")))
            })
    public ResponseEntity<Usuario> criarUsuario(@RequestBody @Valid CadastroUsuario usuario) {
        var novoUsuario = service.criar(usuario);
        return ResponseEntity.status(201).body(novoUsuario);
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar um usuário e gerar um token JWT",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos",
                            content = @Content(schema = @Schema(example = "{\"message\": \"Dados inválidos\", \"errors\": {\"email\": \"E-mail é obrigatório\"}}"))),
                    @ApiResponse(responseCode = "401", description = "Credenciais inválidas",
                            content = @Content(schema = @Schema(example = "{\"message\": \"Email ou senha incorretos\", \"error\": \"INVALID_CREDENTIALS\"}"))),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                            content = @Content(schema = @Schema(example = "{\"message\": \"Email de usuário não cadastrado\", \"status\": 404}")))
            })
    public ResponseEntity<UsuarioToken> login(@RequestBody @Valid LoginUsuario usuario) {
        UsuarioToken token = service.autenticar(usuario);
        return ResponseEntity.ok(token);
    }

    // Lista os usuários
    @GetMapping
    @Operation(summary = "Listar todos os usuários",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de usuários retornada com sucesso"),
                    @ApiResponse(responseCode = "204", description = "Nenhum usuário encontrado"),
                    @ApiResponse(responseCode = "401", description = "Token não fornecido ou inválido",
                            content = @Content(schema = @Schema(example = "{\"message\": \"Erro de autenticação\", \"error\": \"AUTHENTICATION_ERROR\"}"))),
                    @ApiResponse(responseCode = "403", description = "Token expirado ou sem permissões",
                            content = @Content(schema = @Schema(example = "{\"message\": \"Acesso negado\", \"status\": 403}")))
            })
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<List<ListarUsuarios>> listarUsuarios() {
        var usuarios = service.listar();
        return usuarios.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(usuarios);
    }

    // Busca usuário pelo ID
    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Token não fornecido ou inválido",
                            content = @Content(schema = @Schema(example = "{\"message\": \"Erro de autenticação\", \"error\": \"AUTHENTICATION_ERROR\"}"))),
                    @ApiResponse(responseCode = "403", description = "Token expirado ou sem permissões",
                            content = @Content(schema = @Schema(example = "{\"message\": \"Acesso negado\", \"status\": 403}"))),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                            content = @Content(schema = @Schema(example = "{\"timestamp\": \"2025-11-13T10:30:00Z\", \"status\": 404, \"error\": \"Not Found\", \"message\": \"Usuário não encontrado(a) no sistema\", \"path\": \"/usuario/999\"}")))
            })
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<ListarUsuarios> buscarUsuarioById(@PathVariable Long id) {
        var usuario = service.buscarById(id);
        return usuario != null
                ? ResponseEntity.ok(usuario)
                : ResponseEntity.noContent().build();
    }

    // Atualiza um usuário pelo ID
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um usuário existente por ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos",
                            content = @Content(schema = @Schema(example = "{\"message\": \"Dados inválidos\", \"errors\": {\"nome\": \"Nome é obrigatório\"}}"))),
                    @ApiResponse(responseCode = "401", description = "Token não fornecido ou inválido",
                            content = @Content(schema = @Schema(example = "{\"message\": \"Erro de autenticação\", \"error\": \"AUTHENTICATION_ERROR\"}"))),
                    @ApiResponse(responseCode = "403", description = "Token expirado ou sem permissões",
                            content = @Content(schema = @Schema(example = "{\"message\": \"Acesso negado\", \"status\": 403}"))),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado ou ID inconsistente",
                            content = @Content(schema = @Schema(example = "{\"timestamp\": \"2025-11-13T10:30:00Z\", \"status\": 404, \"error\": \"Not Found\", \"message\": \"Usuário não encontrado(a) no sistema\", \"path\": \"/usuario/999\"}")))
            })
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<ListarUsuarios> atualizarUsuario(
            @PathVariable Long id,
            @RequestBody @Valid Usuario usuario
    ) {
        var usuarioAtualizado = service.atualizarById(id, usuario);
        return ResponseEntity.ok(usuarioAtualizado);
    }

    // Deleta um usuário pelo ID
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um usuário existente por ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
                    @ApiResponse(responseCode = "401", description = "Token não fornecido ou inválido",
                            content = @Content(schema = @Schema(example = "{\"message\": \"Erro de autenticação\", \"error\": \"AUTHENTICATION_ERROR\"}"))),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                            content = @Content(schema = @Schema(example = "{\"timestamp\": \"2025-11-13T10:30:00Z\", \"status\": 404, \"error\": \"Not Found\", \"message\": \"Usuário não encontrado(a) no sistema\", \"path\": \"/usuario/999\"}"))),
            })
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        service.deletarById(id);
        return ResponseEntity.noContent().build();
    }
}
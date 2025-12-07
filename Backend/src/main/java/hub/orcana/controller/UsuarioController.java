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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
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
        log.info("Iniciando criação de novo usuário: {}", usuario);
        try {
            var novoUsuario = service.criar(usuario);
            log.info("Usuário criado com sucesso. ID: {}, Email: {}", novoUsuario.getId(), novoUsuario.getEmail());
            return ResponseEntity.status(201).body(novoUsuario);
        } catch (Exception e) {
            log.error("Erro ao criar usuário: {}", e.getMessage(), e);
            throw e;
        }
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
        log.info("Tentativa de login para usuário: {}", usuario);
        try {
            UsuarioToken token = service.autenticar(usuario);
            log.info("Login realizado com sucesso para usuário: {}", usuario);
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            log.warn("Falha na autenticação para usuário: {}", e.getMessage());
            throw e;
        }
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
        log.info("Iniciando busca por todos os usuários");
        try {
            var usuarios = service.listar();
            if (usuarios.isEmpty()) {
                log.info("Nenhum usuário encontrado");
                return ResponseEntity.noContent().build();
            } else {
                log.info("Retornando {} usuários encontrados", usuarios.size());
                return ResponseEntity.ok(usuarios);
            }
        } catch (Exception e) {
            log.error("Erro ao listar usuários: {}", e.getMessage(), e);
            throw e;
        }
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
        log.info("Buscando usuário por ID: {}", id);
        try {
            var usuario = service.buscarById(id);
            if (usuario != null) {
                log.info("Usuário encontrado com sucesso para ID: {}", id);
                return ResponseEntity.ok(usuario);
            } else {
                log.info("Nenhum usuário encontrado para ID: {}", id);
                return ResponseEntity.noContent().build();
            }
        } catch (Exception e) {
            log.error("Erro ao buscar usuário por ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
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
        log.info("Iniciando atualização do usuário ID: {}", id);
        try {
            var usuarioAtualizado = service.atualizarById(id, usuario);
            log.info("Usuário ID {} atualizado com sucesso", id);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (Exception e) {
            log.error("Erro ao atualizar usuário ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @PatchMapping("/{id}/perfil")
    @Operation(summary = "Atualizar perfil do usuário (nome, telefone e data de nascimento)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos",
                            content = @Content(schema = @Schema(example = "{\"message\": \"Dados inválidos\", \"errors\": {\"nome\": \"Nome é obrigatório\"}}"))),
                    @ApiResponse(responseCode = "401", description = "Token não fornecido ou inválido",
                            content = @Content(schema = @Schema(example = "{\"message\": \"Erro de autenticação\", \"error\": \"AUTHENTICATION_ERROR\"}"))),
                    @ApiResponse(responseCode = "403", description = "Token expirado ou sem permissões",
                            content = @Content(schema = @Schema(example = "{\"message\": \"Acesso negado\", \"status\": 403}"))),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado",
                            content = @Content(schema = @Schema(example = "{\"timestamp\": \"2025-11-13T10:30:00Z\", \"status\": 404, \"error\": \"Not Found\", \"message\": \"Usuário não encontrado(a) no sistema\", \"path\": \"/usuario/999/perfil\"}")))
            })
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<ListarUsuarios> atualizarPerfil(
            @PathVariable Long id,
            @RequestBody @Valid hub.orcana.dto.usuario.AtualizarPerfilUsuario perfilUsuario
    ) {
        log.info("Iniciando atualização do perfil do usuário ID: {}", id);
        try {
            var usuarioAtualizado = service.atualizarPerfil(id, perfilUsuario);
            log.info("Perfil do usuário ID {} atualizado com sucesso", id);
            return ResponseEntity.ok(usuarioAtualizado);
        } catch (Exception e) {
            log.error("Erro ao atualizar perfil do usuário ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
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
        log.info("Iniciando exclusão do usuário ID: {}", id);
        try {
            service.deletarById(id);
            log.info("Usuário ID {} excluído com sucesso", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erro ao excluir usuário ID {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }


    //temporario para testes
    @GetMapping("/encode/{senha}")
    public String encode(@PathVariable String senha) {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(senha);
    }


}
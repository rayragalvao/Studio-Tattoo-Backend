package hub.orcana.controller;

import hub.orcana.dto.estoque.CadastroMaterialInput;
import hub.orcana.dto.estoque.DetalhesMaterialOutput;
import hub.orcana.exception.DependenciaNaoEncontradaException;
import hub.orcana.service.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/estoque")
@Tag(name = "Estoque", description = "Gerenciamento de materiais em estoque")
public class EstoqueController {
    private static final Logger log = LoggerFactory.getLogger(EstoqueController.class);
    private final EstoqueService service;

    public EstoqueController(EstoqueService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todos os materiais")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de materiais retornada com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalhesMaterialOutput.class))),
        @ApiResponse(responseCode = "204", description = "Nenhum material encontrado"),
        @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - permissões insuficientes"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<DetalhesMaterialOutput>> getEstoque() {
        log.info("Iniciando busca por todos os materiais do estoque");
        try {
            var materiais = service.getEstoque();
            log.info("Busca por todos os materiais concluída com sucesso. Encontrados {} materiais", materiais.size());
            if (materiais.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(materiais);
        } catch (Exception e) {
            log.error("Erro ao buscar todos os materiais do estoque", e);
            throw e;
        }
    }

    @GetMapping("/{nomeMaterial}")
    @Operation(summary = "Buscar material pelo nome")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Material encontrado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalhesMaterialOutput.class))),
        @ApiResponse(responseCode = "400", description = "Nome do material inválido"),
        @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - permissões insuficientes"),
        @ApiResponse(responseCode = "404", description = "Material não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<DetalhesMaterialOutput> getEstoqueByNome(@PathVariable @Valid String nomeMaterial) {
        log.info("Iniciando busca por material com nome: {}", nomeMaterial);

        if (nomeMaterial == null || nomeMaterial.trim().isEmpty()) {
            log.warn("Nome do material inválido fornecido para busca");
            return ResponseEntity.badRequest().body(null);
        }

        try {
            var material = service.getEstoqueByNome(nomeMaterial);
            log.info("Busca por material '{}' concluída com sucesso.", nomeMaterial);
            return ResponseEntity.ok(material);
        } catch (DependenciaNaoEncontradaException e) {
            log.warn("Material com nome '{}' não encontrado", nomeMaterial);
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            log.error("Erro ao buscar material com nome: {}", nomeMaterial, e);
            throw e;
        }
    }

    @PostMapping
    @Operation(summary = "Inserir material no estoque")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Material criado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalhesMaterialOutput.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou material já existe",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - permissões insuficientes"),
        @ApiResponse(responseCode = "409", description = "Conflito - Material já cadastrado com o mesmo nome",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<DetalhesMaterialOutput> postEstoque(@RequestBody @Valid CadastroMaterialInput estoque) {
        log.info("Iniciando cadastro de novo material no estoque: {}", estoque.nome());
        try {
            var novoMaterial = service.postEstoque(estoque);
            log.info("Material '{}' cadastrado com sucesso. ID: {}", novoMaterial.nome(), novoMaterial.id());
            return ResponseEntity.status(201).body(novoMaterial);
        } catch (Exception e) {
            log.error("Erro ao cadastrar material: {}", estoque.nome(), e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar material pelo ID")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Material atualizado com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalhesMaterialOutput.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos ou ID inválido",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - permissões insuficientes"),
        @ApiResponse(responseCode = "404", description = "Material não encontrado com o ID fornecido",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "409", description = "Conflito - Nome do material já existe",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<DetalhesMaterialOutput> putEstoqueById(@PathVariable Long id, @RequestBody @Valid CadastroMaterialInput estoque) {
        log.info("Iniciando atualização do material com ID: {}", id);
        try {
            DetalhesMaterialOutput novoMaterial = service.putEstoqueById(id, estoque);
            log.info("Material com ID {} atualizado com sucesso. Nome: {}", id, novoMaterial.nome());
            return ResponseEntity.status(200).body(novoMaterial);
        } catch (Exception e) {
            log.error("Erro ao atualizar material com ID: {}", id, e);
            throw e;
        }
    }

    @PatchMapping("/{id}/{quantidade}")
    @Operation(summary = "Atualizar quantidade do material pelo ID")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Quantidade do material atualizada com sucesso",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = DetalhesMaterialOutput.class))),
        @ApiResponse(responseCode = "400", description = "ID inválido ou quantidade inválida (deve ser positiva)",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - permissões insuficientes"),
        @ApiResponse(responseCode = "404", description = "Material não encontrado com o ID fornecido",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<DetalhesMaterialOutput> atualizarQuantidadeById(@PathVariable Long id, @PathVariable Double quantidade) {
        log.info("Iniciando atualização da quantidade do material com ID: {} para quantidade: {}", id, quantidade);
        try {
            DetalhesMaterialOutput novoMaterial = service.atualizarQuantidadeById(id, quantidade);
            log.info("Quantidade do material com ID {} atualizada com sucesso para: {}", id, quantidade);
            return ResponseEntity.status(200).body(novoMaterial);
        } catch (Exception e) {
            log.error("Erro ao atualizar quantidade do material com ID: {} para quantidade: {}", id, quantidade, e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar material pelo ID")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Material deletado com sucesso"),
        @ApiResponse(responseCode = "400", description = "ID inválido",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - permissões insuficientes"),
        @ApiResponse(responseCode = "404", description = "Material não encontrado com o ID fornecido",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "409", description = "Conflito - Material em uso e não pode ser deletado",
                content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<Void> deleteEstoqueById(@PathVariable Long id) {
        log.info("Iniciando exclusão do material com ID: {}", id);
        try {
            service.deleteEstoqueById(id);
            log.info("Material com ID {} excluído com sucesso", id);
            return ResponseEntity.status(204).body(null);
        } catch (Exception e) {
            log.error("Erro ao excluir material com ID: {}", id, e);
            throw e;
        }
    }
}

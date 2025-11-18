package hub.orcana.controller;

import hub.orcana.dto.orcamento.DetalhesOrcamentoOutput;
import hub.orcana.service.OrcamentoService;
import hub.orcana.tables.Orcamento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import hub.orcana.dto.orcamento.CadastroOrcamentoInput;

@Slf4j
@CrossOrigin(origins = "http://localhost:5174")
@RestController
@RequestMapping("/orcamento")
@Tag(name = "Orçamentos", description = "Gerenciamento de orçamentos de tatuagens")
public class OrcamentoController {
    private final OrcamentoService service;

    public OrcamentoController(OrcamentoService service) {
        this.service = service;
    }

    @PostMapping(path = "/cadastro", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Inserir orçamento no banco de dados e enviar e-mail de confirmação ao cliente",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Orçamento criado com sucesso",
                            content = @Content(schema = @Schema(example = "{\"success\": true, \"id\": \"ORC-123\", \"message\": \"Orçamento criado com sucesso\"}"))),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos, arquivo vazio ou erro no processamento",
                            content = @Content(schema = @Schema(example = "{\"message\": \"Dados inválidos\", \"errors\": {\"email\": \"E-mail é obrigatório\", \"ideia\": \"Ideia é obrigatória\"}}"),
                                    examples = {
                                            @ExampleObject(name = "Validação", value = "{\"message\": \"Dados inválidos\", \"errors\": {\"email\": \"E-mail é obrigatório\", \"ideia\": \"Ideia é obrigatória\"}}"),
                                            @ExampleObject(name = "Email inválido", value = "{\"message\": \"Dados inválidos\", \"errors\": {\"email\": \"E-mail deve ter formato válido\"}}"),
                                            @ExampleObject(name = "Campos obrigatórios", value = "{\"message\": \"Dados inválidos\", \"errors\": {\"ideia\": \"Ideia é obrigatória\", \"tamanho\": \"Tamanho é obrigatório\", \"cores\": \"Cores são obrigatórias\", \"localCorpo\": \"Local do corpo é obrigatório\"}}")
                                    })),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(schema = @Schema(example = "{\"timestamp\": \"2025-11-13T10:30:00Z\", \"status\": 500, \"error\": \"Internal Server Error\", \"message\": \"Erro interno do servidor\", \"path\": \"/orcamento\"}")))
            })
    public ResponseEntity<?> postOrcamento(@ModelAttribute @Valid CadastroOrcamentoInput dados) {

        log.info("Iniciando criação de novo orçamento: {}", dados);
        try {
            var novoOrcamento = service.postOrcamento(dados);
            log.info("Orçamento criado com sucesso. Código: {}", novoOrcamento.getCodigoOrcamento());
            return ResponseEntity.status(201).body(Map.of(
                    "success", true,
                    "codigo", novoOrcamento.getCodigoOrcamento(),
                    "message", "Orçamento criado com sucesso"
            ));
        } catch (Exception e) {
            log.error("Erro ao criar orçamento: {}", e.getMessage(), e);
            throw e; //
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos os orçamentos do sistema",
            description = "Retorna uma lista completa de todos os orçamentos cadastrados no sistema")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de orçamentos retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Orcamento.class))),
        @ApiResponse(responseCode = "204", description = "Nenhum orçamento encontrado"),
        @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - permissões insuficientes"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                    content = @Content(schema = @Schema(example = "{\"timestamp\": \"2025-11-13T10:30:00Z\", \"status\": 500, \"error\": \"Internal Server Error\", \"message\": \"Erro ao buscar orçamentos\", \"path\": \"/orcamento\"}")))
    })
    public ResponseEntity<List<DetalhesOrcamentoOutput>> getOrcamentos() {
        log.info("Iniciando busca por todos os orçamentos");
        try {
            List<DetalhesOrcamentoOutput> orcamentos = service.findAllOrcamentos();
            if (orcamentos.isEmpty()) {
                log.info("Nenhum orçamento encontrado");
                return ResponseEntity.noContent().build();
            } else {
                log.info("Retornando {} orçamentos encontrados", orcamentos.size());
                return ResponseEntity.ok(orcamentos);
            }
        } catch (Exception e) {
            log.error("Erro ao buscar orçamentos: {}", e.getMessage(), e);
            throw e;
        }
    }

}

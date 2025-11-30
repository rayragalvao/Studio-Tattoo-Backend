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

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar orçamentos de um usuário específico",
            description = "Retorna todos os orçamentos associados a um usuário")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de orçamentos retornada com sucesso"),
        @ApiResponse(responseCode = "204", description = "Nenhum orçamento encontrado para este usuário"),
        @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<DetalhesOrcamentoOutput>> getOrcamentosPorUsuario(@PathVariable Long usuarioId) {
        log.info("Iniciando busca por orçamentos do usuário ID: {}", usuarioId);
        try {
            List<DetalhesOrcamentoOutput> orcamentos = service.findOrcamentosByUsuarioId(usuarioId);
            if (orcamentos.isEmpty()) {
                log.info("Nenhum orçamento encontrado para o usuário ID: {}", usuarioId);
                return ResponseEntity.noContent().build();
            } else {
                log.info("Retornando {} orçamentos para o usuário ID: {}", orcamentos.size(), usuarioId);
                return ResponseEntity.ok(orcamentos);
            }
        } catch (Exception e) {
            log.error("Erro ao buscar orçamentos do usuário ID {}: {}", usuarioId, e.getMessage(), e);
            throw e;
        }
    }

    @PutMapping("/{codigo}")
    @Operation(summary = "Atualizar informações de um orçamento",
            description = "Permite atualizar dados do orçamento como tamanho, local do corpo, cores e descrição")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orçamento atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
        @ApiResponse(responseCode = "404", description = "Orçamento não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    @PutMapping("/{codigo}")
    @Operation(summary = "Atualizar orçamento com valor e tempo estimado",
            description = "Atualiza um orçamento existente com valor da tatuagem e tempo estimado da sessão")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Orçamento não encontrado"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> atualizarOrcamento(
            @PathVariable String codigo,
            @RequestBody Map<String, Object> dados) {
        log.info("Atualizando orçamento {}: {}", codigo, dados);
        try {
            Double tamanho = dados.get("tamanho") != null ? 
                    ((Number) dados.get("tamanho")).doubleValue() : null;
            String localCorpo = (String) dados.get("localCorpo");
            String cores = (String) dados.get("cores");
            String ideia = (String) dados.get("ideia");

            var orcamentoAtualizado = service.atualizarOrcamento(codigo, tamanho, localCorpo, cores, ideia);

        log.info("Atualizando orçamento: {} com dados: {}", codigo, dados);
        try {
            var orcamentoAtualizado = service.atualizarOrcamento(codigo, dados);
            log.info("Orçamento {} atualizado com sucesso", codigo);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Orçamento atualizado com sucesso",
                    "codigo", orcamentoAtualizado.getCodigoOrcamento()
                    "orcamento", orcamentoAtualizado
            ));
        } catch (RuntimeException e) {
            log.error("Erro ao atualizar orçamento {}: {}", codigo, e.getMessage());
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Erro inesperado ao atualizar orçamento {}: {}", codigo, e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Erro ao atualizar orçamento"
            ));
        }
    }

    @GetMapping("/{codigo}/tem-agendamento")
    @Operation(summary = "Verificar se orçamento possui agendamento",
            description = "Retorna se existe um agendamento vinculado ao orçamento")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verificação realizada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> verificarSeTemAgendamento(@PathVariable String codigo) {
        log.info("Verificando se orçamento {} possui agendamento", codigo);
        try {
            boolean temAgendamento = service.verificarSeTemAgendamento(codigo);
            return ResponseEntity.ok(Map.of(
                    "temAgendamento", temAgendamento
            ));
        } catch (Exception e) {
            log.error("Erro ao verificar agendamento do orçamento {}: {}", codigo, e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Erro ao verificar agendamento"
            ));
        }
    }

    @DeleteMapping("/{codigo}")
    @Operation(summary = "Excluir um orçamento",
            description = "Remove permanentemente um orçamento do sistema e seu agendamento relacionado (se houver)")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orçamento excluído com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
        @ApiResponse(responseCode = "404", description = "Orçamento não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> deletarOrcamento(@PathVariable String codigo) {
        log.info("Deletando orçamento: {}", codigo);
        try {
            service.deletarOrcamento(codigo);
            log.info("Orçamento {} deletado com sucesso", codigo);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Orçamento excluído com sucesso"
            ));
        } catch (RuntimeException e) {
            log.error("Erro ao deletar orçamento {}: {}", codigo, e.getMessage());
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            log.error("Erro inesperado ao deletar orçamento {}: {}", codigo, e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Erro ao excluir orçamento"
            ));
        }
    }

}
            log.error("Erro ao atualizar orçamento {}: {}", codigo, e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Erro ao atualizar orçamento: " + e.getMessage()
            ));
        }
    }

}

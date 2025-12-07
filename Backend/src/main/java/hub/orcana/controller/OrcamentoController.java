package hub.orcana.controller;

import hub.orcana.dto.orcamento.CadastroOrcamentoInput;
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
    @Operation(summary = "Inserir orçamento no banco de dados e enviar e-mail de confirmação ao cliente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orçamento criado com sucesso",
                    content = @Content(schema = @Schema(example = "{\"success\": true, \"id\": \"ORC-123\", \"message\": \"Orçamento criado com sucesso\"}"))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos, arquivo vazio ou erro no processamento",
                    content = @Content(schema = @Schema(example = "{\"message\": \"Dados inválidos\"}"))),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> postOrcamento(@ModelAttribute @Valid CadastroOrcamentoInput dados) {
        log.info("Iniciando criação de novo orçamento: {}", dados);
        try {
            Orcamento novoOrcamento = service.postOrcamento(dados);
            log.info("Orçamento criado com sucesso. Código: {}", novoOrcamento.getCodigoOrcamento());
            return ResponseEntity.status(201).body(Map.of(
                    "success", true,
                    "codigo", novoOrcamento.getCodigoOrcamento(),
                    "message", "Orçamento criado com sucesso"
            ));
        } catch (Exception e) {
            log.error("Erro ao criar orçamento: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping
    @Operation(summary = "Listar todos os orçamentos do sistema")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de orçamentos retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Orcamento.class))),
            @ApiResponse(responseCode = "204", description = "Nenhum orçamento encontrado"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado - permissões insuficientes"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<DetalhesOrcamentoOutput>> getOrcamentos() {
        log.info("Iniciando busca por todos os orçamentos");
        try {
            List<DetalhesOrcamentoOutput> orcamentos = service.findAllOrcamentos();
            if (orcamentos.isEmpty()) {
                log.info("Nenhum orçamento encontrado");
                return ResponseEntity.noContent().build();
            }
            log.info("Retornando {} orçamentos encontrados", orcamentos.size());
            return ResponseEntity.ok(orcamentos);
        } catch (Exception e) {
            log.error("Erro ao buscar orçamentos: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar orçamentos de um usuário específico")
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
            }
            log.info("Retornando {} orçamentos para o usuário ID: {}", orcamentos.size(), usuarioId);
            return ResponseEntity.ok(orcamentos);
        } catch (Exception e) {
            log.error("Erro ao buscar orçamentos do usuário ID {}: {}", usuarioId, e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/{codigo}")
    @Operation(summary = "Buscar orçamento por código")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<DetalhesOrcamentoOutput> getOrcamentoPorCodigo(@PathVariable String codigo) {
        log.info("Buscando orçamento com código: {}", codigo);
        try {
            DetalhesOrcamentoOutput orcamento = service.findByCodigo(codigo);
            return ResponseEntity.ok(orcamento);
        } catch (RuntimeException e) {
            log.error("Orçamento não encontrado: {}", codigo);
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            log.error("Erro ao buscar orçamento {}: {}", codigo, e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/{codigo}")
    @Operation(summary = "Atualizar informações de um orçamento")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orçamento atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
            @ApiResponse(responseCode = "404", description = "Orçamento não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> atualizarOrcamento(@PathVariable String codigo, @RequestBody Map<String, Object> dados) {
        log.info("Atualizando orçamento {}: {}", codigo, dados);
        try {
            DetalhesOrcamentoOutput orcamentoAtualizado = service.atualizarOrcamento(codigo, dados);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Orçamento atualizado com sucesso",
                    "codigo", orcamentoAtualizado.codigoOrcamento(),
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
    @Operation(summary = "Verificar se orçamento possui agendamento")
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
            return ResponseEntity.ok(Map.of("temAgendamento", temAgendamento));
        } catch (Exception e) {
            log.error("Erro ao verificar agendamento do orçamento {}: {}", codigo, e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Erro ao verificar agendamento"
            ));
        }
    }

    @DeleteMapping("/{codigo}")
    @Operation(summary = "Excluir um orçamento")
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

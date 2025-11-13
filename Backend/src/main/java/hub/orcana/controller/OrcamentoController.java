package hub.orcana.controller;

import hub.orcana.service.OrcamentoService;
import hub.orcana.tables.Orcamento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import hub.orcana.dto.DadosCadastroOrcamento;

@Slf4j
@CrossOrigin(origins = "http://localhost:5174")
@RestController
@RequestMapping("/orcamento")
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
    public ResponseEntity<?> postOrcamento(@ModelAttribute @Valid DadosCadastroOrcamento dados) {
        log.info(dados.toString());
        var novoOrcamento = service.postOrcamento(dados);
        return ResponseEntity.status(201).body(Map.of(
                "success", true,
                "id", novoOrcamento.getCodigoOrcamento(),
                "message", "Orçamento criado com sucesso"
        ));
    }

    @GetMapping
    @Operation(summary = "Listar todos os orçamentos do sistema",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de orçamentos retornada com sucesso"),
                    @ApiResponse(responseCode = "204", description = "Nenhum orçamento encontrado"),
                    @ApiResponse(responseCode = "500", description = "Erro interno do servidor",
                            content = @Content(schema = @Schema(example = "{\"timestamp\": \"2025-11-13T10:30:00Z\", \"status\": 500, \"error\": \"Internal Server Error\", \"message\": \"Erro ao buscar orçamentos\", \"path\": \"/orcamento\"}")))
            })
    public ResponseEntity<List<Orcamento>> getOrcamentos() {
        return ResponseEntity.ok(service.findAllOrcamentos());
    }

}

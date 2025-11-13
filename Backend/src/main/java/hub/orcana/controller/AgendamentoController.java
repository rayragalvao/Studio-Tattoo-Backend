package hub.orcana.controller;

import hub.orcana.dto.agendamento.AgendamentoDetalhadoDTO;
import hub.orcana.dto.agendamento.CadastroAgendamento;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import hub.orcana.service.AgendamentoService;
import hub.orcana.tables.Agendamento;
import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/agendamento")
@Tag(name = "Agendamentos", description = "API para gerenciamento de agendamentos")
public class AgendamentoController {

    private final AgendamentoService service;

    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    // ------------------ CRUD BÁSICO ------------------

    @GetMapping
    @Operation(summary = "Listar todos os agendamentos",
               description = "Retorna uma lista com todos os agendamentos cadastrados no sistema")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de agendamentos retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Agendamento.class))),
        @ApiResponse(responseCode = "204", description = "Nenhum agendamento encontrado"),
        @ApiResponse(responseCode = "400", description = "Erro interno do servidor ou parâmetros inválidos",
                    content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - permissões insuficientes"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<AgendamentoDetalhadoDTO>> getAgendamento() {
        log.info("Iniciando busca por todos os agendamentos");
        try {
            List<AgendamentoDetalhadoDTO> agenda = service.getAgendamentos();
            if (agenda.isEmpty()) {
                log.info("Nenhum agendamento encontrado");
                return ResponseEntity.status(204).body(null);
            } else {
                log.info("Retornando {} agendamentos encontrados", agenda.size());
                return ResponseEntity.status(200).body(agenda);
            }
        } catch (Exception e) {
            log.error("Erro ao buscar agendamentos: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Listar agendamento por ID",
               description = "Retorna um agendamento específico baseado no ID fornecido")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Agendamento encontrado com sucesso",
                    content = @Content(schema = @Schema(implementation = Agendamento.class))),
        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado com o ID fornecido",
                    content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "ID inválido ou parâmetros incorretos",
                    content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - permissões insuficientes"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<AgendamentoDetalhadoDTO> getAgendamentoPorId(@PathVariable Long id) {
        log.info("Buscando agendamento por ID: {}", id);
        try {
            AgendamentoDetalhadoDTO agendamento = service.getAgendamentoPorId(id);
            log.info("Agendamento encontrado com sucesso para ID: {}", id);
            return ResponseEntity.status(200).body(agendamento);
        } catch (IllegalArgumentException e) {
            log.warn("Erro de argumento inválido ao buscar agendamento ID {}: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/statusAtual/{status}")
    @Operation(summary = "Listar agendamentos por status",
               description = "Retorna uma lista de agendamentos filtrados pelo status fornecido")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de agendamentos com o status especificado",
                    content = @Content(schema = @Schema(implementation = Agendamento.class))),
        @ApiResponse(responseCode = "204", description = "Nenhum agendamento encontrado com o status especificado"),
        @ApiResponse(responseCode = "400", description = "Status inválido ou erro nos parâmetros",
                    content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - permissões insuficientes"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<AgendamentoDetalhadoDTO>> getAgendamentosByStatus(@PathVariable String status) {
        log.info("Buscando agendamentos por status: {}", status);
        try {
            List<AgendamentoDetalhadoDTO> sitStatus = service.getAgendamentosByStatus(status);
            if (sitStatus.isEmpty()) {
                log.info("Nenhum agendamento encontrado para o status: {}", status);
                return ResponseEntity.status(204).body(null);
            }
            log.info("Encontrados {} agendamentos com status: {}", sitStatus.size(), status);
            return ResponseEntity.ok(sitStatus);
        } catch (Exception e) {
            log.error("Erro ao buscar agendamentos por status '{}': {}", status, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "Inserir novo agendamento",
               description = "Cria um novo agendamento no sistema com os dados fornecidos")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Agendamento criado com sucesso",
                    content = @Content(schema = @Schema(implementation = Agendamento.class))),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou malformados",
                    content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "409", description = "Conflito - agendamento já existe ou horário indisponível",
                    content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - permissões insuficientes"),
        @ApiResponse(responseCode = "422", description = "Dados não processáveis - validação falhou"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<AgendamentoDetalhadoDTO> postAgendamento(@RequestBody CadastroAgendamento agendamento) {
        log.info("Iniciando criação de novo agendamento: {}", agendamento);
        try {
            AgendamentoDetalhadoDTO novaAgenda = service.postAgendamento(agendamento);
            log.info("Agendamento criado com sucesso: {}", novaAgenda);
            return ResponseEntity.status(201).body(novaAgenda);
        } catch (IllegalArgumentException e) {
            log.warn("Conflito ao criar agendamento: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao criar agendamento: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar agendamento pelo ID",
               description = "Atualiza completamente um agendamento existente baseado no ID fornecido")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Agendamento atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos ou malformados",
                    content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado com o ID fornecido",
                    content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - permissões insuficientes"),
        @ApiResponse(responseCode = "409", description = "Conflito - horário indisponível ou dados conflitantes"),
        @ApiResponse(responseCode = "422", description = "Dados não processáveis - validação falhou"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<AgendamentoDetalhadoDTO> putAgendamento(@PathVariable Long id, @RequestBody @Valid CadastroAgendamento agendamento) {
        log.info("Iniciando atualização do agendamento ID: {}", id);
        try {
            service.putAgendamentoById(id, agendamento);
            log.info("Agendamento ID {} atualizado com sucesso", id);
            return ResponseEntity.status(204).body(null);
        } catch (IllegalArgumentException e) {
            log.warn("Agendamento não encontrado para atualização. ID: {} - Erro: {}", id, e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao atualizar agendamento ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar agendamento pelo ID",
               description = "Remove permanentemente um agendamento do sistema baseado no ID fornecido")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Agendamento deletado com sucesso"),
        @ApiResponse(responseCode = "400", description = "ID inválido ou erro nos parâmetros",
                    content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado com o ID fornecido",
                    content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - permissões insuficientes"),
        @ApiResponse(responseCode = "409", description = "Conflito - agendamento não pode ser deletado devido a dependências"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<String> deleteAgendamento(@PathVariable Long id) {
        log.info("Iniciando exclusão do agendamento ID: {}", id);
        try {
            service.deleteAgendamentoById(id);
            log.info("Agendamento ID {} excluído com sucesso", id);
            return ResponseEntity.status(204).body(null);
        } catch (IllegalArgumentException e) {
            log.warn("Tentativa de exclusão de agendamento não encontrado. ID: {} - Erro: {}", id, e.getMessage());
            return ResponseEntity.status(404).body("Erro excluir agenda: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao excluir agendamento ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(400).body("Erro excluir agenda: " + e.getMessage());
        }
    }

    // ------------------ RELACIONAMENTOS ------------------

    @GetMapping("/detalhado/{id}")
    @Operation(summary = "Retorna agendamento com dados do usuário e orçamento",
               description = "Retorna um agendamento completo com informações detalhadas do usuário e orçamento associado")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Agendamento detalhado retornado com sucesso",
                    content = @Content(schema = @Schema(implementation = Agendamento.class))),
        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado com o ID fornecido",
                    content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "ID inválido ou erro nos parâmetros",
                    content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - permissões insuficientes"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<?> getAgendamentoCompleto(@PathVariable Long id) {
        log.info("Buscando agendamento completo com ID: {}", id);
        try {
            var agendamentoCompleto = service.getAgendamentoCompleto(id);
            log.info("Agendamento completo encontrado com sucesso para ID: {}", id);
            return ResponseEntity.ok(agendamentoCompleto);
        } catch (Exception e) {
            log.error("Erro ao buscar agendamento completo ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(400).body("Erro ao buscar agendamento detalhado: " + e.getMessage());
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar todos os agendamentos de um usuário",
               description = "Retorna uma lista com todos os agendamentos associados ao usuário especificado")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de agendamentos do usuário retornada com sucesso",
                    content = @Content(schema = @Schema(implementation = Agendamento.class))),
        @ApiResponse(responseCode = "204", description = "Nenhum agendamento encontrado para o usuário especificado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado com o ID fornecido",
                    content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "400", description = "ID de usuário inválido ou erro nos parâmetros",
                    content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - permissões insuficientes"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<List<AgendamentoDetalhadoDTO>> getAgendamentosPorUsuario(@PathVariable Long usuarioId) {
        log.info("Buscando agendamentos para usuário ID: {}", usuarioId);
        try {
            List<AgendamentoDetalhadoDTO> agendamentos = service.getAgendamentosPorUsuario(usuarioId);
            if (agendamentos.isEmpty()) {
                log.info("Nenhum agendamento encontrado para o usuário ID: {}", usuarioId);
                return ResponseEntity.noContent().build();
            } else {
                log.info("Encontrados {} agendamentos para o usuário ID: {}", agendamentos.size(), usuarioId);
                return ResponseEntity.ok(agendamentos);
            }
        } catch (Exception e) {
            log.error("Erro ao buscar agendamentos do usuário ID {}: {}", usuarioId, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}/orcamento/{orcamentoId}")
    @Operation(summary = "Atualiza o orçamento de um agendamento existente",
               description = "Associa um orçamento específico a um agendamento existente no sistema")
    @SecurityRequirement(name = "Bearer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orçamento do agendamento atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = Agendamento.class))),
        @ApiResponse(responseCode = "400", description = "IDs inválidos ou erro nos parâmetros",
                    content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "404", description = "Agendamento ou orçamento não encontrado com os IDs fornecidos",
                    content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "401", description = "Token de autenticação inválido ou expirado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - permissões insuficientes"),
        @ApiResponse(responseCode = "409", description = "Conflito - orçamento já associado a outro agendamento"),
        @ApiResponse(responseCode = "422", description = "Dados não processáveis - validação falhou"),
        @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    public ResponseEntity<AgendamentoDetalhadoDTO> atualizarOrcamentoDoAgendamento(
            @PathVariable Long id,
            @PathVariable String orcamentoId) {
        log.info("Iniciando associação do orçamento '{}' ao agendamento ID: {}", orcamentoId, id);
        try {
            var atualizado = service.atualizarOrcamento(id, orcamentoId);
            log.info("Orçamento '{}' associado com sucesso ao agendamento ID: {}", orcamentoId, id);
            return ResponseEntity.ok(atualizado);
        } catch (Exception e) {
            log.error("Erro ao associar orçamento '{}' ao agendamento ID {}: {}", orcamentoId, id, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
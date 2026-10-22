package hub.orcana.controller;

import hub.orcana.dto.historico.DetalhesHistoricoClienteOutput;
import hub.orcana.dto.historico.HistoricoClientesOutput;
import hub.orcana.service.HistoricoClientesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/historico-clientes")
@Tag(name = "Historico de clientes", description = "Sessoes concluidas e indicadores por cliente")
@SecurityRequirement(name = "Bearer")
public class HistoricoClientesController {

    private final HistoricoClientesService service;

    public HistoricoClientesController(HistoricoClientesService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar o historico consolidado de clientes")
    public ResponseEntity<HistoricoClientesOutput> listar(
            @RequestParam(required = false, defaultValue = "") String busca
    ) {
        return ResponseEntity.ok(service.listar(busca));
    }

    @GetMapping("/{clienteId}")
    @Operation(summary = "Detalhar o historico de sessoes de um cliente")
    public ResponseEntity<DetalhesHistoricoClienteOutput> detalhar(@PathVariable Long clienteId) {
        return ResponseEntity.ok(service.detalhar(clienteId));
    }
}

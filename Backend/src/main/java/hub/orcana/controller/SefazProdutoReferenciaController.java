package hub.orcana.controller;

import hub.orcana.service.SefazProdutoReferenciaService;
import hub.orcana.tables.SefazProdutoReferencia;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sefaz/produtos")
@Tag(name = "SEFAZ", description = "Consulta de produtos de referência da SEFAZ")
public class SefazProdutoReferenciaController {

    private final SefazProdutoReferenciaService service;

    public SefazProdutoReferenciaController(
            SefazProdutoReferenciaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar produtos de referência da SEFAZ")
    @SecurityRequirement(name = "Bearer")
    public ResponseEntity<List<SefazProdutoReferencia>> listarProdutos() {
        return ResponseEntity.ok(service.listarTodos());
    }
}
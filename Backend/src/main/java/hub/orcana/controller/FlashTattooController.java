package hub.orcana.controller;

import hub.orcana.dto.flash.AtualizacaoFlashInput;
import hub.orcana.dto.flash.CadastroFlashInput;
import hub.orcana.dto.flash.FlashOutput;
import hub.orcana.dto.flash.StatusFlashInput;
import hub.orcana.service.FlashTattooService;
import hub.orcana.tables.FlashTattoo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/flash-tattoos")
@Tag(name = "Flash Tattoos", description = "Catálogo de desenhos prontos para tatuagem")
public class FlashTattooController {
    private final FlashTattooService service;

    public FlashTattooController(FlashTattooService service) {
        this.service = service;
    }

    @Operation(summary = "Listar todos os flashes")
    @GetMapping
    public List<FlashOutput> listar() {
        return service.listar();
    }

    @Operation(summary = "Cadastrar um novo flash")
    @SecurityRequirement(name = "Bearer")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FlashOutput> cadastrar(
            @Valid @ModelAttribute CadastroFlashInput dados
    ) {
        FlashOutput flash = service.cadastrar(dados);
        return ResponseEntity.created(
                URI.create("/flash-tattoos/" + flash.id())
        ).body(flash);
    }

    @Operation(summary = "Editar um flash")
    @SecurityRequirement(name = "Bearer")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FlashOutput atualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute AtualizacaoFlashInput dados
    ) {
        return service.atualizar(id, dados);
    }

    @Operation(summary = "Marcar flash como utilizado ou disponível")
    @SecurityRequirement(name = "Bearer")
    @PatchMapping(value = "/{id}/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    public FlashOutput atualizarStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusFlashInput dados
    ) {
        return service.atualizarStatus(id, dados.aplicado());
    }

    @Operation(summary = "Excluir um flash")
    @SecurityRequirement(name = "Bearer")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Buscar flash por ID")
    @GetMapping("/{id}")
    public FlashOutput buscar(@PathVariable Long id) {
        return FlashOutput.from(service.buscar(id));
    }

    @Operation(summary = "Exibir foto do flash")
    @GetMapping("/{id}/foto")
    public ResponseEntity<Resource> foto(@PathVariable Long id) {
        FlashTattoo flash = service.buscar(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(flash.getTipoImagem()))
                .cacheControl(CacheControl.noCache())
                .body(service.foto(flash));
    }
}
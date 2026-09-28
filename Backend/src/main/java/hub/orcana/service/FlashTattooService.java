package hub.orcana.service;

import hub.orcana.dto.flash.CadastroFlashInput;
import hub.orcana.dto.flash.FlashOutput;
import hub.orcana.tables.FlashTattoo;
import hub.orcana.tables.repository.FlashTattooRepository;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class FlashTattooService {
    private static final long LIMITE_FOTO = 5L * 1024 * 1024;
    private static final Set<String> TIPOS = Set.of("image/jpeg", "image/png", "image/webp");
    private final Path pasta = Path.of("uploads", "flash").toAbsolutePath().normalize();
    private final FlashTattooRepository repository;

    public FlashTattooService(FlashTattooRepository repository) {
        this.repository = repository;
        try { Files.createDirectories(pasta); }
        catch (IOException e) { throw new IllegalStateException("Falha ao preparar pasta dos flashes", e); }
    }

    public List<FlashOutput> listar() {
        return repository.findAll().stream().map(FlashOutput::from).toList();
    }

    public FlashOutput cadastrar(CadastroFlashInput dados) {
        MultipartFile foto = dados.getFoto();
        if (foto == null || foto.isEmpty() || foto.getSize() > LIMITE_FOTO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Foto obrigatória, com até 5 MB");
        }
        String tipo = foto.getContentType();
        if (!TIPOS.contains(tipo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Use uma foto JPEG, PNG ou WebP");
        }
        String extensao = switch (tipo) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            default -> ".webp";
        };
        String nomeArquivo = UUID.randomUUID() + extensao;
        Path destino = pasta.resolve(nomeArquivo);
        try (var entrada = foto.getInputStream()) {
            Files.copy(entrada, destino);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao salvar foto", e);
        }
        try {
            FlashTattoo flash = new FlashTattoo(dados.getNome().trim(), dados.getEstilo().trim(),
                    dados.getPreco(), dados.getDescricao(), nomeArquivo, tipo);
            return FlashOutput.from(repository.save(flash));
        } catch (RuntimeException e) {
            try { Files.deleteIfExists(destino); } catch (IOException ignored) {}
            throw e;
        }
    }

    public FlashTattoo buscar(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Flash não encontrado"));
    }

    public Resource foto(FlashTattoo flash) {
        Path caminho = pasta.resolve(flash.getArquivoImagem()).normalize();
        if (!caminho.startsWith(pasta)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        try {
            Resource resource = new UrlResource(caminho.toUri());
            if (resource.isReadable()) return resource;
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Falha ao ler foto", e);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto não encontrada");
    }
}

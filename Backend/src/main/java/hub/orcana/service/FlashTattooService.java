package hub.orcana.service;

import hub.orcana.dto.flash.AtualizacaoFlashInput;
import hub.orcana.dto.flash.CadastroFlashInput;
import hub.orcana.dto.flash.FlashOutput;
import hub.orcana.tables.FlashTattoo;
import hub.orcana.tables.repository.FlashTattooRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class FlashTattooService {
    private static final Logger log = LoggerFactory.getLogger(FlashTattooService.class);
    private static final long LIMITE_FOTO = 5L * 1024 * 1024;
    private static final Set<String> TIPOS = Set.of("image/jpeg", "image/png", "image/webp");

    private final Path pasta = Path.of("uploads", "flash").toAbsolutePath().normalize();
    private final FlashTattooRepository repository;

    private record FotoSalva(String nomeArquivo, String tipo) {}

    public FlashTattooService(FlashTattooRepository repository) {
        this.repository = repository;
        try {
            Files.createDirectories(pasta);
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao preparar pasta dos flashes", e);
        }
    }

    public List<FlashOutput> listar() {
        return repository.findAll().stream().map(FlashOutput::from).toList();
    }

    public FlashTattoo buscar(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Flash não encontrado")
        );
    }

    public FlashOutput cadastrar(CadastroFlashInput dados) {
        FotoSalva foto = salvarFoto(dados.getFoto());

        try {
            FlashTattoo flash = new FlashTattoo(
                    dados.getNome().trim(),
                    dados.getEstilo().trim(),
                    dados.getPreco(),
                    dados.getDescricao(),
                    foto.nomeArquivo(),
                    foto.tipo()
            );
            return FlashOutput.from(repository.save(flash));
        } catch (RuntimeException e) {
            removerFoto(foto.nomeArquivo());
            throw e;
        }
    }

    public FlashOutput atualizar(Long id, AtualizacaoFlashInput dados) {
        FlashTattoo flash = buscar(id);
        String fotoAnterior = flash.getArquivoImagem();
        FotoSalva fotoNova = null;

        if (dados.getFoto() != null && !dados.getFoto().isEmpty()) {
            fotoNova = salvarFoto(dados.getFoto());
        }

        flash.atualizar(
                dados.getNome().trim(),
                dados.getEstilo().trim(),
                dados.getPreco(),
                dados.getDescricao()
        );

        if (fotoNova != null) {
            flash.atualizarFoto(fotoNova.nomeArquivo(), fotoNova.tipo());
        }

        FlashOutput resultado;
        try {
            resultado = FlashOutput.from(repository.save(flash));
        } catch (RuntimeException e) {
            if (fotoNova != null) removerFoto(fotoNova.nomeArquivo());
            throw e;
        }

        if (fotoNova != null) removerFoto(fotoAnterior);
        return resultado;
    }

    public FlashOutput atualizarStatus(Long id, boolean aplicado) {
        FlashTattoo flash = buscar(id);
        flash.setAplicado(aplicado);
        return FlashOutput.from(repository.save(flash));
    }

    public void excluir(Long id) {
        FlashTattoo flash = buscar(id);
        String nomeArquivo = flash.getArquivoImagem();

        try {
            repository.delete(flash);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Este flash possui registros vinculados e não pode ser excluído.",
                    e
            );
        }

        removerFoto(nomeArquivo);
    }

    private FotoSalva salvarFoto(MultipartFile foto) {
        if (foto == null || foto.isEmpty() || foto.getSize() > LIMITE_FOTO) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Foto obrigatória, com até 5 MB"
            );
        }

        String tipo = foto.getContentType();
        if (tipo == null || !TIPOS.contains(tipo)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Use uma foto JPEG, PNG ou WebP"
            );
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
            removerFoto(nomeArquivo);
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Falha ao salvar foto",
                    e
            );
        }

        return new FotoSalva(nomeArquivo, tipo);
    }

    private void removerFoto(String nomeArquivo) {
        Path caminho = pasta.resolve(nomeArquivo).normalize();
        if (!caminho.startsWith(pasta) || caminho.equals(pasta)) {
            log.warn("Caminho inválido ao remover foto de flash");
            return;
        }

        try {
            Files.deleteIfExists(caminho);
        } catch (IOException e) {
            log.warn("Não foi possível remover a foto do flash: {}", nomeArquivo, e);
        }
    }

    public Resource foto(FlashTattoo flash) {
        Path caminho = pasta.resolve(flash.getArquivoImagem()).normalize();
        if (!caminho.startsWith(pasta)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        try {
            Resource resource = new UrlResource(caminho.toUri());
            if (resource.isReadable()) return resource;
        } catch (IOException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Falha ao ler foto",
                    e
            );
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto não encontrada");
    }
}
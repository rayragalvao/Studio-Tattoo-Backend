package hub.orcana.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record DadosCadastroOrcamento(
        String codigoOrcamento,

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail deve ter formato válido")
        String email,

        @NotBlank(message = "Ideia é obrigatória")
        String ideia,

        @NotNull(message = "Tamanho é obrigatório")
        @Positive(message = "Tamanho deve ser um valor positivo")
        Double tamanho,

        @NotBlank(message = "Cores são obrigatórias")
        String cores,

        @NotBlank(message = "Local do corpo é obrigatório")
        String localCorpo,

        List<MultipartFile> imagemReferencia
) {}

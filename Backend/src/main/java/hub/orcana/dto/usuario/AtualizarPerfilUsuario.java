package hub.orcana.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AtualizarPerfilUsuario(
        @NotBlank
        String nome,

        @Pattern(regexp = "^$|^\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}$")
        String telefone,

        String dtNasc
) {
}

package hub.orcana.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.Date;

public record CadastroUsuario(
        @NotBlank
        String nome,

        @NotBlank
        @Email
        String email,

        @Pattern(regexp = "^$|^\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}$")
        String telefone,

        @NotBlank
        @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
            message = "A senha deve ter no mínimo 8 caracteres, contendo letras maiúsculas, minúsculas, números e caracteres especiais."
        )
        String senha,
        Date dtNasc,
        boolean isAdmin
) {
}
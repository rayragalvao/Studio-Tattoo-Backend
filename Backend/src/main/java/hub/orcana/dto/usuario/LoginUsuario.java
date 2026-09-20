package hub.orcana.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginUsuario (
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail deve ter formato valido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String senha
){}
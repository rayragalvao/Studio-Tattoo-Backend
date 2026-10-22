package hub.orcana.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AlterarSenhaUsuario(
        @NotBlank(message = "Senha atual e obrigatoria")
        String senhaAtual,

        @NotBlank(message = "Nova senha e obrigatoria")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$",
                message = "Nova senha deve ter ao menos 8 caracteres, incluindo maiuscula, minuscula, numero e caractere especial"
        )
        String novaSenha,

        @NotBlank(message = "Confirmacao da nova senha e obrigatoria")
        String confirmarNovaSenha
) {
}

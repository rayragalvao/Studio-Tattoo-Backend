package hub.orcana.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record MensagemChatInput(
        @NotBlank String mensagem,
        List<HistoricoMensagem> historico
) {
    public record HistoricoMensagem(
            @NotBlank String role, // "user" ou "model"
            @NotBlank String texto
    ) {}
}
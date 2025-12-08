package hub.orcana.dto.agendamento;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record MaterialUsadoRequest(
        @NotNull(message = "O ID do material é obrigatório")
        Long materialId,

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantidade
) {}
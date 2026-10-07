package hub.orcana.dto.agendamento;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record FinalizarAgendamentoInput(
        @NotNull @Positive Integer tempoDuracao,
        @NotNull Boolean pagamentoFeito,
        String formaPagamento,
        @NotEmpty List<@NotNull @Valid MaterialUsadoRequest> materiais
) {}
package hub.orcana.dto.agendamento;

import hub.orcana.tables.StatusAgendamento;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusAgendamentoInput(
        @NotNull StatusAgendamento status
) {}
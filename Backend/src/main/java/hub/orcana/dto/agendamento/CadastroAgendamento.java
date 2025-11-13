package hub.orcana.dto.agendamento;

import hub.orcana.tables.StatusAgendamento;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CadastroAgendamento (
    @NotNull
    String emailUsuario,
    @NotNull
    String codigoOrcamento,
    @NotNull
    LocalDateTime dataHora,
    StatusAgendamento status
) {
}

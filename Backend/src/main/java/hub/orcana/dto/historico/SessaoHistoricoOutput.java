package hub.orcana.dto.historico;

import java.time.LocalDateTime;

public record SessaoHistoricoOutput(
        Long id,
        LocalDateTime dataHora,
        String procedimento,
        String estilo,
        Integer duracaoMinutos,
        Double valor
) {
}

package hub.orcana.dto.agendamento;

import java.time.LocalDateTime;

public record DetalhesAgendamentoOutput(
        Long id,
        LocalDateTime dataHora,
        String status,
        String nomeUsuario,
        String emailUsuario,
        String ideia,
        Double tamanho,
        String cores,
        String localCorpo
) {}
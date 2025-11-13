package hub.orcana.dto.agendamento;

import hub.orcana.tables.Orcamento;
import hub.orcana.tables.Usuario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record AgendamentoDetalhadoDTO(
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
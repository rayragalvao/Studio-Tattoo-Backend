package hub.orcana.dto.agendamento;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CompletarAgendamentoInput(
        @NotNull(message = "Tempo de duração é obrigatório")
        @Positive(message = "Tempo de duração deve ser positivo")
        Integer tempoDuracao,

        @NotNull(message = "Informação de pagamento é obrigatória")
        Boolean pagamentoFeito,

        String formaPagamento // obrigatório se pagamentoFeito = true
) {
    public CompletarAgendamentoInput {
        if (Boolean.TRUE.equals(pagamentoFeito) && (formaPagamento == null || formaPagamento.isBlank())) {
            throw new IllegalArgumentException("Forma de pagamento é obrigatória quando pagamento foi feito");
        }
    }
}
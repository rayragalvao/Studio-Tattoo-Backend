package hub.orcana.dto.agendamento;

import java.time.LocalDateTime;
import java.util.List;

public record DetalhesAgendamentoOutput(
        Long id,
        LocalDateTime dataHora,
        String status,
        String nomeUsuario,
        String emailUsuario,
        String codigoOrcamento,
        String ideia,
        Double tamanho,
        String cores,
        String localCorpo,
        String observacoes,
        List<String> imagemReferencia,
        Integer tempoDuracao,
        Boolean pagamentoFeito,
        String formaPagamento
) {}
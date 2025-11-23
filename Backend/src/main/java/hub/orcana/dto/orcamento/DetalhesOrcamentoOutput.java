package hub.orcana.dto.orcamento;

import java.sql.Time;
import java.util.List;

public record DetalhesOrcamentoOutput (
        Long id,
        String codigoOrcamento,
        String nome,
        String email,
        String ideia,
        Double valor,
        Double tamanho,
        String estilo,
        String cores,
        Time tempo,
        String localCorpo,
        List<String> urlsImagensReferencia,
        String status
) {
}

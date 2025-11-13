package hub.orcana.dto.orcamento;

import java.util.List;

public record DetalhesOrcamentoOutput (
        String codigoOrcamento,
        String nome,
        String email,
        String ideia,
        Double tamanho,
        String cores,
        String localCorpo,
        List<String> urlsImagensReferencia
) {
}

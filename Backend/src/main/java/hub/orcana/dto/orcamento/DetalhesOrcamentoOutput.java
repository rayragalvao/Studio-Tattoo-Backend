package hub.orcana.dto.orcamento;

import hub.orcana.tables.StatusOrcamento;
import java.sql.Time;
import java.util.List;

public record DetalhesOrcamentoOutput (
        String codigoOrcamento,
        String nome,
        String email,
        String ideia,
        Double tamanho,
        String cores,
        String localCorpo,
        List<String> imagemReferencia,
        Double valor,
        Time tempo,
        StatusOrcamento status
) {
    public DetalhesOrcamentoOutput(String codigoOrcamento, String nome, String email, String ideia, 
                                    Double tamanho, String cores, String localCorpo, List<String> imagemReferencia) {
        this(codigoOrcamento, nome, email, ideia, tamanho, cores, localCorpo, imagemReferencia, null, null, null);
    }
}

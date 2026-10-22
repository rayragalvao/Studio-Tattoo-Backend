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
        StatusOrcamento status,
        String estilo
) {
    public DetalhesOrcamentoOutput(String codigoOrcamento, String nome, String email, String ideia, Double tamanho, String cores, String localCorpo, List<String> imagemReferencia, Double valor, Time tempo, StatusOrcamento status) {
        this(codigoOrcamento, nome, email, ideia, tamanho, cores, localCorpo, imagemReferencia, valor, tempo, status, null);
    }

    public DetalhesOrcamentoOutput(String codigoOrcamento, String nome, String email, String ideia, Double tamanho, String cores, String localCorpo, List<String> imagemReferencia, Double valor, Time tempo, StatusOrcamento status, String estilo) {
        this.codigoOrcamento = codigoOrcamento;
        this.nome = nome;
        this.email = email;
        this.ideia = ideia;
        this.tamanho = tamanho;
        this.cores = cores;
        this.localCorpo = localCorpo;
        this.imagemReferencia = imagemReferencia;
        this.valor = valor;
        this.tempo = tempo;
        this.status = status;
        this.estilo = estilo;
    }
}
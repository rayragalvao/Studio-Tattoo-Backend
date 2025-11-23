package hub.orcana.tables;

public enum StatusOrcamento {
    AGUARDANDO_RESPOSTA("Aguardando Resposta"),
    RESPONDIDO("Respondido"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusOrcamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

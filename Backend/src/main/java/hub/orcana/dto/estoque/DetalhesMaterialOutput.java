package hub.orcana.dto.estoque;

public record DetalhesMaterialOutput(
        Long id,
        String nome,
        Double quantidade,
        String unidadeMedida,
        Double minAviso
) {
    public DetalhesMaterialOutput(Long id, String nome, Double quantidade, String unidadeMedida, Double minAviso) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
        this.unidadeMedida = unidadeMedida;
        this.minAviso = minAviso;
    }
}

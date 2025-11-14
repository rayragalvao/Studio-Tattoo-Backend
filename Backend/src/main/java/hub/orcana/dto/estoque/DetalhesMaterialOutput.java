package hub.orcana.dto.estoque;

public record DetalhesMaterialOutput(
        Long id,
        String nome,
        Double quantidade,
        String unidadeMedida,
        Double minAviso
) {}

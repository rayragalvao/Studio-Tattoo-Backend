package hub.orcana.dto.predicao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PredicaoOutput(
        @JsonProperty("preco_sugerido")
        Double precoSugerido,

        @JsonProperty("tempo_sugerido")
        Double tempoSugerido
) {}

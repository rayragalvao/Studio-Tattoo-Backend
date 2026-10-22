package hub.orcana.dto.predicao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PredicaoInput (
        String descricao,
        String estilo,

        @JsonProperty("tamanho_cm")
        Double tamanhoCm,

        Integer vermelho,
        Integer preto,
        String local
){}
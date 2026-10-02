package hub.orcana.dto.predicao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PredicaoInput (
        String descricao,

        @JsonProperty("tamanho_cm")
        Double tamanhoCm,

        Integer vermelho,
        Integer preto,
        String local
){}

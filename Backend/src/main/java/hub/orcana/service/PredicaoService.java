package hub.orcana.service;

import hub.orcana.dto.orcamento.CadastroOrcamentoInput;
import hub.orcana.dto.predicao.PredicaoInput;
import hub.orcana.dto.predicao.PredicaoOutput;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Locale;

@Service
public class PredicaoService {
    private final RestClient restClient;

    public PredicaoService(
            RestClient.Builder restClientBuilder,
            @Value("${ml.service.url}") String mlServiceUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(mlServiceUrl)
                .build();
    }

    public PredicaoOutput prever(CadastroOrcamentoInput dados) {
        String cores = dados.cores().toLowerCase(Locale.ROOT);

        boolean temVermelho = Arrays.stream(cores.split(","))
                .map(String::trim)
                .anyMatch("vermelho"::equals);

        boolean temPreto = Arrays.stream(cores.split(","))
                .map(String::trim)
                .anyMatch("preto"::equals);

        PredicaoInput input = new PredicaoInput(
                dados.ideia(),
                dados.tamanho(),
                temVermelho ? 1 : 0,
                temPreto ? 1 : 0,
                dados.localCorpo()
        );

        return restClient.post()
                .uri("/predict")
                .body(input)
                .retrieve()
                .body(PredicaoOutput.class);
    }
}

package hub.orcana.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailClientService {

    @Value("${email.service.url:http://localhost:8081}")
    private String emailServiceUrl;

    @Value("${email.service.timeout:30}")
    private int timeoutSeconds;

    private RestClient restClient;

    @PostConstruct
    public void init() {
        this.restClient = RestClient.builder()
                .baseUrl(emailServiceUrl)
                .build();
        log.info("EmailClientService inicializado com URL: {}", emailServiceUrl);
    }

    public boolean enviarEmailTemplate(String destinatario, String template, Map<String, String> variaveis) {
        // Validação de parâmetros
        if (!StringUtils.hasText(destinatario)) {
            log.error("Destinatário não pode ser vazio");
            return false;
        }

        if (!StringUtils.hasText(template)) {
            log.error("Template não pode ser vazio");
            return false;
        }

        try {
            var body = Map.of(
                    "destinatario", destinatario,
                    "template", template,
                    "variaveis", variaveis != null ? variaveis : Map.of()
            );

            log.debug("Enviando e-mail para {} usando template {}", destinatario, template);

            var response = restClient.post()
                    .uri("/emails/template")
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("E-mail enviado com sucesso para: {}", destinatario);
                return true;
            } else {
                log.warn("Falha ao enviar e-mail. Status: {}", response.getStatusCode());
                return false;
            }

        } catch (RestClientException e) {
            log.error("Erro de comunicação com microserviço de e-mail: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Erro inesperado ao enviar e-mail: {}", e.getMessage(), e);
            return false;
        }
    }

        public boolean testarEnvioTemplate() {
            // Exemplo de uso
            Map<String, String> variaveis = Map.of(
                    "nomeCliente", "João Silva",
                    "codigoOrcamento", "ORC-001",
                    "valor", "1500.00",
                    "tempo", "7"
            );

            return enviarEmailTemplate(
                    "linyaalvesm@gmail.com",
                    "orcamento_cliente",
                    variaveis
            );
        }

}


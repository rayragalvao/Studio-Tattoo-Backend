package hub.orcana.service;

import hub.orcana.dto.chat.MensagemChatInput;
import hub.orcana.dto.chat.RespostaChatOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import jakarta.annotation.PostConstruct;

import java.util.*;

@Slf4j
@Service
public class GeminiService {

    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @PostConstruct
    public void init() {
        log.info(
                "Chave Gemini carregada: {}",
                apiKey != null && !apiKey.isBlank() ? "SIM" : "NÃO"
        );
    }

    private static final String SYSTEM_PROMPT = """
        Você é o assistente do Orcana, um sistema de gestão para estúdio de tatuagem.
        Ajude o tatuador com dúvidas sobre agendamentos, orçamentos, estoque e clientes.
        Responda sempre em português brasileiro, de forma direta e objetiva.
        Não invente dados — se não souber, diga que não tem essa informação no sistema.
        """;

    public GeminiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RespostaChatOutput enviarMensagem(MensagemChatInput input) {
        try {
            String url = apiUrl + "?key=" + apiKey;

            List<Map<String, Object>> contents = new ArrayList<>();

            // Adiciona contexto do sistema como primeira mensagem do usuário
            contents.add(Map.of(
                    "role", "user",
                    "parts", List.of(Map.of("text", SYSTEM_PROMPT))
            ));
            contents.add(Map.of(
                    "role", "model",
                    "parts", List.of(Map.of("text", "Entendido! Sou o assistente do Orcana. Como posso ajudar?"))
            ));

            // Adiciona histórico da conversa
            if (input.historico() != null) {
                for (var msg : input.historico()) {
                    contents.add(Map.of(
                            "role", msg.role(),
                            "parts", List.of(Map.of("text", msg.texto()))
                    ));
                }
            }

            // Adiciona mensagem atual
            contents.add(Map.of(
                    "role", "user",
                    "parts", List.of(Map.of("text", input.mensagem()))
            ));

            Map<String, Object> body = Map.of("contents", contents);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);


            long inicio = System.currentTimeMillis();

            log.info("Enviando solicitação para o Gemini...");

            ResponseEntity<Map> response;

            try {
                response = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        new HttpEntity<>(body, headers),
                        Map.class
                );

                log.info(
                        "Gemini respondeu em {} ms",
                        System.currentTimeMillis() - inicio
                );

            } catch (Exception e) {
                log.error(
                        "Erro após {} ms ao chamar o Gemini",
                        System.currentTimeMillis() - inicio,
                        e
                );
                throw e;
            }

            String resposta = extrairTextoResposta(response.getBody());
            return new RespostaChatOutput(resposta, true);


        } catch (Exception e) {
            log.error("Erro ao chamar Gemini API: {}", e.getMessage(), e);
            return new RespostaChatOutput("Desculpe, ocorreu um erro ao processar sua mensagem.", false);
        }
    }

    @SuppressWarnings("unchecked")
    private String extrairTextoResposta(Map<?, ?> body) {
        try {
            var candidates = (List<Map<String, Object>>) body.get("candidates");
            var content = (Map<String, Object>) candidates.get(0).get("content");
            var parts = (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            log.error("Erro ao extrair texto da resposta Gemini: {}", e.getMessage());
            return "Não consegui processar a resposta.";
        }
    }
}
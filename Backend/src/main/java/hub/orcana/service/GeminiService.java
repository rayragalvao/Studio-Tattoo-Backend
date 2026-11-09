package hub.orcana.service;

import hub.orcana.dto.chat.MensagemChatInput;
import hub.orcana.dto.chat.RespostaChatOutput;
import hub.orcana.tables.StatusAgendamento;
import hub.orcana.tables.StatusOrcamento;
import hub.orcana.tables.repository.AgendamentoRepository;
import hub.orcana.tables.repository.EstoqueRepository;
import hub.orcana.tables.repository.OrcamentoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class GeminiService {

    private final RestTemplate restTemplate;
    private final AgendamentoRepository agendamentoRepository;
    private final EstoqueRepository estoqueRepository;
    private final OrcamentoRepository orcamentoRepository;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private static final String SYSTEM_PROMPT = """
        Você é o assistente do Orcana, um sistema de gestão para estúdio de tatuagem.
        Ajude o tatuador com dúvidas sobre agendamentos, orçamentos, estoque e clientes.
        Responda sempre em português brasileiro, de forma direta e objetiva.
        Não invente dados — use apenas as informações do sistema fornecidas abaixo.
        Se uma informação não estiver nos dados, diga que não tem essa informação disponível.
        """;

    public GeminiService(RestTemplate restTemplate,
                         AgendamentoRepository agendamentoRepository,
                         EstoqueRepository estoqueRepository,
                         OrcamentoRepository orcamentoRepository) {
        this.restTemplate = restTemplate;
        this.agendamentoRepository = agendamentoRepository;
        this.estoqueRepository = estoqueRepository;
        this.orcamentoRepository = orcamentoRepository;
    }

    public RespostaChatOutput enviarMensagem(MensagemChatInput input) {
        int tentativas = 3;
        for (int i = 0; i < tentativas; i++) {
            try {
                String contexto = montarContexto();
                String promptCompleto = SYSTEM_PROMPT + "\n\n## Dados atuais do sistema:\n" + contexto;

                List<Map<String, Object>> contents = new ArrayList<>();
                contents.add(Map.of("role", "user", "parts", List.of(Map.of("text", promptCompleto))));
                contents.add(Map.of("role", "model", "parts", List.of(Map.of("text", "Entendido! Sou o assistente do Orcana. Como posso ajudar?"))));

                if (input.historico() != null) {
                    for (var msg : input.historico()) {
                        contents.add(Map.of("role", msg.role(), "parts", List.of(Map.of("text", msg.texto()))));
                    }
                }
                contents.add(Map.of("role", "user", "parts", List.of(Map.of("text", input.mensagem()))));

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                long inicio = System.currentTimeMillis();
                ResponseEntity<Map> response = restTemplate.exchange(
                        apiUrl, HttpMethod.POST,
                        new HttpEntity<>(Map.of("contents", contents), headers),
                        Map.class
                );
                log.info("Gemini respondeu em {} ms", System.currentTimeMillis() - inicio);
                return new RespostaChatOutput(extrairTextoResposta(response.getBody()), true);

            } catch (Exception e) {
                boolean is503 = e.getMessage() != null && e.getMessage().contains("503");
                if (is503 && i < tentativas - 1) {
                    log.warn("Gemini 503, tentativa {}/{} - aguardando {}ms", i + 1, tentativas, 1500L * (i + 1));
                    try { Thread.sleep(1500L * (i + 1)); } catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                } else {
                    log.error("Gemini erro [tipo={}]: {}", e.getClass().getSimpleName(), e.getMessage());
                    return new RespostaChatOutput("Serviço temporariamente indisponível. Tente novamente.", false);
                }
            }
        }
        return new RespostaChatOutput("Serviço temporariamente indisponível. Tente novamente.", false);
    }

    private String montarContexto() {
        StringBuilder sb = new StringBuilder();
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime em7Dias = agora.plusDays(7);

        try {
            var agendamentos = agendamentoRepository
                    .findByDataHoraBetweenAndStatusNot(agora, em7Dias, StatusAgendamento.CANCELADO);
            sb.append("### Agendamentos (próximos 7 dias)\n");
            if (agendamentos.isEmpty()) {
                sb.append("Nenhum agendamento nos próximos 7 dias.\n");
            } else {
                agendamentos.forEach(a -> sb.append(String.format(
                        "- ID %d | %s | Status: %s | Cliente: %s | Orçamento: %s | Pagamento: %s\n",
                        a.getId(),
                        a.getDataHora().format(FORMATTER),
                        a.getStatus(),
                        a.getUsuario() != null ? a.getUsuario().getNome() : "N/A",
                        a.getOrcamento() != null ? a.getOrcamento().getCodigoOrcamento() : "N/A",
                        Boolean.TRUE.equals(a.getPagamentoFeito()) ? "Feito" : "Pendente"
                )));
            }
        } catch (Exception e) {
            log.warn("Erro ao carregar agendamentos para contexto: {}", e.getMessage());
            sb.append("### Agendamentos\nIndisponível.\n");
        }

        try {
            var itensBaixos = estoqueRepository.findAllByQuantidadeLessThanMinAviso();
            sb.append("\n### Estoque — Itens para repor\n");
            if (itensBaixos.isEmpty()) {
                sb.append("Nenhum item abaixo do estoque mínimo.\n");
            } else {
                itensBaixos.forEach(m -> sb.append(String.format(
                        "- %s | Atual: %.2f %s | Mínimo: %.2f ⚠️\n",
                        m.getNome(), m.getQuantidade(), m.getUnidadeMedida(), m.getMinAviso()
                )));
            }
        } catch (Exception e) {
            log.warn("Erro ao carregar estoque para contexto: {}", e.getMessage());
            sb.append("\n### Estoque\nIndisponível.\n");
        }

        try {
            var orcamentos = orcamentoRepository.findAll().stream()
                    .filter(o -> StatusOrcamento.PENDENTE.equals(o.getStatus()))
                    .toList();
            sb.append("\n### Orçamentos Pendentes\n");
            if (orcamentos.isEmpty()) {
                sb.append("Nenhum orçamento pendente.\n");
            } else {
                orcamentos.forEach(o -> sb.append(String.format(
                        "- %s | Cliente: %s (%s) | Ideia: %s | Tamanho: %.1f cm | Valor: R$ %.2f\n",
                        o.getCodigoOrcamento(),
                        o.getNome(),
                        o.getEmail(),
                        o.getIdeia(),
                        o.getTamanho() != null ? o.getTamanho() : 0.0,
                        o.getValor() != null ? o.getValor() : 0.0
                )));
            }
        } catch (Exception e) {
            log.warn("Erro ao carregar orçamentos para contexto: {}", e.getMessage());
            sb.append("\n### Orçamentos\nIndisponível.\n");
        }

        return sb.toString();
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
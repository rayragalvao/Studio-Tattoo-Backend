package hub.orcana.controller;

import hub.orcana.dto.chat.MensagemChatInput;
import hub.orcana.dto.chat.RespostaChatOutput;
import hub.orcana.service.GeminiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "http://localhost:5174")
@Tag(name = "Chat IA", description = "Assistente inteligente do Orcana")
@SecurityRequirement(name = "bearer-key")
public class ChatController {

    private final GeminiService geminiService;

    public ChatController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/mensagem")
    @Operation(summary = "Enviar mensagem para o assistente IA")
    public ResponseEntity<RespostaChatOutput> enviarMensagem(
            @RequestBody @Valid MensagemChatInput input) {
        log.info("Nova mensagem recebida no chat IA");
        RespostaChatOutput resposta = geminiService.enviarMensagem(input);
        return ResponseEntity.ok(resposta);
    }
}
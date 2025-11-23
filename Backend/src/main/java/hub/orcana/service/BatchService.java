package hub.orcana.service;

import hub.orcana.dto.estoque.DetalhesMaterialOutput;
import hub.orcana.tables.repository.EstoqueRepository;
import hub.orcana.tables.repository.UsuarioRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class BatchService {

    EstoqueRepository estoqueRepository;
    EmailService emailService;
    UsuarioRepository usuarioRepository;

    @Scheduled(cron = "0 0 7 * * *", zone = "America/Sao_Paulo")
    public void verificarEstoque() {
        log.info("Iniciando verificação automática de estoque baixo");

        try {
            List<DetalhesMaterialOutput> materiais = estoqueRepository.findAllByQuantidadeLessThanMinAviso()
                    .stream().map(item -> new DetalhesMaterialOutput(
                            item.getId(),
                            item.getNome(),
                            item.getQuantidade(),
                            item.getUnidadeMedida(),
                            item.getMinAviso()
                    )).toList();

            log.info("Encontrados {} materiais com estoque baixo", materiais.size());

            String assunto;
            String texto;

            if (materiais.isEmpty()) {
                assunto = "Estoque OK";
                texto = "Todos os materiais estão com o estoque acima do nível mínimo de aviso.";
                log.info("Todos os materiais estão com estoque adequado");

                enviarEmailParaTodosAdmins(assunto, texto);
            } else {
                assunto = "Estoque Baixo";
                StringBuilder textoBuilder = new StringBuilder("Os seguintes itens estão com o estoque abaixo do nível mínimo de aviso:\n\n");

                log.warn("Materiais com estoque baixo encontrados:");
                for (DetalhesMaterialOutput material : materiais) {
                    String itemInfo = "- " + material.nome() + ": " + material.quantidade() + " " + material.unidadeMedida() + " (Mínimo: " + material.minAviso() + ")\n";
                    textoBuilder.append(itemInfo);
                    log.warn("Estoque baixo: {} - Atual: {} {}, Mínimo: {}",
                            material.nome(), material.quantidade(), material.unidadeMedida(), material.minAviso());
                }

                texto = textoBuilder.toString();
                enviarEmailParaTodosAdmins(assunto, texto);
            }

            log.info("Verificação de estoque concluída com sucesso");

        } catch (Exception e) {
            log.error("Erro durante a verificação automática de estoque", e);
        }
    }

    public void enviarEmailParaTodosAdmins(String assunto, String texto) {
        log.info("Iniciando envio de emails para administradores. Assunto: {}", assunto);

        try {
            List<String> destinatarios = usuarioRepository.findAllByIsAdmin(true).stream()
                    .map(hub.orcana.tables.Usuario::getEmail)
                    .toList();

            log.info("Encontrados {} administradores para envio de email", destinatarios.size());

            if (destinatarios.isEmpty()) {
                log.warn("Nenhum administrador encontrado para envio de email");
                return;
            }

            destinatarios.forEach(destinatario -> {
                try {
                    emailService.enviarTextoSimples(destinatario, assunto, texto);
                    log.debug("Email enviado com sucesso para: {}", destinatario);
                } catch (Exception e) {
                    log.error("Erro ao enviar email para: {}", destinatario, e);
                }
            });

            log.info("Processo de envio de emails concluído para {} destinatários", destinatarios.size());

        } catch (Exception e) {
            log.error("Erro durante o processo de envio de emails para administradores", e);
        }
    }
}

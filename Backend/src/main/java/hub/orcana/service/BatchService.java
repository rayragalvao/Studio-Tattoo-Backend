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
    //@Scheduled(cron = "0 38 19 * * *", zone = "America/Sao_Paulo")
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

            String nomeTemplate;
            StringBuilder texto = new StringBuilder();

            if (materiais.isEmpty()) {
                nomeTemplate = "estoque_ok";
                log.info("Todos os materiais estão com estoque adequado");

                try {
                emailService.enviarEmailParaTodosAdminsEstoqueOk(nomeTemplate);
                } catch (Exception e) {
                    log.error("Erro ao enviar emails para administradores", e);
                }
            } nomeTemplate = "estoque_baixo_observer";

            for (DetalhesMaterialOutput material : materiais) {
                String itemInfo = "- " + material.nome() + ": " + material.quantidade() + " " + material.unidadeMedida() + " (Mínimo: " + material.minAviso() + ")\n";
                texto.append(itemInfo);
                log.warn("Estoque baixo: {} - Atual: {} {}, Mínimo: {}",
                        material.nome(), material.quantidade(), material.unidadeMedida(), material.minAviso());
            }

            log.info("Iniciando envio de emails para administradores. Materiais com estoque baixo.");

            try {
                emailService.enviarEmailParaTodosAdminsEstoqueBaixo(nomeTemplate, texto.toString());
            } catch (Exception e) {
                log.error("Erro ao enviar emails para administradores", e);
            }
            log.info("Verificação de estoque concluída com sucesso");
        } catch (Exception e) {
            log.error("Erro durante a verificação automática de estoque", e);
        }
    }
}

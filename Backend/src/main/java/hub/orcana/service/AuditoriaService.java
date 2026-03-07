package hub.orcana.service;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * A09:2021 - Falhas de Log e Monitoramento de Segurança.
 *
 * Centraliza o registro de eventos críticos de auditoria de segurança:
 *  - Tentativas de login (sucesso e falha)
 *  - Acessos negados (403 Forbidden)
 *  - Modificações em dados sensíveis
 *
 * Os logs são gravados via SLF4J e podem ser direcionados para arquivos,
 * sistemas SIEM ou qualquer appender configurado no logback-spring.xml.
 */
@Service
public class AuditoriaService {

    private static final Logger AUDIT_LOG = LoggerFactory.getLogger("AUDITORIA_SEGURANCA");

    // ─── Autenticação ────────────────────────────────────────────────────────

    /**
     * Registra uma tentativa de login bem-sucedida.
     *
     * @param usuario e-mail / username que realizou o login
     */
    public void registrarLoginSucesso(String usuario) {
        AUDIT_LOG.info("[LOGIN_SUCESSO] usuario='{}' ip='{}' userAgent='{}'",
                usuario, obterIpRequisicao(), obterUserAgent());
    }

    /**
     * Registra uma tentativa de login malsucedida.
     *
     * @param usuario e-mail / username que tentou o login
     * @param motivo  descrição resumida da falha
     */
    public void registrarLoginFalha(String usuario, String motivo) {
        AUDIT_LOG.warn("[LOGIN_FALHA] usuario='{}' motivo='{}' ip='{}' userAgent='{}'",
                usuario, motivo, obterIpRequisicao(), obterUserAgent());
    }

    // ─── Autorização ─────────────────────────────────────────────────────────

    /**
     * Registra um acesso negado (403).
     *
     * @param usuario   principal que tentou o acesso (e-mail ou "anonimo")
     * @param recurso   URL ou recurso solicitado
     */
    public void registrarAcessoNegado(String usuario, String recurso) {
        AUDIT_LOG.warn("[ACESSO_NEGADO] usuario='{}' recurso='{}' ip='{}' userAgent='{}'",
                usuario, recurso, obterIpRequisicao(), obterUserAgent());
    }

    // ─── Dados Sensíveis ─────────────────────────────────────────────────────

    /**
     * Registra uma modificação em dados sensíveis (criação, atualização ou exclusão).
     *
     * @param executor   usuário que realizou a ação
     * @param acao       ex.: "CRIAR_USUARIO", "ATUALIZAR_PERFIL", "EXCLUIR_ESTOQUE"
     * @param entidade   nome da entidade afetada
     * @param idEntidade identificador da entidade afetada
     */
    public void registrarModificacaoDados(String executor, String acao, String entidade, Object idEntidade) {
        AUDIT_LOG.info("[MODIFICACAO_DADOS] executor='{}' acao='{}' entidade='{}' id='{}' ip='{}'",
                executor, acao, entidade, idEntidade, obterIpRequisicao());
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private String obterIpRequisicao() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return "N/A";
            HttpServletRequest request = attrs.getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            return (ip != null && !ip.isBlank()) ? ip.split(",")[0].trim() : request.getRemoteAddr();
        } catch (Exception e) {
            return "N/A";
        }
    }

    private String obterUserAgent() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return "N/A";
            String ua = attrs.getRequest().getHeader("User-Agent");
            return ua != null ? ua : "N/A";
        } catch (Exception e) {
            return "N/A";
        }
    }
}

package hub.orcana.service;

import hub.orcana.observer.OrcamentoObserver;
import hub.orcana.tables.repository.TemplateEmailRepository;
import hub.orcana.tables.repository.UsuarioRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import hub.orcana.observer.EstoqueObserver;
import hub.orcana.tables.Orcamento;

import java.util.List;

@Service
public class EmailService implements EstoqueObserver, OrcamentoObserver {

    private final JavaMailSender mailSender;
    private final UsuarioRepository usuarioRepository;
    private final TemplateEmailRepository templateEmailRepository;

    public EmailService(JavaMailSender mailSender, UsuarioRepository usuarioRepository, TemplateEmailRepository templateEmailRepository) {
        this.mailSender = mailSender;
        this.usuarioRepository = usuarioRepository;
        this.templateEmailRepository = templateEmailRepository;
    }

    public void enviarTextoSimples(String destinatario, String assunto, String texto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("orcanatechschool@gmail.com"); // e-mail da aplicação Brevo
        message.setTo(destinatario);
        message.setSubject(assunto);
        message.setText(texto);

        mailSender.send(message);
    }

    public void enviaEmailNovoOrcamento(String emailCliente, String nomeCliente, String codigoOrcamento) {
        if (emailCliente == null || emailCliente.isBlank()) {
            throw new IllegalArgumentException("Destinatário inválido para envio de e-mail.");
        }

        String assunto = templateEmailRepository.findByNomeTemplate("orcamento_cliente")
                .map(template -> template.getAssunto())
                .orElseThrow(() -> new IllegalStateException("Template 'orcamento_cliente' não encontrado"));

        String textoInicial = templateEmailRepository.findByNomeTemplate("orcamento_cliente")
                .map(template -> template.getCorpoEmail().toString())
                .orElseThrow(() -> new IllegalStateException("Template 'orcamento_cliente' não encontrado"));

        String textoFinal = textoInicial
                .replace("${nomeCliente}", nomeCliente)
                .replace("${codigoOrcamento}", codigoOrcamento);

        enviarTextoSimples(emailCliente, assunto, textoFinal);
    }

    @Override
    public void updateOrcamento(Orcamento orcamento) {
        List<String> emailTatuador = usuarioRepository.getEmailByIsAdminTrue();

        enviaEmailNovoOrcamento(orcamento.getEmail(), orcamento.getNome(), orcamento.getCodigoOrcamento());
        enviaEmailParaTatuador(orcamento, emailTatuador);
    }

    private void enviaEmailParaTatuador(Orcamento orcamento, List<String> emailTatuador) {
        String assunto = templateEmailRepository.findByNomeTemplate("orcamento_tatuador")
                .map(template -> template.getAssunto())
                .orElseThrow(() -> new IllegalStateException("Template 'orcamento_tatuador' não encontrado"));

        String textoInicial = templateEmailRepository.findByNomeTemplate("orcamento_tatuador")
                .map(template -> template.getCorpoEmail().toString())
                .orElseThrow(() -> new IllegalStateException("Template 'orcamento_tatuador' não encontrado"));

        String nomeAdmin = usuarioRepository.getNomeByIsAdminTrue().stream().findFirst()
                .orElse("Administrador");

        String textoFinal = textoInicial
                .replace("${nomeAdmin}", nomeAdmin)
                .replace("${codigoOrcamento}", orcamento.getCodigoOrcamento())
                .replace("${nomeCliente}", orcamento.getNome())
                .replace("${emailCliente}", orcamento.getEmail())
                .replace("${ideia}", orcamento.getIdeia())
                .replace("${tamanho}", String.format("%.2f", orcamento.getTamanho()))
                .replace("${cores}", orcamento.getCores())
                .replace("${localCorpo}", orcamento.getLocalCorpo())
                .replace("${imagens}", String.valueOf(orcamento.getImagemReferencia().size()));

        emailTatuador.forEach(email -> {
            enviarTextoSimples(email, assunto, textoFinal);
        });

    }

    @Override
    public void updateEstoque(String materialNome, Double quantidadeAtual, Double minAviso) {
        if (minAviso == null || quantidadeAtual > minAviso) {
            return;
        }

        List<String> emailTatuador = usuarioRepository.getEmailByIsAdminTrue();
        String assunto = "ALERTA CRÍTICO DE ESTOQUE: " + materialNome;
        String texto = String.format(
                "Atenção! O material '%s' atingiu o limite crítico.\n" +
                        "Quantidade atual: %.2f %s. O limite mínimo definido é %.2f.\n" +
                        "Por favor, providencie a reposição imediatamente.",
                materialNome, quantidadeAtual, "unidades/ml/g", minAviso
        );

        emailTatuador.forEach(email -> {
            enviarTextoSimples(email, assunto, texto);
        });
    }
}


package hub.orcana.service;

import hub.orcana.observer.OrcamentoObserver;
import hub.orcana.tables.repository.TemplateEmailRepository;
import hub.orcana.tables.repository.UsuarioRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import hub.orcana.observer.EstoqueObserver;
import hub.orcana.tables.Orcamento;

import java.sql.Time;
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

    // código base para envio de e-mail
    public void enviarTextoSimples(String destinatario, String assunto, String texto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("orcanatechschool@gmail.com"); // e-mail da aplicação Brevo
        message.setTo(destinatario);
        message.setSubject(assunto);
        message.setText(texto);

        mailSender.send(message);
    }

    // quando um novo orçamento é criado, envia e-mail para o cliente
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

    // quando um novo orçamento é criado, envia e-mail para o tatuador (admin)
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

    // quando o estoque de um material atinge o limite mínimo, envia e-mail de alerta para o tatuador (admin)
    @Override
    public void updateEstoque(String materialNome, Double quantidadeAtual, Double minAviso) {
        if (minAviso == null || quantidadeAtual > minAviso) {
            return;
        }

        List<String> emailTatuador = usuarioRepository.getEmailByIsAdminTrue();

        String assuntoInicial = templateEmailRepository.findByNomeTemplate("estoque_baixo")
                .map(template -> template.getAssunto())
                .orElseThrow(() -> new IllegalStateException("Template 'estoque_critico' não encontrado"));

        String assuntoFinal = assuntoInicial.replace("${nomeMaterial}", materialNome);

        String textoInicial = templateEmailRepository.findByNomeTemplate("estoque_baixo")
                .map(template -> template.getCorpoEmail().toString())
                .orElseThrow(() -> new IllegalStateException("Template 'estoque_critico' não encontrado"));

        String textoFinal = textoInicial
                .replace("${nomeMaterial}", materialNome)
                .replace("${qtdAtual}", String.format("%.2f", quantidadeAtual))
                .replace("${limite}", String.format("%.2f", minAviso));

        emailTatuador.forEach(email -> {
            enviarTextoSimples(email, assuntoFinal, textoFinal);
        });
    }

    public void enviaEmailOrcamentoAprovado(String email, String nome, String codigoOrcamento, Double valor, Time tempo) {
        String assuntoInicial = templateEmailRepository.findByNomeTemplate("orcamento_aprovado")
                .map(template -> template.getAssunto())
                .orElseThrow(() -> new IllegalStateException("Template 'orcamento_aprovado' não encontrado"));

        String assuntoFinal = assuntoInicial.replace("${codigoOrcamento}", codigoOrcamento);

        String textoInicial = templateEmailRepository.findByNomeTemplate("orcamento_aprovado")
                .map(template -> template.getCorpoEmail().toString())
                .orElseThrow(() -> new IllegalStateException("Template 'orcamento_aprovado' não encontrado"));

        String textoFinal = textoInicial
                .replace("${nomeCliente}", nome)
                .replace("${codigoOrcamento}", codigoOrcamento)
                .replace("${valor}", String.format("R$ %.2f", valor))
                .replace("${tempo}", tempo.toString());

        enviarTextoSimples(email, assuntoFinal, textoFinal);
    }
}


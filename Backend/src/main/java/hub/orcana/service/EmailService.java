package hub.orcana.service;

import hub.orcana.observer.OrcamentoObserver;
import hub.orcana.tables.repository.UsuarioRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import hub.orcana.observer.EstoqueObserver;
import hub.orcana.tables.Orcamento;

@Service
public class EmailService implements EstoqueObserver, OrcamentoObserver {

    private final JavaMailSender mailSender;
    private final UsuarioRepository usuarioRepository;

    public EmailService(JavaMailSender mailSender, UsuarioRepository usuarioRepository) {
        this.mailSender = mailSender;
        this.usuarioRepository = usuarioRepository;
    }

//    private String templateEmail = "<!DOCTYPE html><html lang=\"pt-br\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>[ASSUNTO DO SEU E-MAIL]</title><link rel=\"preconnect\" href=\"https://fonts.googleapis.com\"><link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>";

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

        String assunto = "Confirmação de Recebimento de Orçamento - Júpiter Frito";
        String textoInicial = "Olá $nomeCliente, recebemos sua solicitação de orçamento e " +
                "os detalhes já estão sendo analisados. Em breve, entraremos em contato com você.\n\n" +
                "Não esqueça de anotar o código do seu orçamento para futuras referências: $codigoOrcamento\n\n" +
                "Obrigado por escolher a Júpiter Frito! :) \n\n" +
                "Atenciosamente,\n\n" +
                "Equipe Júpiter Frito";

        String textoFinal = textoInicial
                .replace("$nomeCliente", nomeCliente)
                .replace("$codigoOrcamento", codigoOrcamento);

        enviarTextoSimples(emailCliente, assunto, textoFinal);
    }

    @Override
    public void updateOrcamento(Orcamento orcamento) {
        String emailTatuador = usuarioRepository.getEmailByIsAdminTrue();

        enviaEmailNovoOrcamento(orcamento.getEmail(), orcamento.getNome(), orcamento.getCodigoOrcamento());
        enviaEmailParaTatuador(orcamento, emailTatuador);
    }

    private void enviaEmailParaTatuador(Orcamento orcamento, String emailTatuador) {
        String assunto = "Novo Orçamento Recebido: " + orcamento.getCodigoOrcamento();
        String texto = String.format(
                "Um novo orçamento foi enviado:\n\n" +
                        "Código: %s\n" +
                        "Email do Cliente: %s\n" +
                        "Ideia: %s\n" +
                        "Tamanho: %.2f\n" +
                        "Cores: %s\n" +
                        "Local do Corpo: %s\n" +
                        "Imagens: %d anexos (verifique a pasta de uploads).\n\n" +
                        "Acesse o painel para análise.",
                orcamento.getCodigoOrcamento(),
                orcamento.getEmail(),
                orcamento.getIdeia(),
                orcamento.getTamanho(),
                orcamento.getCores(),
                orcamento.getLocalCorpo(),
                orcamento.getImagemReferencia().size()
        );

        enviarTextoSimples(emailTatuador, assunto, texto);
    }

    @Override
    public void updateEstoque(String materialNome, Double quantidadeAtual, Double minAviso) {
        if (minAviso == null || quantidadeAtual > minAviso) {
            return;
        }

        String emailTatuador = usuarioRepository.getEmailByIsAdminTrue();
        String assunto = "ALERTA CRÍTICO DE ESTOQUE: " + materialNome;
        String texto = String.format(
                "Atenção! O material '%s' atingiu o limite crítico.\n" +
                        "Quantidade atual: %.2f %s. O limite mínimo definido é %.2f.\n" +
                        "Por favor, providencie a reposição imediatamente.",
                materialNome, quantidadeAtual, "unidades/ml/g", minAviso
        );

        enviarTextoSimples(emailTatuador, assunto, texto);
    }
}


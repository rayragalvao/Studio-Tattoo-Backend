package hub.orcana.service;

import hub.orcana.observer.AgendamentoObserver;
import hub.orcana.observer.EstoqueObserver;
import hub.orcana.observer.OrcamentoObserver;
import hub.orcana.tables.Agendamento;
import hub.orcana.tables.Orcamento;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailService implements EstoqueObserver, OrcamentoObserver, AgendamentoObserver {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarTextoSimples(String destinatario, String assunto, String texto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("orcanatechschool@gmail.com");
        message.setTo(destinatario);
        message.setSubject(assunto);
        message.setText(texto);
        mailSender.send(message);
    }

    @Override
    public void updateAgendamento(Agendamento agendamento, String acao) {
        switch (acao) {
            case "CRIADO":
                enviaEmailNovoAgendamento(agendamento);
                break;

            case "CANCELADO":
                enviaEmailAgendamentoCancelado(agendamento);
                break;

            default:
                if (acao.startsWith("STATUS_ALTERADO_")) {
                    enviaEmailMudancaStatus(agendamento, acao);
                }
                break;
        }
    }

    private void enviaEmailNovoAgendamento(Agendamento agendamento) {
        String emailCliente = agendamento.getUsuario().getEmail();
        String nomeCliente = agendamento.getUsuario().getNome();
        String dataFormatada = agendamento.getDataHora()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String horaFormatada = agendamento.getDataHora()
                .format(DateTimeFormatter.ofPattern("HH:mm"));

        String assunto = "Confirmação de Agendamento - Júpiter Frito";
        String texto = String.format(
                "Olá %s!\n\n" +
                        "Seu agendamento foi confirmado com sucesso! 🎉\n\n" +
                        "📅 Data: %s\n" +
                        "🕐 Horário: %s\n" +
                        "📋 Código do Orçamento: %s\n" +
                        "📍 Status: %s\n\n" +
                        "💡 Dica: Chegue 10 minutos antes do horário marcado.\n\n" +
                        "Estamos ansiosos para realizar sua tatuagem!\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe Júpiter Frito",
                nomeCliente,
                dataFormatada,
                horaFormatada,
                agendamento.getOrcamento().getCodigoOrcamento(),
                agendamento.getStatus().name()
        );

        enviarTextoSimples(emailCliente, assunto, texto);

        // Também notifica o tatuador
        enviaEmailParaTatuadorNovoAgendamento(agendamento);
    }

    private void enviaEmailParaTatuadorNovoAgendamento(Agendamento agendamento) {
        String emailTatuador = "nicollas.bpereira@sptech.school"; // Email do gestor

        String dataFormatada = agendamento.getDataHora()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"));

        String assunto = "Novo Agendamento Confirmado - ID: " + agendamento.getId();
        String texto = String.format(
                "Um novo agendamento foi confirmado!\n\n" +
                        "👤 Cliente: %s\n" +
                        "📧 Email: %s\n" +
                        "📞 Telefone: %s\n" +
                        "📅 Data/Hora: %s\n" +
                        "📋 Código Orçamento: %s\n" +
                        "💡 Ideia: %s\n" +
                        "📏 Tamanho: %.2f cm\n" +
                        "🎨 Cores: %s\n" +
                        "📍 Local: %s\n\n" +
                        "Acesse o painel para mais detalhes.",
                agendamento.getUsuario().getNome(),
                agendamento.getUsuario().getEmail(),
                agendamento.getUsuario().getTelefone(),
                dataFormatada,
                agendamento.getOrcamento().getCodigoOrcamento(),
                agendamento.getOrcamento().getIdeia(),
                agendamento.getOrcamento().getTamanho(),
                agendamento.getOrcamento().getCores(),
                agendamento.getOrcamento().getLocalCorpo()
        );

        enviarTextoSimples(emailTatuador, assunto, texto);
    }

    private void enviaEmailAgendamentoCancelado(Agendamento agendamento) {
        String emailCliente = agendamento.getUsuario().getEmail();
        String nomeCliente = agendamento.getUsuario().getNome();
        String dataFormatada = agendamento.getDataHora()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm"));

        String assunto = "Agendamento Cancelado - Júpiter Frito";
        String texto = String.format(
                "Olá %s,\n\n" +
                        "Seu agendamento do dia %s foi cancelado.\n\n" +
                        "📋 Código do Orçamento: %s\n\n" +
                        "Se você deseja reagendar, entre em contato conosco.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe Júpiter Frito",
                nomeCliente,
                dataFormatada,
                agendamento.getOrcamento().getCodigoOrcamento()
        );

        enviarTextoSimples(emailCliente, assunto, texto);
    }

    private void enviaEmailMudancaStatus(Agendamento agendamento, String acao) {
        String emailCliente = agendamento.getUsuario().getEmail();
        String nomeCliente = agendamento.getUsuario().getNome();

        // Extrai status anterior e novo do ação
        String[] partes = acao.split("_");
        String statusAnterior = partes.length > 3 ? partes[2] : "DESCONHECIDO";
        String novoStatus = partes.length > 4 ? partes[4] : agendamento.getStatus().name();

        String assunto = "Atualização do seu Agendamento - Júpiter Frito";
        String texto = String.format(
                "Olá %s!\n\n" +
                        "O status do seu agendamento foi atualizado.\n\n" +
                        "📅 Data: %s\n" +
                        "📋 Código: %s\n" +
                        "🔄 Status Anterior: %s\n" +
                        "✅ Novo Status: %s\n\n" +
                        "Qualquer dúvida, estamos à disposição!\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe Júpiter Frito",
                nomeCliente,
                agendamento.getDataHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'às' HH:mm")),
                agendamento.getOrcamento().getCodigoOrcamento(),
                statusAnterior,
                novoStatus
        );

        enviarTextoSimples(emailCliente, assunto, texto);
    }

    public void enviaEmailNovoOrcamento(String emailCliente, String codigoOrcamento) {
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
                .replace("$nomeCliente", emailCliente)
                .replace("$codigoOrcamento", codigoOrcamento);

        enviarTextoSimples(emailCliente, assunto, textoFinal);
    }

    @Override
    public void updateOrcamento(Orcamento orcamento) {
        enviaEmailNovoOrcamento(orcamento.getEmail(), orcamento.getCodigoOrcamento());
        enviaEmailParaTatuadorOrcamento(orcamento);
    }

    private void enviaEmailParaTatuadorOrcamento(Orcamento orcamento) {
        String emailTatuador = "nicollas.bpereira@sptech.school.com";

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

        String destinatario = "nicollas.bpereira@sptech.school";
        String assunto = "ALERTA CRÍTICO DE ESTOQUE: " + materialNome;
        String texto = String.format(
                "Atenção! O material '%s' atingiu o limite crítico.\n" +
                        "Quantidade atual: %.2f %s. O limite mínimo definido é %.2f.\n" +
                        "Por favor, providencie a reposição imediatamente.",
                materialNome, quantidadeAtual, "unidades/ml/g", minAviso
        );

        enviarTextoSimples(destinatario, assunto, texto);
    }
}
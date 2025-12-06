package hub.orcana.service;

import hub.orcana.observer.AgendamentoObserver;
import hub.orcana.observer.EstoqueObserver;
import hub.orcana.observer.OrcamentoObserver;
import hub.orcana.tables.repository.TemplateEmailRepository;
import hub.orcana.tables.repository.UsuarioRepository;
import hub.orcana.tables.Agendamento;
import hub.orcana.tables.Orcamento;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EmailService implements EstoqueObserver, OrcamentoObserver, AgendamentoObserver {

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

    public void enviaEmailNovoOrcamento(String emailCliente, String codigoOrcamento, String nomeCliente) {
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
        enviaEmailNovoOrcamento(orcamento.getEmail(), orcamento.getCodigoOrcamento(), orcamento.getNome());
        enviaEmailParaTatuador(orcamento, usuarioRepository.getEmailByIsAdminTrue());
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
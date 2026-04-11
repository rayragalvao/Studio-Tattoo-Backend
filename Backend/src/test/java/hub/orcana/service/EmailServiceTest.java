package hub.orcana.service;

import hub.orcana.tables.*;
import hub.orcana.tables.repository.TemplateEmailRepository;
import hub.orcana.tables.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TemplateEmailRepository templateEmailRepository;

    @InjectMocks
    private EmailService emailService;

    private Usuario usuario;
    private Orcamento orcamento;
    private Agendamento agendamento;
    private List<Usuario> admins;
    private List<String> emailsAdmins;

    @BeforeEach
    void setUp() {
        // Setup Usuario
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        usuario.setTelefone("(11) 99999-9999");

        // Setup Orçamento
        orcamento = new Orcamento("ORC123", "João Silva", "joao@email.com",
                "Dragão nas costas", 20.5, "Preto e Vermelho", "Costas",
                Arrays.asList("imagem1.jpg", "imagem2.jpg"));

        // Setup Agendamento
        agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.setDataHora(LocalDateTime.of(2026, 3, 15, 14, 30));
        agendamento.setStatus(StatusAgendamento.PENDENTE);
        agendamento.setUsuario(usuario);
        agendamento.setOrcamento(orcamento);

        // Setup Admins
        Usuario admin1 = new Usuario();
        admin1.setEmail("admin1@tattoo.com");
        admin1.setNome("Admin 1");

        Usuario admin2 = new Usuario();
        admin2.setEmail("admin2@tattoo.com");
        admin2.setNome("Admin 2");

        admins = Arrays.asList(admin1, admin2);
        emailsAdmins = Arrays.asList("admin1@tattoo.com", "admin2@tattoo.com");
    }

    // ------------------ TESTES MÉTODO enviarTextoSimples ------------------

    @Test
    @DisplayName("Deve enviar texto simples com sucesso")
    void deveEnviarTextoSimplesComSucesso() {
        // Arrange
        String destinatario = "teste@email.com";
        String assunto = "Assunto Teste";
        String texto = "Texto do email";

        // Act
        emailService.enviarTextoSimples(destinatario, assunto, texto);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertEquals("orcanatechschool@gmail.com", capturedMessage.getFrom());
        assertArrayEquals(new String[]{destinatario}, capturedMessage.getTo());
        assertEquals(assunto, capturedMessage.getSubject());
        assertEquals(texto, capturedMessage.getText());
    }

    // ------------------ TESTES MÉTODO enviaEmailNovoOrcamento ------------------

    @Test
    @DisplayName("Deve enviar email de novo orçamento com sucesso")
    void deveEnviarEmailNovoOrcamentoComSucesso() {
        // Arrange
        String emailCliente = "cliente@email.com";
        String nomeCliente = "Cliente Teste";
        String codigoOrcamento = "ORC456";

        TemplateEmail template = new TemplateEmail();
        template.setAssunto("Novo Orçamento - ORC456");
        template.setCorpoEmail("Olá ${nomeCliente}, seu orçamento ORC456 foi criado.");

        when(templateEmailRepository.findByNomeTemplate("orcamento_cliente"))
                .thenReturn(Optional.of(template));

        // Act
        emailService.enviaEmailNovoOrcamento(emailCliente, nomeCliente, codigoOrcamento);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertEquals("Novo Orçamento - ORC456", capturedMessage.getSubject());
        assertNotNull(capturedMessage.getText());
        assertTrue(capturedMessage.getText().contains("Cliente Teste"));
        assertTrue(capturedMessage.getText().contains("ORC456"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email cliente for nulo")
    void deveLancarExcecaoQuandoEmailClienteForNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.enviaEmailNovoOrcamento(null, "Cliente", "ORC123")
        );

        assertEquals("Destinatário inválido para envio de e-mail.", exception.getMessage());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email cliente for vazio")
    void deveLancarExcecaoQuandoEmailClienteForVazio() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.enviaEmailNovoOrcamento("", "Cliente", "ORC123")
        );

        assertEquals("Destinatário inválido para envio de e-mail.", exception.getMessage());
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando template não for encontrado")
    void deveLancarExcecaoQuandoTemplateNaoForEncontrado() {
        // Arrange
        when(templateEmailRepository.findByNomeTemplate("orcamento_cliente"))
                .thenReturn(Optional.empty());

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> emailService.enviaEmailNovoOrcamento("cliente@email.com", "Cliente", "ORC123")
        );

        assertEquals("Template 'orcamento_cliente' não encontrado", exception.getMessage());
    }

    // ------------------ TESTES MÉTODO updateOrcamento ------------------

    @Test
    @DisplayName("Deve processar atualização de orçamento enviando emails para cliente e tatuadores")
    void deveProcessarAtualizacaoOrcamentoEnviandoEmails() {
        // Arrange
        setupTemplatesMocks();
        when(usuarioRepository.getEmailByIsAdminTrue()).thenReturn(emailsAdmins);
        when(usuarioRepository.getNomeByIsAdminTrue()).thenReturn(List.of("Admin Principal"));

        // Act
        emailService.updateOrcamento(orcamento);

        // Assert
        verify(mailSender, times(3)).send(any(SimpleMailMessage.class)); // 1 cliente + 2 admins
        verify(templateEmailRepository, times(2)).findByNomeTemplate("orcamento_cliente");
        verify(templateEmailRepository, times(2)).findByNomeTemplate("orcamento_tatuador");
    }

    // ------------------ TESTES MÉTODO updateEstoque ------------------

    @Test
    @DisplayName("Deve enviar email quando estoque estiver baixo")
    void deveEnviarEmailQuandoEstoqueEstiverBaixo() {
        // Arrange
        String materialNome = "Tinta Preta";
        Double quantidadeAtual = 5.0;
        Double minAviso = 10.0;

        setupEstoqueTemplate();
        when(usuarioRepository.getEmailByIsAdminTrue()).thenReturn(emailsAdmins);

        // Act
        emailService.updateEstoque(materialNome, quantidadeAtual, minAviso);

        // Assert
        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(2)).send(messageCaptor.capture());

        List<SimpleMailMessage> capturedMessages = messageCaptor.getAllValues();
        capturedMessages.forEach(message -> {
            assertNotNull(message.getText());
            assertTrue(message.getText().contains(materialNome));
            assertTrue(message.getText().contains("5,00"));
            assertTrue(message.getText().contains("10,00"));
        });
    }

    @Test
    @DisplayName("Não deve enviar email quando estoque estiver adequado")
    void naoDeveEnviarEmailQuandoEstoqueEstiverAdequado() {
        // Arrange
        String materialNome = "Tinta Preta";
        Double quantidadeAtual = 15.0;
        Double minAviso = 10.0;

        // Act
        emailService.updateEstoque(materialNome, quantidadeAtual, minAviso);

        // Assert
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Não deve enviar email quando minAviso for null")
    void naoDeveEnviarEmailQuandoMinAvisoForNull() {
        // Arrange
        String materialNome = "Tinta Preta";
        Double quantidadeAtual = 5.0;
        Double minAviso = null;

        // Act
        emailService.updateEstoque(materialNome, quantidadeAtual, minAviso);

        // Assert
        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    // ------------------ TESTES MÉTODO enviaEmailOrcamentoAprovado ------------------

    @Test
    @DisplayName("Deve enviar email de orçamento aprovado com sucesso")
    void deveEnviarEmailOrcamentoAprovadoComSucesso() {
        // Arrange
        String email = "cliente@email.com";
        String nome = "João";
        String codigoOrcamento = "ORC789";
        Double valor = 350.50;
        Time tempo = Time.valueOf("02:30:00");

        TemplateEmail template = new TemplateEmail();
        template.setAssunto("Orçamento Aprovado - ${codigoOrcamento}");
        template.setCorpoEmail("Olá ${nomeCliente}, orçamento ${codigoOrcamento} aprovado por ${valor} em ${tempo}.");

        when(templateEmailRepository.findByNomeTemplate("orcamento_aprovado"))
                .thenReturn(Optional.of(template));

        // Act
        emailService.enviaEmailOrcamentoAprovado(email, nome, codigoOrcamento, valor, tempo);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertEquals("Orçamento Aprovado - ORC789", capturedMessage.getSubject());
        assertNotNull(capturedMessage.getText());
        assertTrue(capturedMessage.getText().contains("João"));
        assertTrue(capturedMessage.getText().contains("ORC789"));
        assertTrue(capturedMessage.getText().contains("R$ 350,50"));
        assertTrue(capturedMessage.getText().contains("02:30:00"));
    }

    // ------------------ TESTES MÉTODO enviarEmailParaTodosAdminsEstoqueOk ------------------

    @Test
    @DisplayName("Deve enviar email para todos admins quando estoque ok")
    void deveEnviarEmailParaTodosAdminsQuandoEstoqueOk() {
        // Arrange
        String nomeTemplate = "estoque_ok";

        TemplateEmail template = new TemplateEmail();
        template.setAssunto("Estoque Normalizado");
        template.setCorpoEmail("Todos os materiais estão com estoque adequado.");

        when(templateEmailRepository.findByNomeTemplate(nomeTemplate))
                .thenReturn(Optional.of(template));
        when(usuarioRepository.findAllByIsAdmin(true)).thenReturn(admins);

        // Act
        emailService.enviarEmailParaTodosAdminsEstoqueOk(nomeTemplate);

        // Assert
        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando não há administradores para estoque ok")
    void deveLancarExcecaoQuandoNaoHaAdministradoresParaEstoqueOk() {
        // Arrange
        String nomeTemplate = "estoque_ok";

        TemplateEmail template = new TemplateEmail();
        template.setAssunto("Estoque OK");
        template.setCorpoEmail("Estoque normalizado.");

        when(templateEmailRepository.findByNomeTemplate(nomeTemplate))
                .thenReturn(Optional.of(template));
        when(usuarioRepository.findAllByIsAdmin(true)).thenReturn(List.of());

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> emailService.enviarEmailParaTodosAdminsEstoqueOk(nomeTemplate)
        );

        assertEquals("Nenhum administrador encontrado para envio de email.", exception.getMessage());
    }

    // ------------------ TESTES MÉTODO enviarEmailParaTodosAdminsEstoqueBaixo ------------------

    @Test
    @DisplayName("Deve enviar email para todos admins quando estoque baixo")
    void deveEnviarEmailParaTodosAdminsQuandoEstoqueBaixo() {
        // Arrange
        String nomeTemplate = "estoque_baixo_observer";
        String texto = "- Tinta Preta: 2ml (Mínimo: 10ml)\n- Agulha: 1 unidade (Mínimo: 5)";

        TemplateEmail template = new TemplateEmail();
        template.setAssunto("Alerta de Estoque Baixo");
        template.setCorpoEmail("Olá ${nomeAdmin}, materiais com estoque baixo:\n${textoEstoque}");

        when(templateEmailRepository.findByNomeTemplate(nomeTemplate))
                .thenReturn(Optional.of(template));
        when(usuarioRepository.findAllByIsAdmin(true)).thenReturn(admins);

        // Act
        emailService.enviarEmailParaTodosAdminsEstoqueBaixo(nomeTemplate, texto);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(2)).send(messageCaptor.capture());

        List<SimpleMailMessage> capturedMessages = messageCaptor.getAllValues();
        capturedMessages.forEach(message -> {
            assertNotNull(message.getText());
            assertTrue(message.getText().contains("Administrador"));
            assertTrue(message.getText().contains("Tinta Preta"));
            assertTrue(message.getText().contains("Agulha"));
        });
    }

    // ------------------ TESTES MÉTODO updateAgendamento ------------------

    @Test
    @DisplayName("Deve processar atualização de agendamento enviando emails")
    void deveProcessarAtualizacaoAgendamentoEnviandoEmails() {
        // Arrange
        setupAgendamentoTemplates();
        when(usuarioRepository.getEmailByIsAdminTrue()).thenReturn(emailsAdmins);

        // Act
        emailService.updateAgendamento(agendamento);

        // Assert
        verify(mailSender, times(3)).send(any(SimpleMailMessage.class)); // 1 cliente + 2 admins
        verify(templateEmailRepository, times(2)).findByNomeTemplate("agendamento_cliente");
        verify(templateEmailRepository, times(2)).findByNomeTemplate("agendamento_tatuador");
    }

    @Test
    @DisplayName("Deve lançar exceção quando email do cliente do agendamento for inválido")
    void deveLancarExcecaoQuandoEmailClienteAgendamentoForInvalido() {
        // Arrange
        usuario.setEmail(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> emailService.updateAgendamento(agendamento)
        );

        assertEquals("Destinatário inválido para envio de e-mail.", exception.getMessage());
    }

    // ------------------ TESTES TRATAMENTO DE EXCEÇÕES ------------------

    @Test
    @DisplayName("Deve lançar exceção quando JavaMailSender falha")
    void deveLancarExcecaoQuandoJavaMailSenderFalha() {
        // Arrange
        doThrow(new RuntimeException("Falha no envio"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> emailService.enviarTextoSimples("teste@email.com", "Assunto", "Texto")
        );

        assertEquals("Falha no envio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve tratar corretamente template com campos vazios")
    void deveTratarCorretamenteTemplateComCamposVazios() {
        // Arrange
        TemplateEmail template = new TemplateEmail();
        template.setAssunto("");
        template.setCorpoEmail("");

        when(templateEmailRepository.findByNomeTemplate("orcamento_cliente"))
                .thenReturn(Optional.of(template));

        // Act
        emailService.enviaEmailNovoOrcamento("cliente@email.com", "Cliente", "ORC123");

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertEquals("", capturedMessage.getSubject());
        assertNotNull(capturedMessage.getText());
    }

    // ------------------ MÉTODOS AUXILIARES ------------------

    private void setupTemplatesMocks() {
        // Template cliente
        TemplateEmail templateCliente = new TemplateEmail();
        templateCliente.setAssunto("Novo Orçamento");
        templateCliente.setCorpoEmail("Olá ${nomeCliente}, orçamento ${codigoOrcamento} criado.");

        // Template tatuador
        TemplateEmail templateTatuador = new TemplateEmail();
        templateTatuador.setAssunto("Novo Orçamento - ${codigoOrcamento}");
        templateTatuador.setCorpoEmail("${nomeAdmin}, novo orçamento ${codigoOrcamento} de ${nomeCliente}.");

        when(templateEmailRepository.findByNomeTemplate("orcamento_cliente"))
                .thenReturn(Optional.of(templateCliente));
        when(templateEmailRepository.findByNomeTemplate("orcamento_tatuador"))
                .thenReturn(Optional.of(templateTatuador));
    }

    private void setupEstoqueTemplate() {
        TemplateEmail template = new TemplateEmail();
        template.setAssunto("Estoque Baixo - ${nomeMaterial}");
        template.setCorpoEmail("Material ${nomeMaterial} com ${qtdAtual} (mínimo: ${limite})");

        when(templateEmailRepository.findByNomeTemplate("estoque_baixo"))
                .thenReturn(Optional.of(template));
    }

    private void setupAgendamentoTemplates() {
        // Template cliente
        TemplateEmail templateCliente = new TemplateEmail();
        templateCliente.setAssunto("Agendamento Criado");
        templateCliente.setCorpoEmail("${nomeCliente}, agendamento ${codigoOrcamento} em ${dataHora}");

        // Template tatuador
        TemplateEmail templateTatuador = new TemplateEmail();
        templateTatuador.setAssunto("Novo Agendamento - ${codigoOrcamento}");
        templateTatuador.setCorpoEmail("Agendamento ${codigoOrcamento} de ${nomeCliente} em ${dataHora}");

        when(templateEmailRepository.findByNomeTemplate("agendamento_cliente"))
                .thenReturn(Optional.of(templateCliente));
        when(templateEmailRepository.findByNomeTemplate("agendamento_tatuador"))
                .thenReturn(Optional.of(templateTatuador));
    }
}














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
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.RestTemplate;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TemplateEmailRepository templateEmailRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmailService emailService;

    private Usuario usuario;
    private Orcamento orcamento;
    private Agendamento agendamento;
    private List<Usuario> admins;
    private List<String> emailsAdmins;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");
        usuario.setTelefone("(11) 99999-9999");

        orcamento = new Orcamento("ORC123", "João Silva", "joao@email.com",
                "Dragão nas costas", 20.5, "Preto e Vermelho", "Costas",
                Arrays.asList("imagem1.jpg", "imagem2.jpg"));

        agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.setDataHora(LocalDateTime.of(2026, 3, 15, 14, 30));
        agendamento.setStatus(StatusAgendamento.PENDENTE);
        agendamento.setUsuario(usuario);
        agendamento.setOrcamento(orcamento);

        Usuario admin1 = new Usuario();
        admin1.setEmail("admin1@tattoo.com");
        admin1.setNome("Admin 1");

        Usuario admin2 = new Usuario();
        admin2.setEmail("admin2@tattoo.com");
        admin2.setNome("Admin 2");

        admins = Arrays.asList(admin1, admin2);
        emailsAdmins = Arrays.asList("admin1@tattoo.com", "admin2@tattoo.com");
    }

    // ------------------ enviarTextoSimples ------------------

    @Test
    @DisplayName("Deve enviar texto simples com sucesso")
    void deveEnviarTextoSimplesComSucesso() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        emailService.enviarTextoSimples("teste@email.com", "Assunto", "Texto");

        verify(restTemplate, times(1)).postForEntity(
                eq("http://localhost:8081/email/simples"), any(), eq(String.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando RestTemplate falha")
    void deveLancarExcecaoQuandoRestTemplateFalha() {
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenThrow(new RuntimeException("Falha no envio"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> emailService.enviarTextoSimples("teste@email.com", "Assunto", "Texto"));

        assertTrue(exception.getMessage().contains("Falha no envio"));
    }

    // ------------------ enviaEmailNovoOrcamento ------------------

    @Test
    @DisplayName("Deve enviar email de novo orçamento com sucesso")
    void deveEnviarEmailNovoOrcamentoComSucesso() {
        TemplateEmail template = new TemplateEmail();
        template.setAssunto("Novo Orçamento");
        template.setCorpoEmail("Olá ${nomeCliente}, seu orçamento ${codigoOrcamento} foi criado.");

        when(templateEmailRepository.findByNomeTemplate("orcamento_cliente"))
                .thenReturn(Optional.of(template));
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        emailService.enviaEmailNovoOrcamento("cliente@email.com", "Cliente Teste", "ORC456");

        verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email cliente for nulo")
    void deveLancarExcecaoQuandoEmailClienteForNulo() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> emailService.enviaEmailNovoOrcamento(null, "Cliente", "ORC123"));

        assertEquals("Destinatário inválido para envio de e-mail.", exception.getMessage());
        verify(restTemplate, never()).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email cliente for vazio")
    void deveLancarExcecaoQuandoEmailClienteForVazio() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> emailService.enviaEmailNovoOrcamento("", "Cliente", "ORC123"));

        assertEquals("Destinatário inválido para envio de e-mail.", exception.getMessage());
        verify(restTemplate, never()).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando template não for encontrado")
    void deveLancarExcecaoQuandoTemplateNaoForEncontrado() {
        when(templateEmailRepository.findByNomeTemplate("orcamento_cliente"))
                .thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> emailService.enviaEmailNovoOrcamento("cliente@email.com", "Cliente", "ORC123"));

        assertEquals("Template 'orcamento_cliente' não encontrado", exception.getMessage());
    }

    // ------------------ updateOrcamento ------------------

    @Test
    @DisplayName("Deve processar atualização de orçamento enviando emails")
    void deveProcessarAtualizacaoOrcamentoEnviandoEmails() {
        setupTemplatesMocks();
        when(usuarioRepository.getEmailByIsAdminTrue()).thenReturn(emailsAdmins);
        when(usuarioRepository.getNomeByIsAdminTrue()).thenReturn(List.of("Admin Principal"));
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        emailService.updateOrcamento(orcamento);

        verify(restTemplate, times(3)).postForEntity(anyString(), any(), eq(String.class));
    }

    // ------------------ updateEstoque ------------------

    @Test
    @DisplayName("Deve enviar email quando estoque estiver baixo")
    void deveEnviarEmailQuandoEstoqueEstiverBaixo() {
        setupEstoqueTemplate();
        when(usuarioRepository.getEmailByIsAdminTrue()).thenReturn(emailsAdmins);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        emailService.updateEstoque("Tinta Preta", 5.0, 10.0);

        verify(restTemplate, times(2)).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    @DisplayName("Não deve enviar email quando estoque estiver adequado")
    void naoDeveEnviarEmailQuandoEstoqueEstiverAdequado() {
        emailService.updateEstoque("Tinta Preta", 15.0, 10.0);

        verify(restTemplate, never()).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    @DisplayName("Não deve enviar email quando minAviso for null")
    void naoDeveEnviarEmailQuandoMinAvisoForNull() {
        emailService.updateEstoque("Tinta Preta", 5.0, null);

        verify(restTemplate, never()).postForEntity(anyString(), any(), eq(String.class));
    }

    // ------------------ enviaEmailOrcamentoAprovado ------------------

    @Test
    @DisplayName("Deve enviar email de orçamento aprovado com sucesso")
    void deveEnviarEmailOrcamentoAprovadoComSucesso() {
        TemplateEmail template = new TemplateEmail();
        template.setAssunto("Orçamento Aprovado - ${codigoOrcamento}");
        template.setCorpoEmail("Olá ${nomeCliente}, orçamento ${codigoOrcamento} aprovado.");

        when(templateEmailRepository.findByNomeTemplate("orcamento_aprovado"))
                .thenReturn(Optional.of(template));
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        emailService.enviaEmailOrcamentoAprovado("cliente@email.com", "João",
                "ORC789", 350.50, Time.valueOf("02:30:00"));

        verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(String.class));
    }

    // ------------------ enviarEmailParaTodosAdminsEstoqueOk ------------------

    @Test
    @DisplayName("Deve enviar email para todos admins quando estoque ok")
    void deveEnviarEmailParaTodosAdminsQuandoEstoqueOk() {
        TemplateEmail template = new TemplateEmail();
        template.setAssunto("Estoque Normalizado");
        template.setCorpoEmail("Todos os materiais estão com estoque adequado.");

        when(templateEmailRepository.findByNomeTemplate("estoque_ok"))
                .thenReturn(Optional.of(template));
        when(usuarioRepository.findAllByIsAdmin(true)).thenReturn(admins);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        emailService.enviarEmailParaTodosAdminsEstoqueOk("estoque_ok");

        verify(restTemplate, times(2)).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando não há administradores para estoque ok")
    void deveLancarExcecaoQuandoNaoHaAdministradoresParaEstoqueOk() {
        TemplateEmail template = new TemplateEmail();
        template.setAssunto("Estoque OK");
        template.setCorpoEmail("Estoque normalizado.");

        when(templateEmailRepository.findByNomeTemplate("estoque_ok"))
                .thenReturn(Optional.of(template));
        when(usuarioRepository.findAllByIsAdmin(true)).thenReturn(List.of());

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> emailService.enviarEmailParaTodosAdminsEstoqueOk("estoque_ok"));

        assertEquals("Nenhum administrador encontrado para envio de email.", exception.getMessage());
    }

    // ------------------ enviarEmailParaTodosAdminsEstoqueBaixo ------------------

    @Test
    @DisplayName("Deve enviar email para todos admins quando estoque baixo")
    void deveEnviarEmailParaTodosAdminsQuandoEstoqueBaixo() {
        TemplateEmail template = new TemplateEmail();
        template.setAssunto("Alerta de Estoque Baixo");
        template.setCorpoEmail("Olá ${nomeAdmin}, materiais baixos:\n${textoEstoque}");

        when(templateEmailRepository.findByNomeTemplate("estoque_baixo_observer"))
                .thenReturn(Optional.of(template));
        when(usuarioRepository.findAllByIsAdmin(true)).thenReturn(admins);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        emailService.enviarEmailParaTodosAdminsEstoqueBaixo("estoque_baixo_observer",
                "- Tinta Preta: 2ml");

        verify(restTemplate, times(2)).postForEntity(anyString(), any(), eq(String.class));
    }

    // ------------------ updateAgendamento ------------------

    @Test
    @DisplayName("Deve processar atualização de agendamento enviando emails")
    void deveProcessarAtualizacaoAgendamentoEnviandoEmails() {
        setupAgendamentoTemplates();
        when(usuarioRepository.getEmailByIsAdminTrue()).thenReturn(emailsAdmins);
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        emailService.updateAgendamento(agendamento);

        verify(restTemplate, times(3)).postForEntity(anyString(), any(), eq(String.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando email do cliente do agendamento for inválido")
    void deveLancarExcecaoQuandoEmailClienteAgendamentoForInvalido() {
        usuario.setEmail(null);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> emailService.updateAgendamento(agendamento));

        assertEquals("Destinatário inválido para envio de e-mail.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve tratar corretamente template com campos vazios")
    void deveTratarCorretamenteTemplateComCamposVazios() {
        TemplateEmail template = new TemplateEmail();
        template.setAssunto("");
        template.setCorpoEmail("");

        when(templateEmailRepository.findByNomeTemplate("orcamento_cliente"))
                .thenReturn(Optional.of(template));
        when(restTemplate.postForEntity(anyString(), any(), eq(String.class)))
                .thenReturn(ResponseEntity.ok("ok"));

        emailService.enviaEmailNovoOrcamento("cliente@email.com", "Cliente", "ORC123");

        verify(restTemplate, times(1)).postForEntity(anyString(), any(), eq(String.class));
    }

    // ------------------ Métodos auxiliares ------------------

    private void setupTemplatesMocks() {
        TemplateEmail templateCliente = new TemplateEmail();
        templateCliente.setAssunto("Novo Orçamento");
        templateCliente.setCorpoEmail("Olá ${nomeCliente}, orçamento ${codigoOrcamento} criado.");

        TemplateEmail templateTatuador = new TemplateEmail();
        templateTatuador.setAssunto("Novo Orçamento - ${codigoOrcamento}");
        templateTatuador.setCorpoEmail("${nomeAdmin}, novo orçamento ${codigoOrcamento} de ${nomeCliente}." +
                " Email: ${emailCliente}. Ideia: ${ideia}. Tamanho: ${tamanho}. Cores: ${cores}." +
                " Local: ${localCorpo}. Imagens: ${imagens}.");

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
        TemplateEmail templateCliente = new TemplateEmail();
        templateCliente.setAssunto("Agendamento Criado");
        templateCliente.setCorpoEmail("${nomeCliente}, agendamento ${codigoOrcamento} em ${dataHora}. Status: ${status}");

        TemplateEmail templateTatuador = new TemplateEmail();
        templateTatuador.setAssunto("Novo Agendamento - ${codigoOrcamento}");
        templateTatuador.setCorpoEmail("Agendamento ${codigoOrcamento} de ${nomeCliente}" +
                " (${emailCliente}) em ${dataHora}. Status: ${status}");

        when(templateEmailRepository.findByNomeTemplate("agendamento_cliente"))
                .thenReturn(Optional.of(templateCliente));
        when(templateEmailRepository.findByNomeTemplate("agendamento_tatuador"))
                .thenReturn(Optional.of(templateTatuador));
    }
}
package hub.orcana.dto.agendamento;

import hub.orcana.tables.Agendamento;
import hub.orcana.tables.Orcamento;
import hub.orcana.tables.StatusAgendamento;
import hub.orcana.tables.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AgendamentoMapperTest {

    private Usuario usuario;
    private Orcamento orcamento;
    private Agendamento agendamento;
    private LocalDateTime dataHora;

    @BeforeEach
    void setUp() {
        dataHora = LocalDateTime.now().plusDays(1);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");

        orcamento = new Orcamento("ORC123", 1L, "João Silva", "joao@email.com",
                "Dragão nas costas", 20.5, "Preto e Vermelho", "Costas", null);

        agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.setDataHora(dataHora);
        agendamento.setStatus(StatusAgendamento.AGUARDANDO);
        agendamento.setUsuario(usuario);
        agendamento.setOrcamento(orcamento);
    }

    @Test
    @DisplayName("Deve converter Agendamento para DetalhesAgendamentoOutput corretamente")
    void deveConverterAgendamentoParaOutput() {

        DetalhesAgendamentoOutput output = AgendamentoMapper.of(agendamento);


        assertNotNull(output);
        assertEquals(1L, output.id());
        assertEquals(dataHora, output.dataHora());
        assertEquals("AGUARDANDO", output.status());
        assertEquals("João Silva", output.nomeUsuario());
        assertEquals("joao@email.com", output.emailUsuario());
        assertEquals("Dragão nas costas", output.ideia());
        assertEquals(20.5, output.tamanho());
        assertEquals("Preto e Vermelho", output.cores());
        assertEquals("Costas", output.localCorpo());
    }

    @Test
    @DisplayName("Deve converter CadastroAgendamentoInput para Agendamento corretamente")
    void deveConverterInputParaAgendamento() {

        CadastroAgendamentoInput input = new CadastroAgendamentoInput(
                "joao@email.com",
                "ORC123",
                dataHora,
                StatusAgendamento.AGUARDANDO
        );


        Agendamento resultado = AgendamentoMapper.of(input, usuario, orcamento);


        assertNotNull(resultado);
        assertEquals(dataHora, resultado.getDataHora());
        assertEquals(StatusAgendamento.AGUARDANDO, resultado.getStatus());
        assertEquals(usuario, resultado.getUsuario());
        assertEquals(orcamento, resultado.getOrcamento());
    }

    @Test
    @DisplayName("Deve manter status como AGUARDANDO quando não especificado")
    void deveManterStatusPadraoQuandoNaoEspecificado() {

        CadastroAgendamentoInput input = new CadastroAgendamentoInput(
                "joao@email.com",
                "ORC123",
                dataHora,
                null
        );


        Agendamento resultado = AgendamentoMapper.of(input, usuario, orcamento);


        assertNotNull(resultado);
        assertNull(resultado.getStatus());
    }

    @Test
    @DisplayName("Deve converter agendamento com status CONFIRMADO")
    void deveConverterAgendamentoComStatusConfirmado() {

        agendamento.setStatus(StatusAgendamento.CONFIRMADO);


        DetalhesAgendamentoOutput output = AgendamentoMapper.of(agendamento);


        assertNotNull(output);
        assertEquals("CONFIRMADO", output.status());
    }

    @Test
    @DisplayName("Deve converter agendamento com status CANCELADO")
    void deveConverterAgendamentoComStatusCancelado() {

        agendamento.setStatus(StatusAgendamento.CANCELADO);


        DetalhesAgendamentoOutput output = AgendamentoMapper.of(agendamento);


        assertNotNull(output);
        assertEquals("CANCELADO", output.status());
    }

    @Test
    @DisplayName("Deve converter agendamento com status CONCLUIDO")
    void deveConverterAgendamentoComStatusConcluido() {

        agendamento.setStatus(StatusAgendamento.CONCLUIDO);


        DetalhesAgendamentoOutput output = AgendamentoMapper.of(agendamento);


        assertNotNull(output);
        assertEquals("CONCLUIDO", output.status());
    }

    @Test
    @DisplayName("Deve preservar todos os campos do usuário na conversão")
    void devePreservarCamposUsuario() {

        Usuario usuarioCompleto = new Usuario();
        usuarioCompleto.setId(2L);
        usuarioCompleto.setNome("Maria Santos");
        usuarioCompleto.setEmail("maria@email.com");
        agendamento.setUsuario(usuarioCompleto);


        DetalhesAgendamentoOutput output = AgendamentoMapper.of(agendamento);


        assertNotNull(output);
        assertEquals("Maria Santos", output.nomeUsuario());
        assertEquals("maria@email.com", output.emailUsuario());
    }

    @Test
    @DisplayName("Deve preservar todos os campos do orçamento na conversão")
    void devePreservarCamposOrcamento() {

        Orcamento orcamentoCompleto = new Orcamento("ORC456", 2L, "João Silva", "joao@email.com",
                "Leão no braço", 15.0, "Colorido", "Braço", null);
        agendamento.setOrcamento(orcamentoCompleto);


        DetalhesAgendamentoOutput output = AgendamentoMapper.of(agendamento);


        assertNotNull(output);
        assertEquals("Leão no braço", output.ideia());
        assertEquals(15.0, output.tamanho());
        assertEquals("Colorido", output.cores());
        assertEquals("Braço", output.localCorpo());
    }

    @Test
    @DisplayName("Deve converter agendamento com tamanho decimal preciso")
    void deveConverterAgendamentoComTamanhoDecimal() {

        Orcamento orcamentoComTamanho = new Orcamento("ORC123", 1L, "João Silva", "joao@email.com",
                "Dragão nas costas", 12.75, "Preto e Vermelho", "Costas", null);
        agendamento.setOrcamento(orcamentoComTamanho);


        DetalhesAgendamentoOutput output = AgendamentoMapper.of(agendamento);


        assertNotNull(output);
        assertEquals(12.75, output.tamanho());
    }

    @Test
    @DisplayName("Deve converter input com data futura")
    void deveConverterInputComDataFutura() {

        LocalDateTime dataFutura = LocalDateTime.now().plusMonths(2);
        CadastroAgendamentoInput input = new CadastroAgendamentoInput(
                "joao@email.com",
                "ORC123",
                dataFutura,
                StatusAgendamento.AGUARDANDO
        );


        Agendamento resultado = AgendamentoMapper.of(input, usuario, orcamento);


        assertNotNull(resultado);
        assertEquals(dataFutura, resultado.getDataHora());
    }

    @Test
    @DisplayName("Deve manter referências corretas após conversão")
    void deveManterReferenciasCorretas() {

        CadastroAgendamentoInput input = new CadastroAgendamentoInput(
                "joao@email.com",
                "ORC123",
                dataHora,
                StatusAgendamento.CONFIRMADO
        );


        Agendamento resultado = AgendamentoMapper.of(input, usuario, orcamento);


        assertNotNull(resultado);
        assertSame(usuario, resultado.getUsuario());
        assertSame(orcamento, resultado.getOrcamento());
        assertEquals("joao@email.com", resultado.getUsuario().getEmail());
        assertEquals("ORC123", resultado.getOrcamento().getCodigoOrcamento());
    }

    @Test
    @DisplayName("Deve converter múltiplos agendamentos mantendo independência")
    void deveConverterMultiplosAgendamentosIndependentes() {

        Agendamento agendamento2 = new Agendamento();
        agendamento2.setId(2L);
        agendamento2.setDataHora(dataHora.plusDays(1));
        agendamento2.setStatus(StatusAgendamento.CONFIRMADO);
        agendamento2.setUsuario(usuario);
        agendamento2.setOrcamento(orcamento);


        DetalhesAgendamentoOutput output1 = AgendamentoMapper.of(agendamento);
        DetalhesAgendamentoOutput output2 = AgendamentoMapper.of(agendamento2);


        assertNotNull(output1);
        assertNotNull(output2);
        assertNotEquals(output1.id(), output2.id());
        assertNotEquals(output1.dataHora(), output2.dataHora());
        assertNotEquals(output1.status(), output2.status());
    }

    @Test
    @DisplayName("Deve converter agendamento com cores complexas")
    void deveConverterAgendamentoComCoresComplexas() {

        Orcamento orcamentoComCores = new Orcamento("ORC123", 1L, "João Silva", "joao@email.com",
                "Dragão nas costas", 20.5, "Preto, Vermelho, Azul, Verde, Amarelo", "Costas", null);
        agendamento.setOrcamento(orcamentoComCores);


        DetalhesAgendamentoOutput output = AgendamentoMapper.of(agendamento);


        assertNotNull(output);
        assertEquals("Preto, Vermelho, Azul, Verde, Amarelo", output.cores());
    }

    @Test
    @DisplayName("Deve converter agendamento com ideia longa")
    void deveConverterAgendamentoComIdeiaLonga() {

        String ideiaLonga = "Dragão oriental nas costas completas com detalhes em nuvens, " +
                           "flores de cerejeira e elementos tradicionais japoneses";
        Orcamento orcamentoComIdeiaLonga = new Orcamento("ORC123", 1L, "João Silva", "joao@email.com",
                ideiaLonga, 20.5, "Preto e Vermelho", "Costas", null);
        agendamento.setOrcamento(orcamentoComIdeiaLonga);


        DetalhesAgendamentoOutput output = AgendamentoMapper.of(agendamento);


        assertNotNull(output);
        assertEquals(ideiaLonga, output.ideia());
    }

    @Test
    @DisplayName("Deve converter agendamento preservando data e hora exatas")
    void devePreservarDataHoraExatas() {

        LocalDateTime dataHoraEspecifica = LocalDateTime.of(2025, 12, 25, 14, 30, 0);
        agendamento.setDataHora(dataHoraEspecifica);


        DetalhesAgendamentoOutput output = AgendamentoMapper.of(agendamento);


        assertNotNull(output);
        assertEquals(dataHoraEspecifica, output.dataHora());
        assertEquals(2025, output.dataHora().getYear());
        assertEquals(12, output.dataHora().getMonthValue());
        assertEquals(25, output.dataHora().getDayOfMonth());
        assertEquals(14, output.dataHora().getHour());
        assertEquals(30, output.dataHora().getMinute());
    }
}

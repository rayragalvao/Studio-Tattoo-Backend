package hub.orcana.repository;

import hub.orcana.tables.Agendamento;
import hub.orcana.tables.Orcamento;
import hub.orcana.tables.StatusAgendamento;
import hub.orcana.tables.Usuario;
import hub.orcana.tables.repository.AgendamentoRepository;
import hub.orcana.tables.repository.OrcamentoRepository;
import hub.orcana.tables.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AgendamentoRepositoryTest {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    private Usuario usuario;
    private Orcamento orcamento;
    private LocalDateTime dataHora;

    @BeforeEach
    void setUp() {
        agendamentoRepository.deleteAll();
        orcamentoRepository.deleteAll();
        usuarioRepository.deleteAll();

        dataHora = LocalDateTime.now().plusDays(1);

        usuario = new Usuario();
        usuario.setNome("João Silva");
        usuario.setEmail("joao@test.com");
        usuario.setSenha("senha123");
        usuario = usuarioRepository.save(usuario);

        orcamento = new Orcamento("ORC-TEST-123", "João Silva", "joao@test.com",
                "Dragão nas costas", 20.5, "Preto e Vermelho", "Costas", null);
        orcamento = orcamentoRepository.save(orcamento);
    }

    @Test
    @DisplayName("Deve salvar agendamento com sucesso")
    void deveSalvarAgendamentoComSucesso() {

        Agendamento agendamento = new Agendamento();
        agendamento.setDataHora(dataHora);
        agendamento.setStatus(StatusAgendamento.AGUARDANDO);
        agendamento.setUsuario(usuario);
        agendamento.setOrcamento(orcamento);


        Agendamento salvo = agendamentoRepository.save(agendamento);


        assertNotNull(salvo);
        assertNotNull(salvo.getId());
        assertEquals(dataHora, salvo.getDataHora());
        assertEquals(StatusAgendamento.AGUARDANDO, salvo.getStatus());
        assertEquals(usuario.getId(), salvo.getUsuario().getId());
    }

    @Test
    @DisplayName("Deve buscar agendamento por ID")
    void deveBuscarAgendamentoPorId() {

        Agendamento agendamento = new Agendamento();
        agendamento.setDataHora(dataHora);
        agendamento.setStatus(StatusAgendamento.AGUARDANDO);
        agendamento.setUsuario(usuario);
        agendamento.setOrcamento(orcamento);
        Agendamento salvo = agendamentoRepository.save(agendamento);


        Optional<Agendamento> resultado = agendamentoRepository.findById(salvo.getId());


        assertTrue(resultado.isPresent());
        assertEquals(salvo.getId(), resultado.get().getId());
        assertEquals(dataHora, resultado.get().getDataHora());
    }

    @Test
    @DisplayName("Deve retornar Optional.empty quando agendamento não existir")
    void deveRetornarOptionalEmptyQuandoNaoExistir() {

        Optional<Agendamento> resultado = agendamentoRepository.findById(999L);


        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve buscar agendamentos por usuário ID")
    void deveBuscarAgendamentosPorUsuarioId() {

        Agendamento agendamento1 = new Agendamento();
        agendamento1.setDataHora(dataHora);
        agendamento1.setStatus(StatusAgendamento.AGUARDANDO);
        agendamento1.setUsuario(usuario);
        agendamento1.setOrcamento(orcamento);
        agendamentoRepository.save(agendamento1);

        Orcamento orcamento2 = new Orcamento("ORC-TEST-456", "João Silva", "joao@test.com",
                "Rosa no braço", 10.0, "Rosa e Verde", "Braço", null);
        orcamento2 = orcamentoRepository.save(orcamento2);

        Agendamento agendamento2 = new Agendamento();
        agendamento2.setDataHora(dataHora.plusDays(1));
        agendamento2.setStatus(StatusAgendamento.CONFIRMADO);
        agendamento2.setUsuario(usuario);
        agendamento2.setOrcamento(orcamento2);
        agendamentoRepository.save(agendamento2);


        List<Agendamento> resultado = agendamentoRepository.findByUsuarioId(usuario.getId());


        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(a -> a.getUsuario().getId().equals(usuario.getId())));
    }

    @Test
    @DisplayName("Deve buscar agendamento por código de orçamento")
    void deveBuscarAgendamentoPorCodigoOrcamento() {

        Agendamento agendamento = new Agendamento();
        agendamento.setDataHora(dataHora);
        agendamento.setStatus(StatusAgendamento.AGUARDANDO);
        agendamento.setUsuario(usuario);
        agendamento.setOrcamento(orcamento);
        agendamentoRepository.save(agendamento);


        Optional<Agendamento> resultado = 
                agendamentoRepository.findByOrcamentoCodigoOrcamento("ORC-TEST-123");


        assertTrue(resultado.isPresent());
        assertEquals("ORC-TEST-123", resultado.get().getOrcamento().getCodigoOrcamento());
    }

    @Test
    @DisplayName("Deve retornar empty quando código de orçamento não tiver agendamento")
    void deveRetornarEmptyQuandoCodigoOrcamentoNaoTiverAgendamento() {

        Optional<Agendamento> resultado = 
                agendamentoRepository.findByOrcamentoCodigoOrcamento("ORC-INEXISTENTE");


        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve buscar agendamentos por data específica")
    void deveBuscarAgendamentosPorDataEspecifica() {

        LocalDateTime data1 = LocalDateTime.of(2025, 12, 25, 10, 0);
        LocalDateTime data2 = LocalDateTime.of(2025, 12, 25, 14, 0);
        LocalDateTime data3 = LocalDateTime.of(2025, 12, 26, 10, 0);

        Agendamento agendamento1 = new Agendamento();
        agendamento1.setDataHora(data1);
        agendamento1.setStatus(StatusAgendamento.AGUARDANDO);
        agendamento1.setUsuario(usuario);
        agendamento1.setOrcamento(orcamento);
        agendamentoRepository.save(agendamento1);

        Orcamento orcamento2 = new Orcamento("ORC-TEST-456", "João Silva", "joao@test.com",
                "Rosa no braço", 10.0, "Rosa e Verde", "Braço", null);
        orcamento2 = orcamentoRepository.save(orcamento2);

        Agendamento agendamento2 = new Agendamento();
        agendamento2.setDataHora(data2);
        agendamento2.setStatus(StatusAgendamento.CONFIRMADO);
        agendamento2.setUsuario(usuario);
        agendamento2.setOrcamento(orcamento2);
        agendamentoRepository.save(agendamento2);

        Orcamento orcamento3 = new Orcamento("ORC-TEST-789", "João Silva", "joao@test.com",
                "Leão", 15.0, "Amarelo", "Perna", null);
        orcamento3 = orcamentoRepository.save(orcamento3);

        Agendamento agendamento3 = new Agendamento();
        agendamento3.setDataHora(data3);
        agendamento3.setStatus(StatusAgendamento.AGUARDANDO);
        agendamento3.setUsuario(usuario);
        agendamento3.setOrcamento(orcamento3);
        agendamentoRepository.save(agendamento3);


        List<Agendamento> resultado = agendamentoRepository.findByData(data1);


        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(a -> 
                a.getDataHora().toLocalDate().equals(data1.toLocalDate())));
    }

    @Test
    @DisplayName("Deve buscar datas com agendamento a partir de data específica")
    void deveBuscarDatasComAgendamentoAPartirDeData() {

        LocalDateTime hoje = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime amanha = hoje.plusDays(1);
        LocalDateTime doisDias = hoje.plusDays(2);

        Agendamento agendamento1 = new Agendamento();
        agendamento1.setDataHora(amanha);
        agendamento1.setStatus(StatusAgendamento.AGUARDANDO);
        agendamento1.setUsuario(usuario);
        agendamento1.setOrcamento(orcamento);
        agendamentoRepository.save(agendamento1);

        Orcamento orcamento2 = new Orcamento("ORC-TEST-456", "João Silva", "joao@test.com",
                "Rosa", 10.0, "Rosa", "Braço", null);
        orcamento2 = orcamentoRepository.save(orcamento2);

        Agendamento agendamento2 = new Agendamento();
        agendamento2.setDataHora(doisDias);
        agendamento2.setStatus(StatusAgendamento.CONFIRMADO);
        agendamento2.setUsuario(usuario);
        agendamento2.setOrcamento(orcamento2);
        agendamentoRepository.save(agendamento2);


        List<LocalDateTime> datas = agendamentoRepository.findDatasComAgendamento(hoje);


        assertNotNull(datas);
        assertEquals(2, datas.size());
    }

    @Test
    @DisplayName("Deve atualizar status do agendamento")
    void deveAtualizarStatusDoAgendamento() {

        Agendamento agendamento = new Agendamento();
        agendamento.setDataHora(dataHora);
        agendamento.setStatus(StatusAgendamento.AGUARDANDO);
        agendamento.setUsuario(usuario);
        agendamento.setOrcamento(orcamento);
        Agendamento salvo = agendamentoRepository.save(agendamento);


        salvo.setStatus(StatusAgendamento.CONFIRMADO);
        Agendamento atualizado = agendamentoRepository.save(salvo);


        assertNotNull(atualizado);
        assertEquals(StatusAgendamento.CONFIRMADO, atualizado.getStatus());
        assertEquals(salvo.getId(), atualizado.getId());
    }

    @Test
    @DisplayName("Deve deletar agendamento")
    void deveDeletarAgendamento() {

        Agendamento agendamento = new Agendamento();
        agendamento.setDataHora(dataHora);
        agendamento.setStatus(StatusAgendamento.AGUARDANDO);
        agendamento.setUsuario(usuario);
        agendamento.setOrcamento(orcamento);
        Agendamento salvo = agendamentoRepository.save(agendamento);


        agendamentoRepository.deleteById(salvo.getId());


        Optional<Agendamento> resultado = agendamentoRepository.findById(salvo.getId());
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve verificar se agendamento existe por ID")
    void deveVerificarSeAgendamentoExiste() {

        Agendamento agendamento = new Agendamento();
        agendamento.setDataHora(dataHora);
        agendamento.setStatus(StatusAgendamento.AGUARDANDO);
        agendamento.setUsuario(usuario);
        agendamento.setOrcamento(orcamento);
        Agendamento salvo = agendamentoRepository.save(agendamento);

        assertTrue(agendamentoRepository.existsById(salvo.getId()));
        assertFalse(agendamentoRepository.existsById(999L));
    }

    @Test
    @DisplayName("Deve listar todos os agendamentos")
    void deveListarTodosAgendamentos() {

        Agendamento agendamento1 = new Agendamento();
        agendamento1.setDataHora(dataHora);
        agendamento1.setStatus(StatusAgendamento.AGUARDANDO);
        agendamento1.setUsuario(usuario);
        agendamento1.setOrcamento(orcamento);
        agendamentoRepository.save(agendamento1);

        Orcamento orcamento2 = new Orcamento("ORC-TEST-456", "João Silva", "joao@test.com",
                "Rosa", 10.0, "Rosa", "Braço", null);
        orcamento2 = orcamentoRepository.save(orcamento2);

        Agendamento agendamento2 = new Agendamento();
        agendamento2.setDataHora(dataHora.plusDays(1));
        agendamento2.setStatus(StatusAgendamento.CONFIRMADO);
        agendamento2.setUsuario(usuario);
        agendamento2.setOrcamento(orcamento2);
        agendamentoRepository.save(agendamento2);


        List<Agendamento> todos = agendamentoRepository.findAll();


        assertNotNull(todos);
        assertEquals(2, todos.size());
    }

    @Test
    @DisplayName("Deve contar total de agendamentos")
    void deveContarTotalDeAgendamentos() {

        Agendamento agendamento1 = new Agendamento();
        agendamento1.setDataHora(dataHora);
        agendamento1.setStatus(StatusAgendamento.AGUARDANDO);
        agendamento1.setUsuario(usuario);
        agendamento1.setOrcamento(orcamento);
        agendamentoRepository.save(agendamento1);

        Orcamento orcamento2 = new Orcamento("ORC-TEST-456", "João Silva", "joao@test.com",
                "Rosa", 10.0, "Rosa", "Braço", null);
        orcamento2 = orcamentoRepository.save(orcamento2);

        Agendamento agendamento2 = new Agendamento();
        agendamento2.setDataHora(dataHora.plusDays(1));
        agendamento2.setStatus(StatusAgendamento.CONFIRMADO);
        agendamento2.setUsuario(usuario);
        agendamento2.setOrcamento(orcamento2);
        agendamentoRepository.save(agendamento2);


        long count = agendamentoRepository.count();


        assertEquals(2, count);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver agendamentos para usuário")
    void deveRetornarListaVaziaQuandoUsuarioNaoTiverAgendamentos() {

        Usuario outroUsuario = new Usuario();
        outroUsuario.setNome("Maria Santos");
        outroUsuario.setEmail("maria@test.com");
        outroUsuario.setSenha("senha456");
        outroUsuario = usuarioRepository.save(outroUsuario);


        List<Agendamento> resultado = agendamentoRepository.findByUsuarioId(outroUsuario.getId());


        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve manter integridade referencial com usuário")
    void deveManterIntegridadeReferencialComUsuario() {

        Agendamento agendamento = new Agendamento();
        agendamento.setDataHora(dataHora);
        agendamento.setStatus(StatusAgendamento.AGUARDANDO);
        agendamento.setUsuario(usuario);
        agendamento.setOrcamento(orcamento);
        Agendamento salvo = agendamentoRepository.save(agendamento);


        Agendamento recuperado = agendamentoRepository.findById(salvo.getId()).orElse(null);


        assertNotNull(recuperado);
        assertNotNull(recuperado.getUsuario());
        assertEquals(usuario.getId(), recuperado.getUsuario().getId());
        assertEquals(usuario.getNome(), recuperado.getUsuario().getNome());
        assertEquals(usuario.getEmail(), recuperado.getUsuario().getEmail());
    }

    @Test
    @DisplayName("Deve manter integridade referencial com orçamento")
    void deveManterIntegridadeReferencialComOrcamento() {

        Agendamento agendamento = new Agendamento();
        agendamento.setDataHora(dataHora);
        agendamento.setStatus(StatusAgendamento.AGUARDANDO);
        agendamento.setUsuario(usuario);
        agendamento.setOrcamento(orcamento);
        Agendamento salvo = agendamentoRepository.save(agendamento);


        Agendamento recuperado = agendamentoRepository.findById(salvo.getId()).orElse(null);


        assertNotNull(recuperado);
        assertNotNull(recuperado.getOrcamento());
        assertEquals(orcamento.getCodigoOrcamento(), recuperado.getOrcamento().getCodigoOrcamento());
        assertEquals(orcamento.getIdeia(), recuperado.getOrcamento().getIdeia());
    }
}

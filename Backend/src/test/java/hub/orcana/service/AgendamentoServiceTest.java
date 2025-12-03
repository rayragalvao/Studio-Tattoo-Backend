package hub.orcana.service;

import hub.orcana.dto.agendamento.AgendamentoMapper;
import hub.orcana.dto.agendamento.CadastroAgendamentoInput;
import hub.orcana.dto.agendamento.DetalhesAgendamentoOutput;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgendamentoServiceTest {

    @Mock
    private AgendamentoRepository agendamentoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private OrcamentoRepository orcamentoRepository;

    @InjectMocks
    private AgendamentoService agendamentoService;

    private Usuario usuario;
    private Orcamento orcamento;
    private Agendamento agendamento;
    private CadastroAgendamentoInput cadastroInput;
    private LocalDateTime dataHora;

    @BeforeEach
    void setUp() {
        dataHora = LocalDateTime.now().plusDays(1);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");

        orcamento = new Orcamento("ORC123", "João Silva", "joao@email.com",
                "Dragão nas costas", 20.5, "Preto e Vermelho", "Costas", null);

        agendamento = new Agendamento();
        agendamento.setId(1L);
        agendamento.setDataHora(dataHora);
        agendamento.setStatus(StatusAgendamento.AGUARDANDO);
        agendamento.setUsuario(usuario);
        agendamento.setOrcamento(orcamento);

        cadastroInput = new CadastroAgendamentoInput(
                "joao@email.com",
                "ORC123",
                dataHora,
                StatusAgendamento.AGUARDANDO
        );
    }

    @Test
    @DisplayName("Deve retornar todos os agendamentos")
    void deveRetornarTodosAgendamentos() {

        Agendamento agendamento2 = new Agendamento();
        agendamento2.setId(2L);
        agendamento2.setDataHora(dataHora.plusDays(1));
        agendamento2.setStatus(StatusAgendamento.CONFIRMADO);
        agendamento2.setUsuario(usuario);
        agendamento2.setOrcamento(orcamento);

        List<Agendamento> agendamentos = Arrays.asList(agendamento, agendamento2);
        when(agendamentoRepository.findAll()).thenReturn(agendamentos);


        List<DetalhesAgendamentoOutput> resultado = agendamentoService.getAgendamentos();


        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("João Silva", resultado.get(0).nomeUsuario());
        assertEquals("joao@email.com", resultado.get(0).emailUsuario());
        verify(agendamentoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver agendamentos")
    void deveRetornarListaVaziaQuandoNaoHouverAgendamentos() {

        when(agendamentoRepository.findAll()).thenReturn(Arrays.asList());


        List<DetalhesAgendamentoOutput> resultado = agendamentoService.getAgendamentos();


        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(agendamentoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar agendamento por ID")
    void deveRetornarAgendamentoPorId() {

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));


        DetalhesAgendamentoOutput resultado = agendamentoService.getAgendamentoPorId(1L);


        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("João Silva", resultado.nomeUsuario());
        assertEquals("joao@email.com", resultado.emailUsuario());
        assertEquals("Dragão nas costas", resultado.ideia());
        verify(agendamentoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando agendamento não for encontrado por ID")
    void deveLancarExcecaoQuandoAgendamentoNaoEncontrado() {

        when(agendamentoRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> agendamentoService.getAgendamentoPorId(999L)
        );
        assertEquals("Agendamento não encontrado.", exception.getMessage());
        verify(agendamentoRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Deve retornar agendamentos por status")
    void deveRetornarAgendamentosPorStatus() {

        Agendamento agendamento2 = new Agendamento();
        agendamento2.setId(2L);
        agendamento2.setDataHora(dataHora.plusDays(1));
        agendamento2.setStatus(StatusAgendamento.CONFIRMADO);
        agendamento2.setUsuario(usuario);
        agendamento2.setOrcamento(orcamento);

        List<Agendamento> agendamentos = Arrays.asList(agendamento, agendamento2);
        when(agendamentoRepository.findAll()).thenReturn(agendamentos);


        List<DetalhesAgendamentoOutput> resultado = agendamentoService.getAgendamentosByStatus("AGUARDANDO");


        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("AGUARDANDO", resultado.get(0).status());
        verify(agendamentoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver agendamentos com status especificado")
    void deveRetornarListaVaziaQuandoNaoHouverAgendamentosComStatus() {

        when(agendamentoRepository.findAll()).thenReturn(Arrays.asList(agendamento));


        List<DetalhesAgendamentoOutput> resultado = agendamentoService.getAgendamentosByStatus("CANCELADO");


        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(agendamentoRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve criar novo agendamento com sucesso")
    void deveCriarNovoAgendamentoComSucesso() {

        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuario));
        when(orcamentoRepository.findByCodigoOrcamento("ORC123")).thenReturn(Optional.of(orcamento));
        when(agendamentoRepository.findByOrcamentoCodigoOrcamento("ORC123")).thenReturn(Optional.empty());
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamento);


        DetalhesAgendamentoOutput resultado = agendamentoService.postAgendamento(cadastroInput);


        assertNotNull(resultado);
        assertEquals("João Silva", resultado.nomeUsuario());
        assertEquals("joao@email.com", resultado.emailUsuario());
        assertEquals("ORC123", orcamento.getCodigoOrcamento());
        verify(usuarioRepository, times(1)).findByEmail("joao@email.com");
        verify(orcamentoRepository, times(1)).findByCodigoOrcamento("ORC123");
        verify(agendamentoRepository, times(1)).findByOrcamentoCodigoOrcamento("ORC123");
        verify(agendamentoRepository, times(1)).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar agendamento com usuário inexistente")
    void deveLancarExcecaoAoCriarAgendamentoComUsuarioInexistente() {

        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> agendamentoService.postAgendamento(cadastroInput)
        );
        assertEquals("Usuário é obrigatório.", exception.getMessage());
        verify(usuarioRepository, times(1)).findByEmail("joao@email.com");
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar agendamento com orçamento inexistente")
    void deveLancarExcecaoAoCriarAgendamentoComOrcamentoInexistente() {

        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuario));
        when(orcamentoRepository.findByCodigoOrcamento("ORC123")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> agendamentoService.postAgendamento(cadastroInput)
        );
        assertEquals("Orçamento não encontrado.", exception.getMessage());
        verify(orcamentoRepository, times(1)).findByCodigoOrcamento("ORC123");
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar agendamento duplicado para mesmo orçamento")
    void deveLancarExcecaoAoCriarAgendamentoDuplicado() {

        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuario));
        when(orcamentoRepository.findByCodigoOrcamento("ORC123")).thenReturn(Optional.of(orcamento));
        when(agendamentoRepository.findByOrcamentoCodigoOrcamento("ORC123")).thenReturn(Optional.of(agendamento));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> agendamentoService.postAgendamento(cadastroInput)
        );
        assertEquals("Já existe um agendamento para este código de orçamento.", exception.getMessage());
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar agendamento existente")
    void deveAtualizarAgendamentoExistente() {

        LocalDateTime novaDataHora = dataHora.plusDays(2);
        CadastroAgendamentoInput novoInput = new CadastroAgendamentoInput(
                "joao@email.com",
                "ORC123",
                novaDataHora,
                StatusAgendamento.CONFIRMADO
        );

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(usuarioRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(usuario));
        when(orcamentoRepository.findByCodigoOrcamento("ORC123")).thenReturn(Optional.of(orcamento));
        
        Agendamento agendamentoAtualizado = new Agendamento();
        agendamentoAtualizado.setId(1L);
        agendamentoAtualizado.setDataHora(novaDataHora);
        agendamentoAtualizado.setStatus(StatusAgendamento.CONFIRMADO);
        agendamentoAtualizado.setUsuario(usuario);
        agendamentoAtualizado.setOrcamento(orcamento);
        
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamentoAtualizado);


        DetalhesAgendamentoOutput resultado = agendamentoService.putAgendamentoById(1L, novoInput);


        assertNotNull(resultado);
        verify(agendamentoRepository, times(1)).findById(1L);
        verify(agendamentoRepository, times(1)).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar agendamento inexistente")
    void deveLancarExcecaoAoAtualizarAgendamentoInexistente() {

        when(agendamentoRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> agendamentoService.putAgendamentoById(999L, cadastroInput)
        );
        assertEquals("Agendamento não encontrado.", exception.getMessage());
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar agendamento por ID")
    void deveDeletarAgendamentoPorId() {

        when(agendamentoRepository.existsById(1L)).thenReturn(true).thenReturn(false);
        doNothing().when(agendamentoRepository).deleteById(1L);


        agendamentoService.deleteAgendamentoById(1L);


        verify(agendamentoRepository, times(1)).deleteById(1L);
        verify(agendamentoRepository, times(2)).existsById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar agendamento inexistente")
    void deveLancarExcecaoAoDeletarAgendamentoInexistente() {

        when(agendamentoRepository.existsById(999L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> agendamentoService.deleteAgendamentoById(999L)
        );
        assertEquals("Agendamento não encontrado.", exception.getMessage());
        verify(agendamentoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando erro ao excluir agendamento")
    void deveLancarExcecaoQuandoErroAoExcluir() {

        when(agendamentoRepository.existsById(1L)).thenReturn(true).thenReturn(true);
        doNothing().when(agendamentoRepository).deleteById(1L);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> agendamentoService.deleteAgendamentoById(1L)
        );
        assertEquals("Erro ao excluir agendamento.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve verificar código de orçamento disponível")
    void deveVerificarCodigoOrcamentoDisponivel() {

        when(orcamentoRepository.findByCodigoOrcamento("ORC123")).thenReturn(Optional.of(orcamento));
        when(agendamentoRepository.findByOrcamentoCodigoOrcamento("ORC123")).thenReturn(Optional.empty());


        boolean resultado = agendamentoService.verificarCodigoOrcamento("ORC123");


        assertTrue(resultado);
        verify(orcamentoRepository, times(1)).findByCodigoOrcamento("ORC123");
        verify(agendamentoRepository, times(1)).findByOrcamentoCodigoOrcamento("ORC123");
    }

    @Test
    @DisplayName("Deve retornar falso quando orçamento não existe")
    void deveRetornarFalsoQuandoOrcamentoNaoExiste() {

        when(orcamentoRepository.findByCodigoOrcamento("ORC999")).thenReturn(Optional.empty());


        boolean resultado = agendamentoService.verificarCodigoOrcamento("ORC999");


        assertFalse(resultado);
        verify(orcamentoRepository, times(1)).findByCodigoOrcamento("ORC999");
        verify(agendamentoRepository, never()).findByOrcamentoCodigoOrcamento(any());
    }

    @Test
    @DisplayName("Deve retornar falso quando orçamento já está agendado")
    void deveRetornarFalsoQuandoOrcamentoJaAgendado() {

        when(orcamentoRepository.findByCodigoOrcamento("ORC123")).thenReturn(Optional.of(orcamento));
        when(agendamentoRepository.findByOrcamentoCodigoOrcamento("ORC123")).thenReturn(Optional.of(agendamento));


        boolean resultado = agendamentoService.verificarCodigoOrcamento("ORC123");


        assertFalse(resultado);
    }

    @Test
    @DisplayName("Deve retornar datas ocupadas")
    void deveRetornarDatasOcupadas() {

        LocalDateTime amanha = LocalDateTime.now().plusDays(1);
        LocalDateTime doisDias = LocalDateTime.now().plusDays(2);

        Agendamento agendamento1 = new Agendamento();
        agendamento1.setDataHora(amanha);
        agendamento1.setStatus(StatusAgendamento.AGUARDANDO);
        agendamento1.setUsuario(usuario);
        agendamento1.setOrcamento(orcamento);

        Agendamento agendamento2 = new Agendamento();
        agendamento2.setDataHora(doisDias);
        agendamento2.setStatus(StatusAgendamento.CONFIRMADO);
        agendamento2.setUsuario(usuario);
        agendamento2.setOrcamento(orcamento);

        when(agendamentoRepository.findAll()).thenReturn(Arrays.asList(agendamento1, agendamento2));


        List<String> datas = agendamentoService.getDatasOcupadas();


        assertNotNull(datas);
        assertEquals(2, datas.size());
        assertTrue(datas.contains(amanha.toLocalDate().toString()));
        assertTrue(datas.contains(doisDias.toLocalDate().toString()));
    }

    @Test
    @DisplayName("Não deve retornar datas passadas nas datas ocupadas")
    void naoDeveRetornarDatasPassadas() {

        LocalDateTime ontem = LocalDateTime.now().minusDays(1);
        Agendamento agendamentoPassado = new Agendamento();
        agendamentoPassado.setDataHora(ontem);
        agendamentoPassado.setStatus(StatusAgendamento.CONFIRMADO);
        agendamentoPassado.setUsuario(usuario);
        agendamentoPassado.setOrcamento(orcamento);

        when(agendamentoRepository.findAll()).thenReturn(Arrays.asList(agendamentoPassado));


        List<String> datas = agendamentoService.getDatasOcupadas();


        assertNotNull(datas);
        assertTrue(datas.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar agendamento completo")
    void deveRetornarAgendamentoCompleto() {

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));


        DetalhesAgendamentoOutput resultado = agendamentoService.getAgendamentoCompleto(1L);


        assertNotNull(resultado);
        assertEquals(1L, resultado.id());
        assertEquals("João Silva", resultado.nomeUsuario());
        assertEquals("joao@email.com", resultado.emailUsuario());
        assertEquals("Dragão nas costas", resultado.ideia());
        assertEquals(20.5, resultado.tamanho());
        assertEquals("Preto e Vermelho", resultado.cores());
        assertEquals("Costas", resultado.localCorpo());
        verify(agendamentoRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Deve retornar agendamentos por usuário")
    void deveRetornarAgendamentosPorUsuario() {

        List<Agendamento> agendamentos = Arrays.asList(agendamento);
        when(agendamentoRepository.findByUsuarioId(1L)).thenReturn(agendamentos);


        List<DetalhesAgendamentoOutput> resultado = agendamentoService.getAgendamentosPorUsuario(1L);


        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("João Silva", resultado.get(0).nomeUsuario());
        verify(agendamentoRepository, times(1)).findByUsuarioId(1L);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando usuário não tem agendamentos")
    void deveRetornarListaVaziaQuandoUsuarioNaoTemAgendamentos() {

        when(agendamentoRepository.findByUsuarioId(999L)).thenReturn(Arrays.asList());


        List<DetalhesAgendamentoOutput> resultado = agendamentoService.getAgendamentosPorUsuario(999L);


        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve atualizar orçamento de um agendamento")
    void deveAtualizarOrcamentoDeAgendamento() {

        Orcamento novoOrcamento = new Orcamento("ORC456", "João Silva", "joao@email.com",
                "Leão no braço", 15.0, "Colorido", "Braço", null);

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(orcamentoRepository.findByCodigoOrcamento("ORC456")).thenReturn(Optional.of(novoOrcamento));
        
        Agendamento agendamentoAtualizado = new Agendamento();
        agendamentoAtualizado.setId(1L);
        agendamentoAtualizado.setDataHora(dataHora);
        agendamentoAtualizado.setStatus(StatusAgendamento.AGUARDANDO);
        agendamentoAtualizado.setUsuario(usuario);
        agendamentoAtualizado.setOrcamento(novoOrcamento);
        
        when(agendamentoRepository.save(any(Agendamento.class))).thenReturn(agendamentoAtualizado);


        DetalhesAgendamentoOutput resultado = agendamentoService.atualizarOrcamento(1L, "ORC456");


        assertNotNull(resultado);
        verify(agendamentoRepository, times(1)).findById(1L);
        verify(orcamentoRepository, times(1)).findByCodigoOrcamento("ORC456");
        verify(agendamentoRepository, times(1)).save(any(Agendamento.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar orçamento de agendamento inexistente")
    void deveLancarExcecaoAoAtualizarOrcamentoDeAgendamentoInexistente() {

        when(agendamentoRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> agendamentoService.atualizarOrcamento(999L, "ORC456")
        );
        assertEquals("Agendamento não encontrado.", exception.getMessage());
        verify(agendamentoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar com orçamento inexistente")
    void deveLancarExcecaoAoAtualizarComOrcamentoInexistente() {

        when(agendamentoRepository.findById(1L)).thenReturn(Optional.of(agendamento));
        when(orcamentoRepository.findByCodigoOrcamento("ORC999")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> agendamentoService.atualizarOrcamento(1L, "ORC999")
        );
        assertEquals("Orçamento não encontrado.", exception.getMessage());
        verify(agendamentoRepository, never()).save(any());
    }
}

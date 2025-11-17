package hub.orcana.service;

import hub.orcana.dto.estoque.CadastroMaterialInput;
import hub.orcana.dto.estoque.DetalhesMaterialOutput;
import hub.orcana.exception.DependenciaNaoEncontradaException;
import hub.orcana.observer.EstoqueObserver;
import hub.orcana.tables.Estoque;
import hub.orcana.tables.repository.EstoqueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @Mock
    private EstoqueRepository repository;

    @Mock
    private EmailService emailService;

    private EstoqueService estoqueService;

    private List<Estoque> materiaisEsperados;

    @BeforeEach
    void setUp() {
        estoqueService = new EstoqueService(repository, emailService);

        Estoque material1 = new Estoque("Tinta Preta", 10.0, "ml", 5.0);
        material1.setId(1L);

        Estoque material2 = new Estoque("Agulha", 25.0, "unidade", 10.0);
        material2.setId(2L);

        Estoque material3 = new Estoque("Luva Descartável", 100.0, "unidade", 20.0);
        material3.setId(3L);

        materiaisEsperados = Arrays.asList(material1, material2, material3);
    }

    @Test
    @DisplayName("Deve instanciar remover o observador com sucesso")
    void attachDetach_deveInstanciarRemoverObservadorComSucesso() {
        EstoqueObserver observerMock = mock(EstoqueObserver.class);

        estoqueService.attach(observerMock);
        estoqueService.notifyObservers(
                "Tinta Preta",
                10.0,
                5.0
        );
        verify(observerMock, times(1)).updateEstoque(
                "Tinta Preta",
                10.0,
                5.0
        );
        estoqueService.detach(observerMock);
        estoqueService.notifyObservers(
                "Tinta Preta",
                10.0,
                5.0
        );
        verifyNoMoreInteractions(observerMock);
    }

    @Test
    @DisplayName("Deve retornar lista de materiais com sucesso")
    void getEstoque_deveRetornarListaDeMateriaisComSucesso() {
        when(repository.findAll()).thenReturn(materiaisEsperados);

        List<DetalhesMaterialOutput> resultado = estoqueService.getEstoque();

        assertNotNull(resultado);
        assertEquals(3, resultado.size());

        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista de materiais filtrada por nome")
    void getEstoqueByNome_deveRetornarListaDeMateriaisFiltradaPorNome() {
        String nomeFiltro = "Tinta Preta";
        List<Estoque> materiaisFiltrados = materiaisEsperados.stream()
                .filter(m -> m.getNome().equalsIgnoreCase(nomeFiltro))
                .toList();

        when(repository.findEstoqueByNome(nomeFiltro)).thenReturn(materiaisFiltrados);

        DetalhesMaterialOutput resultado = estoqueService.getEstoqueByNome(nomeFiltro);
        assertNotNull(resultado);
        assertEquals("Tinta Preta", resultado.nome());
    }

    @Test
    @DisplayName("Deve lançar exceção quando material não for encontrado por nome")
    void getEstoqueByNome_deveLancarExcecaoQuandoMaterialNaoForEncontradoPorNome() {
        String nomeFiltro = "Material Inexistente";

        when(repository.findEstoqueByNome(nomeFiltro)).thenReturn(List.of());

        assertThrows(DependenciaNaoEncontradaException.class, () -> {
            estoqueService.getEstoqueByNome(nomeFiltro);
        });

        verify(repository, times(1)).findEstoqueByNome(nomeFiltro);
    }

    @Test
    @DisplayName("Deve cadastrar novo material com sucesso")
    void postEstoque_deveCadastrarNovoMaterialComSucesso() {
        var cadastroInput = new CadastroMaterialInput(
                "Papel",
                500.0,
                "folhas",
                100.0
        );

        when(repository.existsByNomeIgnoreCase("Papel")).thenReturn(false);

        DetalhesMaterialOutput resultado = estoqueService.postEstoque(cadastroInput);

        assertNotNull(resultado);
        assertEquals("Papel", resultado.nome());
        assertEquals(500.0, resultado.quantidade());
        assertEquals("folhas", resultado.unidadeMedida());
        assertEquals(100.0, resultado.minAviso());

        verify(repository, times(1)).existsByNomeIgnoreCase("Papel");
        verify(repository, times(1)).save(any(Estoque.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar material já existente")
    void postEstoque_deveLancarExcecaoAoTentarCadastrarMaterialJaExistente() {
        var cadastroInput = new CadastroMaterialInput(
                "Tinta Preta",
                10.0,
                "ml",
                5.0
        );

        when(repository.existsByNomeIgnoreCase("Tinta Preta")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> {
            estoqueService.postEstoque(cadastroInput);
        });
    }

    @Test
    @DisplayName("Deve atualizar material com sucesso")
    void putEstoqueById_deveAtualizarMaterialComSucesso() {
        Long idExistente = 1L;
        var cadastroInput = new CadastroMaterialInput(
                "Tinta Preta Atualizada",
                15.0,
                "ml",
                7.0
        );

        when(repository.existsById(idExistente)).thenReturn(true);
        when(repository.findById(idExistente)).thenReturn(Optional.of(materiaisEsperados.getFirst()));

        DetalhesMaterialOutput resultado = estoqueService.putEstoqueById(idExistente, cadastroInput);
        assertNotNull(resultado);
        assertEquals("Tinta Preta Atualizada", resultado.nome());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar material inexistente")
    void putEstoqueById_deveLancarExcecaoAoTentarAtualizarMaterialInexistente() {
        Long idInexistente = 999L;
        var cadastroInput = new CadastroMaterialInput(
                "Material Inexistente",
                0.0,
                "unidade",
                0.0
        );

        when(repository.existsById(idInexistente)).thenReturn(false);
        assertThrows(DependenciaNaoEncontradaException.class, () -> {
            estoqueService.putEstoqueById(idInexistente, cadastroInput);
        });
    }

    @Test
    @DisplayName("Deve atualizar quantidade de material com sucesso")
    void atualizarQuantidadeById_deveAtualizarQuantidadeComSucesso() {
        Long idExistente = 1L;
        double novaQuantidade = 20.0;

        when(repository.existsById(idExistente)).thenReturn(true);
        when(repository.findById(idExistente)).thenReturn(Optional.of(materiaisEsperados.getFirst()));

        estoqueService.atualizarQuantidadeById(idExistente, novaQuantidade);

        verify(repository, times(1)).save(any(Estoque.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar quantidade de material inexistente")
    void atualizarQuantidadeById_deveLancarExcecaoAoTentarAtualizarQuantidadeDeMaterialInexistente() {
        Long idInexistente = 999L;
        double novaQuantidade = 20.0;

        when(repository.existsById(idInexistente)).thenReturn(false);

        assertThrows(DependenciaNaoEncontradaException.class, () -> {
            estoqueService.atualizarQuantidadeById(idInexistente, novaQuantidade);
        });
    }

    @Test
    @DisplayName("Deve excluir material com sucesso")
    void deleteEstoqueById_deveExcluirMaterialComSucesso() {
        Long idExistente = 1L;

        when(repository.existsById(idExistente)).thenReturn(true).thenReturn(false);

        estoqueService.deleteEstoqueById(idExistente);

        verify(repository, times(1)).deleteById(idExistente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir material inexistente")
    void deleteEstoqueById_deveLancarExcecaoAoTentarExcluirMaterialInexistente() {
        Long idInexistente = 999L;

        when(repository.existsById(idInexistente)).thenReturn(false);
        assertThrows(DependenciaNaoEncontradaException.class, () -> {
            estoqueService.deleteEstoqueById(idInexistente);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção se material não for excluído corretamente")
    void deleteEstoqueById_deveLancarExcecaoSeMaterialNaoForExcluidoCorretamente() {
        Long idExistente = 1L;

        when(repository.existsById(idExistente)).thenReturn(true).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            estoqueService.deleteEstoqueById(idExistente);
        });
    }

}
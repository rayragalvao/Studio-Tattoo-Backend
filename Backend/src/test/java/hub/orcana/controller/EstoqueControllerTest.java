package hub.orcana.controller;

import hub.orcana.dto.estoque.CadastroMaterialInput;
import hub.orcana.dto.estoque.DetalhesMaterialOutput;
import hub.orcana.exception.DependenciaNaoEncontradaException;
import hub.orcana.service.EstoqueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstoqueControllerTest {

    @Mock
    private EstoqueService estoqueService;

    @InjectMocks
    private EstoqueController estoqueController;

    private List<DetalhesMaterialOutput> materiaisEsperados;

    @BeforeEach
    void setUp() {
        // Criando dados de teste
        DetalhesMaterialOutput material1 = new DetalhesMaterialOutput(
                1L,
                "Tinta Preta",
                50.0,
                "ml",
                10.0
        );

        DetalhesMaterialOutput material2 = new DetalhesMaterialOutput(
                2L,
                "Agulha",
                25.0,
                "unidade",
                5.0
        );

        DetalhesMaterialOutput material3 = new DetalhesMaterialOutput(
                3L,
                "Luva Descartável",
                100.0,
                "unidade",
                20.0
        );

        materiaisEsperados = Arrays.asList(material1, material2, material3);
    }

    @Test
    @DisplayName("Deve retornar o estoque com sucesso")
    void getEstoque_deveRetornarListaComSucesso() {
        when(estoqueService.getEstoque()).thenReturn(materiaisEsperados);

        ResponseEntity<List<DetalhesMaterialOutput>> materiais = estoqueController.getEstoque();

        assertNotNull(materiais);
        assertEquals(HttpStatus.OK, materiais.getStatusCode());
        assertNotNull(materiais.getBody());
        assertEquals(3, materiais.getBody().size());

        verify(estoqueService, times(1)).getEstoque();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver materiais em estoque")
    void getEstoque_deveRetornarCorpoVazio() {
        when(estoqueService.getEstoque()).thenReturn(List.of());

        ResponseEntity<List<DetalhesMaterialOutput>> materiais = estoqueController.getEstoque();

        assertEquals(HttpStatus.NO_CONTENT, materiais.getStatusCode());
        assertNull(materiais.getBody());
    }

    @Test
    @DisplayName("Deve lidar com exceção ao obter o estoque")
    void getEstoque_deveLidarComExcecao() {
        when(estoqueService.getEstoque()).thenThrow(new RuntimeException("Erro ao acessar o banco de dados"));
        Exception exception = assertThrows(RuntimeException.class, () -> {
            estoqueController.getEstoque();
        });

        assertEquals("Erro ao acessar o banco de dados", exception.getMessage());
        verify(estoqueService, times(1)).getEstoque();
    }

    @Test
    @DisplayName("Deve encontrar o material com sucesso pelo nome")
    void getEstoqueByNome_deveRetornarListaFiltradaComSucesso() {
        String nomeMaterial = "Tinta Preta";
        DetalhesMaterialOutput materiaisFiltrados = materiaisEsperados.getFirst();

        when(estoqueService.getEstoqueByNome(nomeMaterial)).thenReturn(materiaisFiltrados);

        ResponseEntity<DetalhesMaterialOutput> material = estoqueController.getEstoqueByNome(nomeMaterial);

        assertNotNull(material);
        assertEquals(HttpStatus.OK, material.getStatusCode());
        assertNotNull(material.getBody());
        assertEquals("Tinta Preta", material.getBody().nome());

        verify(estoqueService, times(1)).getEstoqueByNome(nomeMaterial);
    }

    @Test
    @DisplayName("Deve retornar erro 404 quando não encontrar material pelo nome")
    void getEstoqueByNome_deveRetornarMaterialNaoEncontrado() {
        String nomeMaterial = "Material Inexistente";

        when(estoqueService.getEstoqueByNome(nomeMaterial))
                .thenThrow(new DependenciaNaoEncontradaException("Material Inexistente"));

        ResponseEntity<DetalhesMaterialOutput> material = estoqueController.getEstoqueByNome(nomeMaterial);

        assertEquals(HttpStatus.NOT_FOUND, material.getStatusCode());
        verify(estoqueService, times(1)).getEstoqueByNome(nomeMaterial);
    }

    @Test
    @DisplayName("Deve retornar erro 400 se nome de material estiver vazio")
    void getEstoqueByNome_deveLancarExcessaoQuandoNomeVazio() {
        String nomeMaterial = "   ";

        ResponseEntity<DetalhesMaterialOutput> materiais = estoqueController.getEstoqueByNome(nomeMaterial);

        assertEquals(HttpStatus.BAD_REQUEST, materiais.getStatusCode());
        verify(estoqueService, times(0)).getEstoqueByNome(anyString());
    }

    @Test
    @DisplayName("Deve lidar com exceção ao buscar material pelo nome")
    void getEstoqueByNome_deveLidarComExcecao() {
        String nomeMaterial = "Tinta Preta";

        when(estoqueService.getEstoqueByNome(nomeMaterial))
                .thenThrow(new RuntimeException("Erro ao acessar o banco de dados"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            estoqueController.getEstoqueByNome(nomeMaterial);
        });

        assertEquals("Erro ao acessar o banco de dados", exception.getMessage());
        verify(estoqueService, times(1)).getEstoqueByNome(nomeMaterial);
    }

    @Test
    @DisplayName("Deve cadastrar material com sucesso")
    void cadastrarMaterial_deveCadastrarComSucesso() {
        CadastroMaterialInput input = new CadastroMaterialInput(
                "Tinta Vermelha",
                30.0,
                "ml",
                5.0
        );
        DetalhesMaterialOutput output = new DetalhesMaterialOutput(
                1L,
                "Tinta Vermelha",
                30.0,
                "ml",
                5.0
        );

        when(estoqueService.postEstoque(input)).thenReturn(output);

        ResponseEntity<DetalhesMaterialOutput> response = estoqueController.postEstoque(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Tinta Vermelha", response.getBody().nome());
    }

    @Test
    @DisplayName("Deve lidar com exceção ao cadastrar material")
    void cadastrarMaterial_deveLidarComExcecao() {
        CadastroMaterialInput input = new CadastroMaterialInput(
                "Tinta Vermelha",
                30.0,
                "ml",
                5.0
        );

        when(estoqueService.postEstoque(input))
                .thenThrow(new RuntimeException("Erro ao acessar o banco de dados"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            estoqueController.postEstoque(input);
        });

        assertEquals("Erro ao acessar o banco de dados", exception.getMessage());
        verify(estoqueService, times(1)).postEstoque(input);
    }

    @Test
    @DisplayName("Deve atualizar quantidade do material com sucesso")
    void atualizarQuantidadeById_deveAtualizarComSucesso() {
        Long id = 1L;
        Double novaQuantidade = 75.0;
        DetalhesMaterialOutput materialAtualizado = new DetalhesMaterialOutput(
                1L,
                "Tinta Preta",
                75.0,
                "ml",
                10.0
        );

        when(estoqueService.atualizarQuantidadeById(id, novaQuantidade)).thenReturn(materialAtualizado);

        ResponseEntity<DetalhesMaterialOutput> response = estoqueController.atualizarQuantidadeById(id, novaQuantidade);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(75.0, response.getBody().quantidade());
        assertEquals("Tinta Preta", response.getBody().nome());
        verify(estoqueService, times(1)).atualizarQuantidadeById(id, novaQuantidade);
    }

    @Test
    @DisplayName("Deve lidar com exceção ao atualizar quantidade do material")
    void atualizarQuantidadeById_deveLidarComExcecao() {
        Long id = 1L;
        Double novaQuantidade = 75.0;

        when(estoqueService.atualizarQuantidadeById(id, novaQuantidade))
                .thenThrow(new RuntimeException("Erro ao acessar o banco de dados"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            estoqueController.atualizarQuantidadeById(id, novaQuantidade);
        });

        assertEquals("Erro ao acessar o banco de dados", exception.getMessage());
        verify(estoqueService, times(1)).atualizarQuantidadeById(id, novaQuantidade);
    }

    @Test
    @DisplayName("Deve lidar com excessão quando o com material não foi encontrado")
    void atualizarQuantidadeById_deveLidarComExcessaoQuandoMaterialNaoEncontrado() {
        Long id = 999L;
        Double novaQuantidade = 50.0;

        when(estoqueService.atualizarQuantidadeById(id, novaQuantidade))
                .thenThrow(new DependenciaNaoEncontradaException("Material não encontrado"));

        Exception exception = assertThrows(DependenciaNaoEncontradaException.class, () -> {
            estoqueController.atualizarQuantidadeById(id, novaQuantidade);
        });

        assertEquals("Material não encontrado não encontrado(a) no sistema", exception.getMessage());
        verify(estoqueService, times(1)).atualizarQuantidadeById(id, novaQuantidade);
    }

    @Test
    @DisplayName("Deve deletar material com sucesso")
    void deletarMaterialById_deveDeletarComSucesso() {
        Long id = 1L;

        doNothing().when(estoqueService).deleteEstoqueById(id);

        ResponseEntity<Void> response = estoqueController.deleteEstoqueById(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(estoqueService, times(1)).deleteEstoqueById(id);
    }

    @Test
    @DisplayName("Deve lidar com exceção ao deletar material")
    void deletarMaterialById_deveLidarComExcecao() {
        Long id = 1L;

        doThrow(new RuntimeException("Erro ao acessar o banco de dados"))
                .when(estoqueService).deleteEstoqueById(id);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            estoqueController.deleteEstoqueById(id);
        });

        assertEquals("Erro ao acessar o banco de dados", exception.getMessage());
        verify(estoqueService, times(1)).deleteEstoqueById(id);
    }

    @Test
    @DisplayName("Deve lidar com material não encontrado ao deletar")
    void deletarMaterialById_deveLidarComExcessaoQuandoMaterialNaoEncontrado() {
        Long id = 999L;

        doThrow(new DependenciaNaoEncontradaException("Material não encontrado"))
                .when(estoqueService).deleteEstoqueById(id);

        Exception exception = assertThrows(DependenciaNaoEncontradaException.class, () -> {
            estoqueController.deleteEstoqueById(id);
        });

        assertEquals("Material não encontrado não encontrado(a) no sistema", exception.getMessage());
        verify(estoqueService, times(1)).deleteEstoqueById(id);
    }

    @Test
    @DisplayName("Deve atualizar material pelo ID com sucesso")
    void putEstoqueById_deveAtualizarComSucesso() {
        Long id = 1L;
        CadastroMaterialInput input = new CadastroMaterialInput(
                "Tinta Preta",
                60.0,
                "ml",
                10.0
        );
        DetalhesMaterialOutput atualizado = new DetalhesMaterialOutput(
                1L,
                "Tinta Preta",
                60.0,
                "ml",
                10.0
        );

        when(estoqueService.putEstoqueById(id, input)).thenReturn(atualizado);

        ResponseEntity<DetalhesMaterialOutput> response = estoqueController.putEstoqueById(id, input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals("Tinta Preta", response.getBody().nome());
        assertEquals(60.0, response.getBody().quantidade());
        assertEquals("ml", response.getBody().unidadeMedida());
        assertEquals(10.0, response.getBody().minAviso());

        verify(estoqueService, times(1)).putEstoqueById(id, input);
    }

    @Test
    @DisplayName("Deve lidar com exceção ao atualizar material pelo ID")
    void putEstoqueById_deveLidarComExcecao() {
        Long id = 1L;
        CadastroMaterialInput input = new CadastroMaterialInput(
                "Tinta Preta",
                60.0,
                "ml",
                10.0
        );

        when(estoqueService.putEstoqueById(id, input))
                .thenThrow(new RuntimeException("Erro ao acessar o banco de dados"));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            estoqueController.putEstoqueById(id, input);
        });

        assertEquals("Erro ao acessar o banco de dados", exception.getMessage());
        verify(estoqueService, times(1)).putEstoqueById(id, input);
    }

    @Test
    @DisplayName("Deve lançar exceção de material não encontrado ao atualizar por ID")
    void putEstoqueById_deveLidarComExcessaoQuandoMaterialNaoEncontrado() {
        Long id = 999L;
        CadastroMaterialInput input = new CadastroMaterialInput(
                "Material Inexistente",
                10.0,
                "unidade",
                5.0
        );

        when(estoqueService.putEstoqueById(id, input))
                .thenThrow(new DependenciaNaoEncontradaException("Material Inexistente"));

        Exception exception = assertThrows(DependenciaNaoEncontradaException.class, () -> {
            estoqueController.putEstoqueById(id, input);
        });

        assertEquals("Material Inexistente não encontrado(a) no sistema", exception.getMessage());
        verify(estoqueService, times(1)).putEstoqueById(id, input);
    }
}
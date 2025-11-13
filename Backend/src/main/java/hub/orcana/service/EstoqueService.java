package hub.orcana.service;

import hub.orcana.dto.estoque.CadastroMaterialInput;
import hub.orcana.dto.estoque.DetalhesMaterialOutput;
import hub.orcana.exception.DependenciaNaoEncontradaException;
import hub.orcana.tables.Estoque;
import hub.orcana.tables.repository.EstoqueRepository;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import hub.orcana.observer.EstoqueObserver;
import hub.orcana.observer.EstoqueSubject;
import java.util.ArrayList;

import java.util.List;

@Service
public class EstoqueService implements EstoqueSubject{
    private final EstoqueRepository repository;
    private final List<EstoqueObserver> observers;

    public EstoqueService(EstoqueRepository repository, EmailService emailService) {
        this.repository = repository;
        this.observers = new ArrayList<>();
        this.attach(emailService);
    }


    @Override
    public void attach(EstoqueObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void detach(EstoqueObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String materialNome, Double quantidadeAtual, Double minAviso) {
        for (EstoqueObserver observer : observers) {
            observer.updateEstoque(materialNome, quantidadeAtual, minAviso);
        }
    }






    // Lista todos os materiais existentes
    public List<DetalhesMaterialOutput> getEstoque() {
        List<DetalhesMaterialOutput> materiais = repository.findAll().stream()
                .map(atual -> new DetalhesMaterialOutput(
                        atual.getId(),
                        atual.getNome(),
                        atual.getQuantidade(),
                        atual.getUnidadeMedida(),
                        atual.getMinAviso()
                ))
                .toList();
        return materiais;
    }

    // Busca material pelo nome
    public List<DetalhesMaterialOutput> getEstoqueByNome(String nomeMaterial) {
        var materiais = repository.findAll()
                .stream()
                .filter(atual -> nomeMaterial.equals(atual.getNome()))
                .map(atual -> new DetalhesMaterialOutput(
                        atual.getId(),
                        atual.getNome(),
                        atual.getQuantidade(),
                        atual.getUnidadeMedida(),
                        atual.getMinAviso()
                ))
                .toList();

        if (materiais.isEmpty()) {
            throw new DependenciaNaoEncontradaException("Material");
        }
        return materiais;
    }

    // Cadastra um novo material
    public DetalhesMaterialOutput postEstoque(CadastroMaterialInput estoque) {
        if (repository.existsByNomeIgnoreCase(estoque.nome())) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(409), "Material já cadastrado.");
        }

        Estoque novoMaterial = new Estoque(
                estoque.nome(),
                estoque.quantidade(),
                estoque.unidadeMedida(),
                estoque.minAviso()
        );
        repository.save(novoMaterial);
        DetalhesMaterialOutput detalhes = new DetalhesMaterialOutput(
                novoMaterial.getId(),
                novoMaterial.getNome(),
                novoMaterial.getQuantidade(),
                novoMaterial.getUnidadeMedida(),
                novoMaterial.getMinAviso()
        );
        return detalhes;
    }

    // Atualiza um material existente pelo ID
    public DetalhesMaterialOutput putEstoqueById(Long id, CadastroMaterialInput estoque) {
        if (!repository.existsById(id)) {
            throw new DependenciaNaoEncontradaException("Material");
        }

        Estoque existente = repository.findById(id).orElseThrow();
        existente = new Estoque(
                estoque.nome(),
                estoque.quantidade(),
                estoque.unidadeMedida(),
                estoque.minAviso()
        );
        existente.setId(id);
        repository.save(existente);
        DetalhesMaterialOutput detalhes = new DetalhesMaterialOutput(
                existente.getId(),
                existente.getNome(),
                existente.getQuantidade(),
                existente.getUnidadeMedida(),
                existente.getMinAviso()
        );
        return detalhes;
    }

    public DetalhesMaterialOutput atualizarQuantidadeById(Long id, Double qtd) {
        if (!repository.existsById(id)) {
            throw new DependenciaNaoEncontradaException("Material");
        }

        Estoque existente = repository.findById(id).orElseThrow();
        existente.setQuantidade(qtd);
        repository.save(existente);

        notifyObservers(existente.getNome(), existente.getQuantidade(), existente.getMinAviso());

        DetalhesMaterialOutput detalhes = new DetalhesMaterialOutput(
                existente.getId(),
                existente.getNome(),
                existente.getQuantidade(),
                existente.getUnidadeMedida(),
                existente.getMinAviso()
        );
        return detalhes;
    }

    // Exclui um estoque existente pelo ID
    public void deleteEstoqueById(Long id) {
            if (!repository.existsById(id)) {
                throw new DependenciaNaoEncontradaException("Material");
            }
            repository.deleteById(id);

            if (repository.existsById(id)) {
                throw new IllegalArgumentException("Erro ao excluir material.");
            }
    }
}



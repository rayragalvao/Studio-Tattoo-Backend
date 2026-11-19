package hub.orcana.service;

import hub.orcana.tables.SefazProdutoReferencia;
import hub.orcana.tables.repository.SefazProdutoReferenciaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SefazProdutoReferenciaService {

    private final SefazProdutoReferenciaRepository repository;

    public SefazProdutoReferenciaService(
            SefazProdutoReferenciaRepository repository) {
        this.repository = repository;
    }

    public List<SefazProdutoReferencia> listarTodos() {
        return repository.findAll();
    }
}
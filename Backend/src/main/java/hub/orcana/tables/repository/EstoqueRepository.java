package hub.orcana.tables.repository;

import hub.orcana.tables.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    boolean existsByNome(String nome);
    boolean existsByNomeIgnoreCase(String nome);

    List<Estoque> findEstoqueByNome(String nome);
}

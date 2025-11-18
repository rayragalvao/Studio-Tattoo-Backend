package hub.orcana.tables.repository;

import hub.orcana.dto.agendamento.DetalhesAgendamentoOutput;
import hub.orcana.tables.Estoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EstoqueRepository extends JpaRepository<Estoque, Long> {
    boolean existsByNome(String nome);
    boolean existsByNomeIgnoreCase(String nome);

    List<Estoque> findEstoqueByNome(String nome);

    @Query("SELECT e FROM Estoque e WHERE e.quantidade < e.minAviso")
    List<Estoque> findAllByQuantidadeLessThanMinAviso();
}

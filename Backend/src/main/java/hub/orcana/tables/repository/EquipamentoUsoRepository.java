package hub.orcana.tables.repository;

import hub.orcana.tables.EquipamentoUso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EquipamentoUsoRepository extends JpaRepository<EquipamentoUso, Long> {

    @Modifying
    @Query("DELETE FROM EquipamentoUso eu WHERE eu.relatorio.id = :relatorioId")
    void deleteByRelatorioId(@Param("relatorioId") Long relatorioId);
}

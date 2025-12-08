package hub.orcana.tables.repository;

import hub.orcana.tables.Relatorio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RelatorioRepository extends JpaRepository<Relatorio, Long> {
    Optional<Relatorio> findByAgendamentoId(Long agendamentoId);
}



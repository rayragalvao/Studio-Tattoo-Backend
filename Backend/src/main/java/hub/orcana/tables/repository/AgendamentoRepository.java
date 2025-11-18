package hub.orcana.tables.repository;

import hub.orcana.tables.Agendamento;
import hub.orcana.tables.StatusAgendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findByUsuarioId(Long usuarioId);

    List<Agendamento> findAllByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    Optional<Agendamento> findTopByStatusOrderByDataHoraAsc(StatusAgendamento status);

    @Query("SELECT COALESCE(SUM(o.valor), 0.0) " +
            "FROM Agendamento a " +
            "JOIN a.orcamento o " +
            "WHERE a.status = 'FINALIZADO' " +
            "AND a.dataHora BETWEEN :inicio AND :fim")
    Double sumValorTotalAgendamentosPorPeriodo(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );


    Optional<Agendamento> findByOrcamentoCodigoOrcamento(String codigoOrcamento);

    @Query("SELECT a FROM Agendamento a WHERE FUNCTION('DATE', a.dataHora) = FUNCTION('DATE', :data)")
    List<Agendamento> findByData(@Param("data") LocalDateTime data);

    @Query("SELECT DISTINCT FUNCTION('DATE', a.dataHora) FROM Agendamento a WHERE a.dataHora >= :dataInicio")
    List<LocalDateTime> findDatasComAgendamento(@Param("dataInicio") LocalDateTime dataInicio);
}
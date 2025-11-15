package hub.orcana.tables.repository;

import hub.orcana.tables.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    // busca todos os agendamentos de um usuário específico
    List<Agendamento> findByUsuarioId(Long usuarioId);
    
    // busca agendamento por código de orçamento
    Optional<Agendamento> findByOrcamentoCodigoOrcamento(String codigoOrcamento);
    
    // busca agendamentos por data (somente a parte da data, ignorando hora)
    @Query("SELECT a FROM Agendamento a WHERE FUNCTION('DATE', a.dataHora) = FUNCTION('DATE', :data)")
    List<Agendamento> findByData(@Param("data") LocalDateTime data);
    
    // busca todas as datas que possuem agendamentos a partir de hoje
    @Query("SELECT DISTINCT FUNCTION('DATE', a.dataHora) FROM Agendamento a WHERE a.dataHora >= :dataInicio")
    List<LocalDateTime> findDatasComAgendamento(@Param("dataInicio") LocalDateTime dataInicio);
}
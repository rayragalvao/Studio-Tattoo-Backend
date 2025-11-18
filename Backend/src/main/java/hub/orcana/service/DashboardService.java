package hub.orcana.service;

import hub.orcana.tables.StatusAgendamento; // 1. IMPORTAR O ENUM
import hub.orcana.tables.repository.AgendamentoRepository;
import hub.orcana.tables.repository.EstoqueRepository;
import hub.orcana.tables.repository.OrcamentoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final AgendamentoRepository agendamentoRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final EstoqueRepository estoqueRepository;

    public DashboardService(AgendamentoRepository agendamentoRepository,
                            OrcamentoRepository orcamentoRepository,
                            EstoqueRepository estoqueRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.orcamentoRepository = orcamentoRepository;
        this.estoqueRepository = estoqueRepository;
    }


    public List<Double> getFaturamentoUltimos12Meses() {
        List<Double> faturamentoMensal = new ArrayList<>();
        LocalDateTime hoje = LocalDate.now().atTime(23, 59, 59);
        for (int i = 0; i < 12; i++) {
            LocalDateTime fimDoPeriodo = hoje.minusMonths(i);
            LocalDateTime inicioDoPeriodo = fimDoPeriodo.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

            Double faturamento = agendamentoRepository.sumValorTotalAgendamentosPorPeriodo(inicioDoPeriodo, fimDoPeriodo);
            faturamentoMensal.add(faturamento);
        }
        java.util.Collections.reverse(faturamentoMensal);

        return faturamentoMensal;
    }

    public Map<String, Object> getDashboardKPIs() {
        var proximoAgendamentoOptional = agendamentoRepository.findTopByStatusOrderByDataHoraAsc(StatusAgendamento.PENDENTE);
        long orcamentosPendentes = orcamentoRepository.countByStatus("PENDENTE");
        LocalDateTime inicioDoDia = LocalDate.now().atStartOfDay();
        LocalDateTime fimDoDia = LocalDate.now().atTime(23, 59, 59);

        var agendamentosDoDia = agendamentoRepository.findAllByDataHoraBetween(inicioDoDia, fimDoDia);
        var alertasEstoque = estoqueRepository.findAllAlertasEstoque();

        return Map.of(
                "proximoAgendamento", proximoAgendamentoOptional.orElse(null),
                "orcamentosPendentes", orcamentosPendentes,
                "agendamentosDoDia", agendamentosDoDia,
                "alertasEstoque", alertasEstoque
        );
    }
}
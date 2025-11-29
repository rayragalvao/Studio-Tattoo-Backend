package hub.orcana.service;

import hub.orcana.dto.dashboard.DashboardOutput;
import hub.orcana.tables.Estoque;
import hub.orcana.tables.StatusAgendamento;
import hub.orcana.tables.Agendamento;
import hub.orcana.tables.StatusOrcamento;
import hub.orcana.tables.repository.AgendamentoRepository;
import hub.orcana.tables.repository.EstoqueRepository;
import hub.orcana.tables.repository.OrcamentoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
            LocalDateTime inicioDoPeriodo = fimDoPeriodo.withDayOfMonth(1)
                    .withHour(0).withMinute(0).withSecond(0);

            // Busca agendamentos do período com orçamento carregado
            List<Agendamento> agendamentos = agendamentoRepository
                    .findAllWithOrcamentoByDataHoraBetween(inicioDoPeriodo, fimDoPeriodo);

            // Soma os valores dos orçamentos vinculados (apenas agendamentos concluídos)
            Double faturamento = agendamentos.stream()
                    .filter(a -> a.getStatus() == StatusAgendamento.CONCLUIDO) // Só conta concluídos
                    .map(a -> a.getOrcamento() != null && a.getOrcamento().getValor() != null
                            ? a.getOrcamento().getValor()
                            : 0.0)
                    .reduce(0.0, Double::sum);

            faturamentoMensal.add(faturamento);
        }

        java.util.Collections.reverse(faturamentoMensal);
        return faturamentoMensal;
    }

    public DashboardOutput getDashboardKPIs() {
        Agendamento proximoAgendamento = agendamentoRepository
                .findTopByStatusOrderByDataHoraAsc(StatusAgendamento.PENDENTE)
                .orElse(null);

        long orcamentosPendentes = orcamentoRepository.countByStatus(StatusOrcamento.PENDENTE);

        LocalDateTime inicioDoDia = LocalDate.now().atStartOfDay();
        LocalDateTime fimDoDia = LocalDate.now().atTime(23, 59, 59);

        List<Agendamento> agendamentosDoDia = agendamentoRepository.findAllByDataHoraBetween(inicioDoDia, fimDoDia);
        List<Estoque> alertasEstoque = estoqueRepository.findAllByQuantidadeLessThanMinAviso();

        return new DashboardOutput(proximoAgendamento, orcamentosPendentes, agendamentosDoDia, alertasEstoque);
    }
}
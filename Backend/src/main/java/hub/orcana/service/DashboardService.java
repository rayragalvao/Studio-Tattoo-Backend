package hub.orcana.service;

import hub.orcana.dto.dashboard.DashboardKPIsOutput;
import hub.orcana.dto.dashboard.DashboardOutput;
import hub.orcana.tables.Agendamento;
import hub.orcana.tables.Estoque;
import hub.orcana.tables.StatusAgendamento;
import hub.orcana.tables.StatusOrcamento;
import hub.orcana.tables.repository.AgendamentoRepository;
import hub.orcana.tables.repository.EstoqueRepository;
import hub.orcana.tables.repository.OrcamentoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class DashboardService {

    private final AgendamentoRepository agendamentoRepository;
    private final EstoqueRepository estoqueRepository;
    private final OrcamentoRepository orcamentoRepository;

    public DashboardService(AgendamentoRepository agendamentoRepository,
                            EstoqueRepository estoqueRepository,
                            OrcamentoRepository orcamentoRepository) {
        this.agendamentoRepository = agendamentoRepository;
        this.estoqueRepository = estoqueRepository;
        this.orcamentoRepository = orcamentoRepository;
    }

    @Transactional(readOnly = true)
    public List<Double> getFaturamentoUltimos12Meses() {
        List<Double> faturamentoMensal = new ArrayList<>();
        LocalDate hoje = LocalDate.now();

        log.info("Calculando faturamento dos últimos 12 meses a partir de: {}", hoje);

        for (int i = 11; i >= 0; i--) {
            YearMonth mesAno = YearMonth.from(hoje.minusMonths(i));
            
            LocalDateTime inicioDoPeriodo = mesAno.atDay(1)
                    .atTime(0, 0, 0);
            LocalDateTime fimDoPeriodo = mesAno.atEndOfMonth()
                    .atTime(23, 59, 59);

            log.debug("Buscando faturamento de {} a {}", inicioDoPeriodo, fimDoPeriodo);

            // Busca agendamentos do período
            List<Agendamento> agendamentos = agendamentoRepository
                    .findByDataHoraBetween(inicioDoPeriodo, fimDoPeriodo);

            // Soma os valores dos orçamentos vinculados (apenas agendamentos concluídos)
            Double faturamento = agendamentos.stream()
                    .filter(a -> a.getStatus() == StatusAgendamento.CONCLUIDO)
                    .filter(a -> a.getOrcamento() != null)
                    .filter(a -> a.getOrcamento().getValor() != null)
                    .mapToDouble(a -> a.getOrcamento().getValor())
                    .sum();

            log.debug("Faturamento do período {}: R$ {}", mesAno, faturamento);
            faturamentoMensal.add(faturamento);
        }

        log.info("Faturamento total dos 12 meses: {}", faturamentoMensal);
        return faturamentoMensal;
    }

    @Transactional(readOnly = true)
    public DashboardOutput getDashboardKPIs() {
        log.info("Buscando dados do dashboard");

        // Próximo agendamento pendente (apenas futuros)
        LocalDateTime agora = LocalDateTime.now();
        Agendamento proximoAgendamento = agendamentoRepository
                .findProximoAgendamentoPorStatus(StatusAgendamento.CONFIRMADO, agora)
                .orElse(null);
        log.debug("Próximo agendamento: {}", proximoAgendamento != null ? proximoAgendamento.getId() + " em " + proximoAgendamento.getDataHora() : "nenhum");

        // Orçamentos pendentes
        long orcamentosPendentes = orcamentoRepository.countByStatus(StatusOrcamento.PENDENTE);
        log.debug("Orçamentos pendentes: {}", orcamentosPendentes);

        // Agendamentos do dia
        LocalDate hoje = LocalDate.now();
        LocalDateTime inicioDoDia = hoje.atStartOfDay();
        LocalDateTime fimDoDia = hoje.atTime(23, 59, 59);
        List<Agendamento> agendamentosDoDia = agendamentoRepository
                .findAllByDataHoraBetween(inicioDoDia, fimDoDia);
        log.debug("Agendamentos do dia: {}", agendamentosDoDia.size());

        // Alertas de estoque
        List<Estoque> alertasEstoque = estoqueRepository.findAllByQuantidadeLessThanMinAviso();
        log.debug("Alertas de estoque: {}", alertasEstoque.size());

        DashboardOutput output = new DashboardOutput(
                proximoAgendamento,
                orcamentosPendentes,
                agendamentosDoDia,
                alertasEstoque
        );

        log.info("Dashboard carregado com sucesso");
        return output;
    }

    @Transactional(readOnly = true)
    public DashboardKPIsOutput getEstatisticas() {
        log.info("Calculando estatísticas do dashboard");

        // Total de Agendamentos
        long totalAgendamentos = agendamentoRepository.count();
        log.debug("Total de agendamentos: {}", totalAgendamentos);

        // Agendamentos Concluídos
        long agendamentosConcluidos = agendamentoRepository.countByStatus(StatusAgendamento.CONCLUIDO);
        log.debug("Agendamentos concluídos: {}", agendamentosConcluidos);

        // Faturamento Total (soma de todos os orçamentos de agendamentos concluídos)
        Double faturamentoTotal = agendamentoRepository
                .findByStatus(StatusAgendamento.CONCLUIDO)
                .stream()
                .filter(a -> a.getOrcamento() != null)
                .filter(a -> a.getOrcamento().getValor() != null)
                .mapToDouble(a -> a.getOrcamento().getValor())
                .sum();
        log.debug("Faturamento total: R$ {}", faturamentoTotal);

        // Total de Produtos (soma das quantidades de todos os itens em estoque)
        Integer totalProdutos = estoqueRepository
                .findAll()
                .stream()
                .mapToInt(e -> e.getQuantidade().intValue())
                .sum();
        log.debug("Total de produtos em estoque: {}", totalProdutos);

        DashboardKPIsOutput output = new DashboardKPIsOutput(
                totalAgendamentos,
                agendamentosConcluidos,
                faturamentoTotal,
                totalProdutos
        );

        log.info("Estatísticas calculadas: {}", output);
        return output;
    }
}
package hub.orcana.dto.dashboard;

public record DashboardKPIsOutput(
        long totalAgendamentos,
        long agendamentosConcluidos,
        Double faturamentoTotal,
        Integer totalProdutos
) {
}

package hub.orcana.dto.dashboard;

import hub.orcana.tables.Agendamento;
import hub.orcana.tables.Estoque;

import java.util.Collections;
import java.util.List;

public record DashboardOutput(
        Agendamento proximoAgendamento,
        long orcamentosPendentes,
        List<Agendamento> agendamentosDoDia,
        List<Estoque> alertasEstoque
) {
    // Construtor compacto para garantir que listas não sejam null
    public DashboardOutput {
        agendamentosDoDia = agendamentosDoDia != null ? agendamentosDoDia : Collections.emptyList();
        alertasEstoque = alertasEstoque != null ? alertasEstoque : Collections.emptyList();
    }
}
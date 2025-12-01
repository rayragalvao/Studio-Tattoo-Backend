package hub.orcana.observer;

import hub.orcana.tables.Agendamento;

public interface AgendamentoObserver {
    void updateAgendamento(Agendamento agendamento, String acao);
}
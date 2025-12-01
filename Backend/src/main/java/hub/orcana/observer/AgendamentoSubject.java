package hub.orcana.observer;

import hub.orcana.tables.Agendamento;

public interface AgendamentoSubject {
    void attach(AgendamentoObserver observer);
    void detach(AgendamentoObserver observer);
    void notifyObservers(Agendamento agendamento, String acao);
}
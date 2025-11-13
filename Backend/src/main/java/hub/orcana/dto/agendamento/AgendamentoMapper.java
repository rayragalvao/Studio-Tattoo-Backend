package hub.orcana.dto.agendamento;

import hub.orcana.tables.Agendamento;
import hub.orcana.tables.Orcamento;
import hub.orcana.tables.Usuario;

public class AgendamentoMapper {
    public static DetalhesAgendamentoOutput of (Agendamento agendamento) {
        DetalhesAgendamentoOutput dto = new DetalhesAgendamentoOutput(
                agendamento.getId(),
                agendamento.getDataHora(),
                agendamento.getStatus().name(),
                agendamento.getUsuario().getNome(),
                agendamento.getUsuario().getEmail(),
                agendamento.getOrcamento().getIdeia(),
                agendamento.getOrcamento().getTamanho(),
                agendamento.getOrcamento().getCores(),
                agendamento.getOrcamento().getLocalCorpo()
        );
        return dto;
    }

    public static Agendamento of (CadastroAgendamentoInput dto, Usuario usuario, Orcamento orcamento) {
        Agendamento agendamento = new Agendamento();

        agendamento.setUsuario(usuario);
        agendamento.setOrcamento(orcamento);
        agendamento.setDataHora(dto.dataHora());
        agendamento.setStatus(dto.status());
        return agendamento;
    }
}

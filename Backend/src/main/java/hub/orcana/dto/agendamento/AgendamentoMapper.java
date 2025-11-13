package hub.orcana.dto.agendamento;

import hub.orcana.tables.Agendamento;
import hub.orcana.tables.Orcamento;
import hub.orcana.tables.StatusAgendamento;
import hub.orcana.tables.Usuario;

import java.time.LocalDateTime;

public class AgendamentoMapper {
    public static AgendamentoDetalhadoDTO of (Agendamento agendamento) {
        AgendamentoDetalhadoDTO dto = new AgendamentoDetalhadoDTO(
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

    public static Agendamento of (CadastroAgendamento dto, Usuario usuario, Orcamento orcamento) {
        Agendamento agendamento = new Agendamento();

        agendamento.setUsuario(usuario);
        agendamento.setOrcamento(orcamento);
        agendamento.setDataHora(dto.dataHora());
        agendamento.setStatus(dto.status());
        return agendamento;
    }
}

package hub.orcana.dto.agendamento;

import hub.orcana.tables.Agendamento;
import hub.orcana.tables.Orcamento;
import hub.orcana.tables.Usuario;

import java.util.ArrayList;

public class AgendamentoMapper {

    public static DetalhesAgendamentoOutput of (Agendamento agendamento) {
        DetalhesAgendamentoOutput dto = new DetalhesAgendamentoOutput(
                agendamento.getId(),
                agendamento.getDataHora(),
                agendamento.getStatus().name(),
                agendamento.getUsuario().getNome(),
                agendamento.getUsuario().getEmail(),
                agendamento.getOrcamento().getCodigoOrcamento(),
                agendamento.getOrcamento().getIdeia(),
                agendamento.getOrcamento().getTamanho(),
                agendamento.getOrcamento().getCores(),
                agendamento.getOrcamento().getLocalCorpo(),
                null,
                agendamento.getImagemReferencia(), // <- pega do agendamento
                agendamento.getTempoDuracao(),
                agendamento.getPagamentoFeito(),
                agendamento.getFormaPagamento()
        );
        return dto;
    }

    public static Agendamento of (CadastroAgendamentoInput dto, Usuario usuario, Orcamento orcamento) {
        Agendamento agendamento = new Agendamento();

        agendamento.setUsuario(usuario);
        agendamento.setOrcamento(orcamento);
        agendamento.setDataHora(dto.dataHora());
        agendamento.setStatus(dto.status());


        // IMPORTANTE: copiar a lista para evitar erro de Hibernate
        if (orcamento.getImagemReferencia() != null) {
            agendamento.setImagemReferencia(
                    new ArrayList<>(orcamento.getImagemReferencia())
            );
        } else {
            agendamento.setImagemReferencia(null);
        }

        return agendamento;
    }
}

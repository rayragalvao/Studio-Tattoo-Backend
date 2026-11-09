package hub.orcana.service;

import hub.orcana.tables.StatusAgendamento;
import hub.orcana.tables.StatusOrcamento;
import hub.orcana.tables.repository.AgendamentoRepository;
import hub.orcana.tables.repository.EstoqueRepository;
import hub.orcana.tables.repository.OrcamentoRepository;
import hub.orcana.tables.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ContextoService {

    private final AgendamentoRepository agendamentoRepository;
    private final EstoqueRepository estoqueRepository;
    private final OrcamentoRepository orcamentoRepository;
    private final UsuarioRepository usuarioRepository;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public String gerarContexto() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== DADOS ATUAIS DO SISTEMA ORCANA ===\n");
        sb.append("Data/hora atual: ").append(LocalDateTime.now().format(FMT)).append("\n\n");

        appendAgendamentosHoje(sb);
        appendAgendamentosAmanha(sb);
        appendAgendamentosSemanaQueVem(sb);
        appendAgendamentosMesQueVem(sb);
        appendAgendamentosCancelados(sb);
        appendOrcamentosPendentes(sb);
        appendFaturamentoMes(sb);
        appendClientes(sb);
        appendEstoque(sb);
        appendResumoGeral(sb);

        sb.append("=== FIM DOS DADOS ===\n");
        return sb.toString();
    }

    private void appendAgendamentosHoje(StringBuilder sb) {
        var inicio = LocalDate.now().atStartOfDay();
        var fim = LocalDate.now().atTime(23, 59, 59);
        var lista = agendamentoRepository.findAllByDataHoraBetween(inicio, fim);

        sb.append("-- AGENDAMENTOS HOJE (").append(LocalDate.now().format(FMT_DATA)).append(") --\n");
        if (lista.isEmpty()) {
            sb.append("Nenhum agendamento para hoje.\n");
        } else {
            lista.forEach(a -> sb.append(String.format("  • %s | Cliente: %s | Status: %s | Pagamento: %s\n",
                    a.getDataHora().format(FMT),
                    a.getOrcamento() != null ? a.getOrcamento().getNome() : "N/A",
                    a.getStatus(),
                    Boolean.TRUE.equals(a.getPagamentoFeito()) ? "Feito (" + a.getFormaPagamento() + ")" : "Pendente"
            )));
        }
        sb.append("\n");
    }

    private void appendAgendamentosAmanha(StringBuilder sb) {
        var inicio = LocalDate.now().plusDays(1).atStartOfDay();
        var fim = LocalDate.now().plusDays(1).atTime(23, 59, 59);
        var lista = agendamentoRepository.findAllByDataHoraBetween(inicio, fim);

        sb.append("-- AGENDAMENTOS AMANHÃ (").append(inicio.format(FMT_DATA)).append(") --\n");
        if (lista.isEmpty()) {
            sb.append("Nenhum agendamento para amanhã.\n");
        } else {
            lista.forEach(a -> sb.append(String.format("  • %s | Cliente: %s | Status: %s\n",
                    a.getDataHora().format(FMT),
                    a.getOrcamento() != null ? a.getOrcamento().getNome() : "N/A",
                    a.getStatus()
            )));
        }
        sb.append("\n");
    }

    private void appendAgendamentosSemanaQueVem(StringBuilder sb) {
        var inicio = LocalDate.now().plusWeeks(1).with(java.time.DayOfWeek.MONDAY).atStartOfDay();
        var fim = inicio.plusDays(6).withHour(23).withMinute(59).withSecond(59);
        var lista = agendamentoRepository.findAllByDataHoraBetween(inicio, fim);

        sb.append("-- AGENDAMENTOS SEMANA QUE VEM --\n");
        if (lista.isEmpty()) {
            sb.append("Nenhum agendamento na semana que vem.\n");
        } else {
            lista.forEach(a -> sb.append(String.format("  • %s | Cliente: %s | Status: %s\n",
                    a.getDataHora().format(FMT),
                    a.getOrcamento() != null ? a.getOrcamento().getNome() : "N/A",
                    a.getStatus()
            )));
        }
        sb.append("\n");
    }

    private void appendAgendamentosMesQueVem(StringBuilder sb) {
        var proximoMes = LocalDate.now().plusMonths(1);
        var inicio = proximoMes.withDayOfMonth(1).atStartOfDay();
        var fim = proximoMes.withDayOfMonth(proximoMes.lengthOfMonth()).atTime(23, 59, 59);
        var lista = agendamentoRepository.findAllByDataHoraBetween(inicio, fim);

        sb.append("-- AGENDAMENTOS MÊS QUE VEM (").append(proximoMes.getMonth().getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("pt", "BR"))).append(") --\n");
        if (lista.isEmpty()) {
            sb.append("Nenhum agendamento no mês que vem.\n");
        } else {
            lista.forEach(a -> sb.append(String.format("  • %s | Cliente: %s | Status: %s\n",
                    a.getDataHora().format(FMT),
                    a.getOrcamento() != null ? a.getOrcamento().getNome() : "N/A",
                    a.getStatus()
            )));
        }
        sb.append("\n");
    }

    private void appendAgendamentosCancelados(StringBuilder sb) {
        var lista = agendamentoRepository.findByStatus(StatusAgendamento.CANCELADO);
        sb.append("-- AGENDAMENTOS CANCELADOS (total: ").append(lista.size()).append(") --\n");
        lista.stream().limit(10).forEach(a -> sb.append(String.format("  • %s | Cliente: %s\n",
                a.getDataHora().format(FMT),
                a.getOrcamento() != null ? a.getOrcamento().getNome() : "N/A"
        )));
        if (lista.size() > 10) sb.append("  ... e mais ").append(lista.size() - 10).append(" cancelados.\n");
        sb.append("\n");
    }

    private void appendOrcamentosPendentes(StringBuilder sb) {
        var lista = orcamentoRepository.findAll().stream()
                .filter(o -> StatusOrcamento.PENDENTE.equals(o.getStatus()))
                .toList();

        sb.append("-- ORÇAMENTOS AGUARDANDO RESPOSTA (total: ").append(lista.size()).append(") --\n");
        lista.stream().limit(10).forEach(o -> sb.append(String.format("  • Cód: %s | Cliente: %s | Valor: R$ %.2f | Estilo: %s\n",
                o.getCodigoOrcamento(),
                o.getNome(),
                o.getValor() != null ? o.getValor() : 0.0,
                o.getEstilo() != null ? o.getEstilo() : "N/A"
        )));
        if (lista.size() > 10) sb.append("  ... e mais ").append(lista.size() - 10).append(" pendentes.\n");
        sb.append("\n");
    }

    private void appendFaturamentoMes(StringBuilder sb) {
        var inicio = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        var fim = LocalDate.now().atTime(23, 59, 59);
        var faturamento = agendamentoRepository.sumValorTotalAgendamentosPorPeriodo(inicio, fim);

        sb.append("-- FATURAMENTO DO MÊS ATUAL --\n");
        sb.append(String.format("  Total (agendamentos concluídos): R$ %.2f\n", faturamento != null ? faturamento : 0.0));
        sb.append("\n");
    }

    private void appendClientes(StringBuilder sb) {
        var clientes = usuarioRepository.findAllByIsAdmin(false);
        sb.append("-- CLIENTES CADASTRADOS (total: ").append(clientes.size()).append(") --\n");
        clientes.stream().limit(15).forEach(u -> sb.append(String.format("  • %s | %s | Tel: %s\n",
                u.getNome(),
                u.getEmail(),
                u.getTelefone() != null ? u.getTelefone() : "N/A"
        )));
        if (clientes.size() > 15) sb.append("  ... e mais ").append(clientes.size() - 15).append(" clientes.\n");
        sb.append("\n");
    }

    private void appendEstoque(StringBuilder sb) {
        var todos = estoqueRepository.findAll();
        var abaixoMin = estoqueRepository.findAllByQuantidadeLessThanMinAviso();
        var zerados = todos.stream().filter(e -> e.getQuantidade() <= 0).toList();

        sb.append("-- ESTOQUE --\n");
        sb.append("  Total de itens: ").append(todos.size()).append("\n");

        if (!zerados.isEmpty()) {
            sb.append("  ⚠ ZERADOS (compra urgente):\n");
            zerados.forEach(e -> sb.append(String.format("    - %s: %.2f %s\n", e.getNome(), e.getQuantidade(), e.getUnidadeMedida())));
        }

        if (!abaixoMin.isEmpty()) {
            sb.append("  ⚠ ABAIXO DO MÍNIMO (atenção):\n");
            abaixoMin.forEach(e -> sb.append(String.format("    - %s: %.2f %s (mínimo: %.2f)\n",
                    e.getNome(), e.getQuantidade(), e.getUnidadeMedida(),
                    e.getMinAviso() != null ? e.getMinAviso() : 0.0)));
        }

        if (zerados.isEmpty() && abaixoMin.isEmpty()) {
            sb.append("  ✓ Estoque OK — nenhum item abaixo do mínimo.\n");
        }

        sb.append("  Lista completa:\n");
        todos.forEach(e -> sb.append(String.format("    - %s: %.2f %s\n", e.getNome(), e.getQuantidade(), e.getUnidadeMedida())));
        sb.append("\n");
    }

    private void appendResumoGeral(StringBuilder sb) {
        long confirmados = agendamentoRepository.countByStatus(StatusAgendamento.CONFIRMADO);
        long pendentes = agendamentoRepository.countByStatus(StatusAgendamento.PENDENTE);
        long concluidos = agendamentoRepository.countByStatus(StatusAgendamento.CONCLUIDO);
        long cancelados = agendamentoRepository.countByStatus(StatusAgendamento.CANCELADO);
        long orcamentosPendentes = orcamentoRepository.countByStatus(StatusOrcamento.PENDENTE);
        long orcamentosAprovados = orcamentoRepository.countByStatus(StatusOrcamento.APROVADO);

        sb.append("-- RESUMO GERAL --\n");
        sb.append("  Agendamentos: ").append(confirmados).append(" confirmados, ")
                .append(pendentes).append(" pendentes, ")
                .append(concluidos).append(" concluídos, ")
                .append(cancelados).append(" cancelados\n");
        sb.append("  Orçamentos: ").append(orcamentosPendentes).append(" aguardando resposta, ")
                .append(orcamentosAprovados).append(" aprovados\n");
        sb.append("\n");
    }
}
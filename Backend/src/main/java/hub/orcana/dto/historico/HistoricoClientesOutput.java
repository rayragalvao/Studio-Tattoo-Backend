package hub.orcana.dto.historico;

import java.util.List;

public record HistoricoClientesOutput(
        int totalClientes,
        int totalSessoes,
        Double receitaAcumulada,
        List<ClienteHistoricoResumoOutput> clientes
) {
}

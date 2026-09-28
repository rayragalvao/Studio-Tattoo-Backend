package hub.orcana.dto.historico;

import java.time.LocalDateTime;
import java.util.List;

public record DetalhesHistoricoClienteOutput(
        Long id,
        String nome,
        String iniciais,
        List<String> estilos,
        int quantidadeSessoes,
        Double gastoTotal,
        LocalDateTime ultimaSessao,
        List<SessaoHistoricoOutput> sessoes
) {
}

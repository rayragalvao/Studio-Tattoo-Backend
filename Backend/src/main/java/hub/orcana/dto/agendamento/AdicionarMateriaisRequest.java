package hub.orcana.dto.agendamento;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AdicionarMateriaisRequest(
        @NotEmpty(message = "A lista de materiais não pode estar vazia")
        @Valid
        List<MaterialUsadoRequest> materiais
) {}
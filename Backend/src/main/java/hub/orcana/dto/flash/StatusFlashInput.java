package hub.orcana.dto.flash;

import jakarta.validation.constraints.NotNull;

public record StatusFlashInput(@NotNull Boolean aplicado) {}
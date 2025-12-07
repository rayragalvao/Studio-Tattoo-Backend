package hub.orcana.dto.usuario;

import java.util.Date;

public record ListarUsuarios(
        Long id,
        String nome,
        String email,
        String telefone,
        Date dtNasc,
        boolean isAdmin
) {
}
package hub.orcana.tables.repository;

import hub.orcana.tables.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);
    boolean existsByTelefone(String telefone);

    List<Usuario> findAllByIsAdmin(boolean b);

    @Query("SELECT u.email FROM Usuario u WHERE u.isAdmin = true")
    List<String> getEmailByIsAdminTrue();
}
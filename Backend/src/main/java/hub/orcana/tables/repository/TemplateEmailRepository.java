package hub.orcana.tables.repository;

import hub.orcana.tables.TemplateEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateEmailRepository extends JpaRepository<TemplateEmail, Integer> {
    Optional<TemplateEmail> findByNomeTemplate(String nomeTemplate);
}

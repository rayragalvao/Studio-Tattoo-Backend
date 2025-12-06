package hub.orcana.tables;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "template_email")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_template")
    private Integer idTemplate;

    @Column(name = "nome_template", nullable = false, length = 100)
    private String nomeTemplate;

    @Column(name = "assunto", nullable = false, length = 200)
    private String assunto;

    @Column(name = "corpo_email", nullable = false, columnDefinition = "TEXT")
    private String corpoEmail;
}

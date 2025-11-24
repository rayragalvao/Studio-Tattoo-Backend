package hub.orcana.tables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@ToString
@Entity
@Table(name = "usuario")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Setter
    @Column(nullable = false)
    private String nome;

    @Email
    @Setter
    @Column(nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "^$|^\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}$")
    @Setter
    @Column(length = 20)
    private String telefone;

    @Setter
    @Column(nullable = false)
    private String senha;

    @Setter
    @Column(name = "dt_nasc")
    private Date dtNasc;

    @Setter
    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getSenha() {
        return senha;
    }

    public Date getDtNasc() {
        return dtNasc;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}

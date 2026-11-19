package hub.orcana.dto.flash;

import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;

public class CadastroFlashInput {
    @NotBlank @Size(max = 120)
    private String nome;
    @NotBlank @Size(max = 100)
    private String estilo;
    @NotNull @DecimalMin(value = "0.01")
    private BigDecimal preco;
    @Size(max = 2000)
    private String descricao;
    @NotNull
    private MultipartFile foto;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEstilo() { return estilo; }
    public void setEstilo(String estilo) { this.estilo = estilo; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public MultipartFile getFoto() { return foto; }
    public void setFoto(MultipartFile foto) { this.foto = foto; }
}

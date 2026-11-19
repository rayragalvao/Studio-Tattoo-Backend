package hub.orcana.tables;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "flash_tattoo")
public class FlashTattoo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 100)
    private String estilo;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(length = 2000)
    private String descricao;

    @Column(nullable = false, length = 100)
    private String arquivoImagem;

    @Column(nullable = false, length = 30)
    private String tipoImagem;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean aplicado = false;

    protected FlashTattoo() {}

    public FlashTattoo(String nome, String estilo, BigDecimal preco, String descricao,
                       String arquivoImagem, String tipoImagem) {
        this.nome = nome;
        this.estilo = estilo;
        this.preco = preco;
        this.descricao = descricao;
        this.arquivoImagem = arquivoImagem;
        this.tipoImagem = tipoImagem;
    }

    public void atualizar(String nome, String estilo, BigDecimal preco, String descricao) {
        this.nome = nome;
        this.estilo = estilo;
        this.preco = preco;
        this.descricao = descricao;
    }

    public void atualizarFoto(String arquivoImagem, String tipoImagem) {
        this.arquivoImagem = arquivoImagem;
        this.tipoImagem = tipoImagem;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEstilo() { return estilo; }
    public BigDecimal getPreco() { return preco; }
    public String getDescricao() { return descricao; }
    public String getArquivoImagem() { return arquivoImagem; }
    public String getTipoImagem() { return tipoImagem; }
    public boolean isAplicado() { return aplicado; }
    public void setAplicado(boolean aplicado) { this.aplicado = aplicado; }
}
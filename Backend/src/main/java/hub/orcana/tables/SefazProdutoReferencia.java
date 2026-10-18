package hub.orcana.tables;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sefaz_produto_referencia")
public class SefazProdutoReferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(nullable = false, length = 100)
    private String categoria;

    @Column(length = 12)
    private String ncm;

    @Column(name = "quantidade_nfe", nullable = false, precision = 18, scale = 4)
    private BigDecimal quantidadeNfe;

    @Column(name = "unidade_nfe", length = 20)
    private String unidadeNfe;

    @Column(name = "valor_unitario_nfe", nullable = false, precision = 18, scale = 4)
    private BigDecimal valorUnitarioNfe;

    @Column(name = "valor_total_nfe", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorTotalNfe;

    @Column(name = "chave_nfe", columnDefinition = "CHAR(44)")
    private String chaveNfe;

    @Column(name = "valor_calculado", precision = 18, scale = 2)
    private BigDecimal valorCalculado;

    @Column(name = "valor_confere")
    private Boolean valorConfere;

    @Column(name = "atualizado_em", insertable = false, updatable = false)
    private LocalDateTime atualizadoEm;

    public SefazProdutoReferencia() {
    }

    public Long getId() { return id; }
    public String getDescricao() { return descricao; }
    public String getCategoria() { return categoria; }
    public String getNcm() { return ncm; }
    public BigDecimal getQuantidadeNfe() { return quantidadeNfe; }
    public String getUnidadeNfe() { return unidadeNfe; }
    public BigDecimal getValorUnitarioNfe() { return valorUnitarioNfe; }
    public BigDecimal getValorTotalNfe() { return valorTotalNfe; }
    public String getChaveNfe() { return chaveNfe; }
    public BigDecimal getValorCalculado() { return valorCalculado; }
    public Boolean getValorConfere() { return valorConfere; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
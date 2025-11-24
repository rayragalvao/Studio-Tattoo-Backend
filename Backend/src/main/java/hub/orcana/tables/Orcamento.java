package hub.orcana.tables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.ToString;

import java.sql.Time;
import java.util.List;

@ToString
@Entity
@Table(name = "orcamento")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class    Orcamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_orcamento", unique = true, nullable = false, length = 20)
    private String codigoOrcamento;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String ideia;

    private Double valor;
    private Double tamanho;
    private String estilo;

    @Column(length = 500)
    private String cores;

    private Time tempo;

    @Column(name = "local_corpo", length = 200)
    private String localCorpo;

    @ElementCollection
    @CollectionTable(name = "orcamento_imagens", joinColumns = @JoinColumn(name = "orcamento_id"))
    @Column(name = "imagem_url", length = 500)
    private List<String> imagemReferencia;

  //  @ManyToOne(fetch = FetchType.LAZY)
    private String status;
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "senha"})
    private Usuario usuario;

    public Orcamento() {}

    public Orcamento(String codigoOrcamento, String nome, String email, String ideia, Double tamanho, String cores, String localCorpo, List<String> imagemReferencia, Long usuarioId) {
        this.codigoOrcamento = codigoOrcamento;
        this.nome = nome;
        this.email = email;
        this.ideia = ideia;
        this.tamanho = tamanho;
        this.cores = cores;
        this.localCorpo = localCorpo;
        this.imagemReferencia = imagemReferencia;
        if (usuarioId != null) {
            this.usuario = new Usuario();
            this.usuario.setId(usuarioId);
        }
    }

    public Orcamento(String codigoOrcamento, Long id, String nome, String email, String ideia, Double tamanho, String cores, String localCorpo, List<String> imagemReferencia) {
        this.codigoOrcamento = codigoOrcamento;
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.ideia = ideia;
        this.tamanho = tamanho;
        this.cores = cores;
        this.localCorpo = localCorpo;
        this.imagemReferencia = imagemReferencia;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCodigoOrcamento() { return codigoOrcamento; }

    public void setCodigoOrcamento(String codigoOrcamento) { this.codigoOrcamento = codigoOrcamento; }


    public Long getLinhaId() { return id; }

    public void setLinhaId(Long id) { this.id = id; }

    // Compatibilidade: getId() retorna o id numérico como antes
    public Long getId() { return this.id; }

    public String getIdeia() {
        return ideia;
    }

    public Double getValor() {
        return valor;
    }

    public Double getTamanho() {
        return tamanho;
    }

    public String getEstilo() {
        return estilo;
    }

    public String getCores() {
        return cores;
    }

    public Time getTempo() {
        return tempo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getImagemReferencia() { return imagemReferencia;}

    public String getLocalCorpo() {
        return localCorpo;
    }
}

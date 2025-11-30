package hub.orcana.tables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Setter;
import lombok.ToString;

import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

@ToString
@Entity
@Table(name = "orcamento")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class    Orcamento {
    @Id
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

    @Column(length = 500)
    private String cores;

    private Time tempo;

    @Column(name = "local_corpo", length = 200)
    private String localCorpo;

    @ElementCollection
    @CollectionTable(name = "orcamento_imagens", joinColumns = @JoinColumn(name = "codigo_orcamento"))
    @Column(name = "imagem_url", length = 500)
    private List<String> imagemReferencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusOrcamento status;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "senha"})
    private Usuario usuario;

    public Orcamento() {}

    public Orcamento(String codigoOrcamento, String nome, String email, String ideia, Double tamanho, String cores, String localCorpo, List<String> imagemReferencia, Long usuarioId, StatusOrcamento status) {
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
        this.status = status;
    }

    public Orcamento(String codigoOrcamento, Long id, String nome, String email, String ideia, Double tamanho, String cores, String localCorpo, List<String> imagemReferencia) {
        this.codigoOrcamento = codigoOrcamento;
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

    public String getIdeia() {
        return ideia;
    }

    public void setIdeia(String ideia) {
        this.ideia = ideia;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Double getTamanho() {
        return tamanho;
    }

    public void setTamanho(Double tamanho) {
        this.tamanho = tamanho;
    }

    public String getCores() {
        return cores;
    }

    public void setCores(String cores) {
        this.cores = cores;
    }

    public Time getTempo() {
        return tempo;
    }

    public void setTempo(Time tempo) {
        this.tempo = tempo;
    }

    public void setTempo(LocalTime localTime) {
        if (localTime != null) {
            this.tempo = Time.valueOf(localTime);
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return email;
    }

    public void setStatus(StatusOrcamento status) {
        this.status = status;
    }

    public List<String> getImagemReferencia() { return imagemReferencia;}

    public String getLocalCorpo() {
        return localCorpo;
    }

    public void setLocalCorpo(String localCorpo) {
        this.localCorpo = localCorpo;
    }

}
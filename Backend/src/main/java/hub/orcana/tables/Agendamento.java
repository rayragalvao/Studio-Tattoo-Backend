package hub.orcana.tables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "agendamento")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FutureOrPresent
    @NotNull
    @Setter
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Setter
    @Column(name = "status", nullable = false, length = 50)
    private StatusAgendamento status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "senha"})
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "codigo_orcamento", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Orcamento orcamento;

    // --------------------
    // NOVO CAMPO ADICIONADO
    // --------------------
    @ElementCollection
    @CollectionTable(
            name = "agendamento_imagens",
            joinColumns = @JoinColumn(name = "agendamento_id")
    )
    @Column(name = "imagem_referencia")
    private List<String> imagemReferencia;

    // --------------------
    // GETTERS E SETTERS
    // --------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Orcamento getOrcamento() {
        return orcamento;
    }

    public void setOrcamento(Orcamento orcamento) {
        this.orcamento = orcamento;
    }

    public List<String> getImagemReferencia() {
        return imagemReferencia;
    }

    public void setImagemReferencia(List<String> imagemReferencia) {
        this.imagemReferencia = imagemReferencia;
    }

    @Column(name = "tempo_duracao")
    private Integer tempoDuracao; // em minutos

    @Column(name = "pagamento_feito")
    private Boolean pagamentoFeito;

    @Column(name = "forma_pagamento", length = 50)
    private String formaPagamento; // PIX, Dinheiro, Cartão

    public Integer getTempoDuracao() { return tempoDuracao; }
    public void setTempoDuracao(Integer tempoDuracao) { this.tempoDuracao = tempoDuracao; }

    public Boolean getPagamentoFeito() { return pagamentoFeito; }
    public void setPagamentoFeito(Boolean pagamentoFeito) { this.pagamentoFeito = pagamentoFeito; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

}

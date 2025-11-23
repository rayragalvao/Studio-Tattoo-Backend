package hub.orcana.tables;

import jakarta.persistence.*;

@Entity
@Table(name = "equipamento_uso")
public class EquipamentoUso {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipamento_id", nullable = false)
    private Estoque equipamento;

    @Column(nullable = false)
    private int quantidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relatorio_id", nullable = false)
    private Relatorio relatorio;

    public Long getId() {
        return id;
    }

    public Estoque getEquipamento() {
        return equipamento;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public Relatorio getRelatorio() {
        return relatorio;
    }
}

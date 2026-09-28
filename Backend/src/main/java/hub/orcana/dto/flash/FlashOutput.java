package hub.orcana.dto.flash;

import hub.orcana.tables.FlashTattoo;
import java.math.BigDecimal;

public record FlashOutput(Long id, String nome, String estilo, BigDecimal preco,
                          String descricao, String imagemUrl) {
    public static FlashOutput from(FlashTattoo flash) {
        return new FlashOutput(flash.getId(), flash.getNome(), flash.getEstilo(),
                flash.getPreco(), flash.getDescricao(), "/flash-tattoos/" + flash.getId() + "/foto");
    }
}

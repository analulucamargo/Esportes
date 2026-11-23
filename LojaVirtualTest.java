import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class LojaVirtualTest {

    @Test
    void baixarDiminuiOSaldoDoProduto() {
        // preparar
        Estoque estoque = new Estoque();
        Produto caneta = new Produto("P02", "Caneta", 5.0, 0.02);
        estoque.adicionar(caneta, 50);
        estoque.baixar(caneta, 1);
        
        assertEquals(49, estoque.getSaldo(caneta),"baixar deve diminuir o saldo do produto");
    }

    @Test
    void produtosComMesmoCodigoCompartilhamOMesmoSaldo() {
        // preparar
        Estoque estoque = new Estoque();
        Produto canetaA = new Produto("P02", "Caneta", 5.0, 0.02);
        Produto canetaB = new Produto("P02", "Caneta", 5.0, 0.02);
        estoque.adicionar(canetaA, 50);

        int saldo = estoque.getSaldo(canetaB);
        assertEquals(50, saldo,"produtos com o mesmo código devem compartilhar o mesmo saldo");
    }
}
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LojaVirtualTest {
    
    // ---------- D1: Kit.getPreco() deve somar todos os produtos ----------

    @Test
    void kitDeveSomarTodosOsProdutos() {
        // preparar
        Kit kit = new Kit("Teste", 0.0);
        kit.adicionar(new Produto("P01", "Caderno", 25.0, 0.4));
        kit.adicionar(new Produto("P02", "Caneta", 5.0, 0.02));
        // executar
        double preco = kit.getPreco();
        // verificar
        assertEquals(30.0, preco, 0.001,
                "o preço do kit deve somar todos os produtos, inclusive o primeiro");
    }

    // ---------- D2: CentralDeCupons.buscar() deve comparar com equals ----------

    @Test
    void buscarDeveEncontrarCupomPorConteudoDoCodigo() {
        // preparar
        CentralDeCupons central = new CentralDeCupons();
        central.cadastrar(new Cupom("VOLTA10", 10.0));
        String digitado = new String("VOLTA10");
        // executar
        Cupom encontrado = central.buscar(digitado);
        // verificar
        assertNotNull(encontrado,
                "o cupom VOLTA10 deve ser encontrado mesmo com a String vinda de outro objeto");
    }

    // ---------- D3: Estoque.baixar() deve gravar o novo saldo no Map ----------

    @Test
    void baixarDiminuiOSaldoDoProduto() {
        // preparar
        Estoque estoque = new Estoque();
        Produto caneta = new Produto("P02", "Caneta", 5.0, 0.02);
        estoque.adicionar(caneta, 50);
        estoque.baixar(caneta, 1);

        assertEquals(49, estoque.getSaldo(caneta), "baixar deve diminuir o saldo do produto");
    }

    // ---------- D4: Produto com equals precisa de hashCode ----------

    @Test
    void produtosComMesmoCodigoCompartilhamOMesmoSaldo() {
        // preparar
        Estoque estoque = new Estoque();
        Produto canetaA = new Produto("P02", "Caneta", 5.0, 0.02);
        Produto canetaB = new Produto("P02", "Caneta", 5.0, 0.02);
        estoque.adicionar(canetaA, 50);

        int saldo = estoque.getSaldo(canetaB);
        assertEquals(50, saldo, "produtos com o mesmo código devem compartilhar o mesmo saldo");
    }

    // ---------- D5: Pedido.getItens() deve devolver lista somente leitura ----------

    @Test
    void getItensNaoPermiteAdicionarDiretamenteNaLista() {
        Pedido pedido = new Pedido("PAC");
        Produto caneta = new Produto("P02", "Caneta", 5.0, 0.02);
        assertThrows(UnsupportedOperationException.class,
                () -> pedido.getItens().add(caneta));
    }

    @Test
    void getItensNaoPermiteRemoverItem() {
        Pedido pedido = new Pedido("PAC");
        pedido.adicionar(new Produto("P02", "Caneta", 5.0, 0.02));
        assertThrows(UnsupportedOperationException.class,
                () -> pedido.getItens().remove(0));
    }

    @Test
    void getItensNaoAlteraSubtotalDoPedido() {
        Pedido pedido = new Pedido("PAC");
        pedido.adicionar(new Produto("P02", "Caneta", 5.0, 0.02));
        try {
            pedido.getItens().add(new Produto("P03", "Mochila", 120.0, 0.9));
        } catch (UnsupportedOperationException esperado) {
            // comportamento correto
        }
        assertEquals(5.0, pedido.calcularSubtotal(), 0.001);
    }

    @Test
    void getItensReflitePedidoAposAdicionarPeloMetodoOficial() {
        Pedido pedido = new Pedido("PAC");
        Produto caneta = new Produto("P02", "Caneta", 5.0, 0.02);
        pedido.adicionar(caneta);
        assertEquals(1, pedido.getItens().size());
        assertSame(caneta, pedido.getItens().get(0));
    }

    // ---------- D6: frete da TRANSPORTADORA grátis a partir de R$ 300,00 ----------

    @Test
    void transportadoraSubtotalExatamente300EhGratis() {
        Pedido pedido = new Pedido("TRANSPORTADORA");
        pedido.adicionar(new Produto("X01", "Item", 300.0, 1.0));
        assertEquals(0.0, pedido.calcularFrete(), 0.001);
    }

    @Test
    void transportadoraSubtotalAcimaDe300EhGratis() {
        Pedido pedido = new Pedido("TRANSPORTADORA");
        pedido.adicionar(new Produto("X01", "Item", 300.01, 1.0));
        assertEquals(0.0, pedido.calcularFrete(), 0.001);
    }

    @Test
    void transportadoraSubtotalAbaixoDe300Custa40() {
        Pedido pedido = new Pedido("TRANSPORTADORA");
        pedido.adicionar(new Produto("X01", "Item", 299.99, 1.0));
        assertEquals(40.0, pedido.calcularFrete(), 0.001);
    }

    //----------M1: Pode conter outro kit------------
    
    @Test
    void kitPodeConterOutroKit() {
    // preparar
    Kit interno = new Kit("Interno", 0.0);
    interno.adicionar(new Produto("P01", "Caderno", 25.0, 0.4));
    Kit externo = new Kit("Externo", 10.0);
    externo.adicionar(interno);
    externo.adicionar(new Produto("P02", "Caneta", 5.0, 0.02));
    // executar
    double preco = externo.getPreco();
    // verificar
    assertEquals(27.0, preco, 0.001,
        "o kit externo deve somar o kit interno e a caneta, com 10% de desconto");
}

    // ---------- M2: adicionais com Decorator ----------

    @Test
    void embalagemAdicionaCincoReais() {
        Produto produto = new Produto("T01", "Teste", 100.0, 0.5);

        LojaVirtual.Item item = new EmbalagemDecorator(produto);

        assertEquals(105.0, item.getPreco(), 0.001);
    }

    @Test
    void garantiaAdicionaDezPorCento() {
        Produto produto = new Produto("T02", "Teste", 100.0, 0.5);

        LojaVirtual.Item item = new GarantiaDecorator(produto);

        assertEquals(110.0, item.getPreco(), 0.001);
    }

    @Test
    void seguroAdicionaOitoReais() {
        Produto produto = new Produto("T03", "Teste", 100.0, 0.5);

        LojaVirtual.Item item = new SeguroDecorator(produto);

        assertEquals(108.0, item.getPreco(), 0.001);
    }
    
    @Test
    void pedidoAceitaKitProdutoEDecorators() {
        Produto caderno = new Produto("P01", "Caderno", 25.0, 0.4);
        Produto caneta = new Produto("P02", "Caneta", 5.0, 0.02);
        Produto mochila = new Produto("P03", "Mochila", 120.0, 0.9);

        Kit kit = new Kit("Escolar", 10.0);
        kit.adicionar(caderno);
        kit.adicionar(caneta);

        LojaVirtual.Item mochilaPresente = new GarantiaDecorator(new EmbalagemDecorator(mochila));

        Pedido pedido = new Pedido("SEDEX");
        pedido.adicionar(kit);
        pedido.adicionar(mochilaPresente);
        pedido.adicionar(caneta);

        assertEquals(169.50, pedido.calcularSubtotal(), 0.001);
        assertEquals(1.34, pedido.calcularPesoTotal(), 0.001);
    }


    // ---------- M3: testes de caracterização do frete (escritos antes do Strategy) ----------

    @Test
    void fretePacSomaBaseMaisDoisPorQuilo() {
        Pedido pedido = new Pedido("PAC");
        pedido.adicionar(new Produto("X01", "Item", 50.0, 2.5));
        assertEquals(15.0 + 2.5 * 2.0, pedido.calcularFrete(), 0.001);
    }

    @Test
    void freteSedexSomaBaseMaisQuatroPorQuilo() {
        Pedido pedido = new Pedido("SEDEX");
        pedido.adicionar(new Produto("X01", "Item", 50.0, 2.5));
        assertEquals(25.0 + 2.5 * 4.0, pedido.calcularFrete(), 0.001);
    }

    @Test
    void freteTransportadoraNaoDependeDoPeso() {
        Pedido pedido = new Pedido("TRANSPORTADORA");
        pedido.adicionar(new Produto("X01", "Item", 50.0, 30.0));
        assertEquals(40.0, pedido.calcularFrete(), 0.001);
    }

    @Test
    void freteComTipoInvalidoLancaIllegalArgumentException() {
        Pedido pedido = new Pedido("DRONE");
        pedido.adicionar(new Produto("X01", "Item", 50.0, 1.0));
        assertThrows(IllegalArgumentException.class, pedido::calcularFrete);
    }

    @Test
    void totalSomaSubtotalComCupomMaisFrete() {
        Pedido pedido = new Pedido("PAC");
        pedido.adicionar(new Produto("X01", "Item", 100.0, 1.0));
        pedido.aplicarCupom(new Cupom("C10", 10.0));
        assertEquals(90.0 + 15.0 + 1.0 * 2.0, pedido.calcularTotal(), 0.001);
    }

    @Test
    void freteTransportadoraUsaSubtotalSemCupom() {
        Pedido pedido = new Pedido("TRANSPORTADORA");
        pedido.adicionar(new Produto("X01", "Item", 300.0, 1.0));
        pedido.aplicarCupom(new Cupom("C10", 10.0));
        assertEquals(0.0, pedido.calcularFrete(), 0.001);
    }

    // ---------- M3: testes do Strategy ----------

    @Test
    void pedidoAceitaEstrategiaDeFreteNova() {
        Pedido pedido = new Pedido((peso, subtotal) -> 7.5);
        pedido.adicionar(new Produto("X01", "Item", 50.0, 1.0));
        assertEquals(7.5, pedido.calcularFrete(), 0.001);
        assertEquals(57.5, pedido.calcularTotal(), 0.001);
    }

    @Test
    void estrategiaRecebePesoESubtotalDoPedido() {
        Pedido pedido = new Pedido((peso, subtotal) -> peso + subtotal);
        pedido.adicionar(new Produto("X01", "Item", 50.0, 2.0));
        assertEquals(52.0, pedido.calcularFrete(), 0.001);
    }

    @Test
    void estrategiasConcretasCalculamSemPedido() {
        assertEquals(19.0, new FretePac().calcular(2.0, 0.0), 0.001);
        assertEquals(33.0, new FreteSedex().calcular(2.0, 0.0), 0.001);
        assertEquals(0.0, new FreteTransportadora().calcular(2.0, 300.0), 0.001);
        assertEquals(40.0, new FreteTransportadora().calcular(2.0, 299.99), 0.001);
    }    
}

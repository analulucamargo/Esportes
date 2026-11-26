import java.util.Collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Versão do código alterado.

public class LojaVirtual {

    public static void main(String[] args) {
        Produto caderno = new Produto("P01", "Caderno", 25.0, 0.4);
        Produto caneta = new Produto("P02", "Caneta", 5.0, 0.02);
        Produto mochila = new Produto("P03", "Mochila", 120.0, 0.9);

        Kit kitEscolar = new Kit("Escolar", 10.0);
        kitEscolar.adicionar(caderno);
        kitEscolar.adicionar(caneta);

        LojaVirtual.Item mochilaPresente = new GarantiaDecorator(new EmbalagemDecorator(mochila));

        Pedido pedido = new Pedido("SEDEX");
        pedido.adicionar(kitEscolar);
        pedido.adicionar(mochilaPresente);
        pedido.adicionar(caneta);

        CentralDeCupons central = new CentralDeCupons();
        central.cadastrar(new Cupom("VOLTA10", 10.0));
        pedido.aplicarCupom(central.buscar("VOLTA10"));

        Estoque estoque = new Estoque();
        estoque.adicionar(caneta, 50);
        estoque.baixar(caneta, 1);

        System.out.println("Item: " + mochilaPresente.getDescricao());
        System.out.printf("Subtotal: R$ %.2f%n", pedido.calcularSubtotal());
        System.out.printf("Frete:    R$ %.2f%n", pedido.calcularFrete());
        System.out.printf("Total:    R$ %.2f%n", pedido.calcularTotal());
        System.out.println("Saldo de canetas: " + estoque.getSaldo(caneta));
    }
    interface Item {
    double getPreco();
    double getPeso();
    String getDescricao();
    }
}

/**
 * Produto vendido individualmente.
 * Dois produtos são iguais quando têm o mesmo código.
 */
class Produto implements LojaVirtual.Item {
    private final String codigo;
    private final String descricao;
    private final double preco;
    private final double peso;

    Produto(String codigo, String descricao, double preco, double peso) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.preco = preco;
        this.peso = peso;
    }

    String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPreco() {
        return preco;
    }

    /** Peso em quilogramas. */
    public double getPeso() {
        return peso;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Produto)) {
            return false;
        }
        Produto outro = (Produto) o;
        return codigo.equals(outro.codigo);
    }
    @Override
    public int hashCode(){
        return codigo.hashCode();
    }
}

/**
 * Conjunto de produtos vendido com desconto percentual.
 * A equipe de vendas pediu kits que contenham outros kits
 * e itens com adicionais, o que esta classe ainda não aceita.
 */
class Kit implements LojaVirtual.Item {
    private final String nome;
    private final double percentualDesconto;
    private final List<LojaVirtual.Item> itens = new ArrayList<>();

    Kit(String nome, double percentualDesconto) {
        this.nome = nome;
        this.percentualDesconto = percentualDesconto;
    }

    void adicionar(LojaVirtual.Item item) {
        itens.add(item);
    }

    /** Soma os preços de todos os itens do kit e aplica o desconto percentual. */
    public double getPreco() {
        double soma = 0.0;
        for (LojaVirtual.Item item : itens) {
            soma += item.getPreco();
        }
        return soma * (1 - percentualDesconto / 100);
    }

    /** Soma os pesos de todos os itens do kit, em quilogramas. */
    public double getPeso() {
        double soma = 0.0;
        for (LojaVirtual.Item item : itens) {
            soma += item.getPeso();
        }
        return soma;
    }

    public String getDescricao() {
        return "Kit " + nome;
    }
}

/**
 * Produto vendido com adicionais opcionais:
 * embalagem para presente (R$ 5,00), garantia estendida
 * (10% sobre o preço do produto já somado à embalagem, quando houver)
 * e seguro contra roubo (R$ 8,00).
 * O marketing planeja novos adicionais, como cartão de presente.
 */

abstract class AdicionalDecorator implements LojaVirtual.Item {

    protected final LojaVirtual.Item item;

    AdicionalDecorator(LojaVirtual.Item item) {
        this.item = item;
    }

    @Override
    public double getPeso() {
        return item.getPeso();
    }
}

class EmbalagemDecorator extends AdicionalDecorator {

    EmbalagemDecorator(LojaVirtual.Item item) {
        super(item);
    }

    @Override
    public double getPreco() {
        return item.getPreco() + 5.0;
    }

    @Override
    public String getDescricao() {
        return item.getDescricao() + " + embalagem";
    }
}

class GarantiaDecorator extends AdicionalDecorator {

    GarantiaDecorator(LojaVirtual.Item item) {
        super(item);
    }

    @Override
    public double getPreco() {
        return item.getPreco() * 1.10;
    }

    @Override
    public String getDescricao() {
        return item.getDescricao() + " + garantia";
    }
}

class SeguroDecorator extends AdicionalDecorator {

    SeguroDecorator(LojaVirtual.Item item) {
        super(item);
    }

    @Override
    public double getPreco() {
        return item.getPreco() + 8.0;
    }

    @Override
    public String getDescricao() {
        return item.getDescricao() + " + seguro";
    }
}

/** Cupom de desconto percentual sobre o subtotal do pedido. */
class Cupom {
    private final String codigo;
    private final double percentual;

    Cupom(String codigo, double percentual) {
        this.codigo = codigo;
        this.percentual = percentual;
    }

    String getCodigo() {
        return codigo;
    }

    double getPercentual() {
        return percentual;
    }
}

/** Cadastro dos cupons válidos da loja. */
class CentralDeCupons {
    private final List<Cupom> cupons = new ArrayList<>();

    void cadastrar(Cupom cupom) {
        cupons.add(cupom);
    }

    /**
     * Devolve o cupom com o código informado, ou null se ele não existir.
     * O código costuma vir de um campo digitado pelo cliente.
     */
    //Método alterado.
    Cupom buscar(String codigo) {
        for (Cupom cupom : cupons) {
            if (cupom.getCodigo().equals(codigo)) {
                return cupom;
            }
        }
        return null;
    }
}

/**
 * Controla o saldo de cada produto.
 * Produtos com o mesmo código compartilham o mesmo saldo.
 */
class Estoque {
    private final Map<Produto, Integer> saldos = new HashMap<>();

    /** Soma a quantidade ao saldo do produto. */
    void adicionar(Produto produto, int quantidade) {
        saldos.put(produto, getSaldo(produto) + quantidade);
    }

    /** Devolve o saldo do produto, ou zero se ele nunca entrou no estoque. */
    int getSaldo(Produto produto) {
        return saldos.getOrDefault(produto, 0);
    }

    /**
     * Retira a quantidade do saldo do produto.
     * Lança IllegalStateException se o saldo for insuficiente.
     */
    void baixar(Produto produto, int quantidade) {
        int saldo = getSaldo(produto);
        if (quantidade > saldo) {
            throw new IllegalStateException("Saldo insuficiente para " + produto.getCodigo());
        }
        saldo -= quantidade;
        saldos.put(produto, saldo);
    }
}

/** Pedido de compra. Aceita produtos, kits e itens com adicionais. */
class Pedido {
    private final Frete frete;
    private final List<LojaVirtual.Item> itens = new ArrayList<>();
    private Cupom cupom;
    private boolean finalizado;

    /** @param tipoFrete PAC, SEDEX ou TRANSPORTADORA */
    Pedido(String tipoFrete) {
        this(Frete.porTipo(tipoFrete));
    }

    /** Permite informar qualquer estratégia de frete, inclusive novas formas de entrega. */
    Pedido(Frete frete) {
        this.frete = frete;
    }

    void adicionar(LojaVirtual.Item item) {
        verificarAberto();
        itens.add(item);
    }

    private void verificarAberto() {
        if (finalizado) {
            throw new IllegalStateException("Pedido finalizado não aceita novos itens");
        }
    }

    /**
     * Devolve os itens do pedido. A lista devolvida não aceita alterações:
     * novos itens entram somente por adicionar().
     */
    List<LojaVirtual.Item> getItens() {
        return Collections.unmodifiableList(itens);
    }

    void aplicarCupom(Cupom cupom) {
        this.cupom = cupom;
    }

    void finalizar() {
        finalizado = true;
    }

    boolean isFinalizado() {
        return finalizado;
    }

    double calcularSubtotal() {
        double subtotal = 0.0;

        for (LojaVirtual.Item item : itens) {
            subtotal += item.getPreco();
        }

        return subtotal;
    }

    double calcularPesoTotal() {
        double peso = 0.0;

        for (LojaVirtual.Item item : itens) {
            peso += item.getPeso();
        }

        return peso;
    }

    /**
     * Calcula o frete do pedido.
     * PAC: R$ 15,00 mais R$ 2,00 por kg.
     * SEDEX: R$ 25,00 mais R$ 4,00 por kg.
     * TRANSPORTADORA: grátis para subtotal a partir de R$ 300,00; R$ 40,00 abaixo disso.
     * A loja negocia novas formas de entrega com frequência.
     */
    double calcularFrete() {
        return frete.calcular(calcularPesoTotal(), calcularSubtotal());
    }

    /** Subtotal com o desconto do cupom, quando houver, mais o frete. */
    double calcularTotal() {
        double subtotal = calcularSubtotal();
        if (cupom != null) {
            subtotal -= subtotal * cupom.getPercentual() / 100;
        }
        return subtotal + calcularFrete();
    }
}

/**
 * Estratégia de cálculo de frete (padrão Strategy).
 * Uma nova forma de entrega é uma nova implementação desta interface,
 * sem alterar a classe Pedido.
 */
interface Frete {

    /**
     * @param peso     peso total do pedido, em quilogramas
     * @param subtotal subtotal do pedido, antes do cupom
     */
    double calcular(double peso, double subtotal);

    /**
     * Escolhe a estratégia pelo nome: PAC, SEDEX ou TRANSPORTADORA.
     * Para nome desconhecido, a estratégia devolvida lança IllegalArgumentException
     * ao calcular, mantendo o comportamento original do Pedido.
     */
    static Frete porTipo(String tipo) {
        if ("PAC".equals(tipo)) {
            return new FretePac();
        }
        if ("SEDEX".equals(tipo)) {
            return new FreteSedex();
        }
        if ("TRANSPORTADORA".equals(tipo)) {
            return new FreteTransportadora();
        }
        return (peso, subtotal) -> {
            throw new IllegalArgumentException("Tipo de frete inválido: " + tipo);
        };
    }
}

/** PAC: R$ 15,00 mais R$ 2,00 por kg. */
class FretePac implements Frete {
    @Override
    public double calcular(double peso, double subtotal) {
        return 15.0 + peso * 2.0;
    }
}

/** SEDEX: R$ 25,00 mais R$ 4,00 por kg. */
class FreteSedex implements Frete {
    @Override
    public double calcular(double peso, double subtotal) {
        return 25.0 + peso * 4.0;
    }
}

/** TRANSPORTADORA: grátis para subtotal a partir de R$ 300,00; R$ 40,00 abaixo disso. */
class FreteTransportadora implements Frete {
    @Override
    public double calcular(double peso, double subtotal) {
        if (subtotal >= 300.0) {
            return 0.0;
        }
        return 40.0;
    }
}

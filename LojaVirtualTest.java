class Produto implements Item {
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
}
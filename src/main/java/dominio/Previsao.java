package dominio;

public class Previsao {

    private double valor;
    private String significado;
    private String estrategia;

    public Previsao(double valor, String significado, String estrategia) {
        this.valor = valor;
        this.significado = significado;
        this.estrategia = estrategia;
    }

    public double getValor() {
        return valor;
    }

    public String getSignificado() {
        return significado;
    }

    public String getEstrategia() {
        return estrategia;
    }
}

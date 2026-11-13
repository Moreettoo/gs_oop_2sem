package dominio;

public enum TipoSensor {

    CHUVA("chuva", "mm/h", 0, 300, 12),
    NIVEL_CORREGO("nível do córrego", "cm", 0, 500, 25),
    UMIDADE_SOLO("umidade do solo", "%", 0, 100, 3);

    private String nome;
    private String unidade;
    private double min;
    private double max;
    private double aumentoChuvaForte;

    TipoSensor(String nome, String unidade, double min, double max, double aumentoChuvaForte) {
        this.nome = nome;
        this.unidade = unidade;
        this.min = min;
        this.max = max;
        this.aumentoChuvaForte = aumentoChuvaForte;
    }

    public String getNome() {
        return nome;
    }

    public String getUnidade() {
        return unidade;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getAumentoChuvaForte() {
        return aumentoChuvaForte;
    }

    public boolean valorValido(double valor) {
        if (Double.isNaN(valor) || Double.isInfinite(valor)) {
            return false;
        }
        if (valor < min || valor > max) {
            return false;
        }
        return true;
    }
}

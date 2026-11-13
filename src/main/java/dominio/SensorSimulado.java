package dominio;

import excecao.FonteIndisponivelException;
import java.util.Random;

public abstract class SensorSimulado implements Sensor {

    private String id;
    private TipoSensor tipo;
    private double valorAtual;
    private double passo;
    private Random random;
    private double probabilidadeFalha;
    private double tendencia;
    private boolean ultimaLeituraFalhou;

    public SensorSimulado(String id, TipoSensor tipo, double valorInicial, double passo,
            Random random) {
        this.id = id;
        this.tipo = tipo;
        this.valorAtual = valorInicial;
        this.passo = passo;
        this.random = random;
        this.probabilidadeFalha = 0;
        this.tendencia = 0;
        this.ultimaLeituraFalhou = false;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TipoSensor getTipo() {
        return tipo;
    }

    @Override
    public boolean estaFuncionando() {
        return !ultimaLeituraFalhou;
    }

    public void setProbabilidadeFalha(double probabilidadeFalha) {
        this.probabilidadeFalha = probabilidadeFalha;
    }

    public void setTendencia(double tendencia) {
        this.tendencia = tendencia;
    }

    @Override
    public double lerValor() throws FonteIndisponivelException {
        if (random.nextDouble() < probabilidadeFalha) {
            ultimaLeituraFalhou = true;
            throw new FonteIndisponivelException("Sensor " + id + " sem resposta");
        }
        ultimaLeituraFalhou = false;
        valorAtual = valorAtual + (random.nextDouble() * 2 - 1) * passo + tendencia;
        valorAtual = limitarNaFaixa(valorAtual);
        return valorAtual;
    }

    private double limitarNaFaixa(double valor) {
        if (valor < tipo.getMin()) {
            return tipo.getMin();
        }
        if (valor > tipo.getMax()) {
            return tipo.getMax();
        }
        return valor;
    }
}

package dominio;

import java.time.LocalDateTime;

public class Alerta {

    private String sensorId;
    private TipoSensor tipo;
    private double valorPrevisto;
    private double limite;
    private NivelAlerta nivel;
    private String estrategia;
    private String explicacao;
    private LocalDateTime dataHora;

    public Alerta(String sensorId, TipoSensor tipo, double valorPrevisto, double limite,
            NivelAlerta nivel, String estrategia, String explicacao, LocalDateTime dataHora) {
        this.sensorId = sensorId;
        this.tipo = tipo;
        this.valorPrevisto = valorPrevisto;
        this.limite = limite;
        this.nivel = nivel;
        this.estrategia = estrategia;
        this.explicacao = explicacao;
        this.dataHora = dataHora;
    }

    public String getSensorId() {
        return sensorId;
    }

    public TipoSensor getTipo() {
        return tipo;
    }

    public double getValorPrevisto() {
        return valorPrevisto;
    }

    public double getLimite() {
        return limite;
    }

    public NivelAlerta getNivel() {
        return nivel;
    }

    public String getEstrategia() {
        return estrategia;
    }

    public String getExplicacao() {
        return explicacao;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}

package dominio;

import excecao.DadosInvalidosException;
import java.time.LocalDateTime;

public class Medicao {

    private String sensorId;
    private TipoSensor tipo;
    private double valor;
    private LocalDateTime dataHora;

    public Medicao(String sensorId, TipoSensor tipo, double valor, LocalDateTime dataHora)
            throws DadosInvalidosException {
        if (sensorId == null || sensorId.trim().isEmpty()) {
            throw new DadosInvalidosException("Medição inválida: o id do sensor está vazio");
        }
        if (!tipo.valorValido(valor)) {
            String mensagem = "Medição de " + tipo.getNome() + " inválida: " + valor + " "
                    + tipo.getUnidade() + " (faixa " + (int) tipo.getMin() + "–"
                    + (int) tipo.getMax() + ")";
            throw new DadosInvalidosException(mensagem);
        }
        this.sensorId = sensorId;
        this.tipo = tipo;
        this.valor = valor;
        this.dataHora = dataHora;
    }

    public String getSensorId() {
        return sensorId;
    }

    public TipoSensor getTipo() {
        return tipo;
    }

    public double getValor() {
        return valor;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}

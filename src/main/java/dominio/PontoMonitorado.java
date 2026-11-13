package dominio;

import java.util.List;

public class PontoMonitorado extends ComponenteTerritorial {

    private List<Sensor> sensores;

    public PontoMonitorado(String id, String nome, String regiao, List<Sensor> sensores) {
        super(id, nome, regiao);
        this.sensores = sensores;
    }

    public List<Sensor> getSensores() {
        return sensores;
    }

    @Override
    public String verificarStatus() {
        int quantidadeFuncionando = 0;
        for (int posicao = 0; posicao < sensores.size(); posicao++) {
            Sensor sensor = sensores.get(posicao);
            if (sensor.estaFuncionando()) {
                quantidadeFuncionando++;
            }
        }
        String novoStatus;
        if (quantidadeFuncionando == 0) {
            novoStatus = "SEM SENSORES";
        } else if (quantidadeFuncionando == sensores.size()) {
            novoStatus = "OPERACIONAL";
        } else {
            novoStatus = "DEGRADADO";
        }
        setStatus(novoStatus);
        return novoStatus;
    }
}

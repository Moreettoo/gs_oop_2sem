package servico;

import dominio.Alerta;
import dominio.Previsao;
import java.util.List;

public class RelatorioSensor {

    private boolean silenciado;
    private String mensagem;
    private List<Previsao> previsoes;
    private Alerta alerta;

    public RelatorioSensor(boolean silenciado, String mensagem, List<Previsao> previsoes,
            Alerta alerta) {
        this.silenciado = silenciado;
        this.mensagem = mensagem;
        this.previsoes = previsoes;
        this.alerta = alerta;
    }

    public boolean isSilenciado() {
        return silenciado;
    }

    public String getMensagem() {
        return mensagem;
    }

    public List<Previsao> getPrevisoes() {
        return previsoes;
    }

    public Alerta getAlerta() {
        return alerta;
    }
}

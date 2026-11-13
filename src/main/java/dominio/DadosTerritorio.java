package dominio;

import excecao.DadosInvalidosException;

import java.util.HashMap;

public class DadosTerritorio {

    private String regiao;
    private int populacaoExposta;

    private HashMap<TipoSensor, double[]> limiares;

    public DadosTerritorio(String regiao, int populacaoExposta) throws DadosInvalidosException {
        if (regiao == null || regiao.trim().isEmpty()) {
            throw new DadosInvalidosException("Território inválido: o nome da região está vazio");
        }
        validarPopulacao(populacaoExposta);
        this.regiao = regiao;
        this.populacaoExposta = populacaoExposta;
        this.limiares = new HashMap<TipoSensor, double[]>();
        this.limiares.put(TipoSensor.CHUVA, new double[] {30, 50, 80});
        this.limiares.put(TipoSensor.NIVEL_CORREGO, new double[] {150, 250, 350});
        this.limiares.put(TipoSensor.UMIDADE_SOLO, new double[] {70, 85, 95});
    }

    public String getRegiao() {
        return regiao;
    }

    public int getPopulacaoExposta() {
        return populacaoExposta;
    }

    public void setPopulacaoExposta(int populacaoExposta) throws DadosInvalidosException {
        validarPopulacao(populacaoExposta);
        this.populacaoExposta = populacaoExposta;
    }

    public double getLimiar(TipoSensor tipo, NivelAlerta nivel) {
        double[] limiaresDoSensor = limiares.get(tipo);
        return limiaresDoSensor[nivel.ordinal()];
    }

    public void setLimiares(TipoSensor tipo, double atencao, double alerta, double critico)
            throws DadosInvalidosException {
        validarLimiares(tipo, atencao, alerta, critico);
        double[] limiaresDoSensor = limiares.get(tipo);
        limiaresDoSensor[0] = atencao;
        limiaresDoSensor[1] = alerta;
        limiaresDoSensor[2] = critico;
    }

    public NivelAlerta classificar(TipoSensor tipo, double valor) {
        double[] limiaresDoSensor = limiares.get(tipo);
        if (valor >= limiaresDoSensor[2]) {
            return NivelAlerta.CRITICO;
        }
        if (valor >= limiaresDoSensor[1]) {
            return NivelAlerta.ALERTA;
        }
        if (valor >= limiaresDoSensor[0]) {
            return NivelAlerta.ATENCAO;
        }
        return null;
    }

    private void validarPopulacao(int populacao) throws DadosInvalidosException {
        if (populacao < 0) {
            throw new DadosInvalidosException(
                    "População exposta inválida: " + populacao + " (não pode ser negativa)");
        }
    }

    private void validarLimiares(TipoSensor tipo, double atencao, double alerta, double critico)
            throws DadosInvalidosException {
        if (Double.isNaN(atencao) || Double.isNaN(alerta) || Double.isNaN(critico)) {
            throw new DadosInvalidosException(
                    "Limiares de " + tipo.getNome() + " inválidos: há valor não numérico");
        }
        if (atencao < 0 || atencao >= alerta || alerta >= critico || critico > tipo.getMax()) {
            String mensagem = "Limiares de " + tipo.getNome() + " inválidos: " + atencao + ", "
                    + alerta + ", " + critico + " (precisa ser 0 <= atenção < alerta < crítico <= "
                    + (int) tipo.getMax() + ")";
            throw new DadosInvalidosException(mensagem);
        }
    }
}

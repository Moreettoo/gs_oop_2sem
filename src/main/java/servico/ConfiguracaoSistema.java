package servico;

import dominio.DadosTerritorio;
import excecao.DadosInvalidosException;

public class ConfiguracaoSistema {

    private static ConfiguracaoSistema instancia;

    private DadosTerritorio dadosTerritorio;
    private int janela = 5;
    private int minimoLeituras = 5;

    private ConfiguracaoSistema() {
        this.dadosTerritorio = criarTerritorioPadrao();
    }

    public static ConfiguracaoSistema getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracaoSistema();
        }
        return instancia;
    }

    public DadosTerritorio getDadosTerritorio() {
        return dadosTerritorio;
    }

    public int getJanela() {
        return janela;
    }

    public int getMinimoLeituras() {
        return minimoLeituras;
    }

    public void redefinirPadroes() {
        this.dadosTerritorio = criarTerritorioPadrao();
    }

    private DadosTerritorio criarTerritorioPadrao() {
        try {
            return new DadosTerritorio("Zona Leste (fictícia)", 1200);
        } catch (DadosInvalidosException erro) {
            throw new IllegalStateException(
                    "Os valores padrão do território deveriam ser válidos: " + erro.getMessage());
        }
    }
}

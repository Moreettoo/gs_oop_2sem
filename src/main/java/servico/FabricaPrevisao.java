package servico;

import dominio.EstrategiaPrevisao;
import dominio.PrevisaoComparacao;
import dominio.PrevisaoMediaMovel;
import excecao.DadosInvalidosException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FabricaPrevisao {

    private ConfiguracaoSistema configuracao;

    public FabricaPrevisao(ConfiguracaoSistema configuracao) {
        this.configuracao = configuracao;
    }

    public EstrategiaPrevisao criar(String nome) throws DadosInvalidosException {
        int janela = configuracao.getJanela();
        HashMap<String, EstrategiaPrevisao> mapa = new HashMap<>();
        mapa.put("MEDIA_MOVEL", new PrevisaoMediaMovel(janela));
        mapa.put("COMPARACAO", new PrevisaoComparacao(janela));

        EstrategiaPrevisao estrategia = mapa.get(nome);
        if (estrategia == null) {
            throw new DadosInvalidosException("Estratégia desconhecida: " + nome
                    + " (use MEDIA_MOVEL ou COMPARACAO)");
        }
        return estrategia;
    }

    public List<EstrategiaPrevisao> criarTodas() throws DadosInvalidosException {
        List<EstrategiaPrevisao> estrategias = new ArrayList<>();
        estrategias.add(criar("MEDIA_MOVEL"));
        estrategias.add(criar("COMPARACAO"));
        return estrategias;
    }
}

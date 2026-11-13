package dominio;

import excecao.DadosInvalidosException;
import java.util.List;
import java.util.Locale;

public class PrevisaoMediaMovel extends EstrategiaPrevisao {

    public PrevisaoMediaMovel(int janela) throws DadosInvalidosException {
        super("Média móvel", janela);
    }

    @Override
    public Previsao prever(List<Medicao> historico) throws DadosInvalidosException {
        super.prever(historico);

        int total = historico.size();
        int quantidadeUsada = getJanela();
        if (total < quantidadeUsada) {
            quantidadeUsada = total;
        }
        double media = calcularMedia(historico, total - quantidadeUsada, total);

        String significado = "Média das últimas " + quantidadeUsada
                + " leituras (janela de " + getJanela() + "): " + String.format(Locale.forLanguageTag("pt-BR"), "%.1f", media);
        return new Previsao(media, significado, getNome());
    }
}

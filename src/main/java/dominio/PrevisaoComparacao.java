package dominio;

import excecao.DadosInvalidosException;
import java.util.List;
import java.util.Locale;

public class PrevisaoComparacao extends EstrategiaPrevisao {

    public PrevisaoComparacao(int janela) throws DadosInvalidosException {
        super("Comparação com período anterior", janela);
    }

    @Override
    public Previsao prever(List<Medicao> historico) throws DadosInvalidosException {
        super.prever(historico);

        int total = historico.size();
        if (total < 2) {
            throw new DadosInvalidosException(
                    "São necessárias pelo menos 2 leituras para comparar períodos");
        }
        int tamanhoPeriodo = getJanela();
        if (tamanhoPeriodo > total / 2) {
            tamanhoPeriodo = total / 2;
        }

        double mediaRecente = calcularMedia(historico, total - tamanhoPeriodo, total);
        double mediaAnterior = calcularMedia(historico,
                total - 2 * tamanhoPeriodo, total - tamanhoPeriodo);
        double variacao = mediaRecente - mediaAnterior;

        double valorPrevisto = mediaRecente + variacao;
        if (valorPrevisto < 0) {
            valorPrevisto = 0;
        }

        String significado = "O valor está " + descreverTendencia(variacao)
                + " (período recente " + String.format(Locale.forLanguageTag("pt-BR"), "%.1f", mediaRecente)
                + " contra anterior " + String.format(Locale.forLanguageTag("pt-BR"), "%.1f", mediaAnterior) + ")";
        return new Previsao(valorPrevisto, significado, getNome());
    }

    private String descreverTendencia(double variacao) {
        if (Math.abs(variacao) < 1e-9) {
            return "estável";
        }
        if (variacao > 0) {
            return "subindo";
        }
        return "caindo";
    }
}

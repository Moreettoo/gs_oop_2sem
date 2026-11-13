package dominio;

import excecao.DadosInvalidosException;
import java.util.List;

public abstract class EstrategiaPrevisao {

    private String nome;
    private int janela;

    protected EstrategiaPrevisao(String nome, int janela) throws DadosInvalidosException {
        if (janela < 1) {
            throw new DadosInvalidosException("Janela inválida: " + janela + " (o mínimo é 1)");
        }
        this.nome = nome;
        this.janela = janela;
    }

    public String getNome() {
        return nome;
    }

    public int getJanela() {
        return janela;
    }

    public Previsao prever(List<Medicao> historico) throws DadosInvalidosException {
        validarHistorico(historico);
        Medicao ultimaMedicao = historico.get(historico.size() - 1);
        double ultimoValor = ultimaMedicao.getValor();
        String significado = "Previsão simples: repete o último valor lido (" + ultimoValor + ")";
        return new Previsao(ultimoValor, significado, nome);
    }

    protected void validarHistorico(List<Medicao> historico) throws DadosInvalidosException {
        if (historico == null || historico.isEmpty()) {
            throw new DadosInvalidosException("Não há leituras para fazer a previsão");
        }
    }

    protected double calcularMedia(List<Medicao> historico, int inicio, int fim) {
        double soma = 0;
        for (int posicao = inicio; posicao < fim; posicao++) {
            soma = soma + historico.get(posicao).getValor();
        }
        return soma / (fim - inicio);
    }
}

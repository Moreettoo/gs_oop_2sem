package servico;

import dominio.Medicao;
import excecao.FonteIndisponivelException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ExportadorCsv {

    public static void exportarMedicoes(List<Medicao> medicoes, Path destino)
            throws FonteIndisponivelException {
        List<String> linhas = new ArrayList<>();
        linhas.add("sensor_id,tipo,valor,data_hora");
        for (int posicao = 0; posicao < medicoes.size(); posicao++) {
            linhas.add(montarLinha(medicoes.get(posicao)));
        }
        try {
            Path pastaDoArquivo = destino.toAbsolutePath().getParent();
            Files.createDirectories(pastaDoArquivo);
            Files.write(destino, linhas);
        } catch (IOException erro) {
            throw new FonteIndisponivelException("Não foi possível gravar o arquivo "
                    + destino + ": " + erro.getMessage(), erro);
        }
    }

    private static String montarLinha(Medicao medicao) {
        return escaparCampo(medicao.getSensorId()) + ","
                + escaparCampo(medicao.getTipo().name()) + ","
                + medicao.getValor() + ","
                + medicao.getDataHora().toString();
    }

    private static String escaparCampo(String texto) {
        boolean precisaDeAspas = texto.contains(",") || texto.contains("\"")
                || texto.contains("\n") || texto.contains("\r");
        if (!precisaDeAspas) {
            return texto;
        }
        return "\"" + texto.replace("\"", "\"\"") + "\"";
    }
}

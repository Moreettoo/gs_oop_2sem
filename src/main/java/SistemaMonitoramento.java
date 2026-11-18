import dominio.*;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import persistencia.*;
import servico.*;

public class SistemaMonitoramento {

    private static final int INVALIDO = Integer.MIN_VALUE;

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private Scanner entrada = new Scanner(System.in);
    private boolean entradaAcabou = false;
    private ServicoMonitoramento servico;
    private ConfiguracaoSistema configuracao;
    private PontoMonitorado ponto;
    private List<SensorSimulado> sensoresSimulados = new ArrayList<>();

    public static void main(String[] args) {
        try {
            new SistemaMonitoramento().executarMenu();
        } catch (FonteIndisponivelException erro) {
            System.out.println("Fonte indisponível: " + erro.getMessage());
        }
    }

    public SistemaMonitoramento() throws FonteIndisponivelException {
        ConexaoBanco conexaoBanco = ConexaoBanco.padrao();
        conexaoBanco.inicializarSchema();
        configuracao = ConfiguracaoSistema.getInstancia();
        servico = new ServicoMonitoramento(new MedicaoDAO(conexaoBanco),
                new AlertaDAO(conexaoBanco), configuracao, new FabricaPrevisao(configuracao));
        sensoresSimulados.add(new SensorPluviometro("PLUV-01", new Random()));
        sensoresSimulados.add(new SensorNivelCorrego("NIVEL-01", new Random()));
        sensoresSimulados.add(new SensorUmidadeSolo("SOLO-01", new Random()));
        ponto = new PontoMonitorado("PONTO-01", "Córrego Água Branca – Ponte da Rua 5",
                "Zona Leste (fictícia)", new ArrayList<Sensor>(sensoresSimulados));
    }

    private void executarMenu() {
        System.out.println("Sistema de monitoramento - " + ponto.getNome());
        int opcao = INVALIDO;
        while (opcao != 0 && !entradaAcabou) {
            mostrarMenu();
            opcao = lerInteiro("Escolha uma opção: ");
            if (!entradaAcabou) {
                tratarOpcao(opcao);
            }
        }
        if (entradaAcabou) {
            System.out.println("Fim da entrada. Encerrando.");
        }
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("Menu principal");
        System.out.println("1 - Verificar sensores");
        System.out.println("2 - Rodar previsões");
        System.out.println("3 - Ver histórico");
        System.out.println("4 - Gerenciar dados do território");
        System.out.println("5 - Status completo");
        System.out.println("6 - Simular 10 leituras com chuva forte");
        System.out.println("0 - Encerrar");
    }

    private void tratarOpcao(int opcao) {
        try {
            switch (opcao) {
                case 1: lerSensores(1); break;
                case 2: rodarPrevisoes(); break;
                case 3: verHistorico(); break;
                case 4: gerenciarDadosTerritorio(); break;
                case 5: mostrarStatusCompleto(); break;
                case 6: simularChuvaForte(); break;
                case 0: System.out.println("Encerrando. Os dados ficam salvos no banco."); break;
                default: System.out.println("Opção inválida"); break;
            }
        } catch (DadosInvalidosException erro) {
            System.out.println("Dado inválido: " + erro.getMessage());
        } catch (FonteIndisponivelException erro) {
            System.out.println("Fonte indisponível: " + erro.getMessage());
        }
    }

    private void lerSensores(int quantidade) throws DadosInvalidosException,
            FonteIndisponivelException {
        for (Sensor sensor : ponto.getSensores()) {
            List<Medicao> gravadas = servico.simularLeituras(sensor, quantidade);
            Medicao ultima = gravadas.get(gravadas.size() - 1);
            System.out.println(sensor.getId() + ": " + gravadas.size()
                    + " leitura(s) gravada(s), última " + formatarNumero(ultima.getValor())
                    + " " + sensor.getTipo().getUnidade());
        }
    }

    private void rodarPrevisoes() throws DadosInvalidosException, FonteIndisponivelException {
        for (Sensor sensor : ponto.getSensores()) {
            mostrarRelatorio(sensor, servico.rodarPrevisao(sensor));
        }
    }

    private void mostrarRelatorio(Sensor sensor, RelatorioSensor relatorio) {
        System.out.println(sensor.getId() + " (" + sensor.getTipo().getNome() + ")");
        if (relatorio.isSilenciado()) {
            System.out.println("  " + relatorio.getMensagem());
            return;
        }
        for (Previsao previsao : relatorio.getPrevisoes()) {
            System.out.println("  " + previsao.getEstrategia() + ": " + formatarNumero(previsao.getValor())
                    + " " + sensor.getTipo().getUnidade());
            System.out.println("    Significado: " + previsao.getSignificado());
        }
        Alerta alerta = relatorio.getAlerta();
        if (alerta == null) {
            System.out.println("  Nenhum alerta: as previsões estão abaixo dos limiares.");
        } else {
            System.out.println("  Alerta " + alerta.getNivel().getRotulo() + " emitido: "
                    + alerta.getExplicacao());
        }
    }

    private void verHistorico() throws FonteIndisponivelException {
        System.out.println("1 - Medições de um sensor");
        System.out.println("2 - Alertas emitidos");
        System.out.println("3 - Exportar CSV de um sensor");
        switch (lerInteiro("Escolha: ")) {
            case 1: mostrarMedicoes(); break;
            case 2: mostrarAlertas(); break;
            case 3: exportarCsv(); break;
            default: System.out.println("Opção inválida"); break;
        }
    }

    private void mostrarMedicoes() throws FonteIndisponivelException {
        Sensor sensor = escolherSensor();
        if (sensor == null) {
            return;
        }
        List<Medicao> medicoes = servico.historicoPorSensor(sensor.getId());
        System.out.println(sensor.getId() + ": " + medicoes.size() + " medições gravadas");
        for (int posicao = Math.max(0, medicoes.size() - 20); posicao < medicoes.size(); posicao++) {
            Medicao medicao = medicoes.get(posicao);
            System.out.println(medicao.getDataHora().format(FORMATO_DATA) + "  "
                    + formatarNumero(medicao.getValor()) + " " + sensor.getTipo().getUnidade());
        }
    }

    private void mostrarAlertas() throws FonteIndisponivelException {
        List<Alerta> alertas = servico.listarAlertas();
        System.out.println(alertas.size() + " alerta(s) emitido(s)");
        for (Alerta alerta : alertas) {
            System.out.println(alerta.getDataHora().format(FORMATO_DATA) + "  "
                    + alerta.getNivel().getRotulo() + "  " + alerta.getExplicacao());
        }
    }

    private void exportarCsv() throws FonteIndisponivelException {
        Sensor sensor = escolherSensor();
        if (sensor == null) {
            return;
        }
        List<Medicao> medicoes = servico.historicoPorSensor(sensor.getId());
        Path destino = Paths.get("exportacoes", sensor.getId() + ".csv");
        ExportadorCsv.exportarMedicoes(medicoes, destino);
        System.out.println(medicoes.size() + " medições gravadas em " + destino);
    }

    private void gerenciarDadosTerritorio() throws DadosInvalidosException {
        System.out.println("1 - Ver dados do território");
        System.out.println("2 - Alterar população exposta");
        System.out.println("3 - Alterar limiares de um tipo de sensor");
        switch (lerInteiro("Escolha: ")) {
            case 1: mostrarDadosTerritorio(); break;
            case 2: alterarPopulacao(); break;
            case 3: alterarLimiares(); break;
            default: System.out.println("Opção inválida"); break;
        }
    }

    private void mostrarDadosTerritorio() {
        DadosTerritorio territorio = configuracao.getDadosTerritorio();
        System.out.println("Região: " + territorio.getRegiao());
        System.out.println("População exposta: " + territorio.getPopulacaoExposta());
        for (TipoSensor tipo : TipoSensor.values()) {
            System.out.println("Limiares de " + tipo.getNome() + " (" + tipo.getUnidade() + "): "
                    + "ATENÇÃO " + formatarNumero(territorio.getLimiar(tipo, NivelAlerta.ATENCAO))
                    + ", ALERTA " + formatarNumero(territorio.getLimiar(tipo, NivelAlerta.ALERTA))
                    + ", CRÍTICO " + formatarNumero(territorio.getLimiar(tipo, NivelAlerta.CRITICO)));
        }
    }

    private void alterarPopulacao() throws DadosInvalidosException {
        int novaPopulacao = lerInteiro("Nova população exposta: ");
        if (novaPopulacao == INVALIDO) {
            System.out.println("Opção inválida: digite um número inteiro.");
            return;
        }
        configuracao.getDadosTerritorio().setPopulacaoExposta(novaPopulacao);
        System.out.println("População exposta alterada para " + novaPopulacao);
    }

    private void alterarLimiares() throws DadosInvalidosException {
        TipoSensor tipo = escolherTipoSensor();
        if (tipo == null) {
            return;
        }
        double atencao = lerDecimal("Novo limiar de ATENÇÃO: ");
        double alerta = lerDecimal("Novo limiar de ALERTA: ");
        double critico = lerDecimal("Novo limiar de CRÍTICO: ");
        configuracao.getDadosTerritorio().setLimiares(tipo, atencao, alerta, critico);
        System.out.println("Limiares de " + tipo.getNome() + " alterados.");
    }

    private void mostrarStatusCompleto() throws FonteIndisponivelException {
        System.out.println("Ponto: " + ponto.getNome() + " (" + ponto.getRegiao() + ")");
        System.out.println("Status do ponto: " + ponto.verificarStatus());
        for (Sensor sensor : ponto.getSensores()) {
            int quantidade = servico.historicoPorSensor(sensor.getId()).size();
            System.out.println("Sensor " + sensor.getId() + ": " + quantidade + " leitura(s) gravada(s)");
        }
        mostrarDadosTerritorio();
        System.out.println("Janela de previsão " + configuracao.getJanela()
                + ", mínimo de leituras " + configuracao.getMinimoLeituras());
        System.out.println("Alertas emitidos: " + servico.listarAlertas().size());
    }

    private void simularChuvaForte() throws DadosInvalidosException, FonteIndisponivelException {
        for (SensorSimulado sensor : sensoresSimulados) {
            sensor.setTendencia(sensor.getTipo().getAumentoChuvaForte());
        }
        System.out.println("Simulando chuva forte...");
        try {
            lerSensores(10);
        } finally {
            for (SensorSimulado sensor : sensoresSimulados) {
                sensor.setTendencia(0);
            }
        }
    }

    private Sensor escolherSensor() {
        List<Sensor> sensores = ponto.getSensores();
        for (int posicao = 0; posicao < sensores.size(); posicao++) {
            System.out.println((posicao + 1) + " - " + sensores.get(posicao).getId());
        }
        int numero = lerNumeroEntre("Escolha o sensor: ", sensores.size());
        if (numero == 0) {
            return null;
        }
        return sensores.get(numero - 1);
    }

    private TipoSensor escolherTipoSensor() {
        TipoSensor[] tipos = TipoSensor.values();
        for (int posicao = 0; posicao < tipos.length; posicao++) {
            System.out.println((posicao + 1) + " - " + tipos[posicao].getNome());
        }
        int numero = lerNumeroEntre("Escolha o tipo de sensor: ", tipos.length);
        if (numero == 0) {
            return null;
        }
        return tipos[numero - 1];
    }

    private int lerNumeroEntre(String pergunta, int maximo) {
        int numero = lerInteiro(pergunta);
        if (numero < 1 || numero > maximo) {
            System.out.println("Opção inválida");
            return 0;
        }
        return numero;
    }

    private String lerLinha(String pergunta) {
        System.out.print(pergunta);
        if (!entrada.hasNextLine()) {
            entradaAcabou = true;
            System.out.println();
            return "";
        }
        return entrada.nextLine().trim();
    }

    private int lerInteiro(String pergunta) {
        try {
            return Integer.parseInt(lerLinha(pergunta));
        } catch (NumberFormatException erro) {
            return INVALIDO;
        }
    }

    private double lerDecimal(String pergunta) {
        try {
            return Double.parseDouble(lerLinha(pergunta).replace(',', '.'));
        } catch (NumberFormatException erro) {
            return Double.NaN;
        }
    }

    private String formatarNumero(double numero) {
        return String.format(Locale.forLanguageTag("pt-BR"), "%.1f", numero);
    }
}

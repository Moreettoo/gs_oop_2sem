package servico;

import dominio.Alerta;
import dominio.EstrategiaPrevisao;
import dominio.Medicao;
import dominio.NivelAlerta;
import dominio.Previsao;
import dominio.Sensor;
import dominio.TipoSensor;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import persistencia.AlertaDAO;
import persistencia.MedicaoDAO;

public class ServicoMonitoramento {

    private MedicaoDAO medicaoDAO;
    private AlertaDAO alertaDAO;
    private ConfiguracaoSistema configuracao;
    private FabricaPrevisao fabrica;

    public ServicoMonitoramento(MedicaoDAO medicaoDAO, AlertaDAO alertaDAO,
            ConfiguracaoSistema configuracao, FabricaPrevisao fabrica) {
        this.medicaoDAO = medicaoDAO;
        this.alertaDAO = alertaDAO;
        this.configuracao = configuracao;
        this.fabrica = fabrica;
    }

    public Medicao lerEGravar(Sensor sensor) throws DadosInvalidosException,
            FonteIndisponivelException {
        double valorLido = sensor.lerValor();
        Medicao medicao = new Medicao(sensor.getId(), sensor.getTipo(), valorLido,
                LocalDateTime.now());
        medicaoDAO.inserir(medicao);
        return medicao;
    }

    public List<Medicao> simularLeituras(Sensor sensor, int quantidade)
            throws DadosInvalidosException, FonteIndisponivelException {
        if (quantidade < 1) {
            throw new DadosInvalidosException("Quantidade de leituras inválida: " + quantidade
                    + " (o mínimo é 1)");
        }
        List<Medicao> medicoesGravadas = new ArrayList<>();
        for (int contador = 0; contador < quantidade; contador++) {
            medicoesGravadas.add(lerEGravar(sensor));
        }
        return medicoesGravadas;
    }

    public RelatorioSensor rodarPrevisao(Sensor sensor) throws FonteIndisponivelException,
            DadosInvalidosException {
        List<Medicao> historico = medicaoDAO.buscarUltimas(sensor.getId(),
                2 * configuracao.getJanela());
        int minimoLeituras = configuracao.getMinimoLeituras();

        if (historico.size() < minimoLeituras) {
            String aviso = "Histórico insuficiente (" + historico.size() + " de "
                    + minimoLeituras + "): ainda não dá para prever.";
            return new RelatorioSensor(true, aviso, new ArrayList<Previsao>(), null);
        }

        List<Previsao> previsoes = new ArrayList<>();
        for (EstrategiaPrevisao estrategia : fabrica.criarTodas()) {
            previsoes.add(estrategia.prever(historico));
        }
        Alerta alerta = avaliarAlerta(sensor.getId(), sensor.getTipo(), previsoes);
        if (alerta != null) {
            alertaDAO.inserir(alerta);
        }
        return new RelatorioSensor(false, "Previsão feita com " + historico.size() + " leituras.",
                previsoes, alerta);
    }

    public Alerta avaliarAlerta(String sensorId, TipoSensor tipo, List<Previsao> previsoes) {
        NivelAlerta piorNivel = null;
        Previsao piorPrevisao = null;
        for (Previsao previsao : previsoes) {
            NivelAlerta nivel = configuracao.getDadosTerritorio().classificar(tipo,
                    previsao.getValor());
            if (nivel != null && (piorNivel == null || nivel.ordinal() > piorNivel.ordinal())) {
                piorNivel = nivel;
                piorPrevisao = previsao;
            }
        }
        if (piorPrevisao == null) {
            return null;
        }
        double limite = configuracao.getDadosTerritorio().getLimiar(tipo, piorNivel);
        String explicacao = montarExplicacao(sensorId, tipo, piorPrevisao, piorNivel, limite);
        return new Alerta(sensorId, tipo, piorPrevisao.getValor(), limite, piorNivel,
                piorPrevisao.getEstrategia(), explicacao, LocalDateTime.now());
    }

    public List<Medicao> historicoPorSensor(String sensorId) throws FonteIndisponivelException {
        return medicaoDAO.buscarHistoricoPorSensor(sensorId);
    }

    public List<Alerta> listarAlertas() throws FonteIndisponivelException {
        return alertaDAO.listarTodos();
    }

    private String montarExplicacao(String sensorId, TipoSensor tipo, Previsao previsao,
            NivelAlerta nivel, double limite) {
        String unidade = tipo.getUnidade();
        return "Sensor " + sensorId + " (" + tipo.getNome() + "): previsto "
                + formatarNumero(previsao.getValor()) + " " + unidade + " pela "
                + previsao.getEstrategia() + ", acima do limite de " + nivel.getRotulo()
                + " (" + formatarNumero(limite) + " " + unidade + ").";
    }

    private String formatarNumero(double numero) {
        return String.format(Locale.forLanguageTag("pt-BR"), "%.1f", numero);
    }
}

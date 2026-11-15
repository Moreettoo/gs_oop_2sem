package persistencia;

import dominio.Medicao;
import dominio.TipoSensor;
import excecao.DadosInvalidosException;
import excecao.FonteIndisponivelException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class MedicaoDAO {

    private ConexaoBanco conexaoBanco;

    public MedicaoDAO(ConexaoBanco conexaoBanco) {
        this.conexaoBanco = conexaoBanco;
    }

    public void inserir(Medicao medicao) throws FonteIndisponivelException {
        String sql = "INSERT INTO MEDICAO (SENSOR_ID, TIPO, VALOR, DATA_HORA) VALUES (?, ?, ?, ?)";
        try (Connection conexao = conexaoBanco.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, medicao.getSensorId());
            comando.setString(2, medicao.getTipo().name());
            comando.setDouble(3, medicao.getValor());
            comando.setTimestamp(4, Timestamp.valueOf(medicao.getDataHora()));
            comando.executeUpdate();
        } catch (SQLException erro) {
            throw new FonteIndisponivelException("Erro ao gravar a medição do sensor "
                    + medicao.getSensorId() + ": " + erro.getMessage(), erro);
        }
    }

    public List<Medicao> buscarUltimas(String sensorId, int n) throws FonteIndisponivelException {
        String sql = "SELECT SENSOR_ID, TIPO, VALOR, DATA_HORA FROM ("
                + "SELECT * FROM MEDICAO WHERE SENSOR_ID = ? ORDER BY DATA_HORA DESC, ID DESC LIMIT ?) "
                + "ORDER BY DATA_HORA ASC, ID ASC";
        List<Medicao> ultimas = new ArrayList<>();
        try (Connection conexao = conexaoBanco.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, sensorId);
            comando.setInt(2, n);
            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    ultimas.add(montarMedicao(resultado));
                }
            }
        } catch (SQLException erro) {
            throw new FonteIndisponivelException("Erro ao buscar as últimas medições do sensor "
                    + sensorId + ": " + erro.getMessage(), erro);
        }
        return ultimas;
    }

    public List<Medicao> buscarHistoricoPorSensor(String sensorId)
            throws FonteIndisponivelException {
        String sql = "SELECT SENSOR_ID, TIPO, VALOR, DATA_HORA FROM MEDICAO "
                + "WHERE SENSOR_ID = ? ORDER BY DATA_HORA ASC, ID ASC";
        List<Medicao> historico = new ArrayList<>();
        try (Connection conexao = conexaoBanco.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, sensorId);
            try (ResultSet resultado = comando.executeQuery()) {
                while (resultado.next()) {
                    historico.add(montarMedicao(resultado));
                }
            }
        } catch (SQLException erro) {
            throw new FonteIndisponivelException("Erro ao buscar o histórico do sensor "
                    + sensorId + ": " + erro.getMessage(), erro);
        }
        return historico;
    }

    private Medicao montarMedicao(ResultSet resultado) throws SQLException,
            FonteIndisponivelException {
        String sensorId = resultado.getString("SENSOR_ID");
        TipoSensor tipo = TipoSensor.valueOf(resultado.getString("TIPO"));
        double valor = resultado.getDouble("VALOR");
        LocalDateTime dataHora = resultado.getTimestamp("DATA_HORA").toLocalDateTime();
        try {
            return new Medicao(sensorId, tipo, valor, dataHora);
        } catch (DadosInvalidosException erro) {
            throw new FonteIndisponivelException("O banco tem uma medição inválida do sensor "
                    + sensorId + ": " + erro.getMessage(), erro);
        }
    }
}

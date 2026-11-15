package persistencia;

import dominio.Alerta;
import dominio.NivelAlerta;
import dominio.TipoSensor;
import excecao.FonteIndisponivelException;
import java.sql.*;
import java.util.*;

public class AlertaDAO {

    private ConexaoBanco conexaoBanco;

    public AlertaDAO(ConexaoBanco conexaoBanco) {
        this.conexaoBanco = conexaoBanco;
    }

    public void inserir(Alerta alerta) throws FonteIndisponivelException {
        String sql = "INSERT INTO ALERTA (SENSOR_ID, TIPO, VALOR_PREVISTO, LIMITE, NIVEL, "
                + "ESTRATEGIA, EXPLICACAO, DATA_HORA) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conexao = conexaoBanco.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql)) {
            comando.setString(1, alerta.getSensorId());
            comando.setString(2, alerta.getTipo().name());
            comando.setDouble(3, alerta.getValorPrevisto());
            comando.setDouble(4, alerta.getLimite());
            comando.setString(5, alerta.getNivel().name());
            comando.setString(6, alerta.getEstrategia());
            comando.setString(7, alerta.getExplicacao());
            comando.setTimestamp(8, Timestamp.valueOf(alerta.getDataHora()));
            comando.executeUpdate();
        } catch (SQLException erro) {
            throw new FonteIndisponivelException("Erro ao gravar o alerta do sensor "
                    + alerta.getSensorId() + ": " + erro.getMessage(), erro);
        }
    }

    public List<Alerta> listarTodos() throws FonteIndisponivelException {
        String sql = "SELECT SENSOR_ID, TIPO, VALOR_PREVISTO, LIMITE, NIVEL, ESTRATEGIA, "
                + "EXPLICACAO, DATA_HORA FROM ALERTA ORDER BY DATA_HORA DESC, ID DESC";
        List<Alerta> alertas = new ArrayList<>();
        try (Connection conexao = conexaoBanco.abrir();
                PreparedStatement comando = conexao.prepareStatement(sql);
                ResultSet resultado = comando.executeQuery()) {
            while (resultado.next()) {
                alertas.add(montarAlerta(resultado));
            }
        } catch (SQLException erro) {
            throw new FonteIndisponivelException("Erro ao listar os alertas: "
                    + erro.getMessage(), erro);
        }
        return alertas;
    }

    private Alerta montarAlerta(ResultSet resultado) throws SQLException {
        String sensorId = resultado.getString("SENSOR_ID");
        TipoSensor tipo = TipoSensor.valueOf(resultado.getString("TIPO"));
        double valorPrevisto = resultado.getDouble("VALOR_PREVISTO");
        double limite = resultado.getDouble("LIMITE");
        NivelAlerta nivel = NivelAlerta.valueOf(resultado.getString("NIVEL"));
        String estrategia = resultado.getString("ESTRATEGIA");
        String explicacao = resultado.getString("EXPLICACAO");
        return new Alerta(sensorId, tipo, valorPrevisto, limite, nivel, estrategia, explicacao,
                resultado.getTimestamp("DATA_HORA").toLocalDateTime());
    }
}

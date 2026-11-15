package persistencia;

import excecao.FonteIndisponivelException;
import java.sql.*;

public class ConexaoBanco {

    private String url;
    private String usuario;
    private String senha;

    public ConexaoBanco(String url) {
        this.url = url;
        this.usuario = "sa";
        this.senha = "";
    }

    public static ConexaoBanco padrao() {
        return new ConexaoBanco("jdbc:h2:file:./data/territorio");
    }

    public Connection abrir() throws FonteIndisponivelException {
        try {
            return DriverManager.getConnection(url, usuario, senha);
        } catch (SQLException erro) {
            throw new FonteIndisponivelException("Não foi possível abrir o banco de dados: "
                    + erro.getMessage(), erro);
        }
    }

    public void inicializarSchema() throws FonteIndisponivelException {
        String sqlMedicao = "CREATE TABLE IF NOT EXISTS MEDICAO ("
                + "ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "SENSOR_ID VARCHAR(40), "
                + "TIPO VARCHAR(20), "
                + "VALOR DOUBLE, "
                + "DATA_HORA TIMESTAMP)";
        String sqlAlerta = "CREATE TABLE IF NOT EXISTS ALERTA ("
                + "ID BIGINT AUTO_INCREMENT PRIMARY KEY, "
                + "SENSOR_ID VARCHAR(40), "
                + "TIPO VARCHAR(20), "
                + "VALOR_PREVISTO DOUBLE, "
                + "LIMITE DOUBLE, "
                + "NIVEL VARCHAR(10), "
                + "ESTRATEGIA VARCHAR(60), "
                + "EXPLICACAO VARCHAR(500), "
                + "DATA_HORA TIMESTAMP)";
        try (Connection conexao = abrir();
                PreparedStatement comandoMedicao = conexao.prepareStatement(sqlMedicao);
                PreparedStatement comandoAlerta = conexao.prepareStatement(sqlAlerta)) {
            comandoMedicao.execute();
            comandoAlerta.execute();
        } catch (SQLException erro) {
            throw new FonteIndisponivelException("Não foi possível criar as tabelas do banco: "
                    + erro.getMessage(), erro);
        }
    }
}

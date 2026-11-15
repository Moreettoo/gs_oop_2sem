import static org.junit.jupiter.api.Assertions.assertEquals;

import dominio.*;
import excecao.*;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import persistencia.*;

public class MedicaoDAOTest {

    private static int contadorBancos = 0;

    private final LocalDateTime base = LocalDateTime.of(2026, 11, 13, 10, 0);

    private ConexaoBanco criarBancoNovo() throws FonteIndisponivelException {
        contadorBancos++;
        ConexaoBanco conexaoBanco = new ConexaoBanco("jdbc:h2:mem:teste" + System.nanoTime()
                + "_" + contadorBancos + ";DB_CLOSE_DELAY=-1");
        conexaoBanco.inicializarSchema();
        return conexaoBanco;
    }

    @Test
    void inserirEBuscarHistoricoDevolveEmOrdemCronologica()
            throws FonteIndisponivelException, DadosInvalidosException {
        // Arrange
        MedicaoDAO medicaoDAO = new MedicaoDAO(criarBancoNovo());
        medicaoDAO.inserir(new Medicao("CHUVA-01", TipoSensor.CHUVA, 20.0, base));
        medicaoDAO.inserir(new Medicao("CHUVA-01", TipoSensor.CHUVA, 40.0, base.plusMinutes(10)));
        medicaoDAO.inserir(new Medicao("CHUVA-01", TipoSensor.CHUVA, 30.0, base.plusMinutes(5)));

        // Act
        List<Medicao> historico = medicaoDAO.buscarHistoricoPorSensor("CHUVA-01");

        // Assert
        assertEquals(3, historico.size());
        assertEquals(20.0, historico.get(0).getValor());
        assertEquals(30.0, historico.get(1).getValor());
        assertEquals(40.0, historico.get(2).getValor());
        assertEquals(base, historico.get(0).getDataHora());
    }

    @Test
    void buscarUltimasRespeitaLimiteEMantemOrdemCrescente()
            throws FonteIndisponivelException, DadosInvalidosException {
        // Arrange
        MedicaoDAO medicaoDAO = new MedicaoDAO(criarBancoNovo());
        for (int posicao = 1; posicao <= 6; posicao++) {
            medicaoDAO.inserir(new Medicao("CHUVA-01", TipoSensor.CHUVA, posicao * 10.0,
                    base.plusMinutes(posicao)));
        }

        // Act
        List<Medicao> ultimas = medicaoDAO.buscarUltimas("CHUVA-01", 3);

        // Assert
        assertEquals(3, ultimas.size());
        assertEquals(40.0, ultimas.get(0).getValor());
        assertEquals(50.0, ultimas.get(1).getValor());
        assertEquals(60.0, ultimas.get(2).getValor());
    }

    @Test
    void alertaComApostrofoInseridoApareceEmListarTodos() throws FonteIndisponivelException {
        // Arrange
        AlertaDAO alertaDAO = new AlertaDAO(criarBancoNovo());
        Alerta antigo = new Alerta("CHUVA-01", TipoSensor.CHUVA, 55.0, 50.0, NivelAlerta.ALERTA,
                "Média móvel", "Chuva forte no Córrego d'Água", base);
        Alerta recente = new Alerta("CORREGO-01", TipoSensor.NIVEL_CORREGO, 360.0, 350.0,
                NivelAlerta.CRITICO, "Comparação", "Córrego subindo", base.plusHours(1));

        // Act
        alertaDAO.inserir(antigo);
        alertaDAO.inserir(recente);
        List<Alerta> alertas = alertaDAO.listarTodos();

        // Assert
        assertEquals(2, alertas.size());
        assertEquals("CORREGO-01", alertas.get(0).getSensorId());
        assertEquals(NivelAlerta.CRITICO, alertas.get(0).getNivel());
        Alerta lido = alertas.get(1);
        assertEquals(TipoSensor.CHUVA, lido.getTipo());
        assertEquals(55.0, lido.getValorPrevisto());
        assertEquals(50.0, lido.getLimite());
        assertEquals("Chuva forte no Córrego d'Água", lido.getExplicacao());
        assertEquals(base, lido.getDataHora());
    }
}

import static org.junit.jupiter.api.Assertions.*;

import dominio.Medicao;
import dominio.TipoSensor;
import excecao.DadosInvalidosException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

public class MedicaoTest {

    private final LocalDateTime dataHora = LocalDateTime.of(2026, 11, 13, 10, 30);

    @Test
    void criaMedicaoValidaGuardaOsDados() throws DadosInvalidosException {
        Medicao medicao = new Medicao("CHUVA-01", TipoSensor.CHUVA, 42.5, dataHora);

        // Assert
        assertEquals("CHUVA-01", medicao.getSensorId());
        assertEquals(TipoSensor.CHUVA, medicao.getTipo());
        assertEquals(42.5, medicao.getValor());
        assertEquals(dataHora, medicao.getDataHora());
    }

    @Test
    void chuvaNegativaLancaDadosInvalidosException() {
        DadosInvalidosException erro = assertThrows(DadosInvalidosException.class,
                () -> new Medicao("CHUVA-01", TipoSensor.CHUVA, -3.0, dataHora));

        // Assert
        assertTrue(erro.getMessage().contains("chuva"));
    }

    @Test
    void valorNaNOuAcimaDoMaximoLancaDadosInvalidosException() {
        assertThrows(DadosInvalidosException.class,
                () -> new Medicao("NIVEL-01", TipoSensor.NIVEL_CORREGO, Double.NaN, dataHora));
        assertThrows(DadosInvalidosException.class,
                () -> new Medicao("SOLO-01", TipoSensor.UMIDADE_SOLO, 100.1, dataHora));
    }
}

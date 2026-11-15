import static org.junit.jupiter.api.Assertions.*;

import dominio.SensorNivelCorrego;
import dominio.SensorPluviometro;
import excecao.FonteIndisponivelException;
import java.util.Random;
import org.junit.jupiter.api.Test;

public class SensorSimuladoTest {

    @Test
    void leituraFicaSempreDentroDaFaixaDoTipo() throws FonteIndisponivelException {
        // Arrange
        SensorPluviometro sensor = new SensorPluviometro("CHUVA-01", new Random(42));
        sensor.setTendencia(50);

        // Act e Assert
        for (int contador = 0; contador < 200; contador++) {
            double valor = sensor.lerValor();
            assertTrue(valor >= 0 && valor <= 300, "valor fora da faixa: " + valor);
        }
    }

    @Test
    void probabilidadeDeFalhaUmLancaFonteIndisponivelException() {
        // Arrange
        SensorNivelCorrego sensor = new SensorNivelCorrego("NIVEL-01", new Random(42));
        sensor.setProbabilidadeFalha(1.0);

        // Act
        FonteIndisponivelException erro = assertThrows(FonteIndisponivelException.class,
                () -> sensor.lerValor());

        // Assert
        assertTrue(erro.getMessage().contains("NIVEL-01"));
    }
}

import static org.junit.jupiter.api.Assertions.*;

import dominio.*;
import excecao.DadosInvalidosException;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.Test;

public class PrevisaoMediaMovelTest {

    private List<Medicao> criarMedicoesDeChuva(double... valores) throws DadosInvalidosException {
        List<Medicao> medicoes = new ArrayList<>();
        LocalDateTime inicio = LocalDateTime.of(2026, 11, 13, 8, 0);
        for (int posicao = 0; posicao < valores.length; posicao++) {
            medicoes.add(new Medicao("CHUVA-01", TipoSensor.CHUVA, valores[posicao],
                    inicio.plusMinutes(posicao * 10)));
        }
        return medicoes;
    }

    @Test
    void mediaDasUltimasCincoLeiturasIgnoraAsMaisAntigas() throws DadosInvalidosException {
        // Arrange
        List<Medicao> historico = criarMedicoesDeChuva(100, 10, 20, 30, 40, 50);
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel(5);

        // Act
        Previsao previsao = estrategia.prever(historico);

        // Assert
        assertEquals(30.0, previsao.getValor(), 1e-9);
        assertEquals("Média móvel", previsao.getEstrategia());
    }

    @Test
    void historicoVazioLancaDadosInvalidosException() throws DadosInvalidosException {
        // Arrange
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel(5);
        List<Medicao> historicoVazio = new ArrayList<>();

        // Act e Assert
        assertThrows(DadosInvalidosException.class, () -> estrategia.prever(historicoVazio));
    }

    @Test
    void comMenosLeiturasQueAJanelaUsaTodas() throws DadosInvalidosException {
        // Arrange
        List<Medicao> historico = criarMedicoesDeChuva(10, 20);
        PrevisaoMediaMovel estrategia = new PrevisaoMediaMovel(5);

        // Act
        Previsao previsao = estrategia.prever(historico);

        // Assert
        assertEquals(15.0, previsao.getValor(), 1e-9);
    }
}

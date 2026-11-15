import static org.junit.jupiter.api.Assertions.*;

import dominio.*;
import excecao.DadosInvalidosException;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.Test;

public class PrevisaoComparacaoTest {

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
    void tendenciaDeAltaProjetaValorMaiorQueAMediaAtual() throws DadosInvalidosException {
        // Arrange
        List<Medicao> historico = criarMedicoesDeChuva(10, 10, 20, 20);
        PrevisaoComparacao estrategia = new PrevisaoComparacao(5);

        // Act
        Previsao previsao = estrategia.prever(historico);

        // Assert
        assertEquals(30.0, previsao.getValor(), 1e-9);
        assertTrue(previsao.getSignificado().contains("subindo"));
        assertEquals("Comparação com período anterior", previsao.getEstrategia());
    }

    @Test
    void umaUnicaLeituraLancaDadosInvalidosException() throws DadosInvalidosException {
        // Arrange
        List<Medicao> historico = criarMedicoesDeChuva(10);
        PrevisaoComparacao estrategia = new PrevisaoComparacao(5);

        // Act e Assert
        assertThrows(DadosInvalidosException.class, () -> estrategia.prever(historico));
    }
}

import static org.junit.jupiter.api.Assertions.*;

import dominio.*;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;

public class DadosTerritorioTest {

    @Test
    void populacaoNegativaLancaDadosInvalidosException() {
        assertThrows(DadosInvalidosException.class,
                () -> new DadosTerritorio("Zona Leste (fictícia)", -1));
    }

    @Test
    void limiaresForaDeOrdemLancamEMantemValoresAnteriores() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio territorio = new DadosTerritorio("Zona Leste (fictícia)", 1200);

        // Act
        assertThrows(DadosInvalidosException.class,
                () -> territorio.setLimiares(TipoSensor.CHUVA, 60.0, 50.0, 80.0));

        // Assert
        assertEquals(30.0, territorio.getLimiar(TipoSensor.CHUVA, NivelAlerta.ATENCAO));
        assertEquals(50.0, territorio.getLimiar(TipoSensor.CHUVA, NivelAlerta.ALERTA));
        assertEquals(80.0, territorio.getLimiar(TipoSensor.CHUVA, NivelAlerta.CRITICO));
    }

    @Test
    void classificarRetornaCriticoQuandoValorIgualAoLimiarCritico() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio territorio = new DadosTerritorio("Zona Leste (fictícia)", 1200);

        // Act
        NivelAlerta nivel = territorio.classificar(TipoSensor.CHUVA, 80.0);

        // Assert
        assertEquals(NivelAlerta.CRITICO, nivel);
    }

    @Test
    void classificarRetornaNullAbaixoDeAtencao() throws DadosInvalidosException {
        // Arrange
        DadosTerritorio territorio = new DadosTerritorio("Zona Leste (fictícia)", 1200);

        // Act
        NivelAlerta nivel = territorio.classificar(TipoSensor.CHUVA, 29.9);

        // Assert
        assertNull(nivel);
    }
}

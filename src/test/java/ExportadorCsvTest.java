import static org.junit.jupiter.api.Assertions.*;

import dominio.Medicao;
import dominio.TipoSensor;
import excecao.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import servico.ExportadorCsv;

public class ExportadorCsvTest {

    @TempDir
    Path pastaTemporaria;

    @Test
    void exportaCabecalhoELinhasDeMedicoes()
            throws DadosInvalidosException, FonteIndisponivelException, IOException {
        // Arrange
        LocalDateTime base = LocalDateTime.of(2026, 11, 13, 10, 0);
        List<Medicao> medicoes = new ArrayList<>();
        medicoes.add(new Medicao("PLUV-01", TipoSensor.CHUVA, 12.5, base));
        medicoes.add(new Medicao("PLUV-01", TipoSensor.CHUVA, 30.0, base.plusMinutes(10)));
        Path destino = pastaTemporaria.resolve("pluv.csv");

        // Act
        ExportadorCsv.exportarMedicoes(medicoes, destino);

        // Assert
        List<String> linhas = Files.readAllLines(destino);
        assertEquals(3, linhas.size());
        assertEquals("sensor_id,tipo,valor,data_hora", linhas.get(0));
        assertEquals("PLUV-01,CHUVA,12.5,2026-11-13T10:00", linhas.get(1));
        assertEquals("PLUV-01,CHUVA,30.0,2026-11-13T10:10", linhas.get(2));
    }

    @Test
    void camposComVirgulaOuAspasSaoEscapados()
            throws DadosInvalidosException, FonteIndisponivelException, IOException {
        // Arrange
        LocalDateTime base = LocalDateTime.of(2026, 11, 13, 10, 0);
        List<Medicao> medicoes = new ArrayList<>();
        medicoes.add(new Medicao("A,\"B\"", TipoSensor.NIVEL_CORREGO, 100.0, base));
        Path destino = pastaTemporaria.resolve("escape.csv");

        // Act
        ExportadorCsv.exportarMedicoes(medicoes, destino);

        // Assert
        List<String> linhas = Files.readAllLines(destino);
        assertEquals("\"A,\"\"B\"\"\",NIVEL_CORREGO,100.0,2026-11-13T10:00", linhas.get(1));
    }

    @Test
    void criaPastaDeDestinoQuandoNaoExiste()
            throws DadosInvalidosException, FonteIndisponivelException {
        // Arrange
        List<Medicao> medicoes = new ArrayList<>();
        medicoes.add(new Medicao("SOLO-01", TipoSensor.UMIDADE_SOLO, 55.0,
                LocalDateTime.of(2026, 11, 13, 10, 0)));
        Path destino = pastaTemporaria.resolve("pasta_nova").resolve("solo.csv");

        // Act
        ExportadorCsv.exportarMedicoes(medicoes, destino);

        // Assert
        assertTrue(Files.exists(destino));
    }
}

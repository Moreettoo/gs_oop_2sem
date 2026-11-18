import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dominio.*;
import excecao.*;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import persistencia.*;
import servico.*;

@ExtendWith(MockitoExtension.class)
public class ServicoMonitoramentoTest {

    @Mock
    private MedicaoDAO medicaoDAO;

    @Mock
    private AlertaDAO alertaDAO;

    private ServicoMonitoramento servico;

    @BeforeEach
    void prepararServico() {
        ConfiguracaoSistema configuracao = ConfiguracaoSistema.getInstancia();
        configuracao.redefinirPadroes();
        servico = new ServicoMonitoramento(medicaoDAO, alertaDAO, configuracao,
                new FabricaPrevisao(configuracao));
    }

    private List<Medicao> criarMedicoesDeChuva(double valor, int quantidade)
            throws DadosInvalidosException {
        List<Medicao> medicoes = new ArrayList<>();
        for (int contador = 0; contador < quantidade; contador++) {
            medicoes.add(new Medicao("PLUV-01", TipoSensor.CHUVA, valor, LocalDateTime.now()));
        }
        return medicoes;
    }

    @Test
    void rodarPrevisaoComMenosDeCincoLeiturasSilenciaEnaoAlerta() throws Exception {
        // Arrange
        SensorDeTeste sensor = new SensorDeTeste("PLUV-01", TipoSensor.CHUVA, 10, 0);
        when(medicaoDAO.buscarUltimas("PLUV-01", 10)).thenReturn(criarMedicoesDeChuva(90, 3));

        // Act
        RelatorioSensor relatorio = servico.rodarPrevisao(sensor);

        // Assert
        assertTrue(relatorio.isSilenciado());
        assertTrue(relatorio.getMensagem().contains("Histórico insuficiente (3 de 5)"));
        assertNull(relatorio.getAlerta());
        verify(alertaDAO, never()).inserir(any(Alerta.class));
    }

    @Test
    void rodarPrevisaoComChuvaForteInsereUmAlertaCriticoExplicado() throws Exception {
        // Arrange
        SensorDeTeste sensor = new SensorDeTeste("PLUV-01", TipoSensor.CHUVA, 10, 0);
        when(medicaoDAO.buscarUltimas("PLUV-01", 10)).thenReturn(criarMedicoesDeChuva(90, 5));

        // Act
        RelatorioSensor relatorio = servico.rodarPrevisao(sensor);

        // Assert
        verify(alertaDAO, times(1)).inserir(any(Alerta.class));
        assertEquals(2, relatorio.getPrevisoes().size());
        assertEquals(NivelAlerta.CRITICO, relatorio.getAlerta().getNivel());
        assertTrue(relatorio.getAlerta().getExplicacao().contains("PLUV-01"));
        assertTrue(relatorio.getAlerta().getExplicacao().contains("90"));
    }

    @Test
    void avaliarAlertaPegaOPiorNivelEntreAsPrevisoes() {
        // Arrange
        List<Previsao> previsoes = new ArrayList<>();
        previsoes.add(new Previsao(55, "previsão mais leve", "Média móvel"));
        previsoes.add(new Previsao(85, "previsão mais forte", "Comparação com período anterior"));

        // Act
        Alerta alerta = servico.avaliarAlerta("PLUV-01", TipoSensor.CHUVA, previsoes);

        // Assert
        assertNotNull(alerta);
        assertEquals(NivelAlerta.CRITICO, alerta.getNivel());
        assertEquals(85, alerta.getValorPrevisto());
        assertEquals(80, alerta.getLimite());
        assertTrue(alerta.getExplicacao().contains("Comparação com período anterior"));
    }

    @Test
    void lerEGravarComValorNegativoLancaEnaoGrava() throws Exception {
        // Arrange
        SensorDeTeste sensor = new SensorDeTeste("PLUV-01", TipoSensor.CHUVA, -5, 0);

        // Act e Assert
        assertThrows(DadosInvalidosException.class, () -> servico.lerEGravar(sensor));
        verify(medicaoDAO, never()).inserir(any(Medicao.class));
    }

    @Test
    void simularLeiturasParaNaPrimeiraFalhaMantendoAsGravadas() throws Exception {
        // Arrange
        SensorDeTeste sensor = new SensorDeTeste("PLUV-01", TipoSensor.CHUVA, 10, 4);

        // Act e Assert
        assertThrows(FonteIndisponivelException.class, () -> servico.simularLeituras(sensor, 10));
        verify(medicaoDAO, times(3)).inserir(any(Medicao.class));
    }
}

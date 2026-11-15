import static org.junit.jupiter.api.Assertions.*;

import dominio.EstrategiaPrevisao;
import dominio.PrevisaoMediaMovel;
import excecao.DadosInvalidosException;
import org.junit.jupiter.api.Test;
import servico.*;

public class FabricaPrevisaoTest {

    @Test
    void getInstanciaSempreDevolveOMesmoObjeto() {
        // Arrange
        ConfiguracaoSistema primeiraChamada = ConfiguracaoSistema.getInstancia();

        // Act
        ConfiguracaoSistema segundaChamada = ConfiguracaoSistema.getInstancia();

        // Assert
        assertSame(primeiraChamada, segundaChamada);
    }

    @Test
    void criarMediaMovelDevolvePrevisaoMediaMovel() throws DadosInvalidosException {
        // Arrange
        FabricaPrevisao fabrica = new FabricaPrevisao(ConfiguracaoSistema.getInstancia());

        // Act
        EstrategiaPrevisao estrategia = fabrica.criar("MEDIA_MOVEL");

        // Assert
        assertInstanceOf(PrevisaoMediaMovel.class, estrategia);
        assertEquals(5, estrategia.getJanela());
    }

    @Test
    void criarNomeDesconhecidoLancaDadosInvalidosException() {
        // Arrange
        FabricaPrevisao fabrica = new FabricaPrevisao(ConfiguracaoSistema.getInstancia());

        // Act e Assert
        assertThrows(DadosInvalidosException.class, () -> fabrica.criar("REDE_NEURAL"));
    }
}

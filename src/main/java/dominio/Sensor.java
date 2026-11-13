package dominio;

import excecao.FonteIndisponivelException;

public interface Sensor {

    String getId();

    TipoSensor getTipo();

    boolean estaFuncionando();

    double lerValor() throws FonteIndisponivelException;
}

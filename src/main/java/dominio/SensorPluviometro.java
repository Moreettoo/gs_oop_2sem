package dominio;

import java.util.Random;

public class SensorPluviometro extends SensorSimulado {

    public SensorPluviometro(String id, Random random) {
        super(id, TipoSensor.CHUVA, 5, 4, random);
    }
}

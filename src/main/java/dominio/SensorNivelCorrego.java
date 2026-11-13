package dominio;

import java.util.Random;

public class SensorNivelCorrego extends SensorSimulado {

    public SensorNivelCorrego(String id, Random random) {
        super(id, TipoSensor.NIVEL_CORREGO, 80, 15, random);
    }
}

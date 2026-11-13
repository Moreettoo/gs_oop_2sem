package dominio;

import java.util.Random;

public class SensorUmidadeSolo extends SensorSimulado {

    public SensorUmidadeSolo(String id, Random random) {
        super(id, TipoSensor.UMIDADE_SOLO, 55, 4, random);
    }
}

import dominio.Sensor;
import dominio.TipoSensor;
import excecao.FonteIndisponivelException;

class SensorDeTeste implements Sensor {

    private String id;
    private TipoSensor tipo;
    private double valorFixo;
    private int numeroDaLeituraQueFalha;
    private int leiturasFeitas = 0;

    SensorDeTeste(String id, TipoSensor tipo, double valorFixo, int numeroDaLeituraQueFalha) {
        this.id = id;
        this.tipo = tipo;
        this.valorFixo = valorFixo;
        this.numeroDaLeituraQueFalha = numeroDaLeituraQueFalha;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TipoSensor getTipo() {
        return tipo;
    }

    @Override
    public boolean estaFuncionando() {
        return numeroDaLeituraQueFalha == 0 || leiturasFeitas < numeroDaLeituraQueFalha;
    }

    @Override
    public double lerValor() throws FonteIndisponivelException {
        leiturasFeitas = leiturasFeitas + 1;
        if (leiturasFeitas == numeroDaLeituraQueFalha) {
            throw new FonteIndisponivelException("Sensor " + id + " não respondeu");
        }
        return valorFixo;
    }
}

package dominio;

public abstract class ComponenteTerritorial {

    private String id;
    private String nome;
    private String regiao;
    private String status;

    public ComponenteTerritorial(String id, String nome, String regiao) {
        this.id = id;
        this.nome = nome;
        this.regiao = regiao;
        this.status = "NAO VERIFICADO";
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getRegiao() {
        return regiao;
    }

    public String getStatus() {
        return status;
    }

    protected void setStatus(String status) {
        this.status = status;
    }

    public abstract String verificarStatus();
}

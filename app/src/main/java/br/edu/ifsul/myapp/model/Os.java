package br.edu.ifsul.myapp.model;

public class Os {
    private String uid;
    private Equipamento equipamento;
    private String descricao;
    private String defeito;
    private String laudotecnico;
    private String previsao;
    private String valor;
    private String status;

    public Os() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Equipamento getEquipamento() { return equipamento; }

    public void setEquipamento(Equipamento equipamento) { this.equipamento = equipamento; }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDefeito() {
        return defeito;
    }

    public void setDefeito(String defeito) {
        this.defeito = defeito;
    }

    public String getLaudotecnico() {
        return laudotecnico;
    }

    public void setLaudotecnico(String laudotecnico) {
        this.laudotecnico = laudotecnico;
    }

    public String getPrevisao() {
        return previsao;
    }

    public void setPrevisao(String previsao) {
        this.previsao = previsao;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Os{" +
                "uid='" + uid + '\'' +
                ", equipamento='" + equipamento + '\'' +
                ", descricao='" + descricao + '\'' +
                ", defeito='" + defeito + '\'' +
                ", laudotecnico='" + laudotecnico + '\'' +
                ", previsao='" + previsao + '\'' +
                ", valor='" + valor + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

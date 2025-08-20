package br.com.martins_borges.model; 

public class MotivoServico {
    private int idMotivo;
    private String descricaoMotivo;


    public MotivoServico() {
    }

   
    public MotivoServico(String descricaoMotivo) {
        this.descricaoMotivo = descricaoMotivo;
    }

   
    public int getIdMotivo() {
        return idMotivo;
    }

    public void setIdMotivo(int idMotivo) {
        this.idMotivo = idMotivo;
    }

    public String getDescricaoMotivo() {
        return descricaoMotivo;
    }

    public void setDescricaoMotivo(String descricaoMotivo) {
        this.descricaoMotivo = descricaoMotivo;
    }

    @Override
    public String toString() {
        return descricaoMotivo;
    }
}
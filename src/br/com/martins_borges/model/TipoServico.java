package br.com.martins_borges.model; 

public class TipoServico {
    private int idTipoServico;
    private String nomeTipoServico;

    
    public TipoServico() {
    }


    public TipoServico(String nomeTipoServico) {
        this.nomeTipoServico = nomeTipoServico;
    }


    public int getIdTipoServico() {
        return idTipoServico;
    }

    public void setIdTipoServico(int idTipoServico) {
        this.idTipoServico = idTipoServico;
    }

    public String getNomeTipoServico() {
        return nomeTipoServico;
    }

    public void setNomeTipoServico(String nomeTipoServico) {
        this.nomeTipoServico = nomeTipoServico;
    }

    @Override
    public String toString() {
        return nomeTipoServico;
    }
}
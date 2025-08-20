package br.com.martins_borges.model; 

public class Parceiro {
    private int idParceiro;
    private String nomeParceiro;
   

   
    public Parceiro() {
    }

   
    public Parceiro(String nomeParceiro) {
        this.nomeParceiro = nomeParceiro;
    }

   
    public int getIdParceiro() {
        return idParceiro;
    }

    public void setIdParceiro(int idParceiro) {
        this.idParceiro = idParceiro;
    }

    public String getNomeParceiro() {
        return nomeParceiro;
    }

    public void setNomeParceiro(String nomeParceiro) {
        this.nomeParceiro = nomeParceiro;
    }

    @Override
    public String toString() {
       
        return nomeParceiro;
    }
}

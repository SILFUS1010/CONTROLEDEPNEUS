package br.com.martins_borges.model; // Confirme se este é o seu pacote de modelos

public class Fabricante {
    private int idFabricante;
    private String nomeFabricante;

    // Construtor vazio
    public Fabricante() { }

    // Construtor com nome
    public Fabricante(String nomeFabricante) {
        this.nomeFabricante = nomeFabricante;
    }

    // Getters e Setters
    public int getIdFabricante() { return idFabricante; }
    public void setIdFabricante(int idFabricante) { this.idFabricante = idFabricante; }
    public String getNomeFabricante() { return nomeFabricante; }
    public void setNomeFabricante(String nomeFabricante) { this.nomeFabricante = nomeFabricante; }

    @Override
    public String toString() {
        return nomeFabricante; // Útil para debug
    }
}
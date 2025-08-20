package br.com.martins_borges.model;

public class ModeloPneu {
    private int idModelo;
    private String nomeModelo;
    // private int idFabricanteFK; // Descomentar se adicionou a FK na tabela

    // Construtor vazio
    public ModeloPneu() {}

    // Construtor com nome
    public ModeloPneu(String nomeModelo) {
        this.nomeModelo = nomeModelo;
    }

    // Getters e Setters
    public int getIdModelo() { return idModelo; }
    public void setIdModelo(int idModelo) { this.idModelo = idModelo; }

    public String getNomeModelo() { return nomeModelo; }
    public void setNomeModelo(String nomeModelo) { this.nomeModelo = nomeModelo; }

    /* Descomentar se usar FK
    public int getIdFabricanteFK() { return idFabricanteFK; }
    public void setIdFabricanteFK(int idFabricanteFK) { this.idFabricanteFK = idFabricanteFK; }
    */

    @Override
    public String toString() {
        return nomeModelo; // Para exibição em ComboBox
    }
}
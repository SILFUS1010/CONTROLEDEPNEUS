package br.com.martins_borges.model; // Confirme o nome do seu pacote

public class TipoPneu {
    private int idTipo;
    private String nomeTipo; // Ex: "Novo", "Recapado"

    // Construtor vazio
    public TipoPneu() {}

    // Construtor com nome
    public TipoPneu(String nomeTipo) {
        this.nomeTipo = nomeTipo;
    }

    // Getters e Setters
    public int getIdTipo() { return idTipo; }
    public void setIdTipo(int idTipo) { this.idTipo = idTipo; }

    public String getNomeTipo() { return nomeTipo; }
    public void setNomeTipo(String nomeTipo) { this.nomeTipo = nomeTipo; }

    @Override
    public String toString() {
        return nomeTipo; // Para exibição em ComboBox
    }
}
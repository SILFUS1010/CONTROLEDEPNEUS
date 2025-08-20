package br.com.martins_borges.model; // Confirme o nome do seu pacote

public class MedidaPneu {
    private int idMedida;
    private String descricaoMedida; // Ex: "295/80R22.5"

    // Construtor vazio
    public MedidaPneu() {}

    // Construtor com descrição
    public MedidaPneu(String descricaoMedida) {
        this.descricaoMedida = descricaoMedida;
    }

    // Getters e Setters
    public int getIdMedida() { return idMedida; }
    public void setIdMedida(int idMedida) { this.idMedida = idMedida; }

    public String getDescricaoMedida() { return descricaoMedida; }
    public void setDescricaoMedida(String descricaoMedida) { this.descricaoMedida = descricaoMedida; }

    @Override
    public String toString() {
        return descricaoMedida; // Para exibição em ComboBox
    }
}
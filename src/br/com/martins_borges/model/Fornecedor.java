package br.com.martins_borges.model; // Ou seu pacote de modelos

public class Fornecedor {
    private int idFornecedor;
    private String nomeFornecedor;

    // Construtor vazio
    public Fornecedor() { }

    // Construtor com nome (útil para criar um novo)
    public Fornecedor(String nomeFornecedor) {
        this.nomeFornecedor = nomeFornecedor;
    }

    // Getters e Setters
    public int getIdFornecedor() { return idFornecedor; }
    public void setIdFornecedor(int idFornecedor) { this.idFornecedor = idFornecedor; }
    public String getNomeFornecedor() { return nomeFornecedor; }
    public void setNomeFornecedor(String nomeFornecedor) { this.nomeFornecedor = nomeFornecedor; }

    @Override
    public String toString() { // Importante para exibição no ComboBox (se usarmos objetos)
        return nomeFornecedor;
    }
}
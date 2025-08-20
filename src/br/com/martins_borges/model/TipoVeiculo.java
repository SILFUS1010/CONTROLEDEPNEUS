package br.com.martins_borges.Model;

public class TipoVeiculo {

    // Atributos baseados nas colunas de TIPOS_CONFIGURACAO (use CAIXA ALTA se renomeou no banco)
    private int idConfig;        // ID_CONFIG
    private String nomeConfig;   // NOME_CONFIG
    private Integer qtdPneusPadrao; // QTD_PNEUS_PADRAO (Integer para permitir null)
    // private String descricaoConfig; // DESCRICAO_CONFIG (Se você adicionou)

    // Construtor vazio
    public TipoVeiculo() {
    }

    // Getters e Setters (gere com Alt+Insert ou escreva)
    public int getIdConfig() {
        return idConfig;
    }

    public void setIdConfig(int idConfig) {
        this.idConfig = idConfig;
    }

    public String getNomeConfig() {
        return nomeConfig;
    }

    public void setNomeConfig(String nomeConfig) {
        this.nomeConfig = nomeConfig;
    }

    public Integer getQtdPneusPadrao() {
        return qtdPneusPadrao;
    }

    public void setQtdPneusPadrao(Integer qtdPneusPadrao) {
        this.qtdPneusPadrao = qtdPneusPadrao;
    }

    // Getter/Setter para descricaoConfig se existir
    // Método toString (útil para debug)
    @Override
    public String toString() {
        return this.getNomeConfig();
    }
}

package br.com.martins_borges.model; // Confirme o pacote

import java.time.LocalDate;


public class Veiculo {

   
    private int ID; 
    private String FROTA;
    private String PLACA;
    private Integer ID_CONFIG_FK; 
    private Integer QTD_PNEUS;   
    private LocalDate DATA_CADASTRO; 
    private String MEDIDA_PNEU;
    private String STATUS_VEICULO;
    private Integer posicaoCarreta;



    public Veiculo() {
      
    }

    
    public Veiculo(String FROTA, String PLACA, Integer ID_CONFIG_FK, Integer QTD_PNEUS, LocalDate DATA_CADASTRO, String MEDIDA_PNEU, String STATUS_VEICULO, Integer posicaoCarreta) {
        this.FROTA = FROTA;
        this.PLACA = PLACA;
        this.ID_CONFIG_FK = ID_CONFIG_FK;
        this.QTD_PNEUS = QTD_PNEUS;
        this.DATA_CADASTRO = DATA_CADASTRO;
        this.MEDIDA_PNEU = MEDIDA_PNEU;
        this.STATUS_VEICULO = STATUS_VEICULO;
        this.posicaoCarreta = posicaoCarreta;
    }


    // --- Getters e Setters ---
    // Gere para todos os atributos (Sua IDE pode fazer isso: Alt+Insert ou Source -> Insert Code...)

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getFROTA() {
        return FROTA;
    }

    public void setFROTA(String FROTA) {
        this.FROTA = FROTA;
    }

    public String getPLACA() {
        return PLACA;
    }

    public void setPLACA(String PLACA) {
        this.PLACA = PLACA;
    }

    public Integer getID_CONFIG_FK() {
        return ID_CONFIG_FK;
    }

    public void setID_CONFIG_FK(Integer ID_CONFIG_FK) {
        this.ID_CONFIG_FK = ID_CONFIG_FK;
    }

    public Integer getQTD_PNEUS() {
        return QTD_PNEUS;
    }

    public void setQTD_PNEUS(Integer QTD_PNEUS) {
        this.QTD_PNEUS = QTD_PNEUS;
    }

    public LocalDate getDATA_CADASTRO() {
        return DATA_CADASTRO;
    }

    public void setDATA_CADASTRO(LocalDate DATA_CADASTRO) {
        this.DATA_CADASTRO = DATA_CADASTRO;
    }

    public String getMEDIDA_PNEU() {
        return MEDIDA_PNEU;
    }

    public void setMEDIDA_PNEU(String MEDIDA_PNEU) {
        this.MEDIDA_PNEU = MEDIDA_PNEU;
    }

    public String getSTATUS_VEICULO() {
        return STATUS_VEICULO;
    }

    public void setSTATUS_VEICULO(String STATUS_VEICULO) {
        this.STATUS_VEICULO = STATUS_VEICULO;
    }

    public Integer getPosicaoCarreta() {
        return posicaoCarreta;
    }

    public void setPosicaoCarreta(Integer posicaoCarreta) {
        this.posicaoCarreta = posicaoCarreta;
    }

   

   
    @Override
    public String toString() {
        return "Veiculo{" + "ID=" + ID + ", FROTA=" + FROTA + ", PLACA=" + PLACA + ", ID_CONFIG_FK=" + ID_CONFIG_FK + ", QTD_PNEUS=" + QTD_PNEUS + ", DATA_CADASTRO=" + DATA_CADASTRO + ", MEDIDA_PNEU=" + MEDIDA_PNEU + ", STATUS_VEICULO=" + STATUS_VEICULO + '}';
    }
}
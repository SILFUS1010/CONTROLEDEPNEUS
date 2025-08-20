package br.com.martins_borges.model; 

import java.time.LocalDateTime;


public class RegistroEngate {

   
    private int id; 

    
    private int idCavaloFk;
    private Integer idImplemento1Fk; 
    private Integer idDollyFk;       
    private Integer idImplemento2Fk; 
    

    
    private LocalDateTime dataHoraEngate;
    private double kmEngate;
    private String usuarioEngate;

   
    private LocalDateTime dataHoraDesengate;
    private Double kmDesengate; 
    private String usuarioDesengate;

    
    private String statusRegistro; 

   
    
    public RegistroEngate() {
    }

   
    public RegistroEngate(int idCavaloFk, Integer idImplemento1Fk, Integer idDollyFk, Integer idImplemento2Fk,
                          LocalDateTime dataHoraEngate, double kmEngate, String usuarioEngate) {
        this.idCavaloFk = idCavaloFk;
        this.idImplemento1Fk = idImplemento1Fk;
        this.idDollyFk = idDollyFk;
        this.idImplemento2Fk = idImplemento2Fk;
        this.dataHoraEngate = dataHoraEngate;
        this.kmEngate = kmEngate;
        this.usuarioEngate = usuarioEngate;
        // O status e os dados de desengate normalmente começam nulos/padrão
    }


   

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCavaloFk() {
        return idCavaloFk;
    }

    public void setIdCavaloFk(int idCavaloFk) {
        this.idCavaloFk = idCavaloFk;
    }

    public Integer getIdImplemento1Fk() {
        return idImplemento1Fk;
    }

    public void setIdImplemento1Fk(Integer idImplemento1Fk) {
        this.idImplemento1Fk = idImplemento1Fk;
    }

    public Integer getIdDollyFk() {
        return idDollyFk;
    }

    public void setIdDollyFk(Integer idDollyFk) {
        this.idDollyFk = idDollyFk;
    }

    public Integer getIdImplemento2Fk() {
        return idImplemento2Fk;
    }

    public void setIdImplemento2Fk(Integer idImplemento2Fk) {
        this.idImplemento2Fk = idImplemento2Fk;
    }

    public LocalDateTime getDataHoraEngate() {
        return dataHoraEngate;
    }

    public void setDataHoraEngate(LocalDateTime dataHoraEngate) {
        this.dataHoraEngate = dataHoraEngate;
    }

    public double getKmEngate() {
        return kmEngate;
    }

    public void setKmEngate(double kmEngate) {
        this.kmEngate = kmEngate;
    }

    public String getUsuarioEngate() {
        return usuarioEngate;
    }

    public void setUsuarioEngate(String usuarioEngate) {
        this.usuarioEngate = usuarioEngate;
    }

    public LocalDateTime getDataHoraDesengate() {
        return dataHoraDesengate;
    }

    public void setDataHoraDesengate(LocalDateTime dataHoraDesengate) {
        this.dataHoraDesengate = dataHoraDesengate;
    }

    public Double getKmDesengate() {
        return kmDesengate;
    }

    public void setKmDesengate(Double kmDesengate) {
        this.kmDesengate = kmDesengate;
    }

    public String getUsuarioDesengate() {
        return usuarioDesengate;
    }

    public void setUsuarioDesengate(String usuarioDesengate) {
        this.usuarioDesengate = usuarioDesengate;
    }

    public String getStatusRegistro() {
        return statusRegistro;
    }

    public void setStatusRegistro(String statusRegistro) {
        this.statusRegistro = statusRegistro;
    }

    
}
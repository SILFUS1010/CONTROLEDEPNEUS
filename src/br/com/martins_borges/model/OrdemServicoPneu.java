package br.com.martins_borges.model; 

import java.time.LocalDateTime; 

public class OrdemServicoPneu {
    private int idServico;
    private int idPneuFk; 
    private String numOrcamento;
    private LocalDateTime dataEnvio;
    private Integer idParceiroFk; 
    private int idTipoServicoFk;
    private Double valorServico; 
    private Integer idMotivoFk;  
    private LocalDateTime dataRetorno; 
    private String observacoesServico;
    private String usuarioRegistroServico;
    private LocalDateTime timestampRegistroServico; 

  
    public OrdemServicoPneu() {
    }

   

    public int getIdServico() { return idServico; }
    public void setIdServico(int idServico) { this.idServico = idServico; }

    public int getIdPneuFk() { return idPneuFk; }
    public void setIdPneuFk(int idPneuFk) { this.idPneuFk = idPneuFk; }

    public String getNumOrcamento() { return numOrcamento; }
    public void setNumOrcamento(String numOrcamento) { this.numOrcamento = numOrcamento; }

    public LocalDateTime getDataEnvio() { return dataEnvio; }
    public void setDataEnvio(LocalDateTime dataEnvio) { this.dataEnvio = dataEnvio; }

    public Integer getIdParceiroFk() { return idParceiroFk; }
    public void setIdParceiroFk(Integer idParceiroFk) { this.idParceiroFk = idParceiroFk; }

    public int getIdTipoServicoFk() { return idTipoServicoFk; }
    public void setIdTipoServicoFk(int idTipoServicoFk) { this.idTipoServicoFk = idTipoServicoFk; }

    public Double getValorServico() { return valorServico; }
    public void setValorServico(Double valorServico) { this.valorServico = valorServico; }

    public Integer getIdMotivoFk() { return idMotivoFk; }
    public void setIdMotivoFk(Integer idMotivoFk) { this.idMotivoFk = idMotivoFk; }

    public LocalDateTime getDataRetorno() { return dataRetorno; }
    public void setDataRetorno(LocalDateTime dataRetorno) { this.dataRetorno = dataRetorno; }

    public String getObservacoesServico() { return observacoesServico; }
    public void setObservacoesServico(String observacoesServico) { this.observacoesServico = observacoesServico; }

    public String getUsuarioRegistroServico() { return usuarioRegistroServico; }
    public void setUsuarioRegistroServico(String usuarioRegistroServico) { this.usuarioRegistroServico = usuarioRegistroServico; }

    public LocalDateTime getTimestampRegistroServico() { return timestampRegistroServico; }
    public void setTimestampRegistroServico(LocalDateTime timestampRegistroServico) { this.timestampRegistroServico = timestampRegistroServico; }
}

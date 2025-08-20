package br.com.martins_borges.model; // Padronizando pacote

import java.time.LocalDate;
import java.util.Date;

public class Pneu {
    private int id;
    private int idEmpresaProprietaria; // NOVO CAMPO para armazenar 1, 2 ou 3
    private String fogo;
    private String fornecedor;
    private Double valor;
    private String fabricante;
    private String tipoPneu;
    private String modelo;
    private String dot;
    private String medida;
    private Double profundidade;
    private LocalDate dataCadastro;
    private int nRecapagens;
    private Integer projetadoKm;
    private String observacoes;
    private Date dataRetorno;
    // Construtor vazio
    public Pneu() {}

    // Getters e Setters para TODOS (incluindo o novo idEmpresaProprietaria)

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdEmpresaProprietaria() { return idEmpresaProprietaria; } // Getter Novo
    public void setIdEmpresaProprietaria(int idEmpresaProprietaria) { this.idEmpresaProprietaria = idEmpresaProprietaria; } // Setter Novo

    public String getFogo() { return fogo; }
    public void setFogo(String fogo) { this.fogo = fogo; }
    public String getFornecedor() { return fornecedor; }
    public void setFornecedor(String fornecedor) { this.fornecedor = fornecedor; }
    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }
    public String getFabricante() { return fabricante; }
    public void setFabricante(String fabricante) { this.fabricante = fabricante; }
    public String getTipoPneu() { return tipoPneu; }
    public void setTipoPneu(String tipoPneu) { this.tipoPneu = tipoPneu; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getDot() { return dot; }
    public void setDot(String dot) { this.dot = dot; }
    public String getMedida() { return medida; }
    public void setMedida(String medida) { this.medida = medida; }
    public Double getProfundidade() { return profundidade; }
    public void setProfundidade(Double profundidade) { this.profundidade = profundidade; }
    public LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }
    public int getnRecapagens() { return nRecapagens; } // Nome correto do getter
    public void setnRecapagens(int nRecapagens) { this.nRecapagens = nRecapagens; } // Nome correto do setter
    public Integer getProjetadoKm() { return projetadoKm; }
    public void setProjetadoKm(Integer projetadoKm) { this.projetadoKm = projetadoKm; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    private String statusPneu;
    public void setDataRetorno(Date dataRetorno) {
        this.dataRetorno = dataRetorno;
    }

    // Método para obter a data de retorno
    public Date getDataRetorno() {
        return dataRetorno;
    }

    public String getStatusPneu() {
        return statusPneu;
    }

    public void setStatusPneu(String statusPneu) {
        this.statusPneu = statusPneu;
    }
    @Override
    public String toString() {
        return "Pneu{" + "id=" + id + ", idEmpresaProprietaria=" + idEmpresaProprietaria + ", fogo=" + fogo + /* Adicione outros campos se necessário */ '}';
    }
}
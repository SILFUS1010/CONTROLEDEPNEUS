package br.com.martins_borges.service;

import br.com.martins_borges.dal.PneuDAO;
import br.com.martins_borges.model.Pneu;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JOptionPane;
import br.com.martins_borges.dal.ModuloConexao;

public class PneuService {
    
    private final PneuDAO pneuDAO;
    
    public PneuService() {
        this.pneuDAO = new PneuDAO();
    }
    
    /**
     * Atualiza a localização de um pneu no veículo usando transação
     * @param pneuId ID do pneu a ser atualizado
     * @param veiculoId ID do veículo (pode ser null para remover do veículo)
     * @param posicao Posição no veículo (pode ser null)
     * @param statusNovo Novo status do pneu
     * @return true se a operação for bem-sucedida, false caso contrário
     */
    public boolean atualizarLocalizacaoPneu(int pneuId, Integer veiculoId, String posicao, String statusNovo) {
        Connection conn = null;
        boolean sucesso = false;
        
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Erro de conexão com o banco de dados.", "Erro", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            // Desativa o auto-commit para iniciar a transação
            conn.setAutoCommit(false);
            
            // Se o pneu está sendo movido para um veículo
            if (veiculoId != null) {
                // Verifica se a posição no veículo já está ocupada
                if (posicao != null && !posicao.trim().isEmpty()) {
                    Pneu pneuExistente = pneuDAO.buscarPneuPorPosicaoNoVeiculo(veiculoId, posicao);
                    if (pneuExistente != null && pneuExistente.getId() != pneuId) {
                        JOptionPane.showMessageDialog(null, 
                            "A posição " + posicao + " já está ocupada pelo pneu " + pneuExistente.getFogo(), 
                            "Posição Ocupada", 
                            JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                }
                
                // Atualiza a localização do pneu
                if (!pneuDAO.atualizarLocalizacaoPneu(pneuId, veiculoId, posicao, statusNovo)) {
                    throw new SQLException("Falha ao atualizar a localização do pneu.");
                }
            } 
            // Se o pneu está sendo removido do veículo
            else {
                if (!pneuDAO.removerPneuDoVeiculo(pneuId, statusNovo)) {
                    throw new SQLException("Falha ao remover o pneu do veículo.");
                }
            }
            
            // Se chegou até aqui, confirma a transação
            conn.commit();
            sucesso = true;
            
        } catch (SQLException e) {
            // Em caso de erro, faz rollback
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Erro ao reverter a operação: " + ex.getMessage(), 
                    "Erro no Rollback", JOptionPane.ERROR_MESSAGE);
            }
            
            JOptionPane.showMessageDialog(null, "Erro ao atualizar a localização do pneu: " + e.getMessage(), 
                "Erro no Banco de Dados", JOptionPane.ERROR_MESSAGE);
            
        } finally {
            // Restaura o auto-commit e fecha a conexão
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Erro ao fechar a conexão: " + e.getMessage(), 
                    "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        return sucesso;
    }
    
    /**
     * Obtém a lista de pneus de um veículo específico
     * @param veiculoId ID do veículo
     * @return Lista de pneus do veículo
     */
    public List<Pneu> listarPneusNoVeiculo(int veiculoId) {
        return pneuDAO.listarPneusNoVeiculo(veiculoId);
    }
    
    /**
     * Busca um pneu pela sua posição no veículo
     * @param veiculoId ID do veículo
     * @param posicao Posição no veículo
     * @return O pneu encontrado ou null se não existir
     */
    public Pneu buscarPneuPorPosicaoNoVeiculo(int veiculoId, String posicao) {
        return pneuDAO.buscarPneuPorPosicaoNoVeiculo(veiculoId, posicao);
    }
}

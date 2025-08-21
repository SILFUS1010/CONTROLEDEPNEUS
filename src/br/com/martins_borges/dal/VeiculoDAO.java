package br.com.martins_borges.dal;

import br.com.martins_borges.model.Veiculo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane; // Import adicionado para consistência

public class VeiculoDAO {

    public boolean salvarPlacaVeiculo(String placa) {
        String sql = "INSERT INTO CAD_EQP (PLACA, STATUS_VEICULO) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                System.err.println("DAO: Falha conexão salvarPlacaVeiculo.");
                return false;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, placa);
            pstmt.setString(2, "DISPONIVEL");

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            System.err.println("DAO Error: Placa '" + placa + "' já existe? " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("DAO SQL Error: Erro ao salvar placa - " + e.getMessage());
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException ex) {}
            try { if (conn != null) conn.close(); } catch (SQLException ex) {}
        }
    }

    public boolean salvarVeiculoCompleto(Veiculo veiculo) {
        String sql = "INSERT INTO CAD_VEICULOS (FROTA, PLACA, ID_CONFIG_FK, QTD_PNEUS, DATA_CADASTRO, MEDIDA_PNEU, STATUS_VEICULO, posicao_carreta) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                System.err.println("DAO: Falha conexão salvarVeiculoCompleto.");
                return false;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, veiculo.getFROTA());
            pstmt.setString(2, veiculo.getPLACA());
            pstmt.setObject(3, veiculo.getID_CONFIG_FK()); // Corrigido para setObject para aceitar Integer
            pstmt.setObject(4, veiculo.getQTD_PNEUS());   // Corrigido para setObject para aceitar Integer
            pstmt.setDate(5, java.sql.Date.valueOf(veiculo.getDATA_CADASTRO()));
            pstmt.setString(6, veiculo.getMEDIDA_PNEU());
            pstmt.setString(7, veiculo.getSTATUS_VEICULO());
            pstmt.setObject(8, veiculo.getPosicaoCarreta());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("DAO SQL Error: Erro ao salvar veículo completo - " + e.getMessage());
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException ex) {}
            try { if (conn != null) conn.close(); } catch (SQLException ex) {}
        }
    }

    public Veiculo buscarPorPlaca(String placa) {
        String sql = "SELECT * FROM CAD_VEICULOS WHERE PLACA = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Veiculo veiculo = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                System.err.println("DAO: Falha conexão buscarPorPlaca.");
                return null;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, placa);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                veiculo = new Veiculo();
                veiculo.setID(rs.getInt("ID"));
                veiculo.setFROTA(rs.getString("FROTA"));
                veiculo.setPLACA(rs.getString("PLACA"));
                veiculo.setID_CONFIG_FK(rs.getInt("ID_CONFIG_FK")); // Autoboxing resolve aqui
                veiculo.setQTD_PNEUS(rs.getInt("QTD_PNEUS"));       // Autoboxing resolve aqui
                veiculo.setDATA_CADASTRO(rs.getDate("DATA_CADASTRO").toLocalDate());
                veiculo.setMEDIDA_PNEU(rs.getString("MEDIDA_PNEU"));
                veiculo.setSTATUS_VEICULO(rs.getString("STATUS_VEICULO"));
                veiculo.setPosicaoCarreta((Integer) rs.getObject("posicao_carreta"));
            }
        } catch (SQLException e) {
            System.err.println("DAO SQL Error: Erro ao buscar veículo por placa - " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ex) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException ex) {}
            try { if (conn != null) conn.close(); } catch (SQLException ex) {}
        }
        return veiculo;
    }

    public List<Veiculo> listarTodos() {
        String sql = "SELECT * FROM CAD_VEICULOS";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Veiculo> veiculos = new ArrayList<>();
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                System.err.println("DAO: Falha conexão listarTodos.");
                return veiculos;
            }
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Veiculo veiculo = new Veiculo();
                veiculo.setID(rs.getInt("ID"));
                veiculo.setFROTA(rs.getString("FROTA"));
                veiculo.setPLACA(rs.getString("PLACA"));
                veiculo.setID_CONFIG_FK(rs.getInt("ID_CONFIG_FK")); // Autoboxing resolve aqui
                veiculo.setQTD_PNEUS(rs.getInt("QTD_PNEUS"));       // Autoboxing resolve aqui
                veiculo.setDATA_CADASTRO(rs.getDate("DATA_CADASTRO").toLocalDate());
                veiculo.setMEDIDA_PNEU(rs.getString("MEDIDA_PNEU"));
                veiculo.setSTATUS_VEICULO(rs.getString("STATUS_VEICULO"));
                veiculo.setPosicaoCarreta((Integer) rs.getObject("posicao_carreta"));
                veiculos.add(veiculo);
            }
        } catch (SQLException e) {
            System.err.println("DAO SQL Error: Erro ao listar veículos - " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ex) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException ex) {}
            try { if (conn != null) conn.close(); } catch (SQLException ex) {}
        }
        return veiculos;
    }

    public boolean atualizarVeiculo(Veiculo veiculo) {
        String sql = "UPDATE CAD_VEICULOS SET FROTA = ?, PLACA = ?, ID_CONFIG_FK = ?, QTD_PNEUS = ?, DATA_CADASTRO = ?, MEDIDA_PNEU = ?, STATUS_VEICULO = ?, posicao_carreta = ? WHERE ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                System.err.println("DAO: Falha conexão atualizarVeiculo.");
                return false;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, veiculo.getFROTA());
            pstmt.setString(2, veiculo.getPLACA());
            pstmt.setObject(3, veiculo.getID_CONFIG_FK()); // Corrigido para setObject
            pstmt.setObject(4, veiculo.getQTD_PNEUS());   // Corrigido para setObject
            pstmt.setDate(5, java.sql.Date.valueOf(veiculo.getDATA_CADASTRO()));
            pstmt.setString(6, veiculo.getMEDIDA_PNEU());
            pstmt.setString(7, veiculo.getSTATUS_VEICULO());
            pstmt.setObject(8, veiculo.getPosicaoCarreta());
            pstmt.setInt(9, veiculo.getID());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("DAO SQL Error: Erro ao atualizar veículo - " + e.getMessage());
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException ex) {}
            try { if (conn != null) conn.close(); } catch (SQLException ex) {}
        }
    }

    public boolean excluirPorPlaca(String placa) {
        String sql = "DELETE FROM CAD_VEICULOS WHERE PLACA = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                System.err.println("DAO: Falha conexão excluirPorPlaca.");
                return false;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, placa);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("DAO SQL Error: Erro ao excluir veículo por placa - " + e.getMessage());
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException ex) {}
            try { if (conn != null) conn.close(); } catch (SQLException ex) {}
        }
    }
    
    public boolean frotaExiste(String frota) {
        String sql = "SELECT COUNT(*) FROM CAD_VEICULOS WHERE frota = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                System.err.println("DAO: Falha na conexão ao verificar frota.");
                return false;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, frota);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("DAO SQL Error: Erro ao verificar se a frota existe - " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException ex) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException ex) {}
            try { if (conn != null) conn.close(); } catch (SQLException ex) {}
        }
        return false;
    }

    public boolean excluir(int id) {
        String sql = "DELETE FROM CAD_VEICULOS WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                return false;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir veículo: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.err.println("Erro ao fechar a conexão: " + ex.getMessage());
            }
        }
    }
}


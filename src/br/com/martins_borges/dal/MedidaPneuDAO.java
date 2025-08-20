package br.com.martins_borges.dal; 

import br.com.martins_borges.model.MedidaPneu; // Importa o modelo
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
// Import do ModuloConexao
import br.com.martins_borges.dal.ModuloConexao;

public class MedidaPneuDAO {

   
    public List<String> listarNomes() { // Mantive listarNomes para padrão, mas busca descrição
        String sql = "SELECT descricao_medida FROM cad_medidas_pneu ORDER BY descricao_medida";
        List<String> nomes = new ArrayList<>();
        Connection conn = null; PreparedStatement pstmt = null; ResultSet rs = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) return nomes;
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                nomes.add(rs.getString("descricao_medida"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao listar descrições de medidas: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return nomes;
    }

    
    public MedidaPneu inserirMedidaPneu(String novaDescricaoMedida) {
         if (novaDescricaoMedida == null || novaDescricaoMedida.trim().isEmpty()) return null;
         novaDescricaoMedida = novaDescricaoMedida.trim();

        String sql = "INSERT INTO cad_medidas_pneu (descricao_medida) VALUES (?)";
        Connection conn = null; PreparedStatement pstmt = null; ResultSet generatedKeys = null;
        MedidaPneu medidaCriada = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) return null;
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, novaDescricaoMedida);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    medidaCriada = new MedidaPneu(novaDescricaoMedida);
                    medidaCriada.setIdMedida(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
             if (e.getSQLState() != null && e.getSQLState().equals("23000")) { // UNIQUE constraint
                 JOptionPane.showMessageDialog(null, "Medida '" + novaDescricaoMedida + "' já existe.", "Erro de Duplicidade", JOptionPane.WARNING_MESSAGE);
             } else {
                 JOptionPane.showMessageDialog(null, "Erro DAO ao inserir medida: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
                 
             }
             return null;
        } finally {
             try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException e) {}
             try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
             try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return medidaCriada;
    }

  
    public List<String> buscarNomesParcial(String termo) { // Mantive nome padrão
        String sql = "SELECT descricao_medida FROM cad_medidas_pneu WHERE UPPER(descricao_medida) LIKE UPPER(?) ORDER BY descricao_medida";
        List<String> nomes = new ArrayList<>();
        Connection conn = null; PreparedStatement pstmt = null; ResultSet rs = null;
        if (termo == null || termo.trim().isEmpty()) return nomes;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) return nomes;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + termo.trim() + "%");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                nomes.add(rs.getString("descricao_medida"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao buscar nomes parciais de medidas: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return nomes;
    }
    
   

    /**
     * Atualiza a descrição de uma medida existente, identificada pelo ID.
     */
    public boolean atualizarDescricaoMedida(int idMedida, String novaDescricao) {
        if (idMedida <= 0 || novaDescricao == null || novaDescricao.trim().isEmpty()) return false;
        novaDescricao = novaDescricao.trim();

        if (existeOutraMedidaComDescricao(novaDescricao, idMedida)) {
             JOptionPane.showMessageDialog(null, "Erro: Já existe outra medida com a descrição '" + novaDescricao + "'.", "Descrição Duplicada", JOptionPane.WARNING_MESSAGE);
             return false;
        }

        String sql = "UPDATE cad_medidas_pneu SET descricao_medida = ? WHERE id_medida = ?"; // Tabela/Colunas corretas
        Connection conn = null; PreparedStatement pstmt = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) return false;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, novaDescricao);
            pstmt.setInt(2, idMedida);
            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas == 1;
        } catch (SQLException e) {
             if (e.getSQLState() != null && e.getSQLState().equals("23000")) { JOptionPane.showMessageDialog(null, "Erro SQL: Descrição duplicada.", "Erro", 0); }
             else { JOptionPane.showMessageDialog(null, "Erro DAO ao atualizar medida: " + e.getMessage(), "Erro SQL", 0);  }
             return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    /**
     * Verifica se OUTRA medida já tem a descrição desejada.
     */
    private boolean existeOutraMedidaComDescricao(String descricao, int idExcluir) {
        String sql = "SELECT 1 FROM cad_medidas_pneu WHERE UPPER(descricao_medida) = UPPER(?) AND id_medida != ?"; // Tabela/Colunas corretas
        Connection conn = null; PreparedStatement pstmt = null; ResultSet rs = null;
        boolean existe = false;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) return true;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, descricao);
            pstmt.setInt(2, idExcluir);
            rs = pstmt.executeQuery();
            existe = rs.next();
        } catch (SQLException e) {
             System.err.println("DAO (existeOutraMed): Erro: " + e.getMessage()); return true;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return existe;
    }

     /**
     * Busca uma MedidaPneu pela descrição exata (case-insensitive).
     */
     public MedidaPneu buscarPorDescricaoExata(String descricao) {
         if (descricao == null || descricao.trim().isEmpty()) return null;
         String sql = "SELECT id_medida, descricao_medida FROM cad_medidas_pneu WHERE UPPER(descricao_medida) = UPPER(?)"; // Tabela/Colunas corretas
         MedidaPneu medida = null;
         Connection conn = null; PreparedStatement pstmt = null; ResultSet rs = null;
         try {
             conn = ModuloConexao.conector();
             if (conn == null) return null;
             pstmt = conn.prepareStatement(sql);
             pstmt.setString(1, descricao.trim());
             rs = pstmt.executeQuery();
             if (rs.next()) {
                 medida = new MedidaPneu(rs.getString("descricao_medida")); // Usa o modelo MedidaPneu
                 medida.setIdMedida(rs.getInt("id_medida"));      // Usa o setter correto
             }
         } catch (SQLException e) { JOptionPane.showMessageDialog(null, "Erro DAO buscar med. p/ desc: " + e.getMessage(), "Erro SQL", 0);
         } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) {}
             try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
             try { if (conn != null) conn.close(); } catch (SQLException e) {}
         }
         return medida;
     }

   
}
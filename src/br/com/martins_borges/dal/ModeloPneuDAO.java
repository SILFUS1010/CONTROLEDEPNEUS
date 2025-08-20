package br.com.martins_borges.dal; 

import br.com.martins_borges.model.ModeloPneu;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;



public class ModeloPneuDAO {


    public List<String> listarNomes() {
        String sql = "SELECT nome_modelo FROM cad_modelos_pneu ORDER BY nome_modelo";
        List<String> nomes = new ArrayList<>();
        Connection conn = null; PreparedStatement pstmt = null; ResultSet rs = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) return nomes;
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                nomes.add(rs.getString("nome_modelo"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao listar nomes de modelos: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return nomes;
    }
    public ModeloPneu inserirModeloPneu(String nomeNovoModelo) {
         if (nomeNovoModelo == null || nomeNovoModelo.trim().isEmpty()) return null;
         nomeNovoModelo = nomeNovoModelo.trim();

        String sql = "INSERT INTO cad_modelos_pneu (nome_modelo) VALUES (?)";
        Connection conn = null; PreparedStatement pstmt = null; ResultSet generatedKeys = null;
        ModeloPneu modeloCriado = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) return null;
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, nomeNovoModelo);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    modeloCriado = new ModeloPneu(nomeNovoModelo);
                    modeloCriado.setIdModelo(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
             if (e.getSQLState() != null && e.getSQLState().equals("23000")) {
                 JOptionPane.showMessageDialog(null, "Modelo '" + nomeNovoModelo + "' já existe.", "Erro de Duplicidade", JOptionPane.WARNING_MESSAGE);
             } else {
                 JOptionPane.showMessageDialog(null, "Erro DAO ao inserir modelo: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
                 
             }
             return null;
        } finally {
             try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException e) {}
             try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
             try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return modeloCriado;
    }

    public List<String> buscarNomesParcial(String termo) {
        String sql = "SELECT nome_modelo FROM cad_modelos_pneu WHERE UPPER(nome_modelo) LIKE UPPER(?) ORDER BY nome_modelo";
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
                nomes.add(rs.getString("nome_modelo"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao buscar nomes parciais de modelos: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return nomes;
    }
    


    /**
     * Atualiza o nome de um modelo existente, identificado pelo ID.
     */
    public boolean atualizarNomeModelo(int idModelo, String novoNome) {
        if (idModelo <= 0 || novoNome == null || novoNome.trim().isEmpty()) return false;
        novoNome = novoNome.trim();

        if (existeOutroModeloComNome(novoNome, idModelo)) {
             JOptionPane.showMessageDialog(null, "Erro: Já existe outro modelo com o nome '" + novoNome + "'.", "Nome Duplicado", JOptionPane.WARNING_MESSAGE);
             return false;
        }

        String sql = "UPDATE cad_modelos_pneu SET nome_modelo = ? WHERE id_modelo = ?";
        Connection conn = null; PreparedStatement pstmt = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) return false;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, novoNome);
            pstmt.setInt(2, idModelo);
            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas == 1;
        } catch (SQLException e) {
             if (e.getSQLState() != null && e.getSQLState().equals("23000")) { JOptionPane.showMessageDialog(null, "Erro SQL: Nome de modelo duplicado.", "Erro", 0); }
             else { JOptionPane.showMessageDialog(null, "Erro DAO ao atualizar modelo: " + e.getMessage(), "Erro SQL", 0);  }
             return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    /**
     * Verifica se OUTRO modelo já tem o nome desejado.
     */
    private boolean existeOutroModeloComNome(String nome, int idExcluir) {
        String sql = "SELECT 1 FROM cad_modelos_pneu WHERE UPPER(nome_modelo) = UPPER(?) AND id_modelo != ?";
        Connection conn = null; PreparedStatement pstmt = null; ResultSet rs = null;
        boolean existe = false;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) return true;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nome);
            pstmt.setInt(2, idExcluir);
            rs = pstmt.executeQuery();
            existe = rs.next();
        } catch (SQLException e) {
             System.err.println("DAO (existeOutroMod): Erro: " + e.getMessage()); return true;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return existe;
    }

     /**
     * Busca um ModeloPneu pelo nome exato (case-insensitive).
     * @param nome
     */
     public ModeloPneu buscarPorNomeExato(String nome) {
         if (nome == null || nome.trim().isEmpty()) return null;
         String sql = "SELECT id_modelo, nome_modelo FROM cad_modelos_pneu WHERE UPPER(nome_modelo) = UPPER(?)";
         ModeloPneu modelo = null;
         Connection conn = null; PreparedStatement pstmt = null; ResultSet rs = null;
         try {
             conn = ModuloConexao.conector();
             if (conn == null) return null;
             pstmt = conn.prepareStatement(sql);
             pstmt.setString(1, nome.trim());
             rs = pstmt.executeQuery();
             if (rs.next()) {
                 modelo = new ModeloPneu(rs.getString("nome_modelo"));
                 modelo.setIdModelo(rs.getInt("id_modelo"));    
             }
         } catch (SQLException e) { JOptionPane.showMessageDialog(null, "Erro DAO buscar mod. p/ nome: " + e.getMessage(), "Erro SQL", 0); 
         } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) {}
             try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
             try { if (conn != null) conn.close(); } catch (SQLException e) {}
         }
         return modelo;
     }

}
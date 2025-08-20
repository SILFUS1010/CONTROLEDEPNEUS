package br.com.martins_borges.dal; 

import br.com.martins_borges.model.TipoPneu; 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class TipoPneuDAO {

    public List<String> listarNomes() {
        String sql = "SELECT nome_tipo FROM cad_tipos_pneu ORDER BY nome_tipo";
        List<String> nomes = new ArrayList<>();
        Connection conn = null; PreparedStatement pstmt = null; ResultSet rs = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) return nomes;
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                nomes.add(rs.getString("nome_tipo"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao listar nomes de tipos de pneu: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return nomes;
    }

    public TipoPneu inserirTipoPneu(String nomeNovoTipo) {
        if (nomeNovoTipo == null || nomeNovoTipo.trim().isEmpty()) return null;
        nomeNovoTipo = nomeNovoTipo.trim();

        String sql = "INSERT INTO cad_tipos_pneu (nome_tipo) VALUES (?)";
        Connection conn = null; PreparedStatement pstmt = null; ResultSet generatedKeys = null;
        TipoPneu tipoCriado = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) return null;
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, nomeNovoTipo);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    tipoCriado = new TipoPneu(nomeNovoTipo);
                    tipoCriado.setIdTipo(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().equals("23000")) {
                JOptionPane.showMessageDialog(null, "Tipo de Pneu '" + nomeNovoTipo + "' já existe.", "Erro de Duplicidade", JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Erro DAO ao inserir tipo de pneu: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
                
            }
            return null;
        } finally {
            try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return tipoCriado;
    }

    public List<String> buscarNomesParcial(String termo) {
        String sql = "SELECT nome_tipo FROM cad_tipos_pneu WHERE UPPER(nome_tipo) LIKE UPPER(?) ORDER BY nome_tipo";
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
                nomes.add(rs.getString("nome_tipo"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao buscar nomes parciais de tipos de pneu: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return nomes;
    }

    public boolean atualizarNomeTipo(int idTipo, String novoNome) {
        if (idTipo <= 0 || novoNome == null || novoNome.trim().isEmpty()) return false;
        novoNome = novoNome.trim();

        if (existeOutroTipoComNome(novoNome, idTipo)) {
            JOptionPane.showMessageDialog(null, "Erro: Já existe outro tipo de pneu com o nome '" + novoNome + "'.", "Nome Duplicado", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        String sql = "UPDATE cad_tipos_pneu SET nome_tipo = ? WHERE id_tipo = ?";
        Connection conn = null; PreparedStatement pstmt = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) return false;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, novoNome);
            pstmt.setInt(2, idTipo);
            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas == 1;
        } catch (SQLException e) {
            if (e.getSQLState() != null && e.getSQLState().equals("23000")) {
                JOptionPane.showMessageDialog(null, "Erro SQL: Nome duplicado.", "Erro", 0);
            } else {
                JOptionPane.showMessageDialog(null, "Erro DAO ao atualizar tipo de pneu: " + e.getMessage(), "Erro SQL", 0);
                
            }
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    private boolean existeOutroTipoComNome(String nome, int idExcluir) {
        String sql = "SELECT 1 FROM cad_tipos_pneu WHERE UPPER(nome_tipo) = UPPER(?) AND id_tipo != ?";
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
            System.err.println("DAO (existeOutroTipo): Erro: " + e.getMessage());
            return true;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return existe;
    }

    public TipoPneu buscarPorNomeExato(String nome) {
        if (nome == null || nome.trim().isEmpty()) return null;
        String sql = "SELECT id_tipo, nome_tipo FROM cad_tipos_pneu WHERE UPPER(nome_tipo) = UPPER(?)"; 
        TipoPneu tipo = null;
        Connection conn = null; PreparedStatement pstmt = null; ResultSet rs = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) return null;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nome.trim());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                tipo = new TipoPneu(rs.getString("nome_tipo"));
                tipo.setIdTipo(rs.getInt("id_tipo")); 
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO buscar tipo p/ nome: " + e.getMessage(), "Erro SQL", 0);
            
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return tipo;
    }
}
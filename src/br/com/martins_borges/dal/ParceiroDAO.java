package br.com.martins_borges.dal;

import br.com.martins_borges.model.Parceiro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ParceiroDAO {

    public List<String> listarNomes() {
        String sql = "SELECT nome_parceiro FROM cad_parceiros ORDER BY nome_parceiro";
        List<String> nomes = new ArrayList<>();
        Connection conn = null; PreparedStatement pstmt = null; ResultSet rs = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) { System.err.println("DAO: Falha de conexão ao listar nomes de parceiros."); return nomes; }
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                nomes.add(rs.getString("nome_parceiro"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao listar parceiros: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return nomes;
    }

    public Parceiro inserirParceiro(String nomeNovoParceiro) {
         if (nomeNovoParceiro == null || nomeNovoParceiro.trim().isEmpty()) { return null; }
         nomeNovoParceiro = nomeNovoParceiro.trim();

        String sql = "INSERT INTO cad_parceiros (nome_parceiro) VALUES (?)";
        Connection conn = null; PreparedStatement pstmt = null; ResultSet generatedKeys = null;
        Parceiro parceiroCriado = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) { System.err.println("DAO: Falha de conexão ao inserir parceiro."); return null; }
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, nomeNovoParceiro);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    parceiroCriado = new Parceiro(nomeNovoParceiro);
                    parceiroCriado.setIdParceiro(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
             if (e.getSQLState() != null && e.getSQLState().equals("23000")) {
                 JOptionPane.showMessageDialog(null, "Parceiro '" + nomeNovoParceiro + "' já existe.", "Erro de Duplicidade", JOptionPane.WARNING_MESSAGE);
             } else {
                 JOptionPane.showMessageDialog(null, "Erro DAO ao inserir parceiro: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
                 
             }
             return null;
        } finally {
             try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException e) {}
             try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
             try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return parceiroCriado;
    }

     public Parceiro buscarPorNomeExato(String nome) {
         if (nome == null || nome.trim().isEmpty()) return null;
         String sql = "SELECT id_parceiro, nome_parceiro FROM cad_parceiros WHERE UPPER(nome_parceiro) = UPPER(?)";
         Parceiro parceiro = null;
         Connection conn = null; PreparedStatement pstmt = null; ResultSet rs = null;
         try {
             conn = ModuloConexao.conector();
             if (conn == null) { System.err.println("DAO: Falha de conexão ao buscar parceiro por nome."); return null; }
             pstmt = conn.prepareStatement(sql);
             pstmt.setString(1, nome.trim());
             rs = pstmt.executeQuery();
             if (rs.next()) {
                 parceiro = new Parceiro(rs.getString("nome_parceiro"));
                 parceiro.setIdParceiro(rs.getInt("id_parceiro"));
             }
         } catch (SQLException e) { JOptionPane.showMessageDialog(null, "Erro DAO buscar parceiro p/ nome: " + e.getMessage(), "Erro SQL", 0); 
         } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) {}
             try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
             try { if (conn != null) conn.close(); } catch (SQLException e) {}
         }
         return parceiro;
     }

    public boolean atualizarNomeParceiro(int idParceiro, String novoNome) {
        if (idParceiro <= 0 || novoNome == null || novoNome.trim().isEmpty()) return false;
        novoNome = novoNome.trim();

        if (existeOutroParceiroComNome(novoNome, idParceiro)) {
             JOptionPane.showMessageDialog(null, "Erro: Já existe outro parceiro com o nome '" + novoNome + "'.", "Nome Duplicado", JOptionPane.WARNING_MESSAGE);
             return false;
        }

        String sql = "UPDATE cad_parceiros SET nome_parceiro = ? WHERE id_parceiro = ?";
        Connection conn = null; PreparedStatement pstmt = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) return false;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, novoNome);
            pstmt.setInt(2, idParceiro);
            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas == 1;
        } catch (SQLException e) {
             if (e.getSQLState() != null && e.getSQLState().equals("23000")) { JOptionPane.showMessageDialog(null, "Erro SQL: Nome de parceiro duplicado.", "Erro", 0); }
             else { JOptionPane.showMessageDialog(null, "Erro DAO ao atualizar parceiro: " + e.getMessage(), "Erro SQL", 0);  }
             return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    private boolean existeOutroParceiroComNome(String nome, int idExcluir) {
        String sql = "SELECT 1 FROM cad_parceiros WHERE UPPER(nome_parceiro) = UPPER(?) AND id_parceiro != ?";
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
             System.err.println("DAO (existeOutroParceiro): Erro: " + e.getMessage()); return true;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return existe;
    }

public Parceiro buscarPorId(int idParceiro) {
    if (idParceiro <= 0) {
        return null;
    }
    String sql = "SELECT id_parceiro, nome_parceiro FROM cad_parceiros WHERE id_parceiro = ?";
    Parceiro parceiro = null;
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
        conn = ModuloConexao.conector();
        if (conn == null) {
            System.err.println("DAO (ParceiroDAO - buscarPorId): Falha de conexão.");
            return null;
        }
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, idParceiro);
        rs = pstmt.executeQuery();

        if (rs.next()) {
            parceiro = new Parceiro(rs.getString("nome_parceiro"));
            parceiro.setIdParceiro(rs.getInt("id_parceiro"));
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro DAO ao buscar Parceiro por ID: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
    return parceiro;
}


    public String buscarNomePorId(Integer idParceiro) {
        if (idParceiro == null || idParceiro <= 0) {
            return null;
        }
        String sql = "SELECT nome_parceiro FROM cad_parceiros WHERE id_parceiro = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String nome = null;

        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                System.err.println("DAO (ParceiroDAO - buscarNomePorId): Falha de conexão.");
                return null;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idParceiro);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                nome = rs.getString("nome_parceiro");
            }
        } catch (SQLException e) {
            System.err.println("Erro DAO ao buscar nome do Parceiro por ID (ID: " + idParceiro + "): " + e.getMessage());
            
             return null;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return nome;
    }
}
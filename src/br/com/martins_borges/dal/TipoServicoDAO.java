package br.com.martins_borges.dal;

import br.com.martins_borges.model.TipoServico;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;




public class TipoServicoDAO {

    public List<String> listarNomes() {
        String sql = "SELECT nome_tipo_servico FROM cad_tipos_servico ORDER BY nome_tipo_servico";
        List<String> nomes = new ArrayList<>();
        Connection conn = null; PreparedStatement pstmt = null; ResultSet rs = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) { System.err.println("DAO: Falha de conexão ao listar nomes de tipos de serviço."); return nomes; }
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                nomes.add(rs.getString("nome_tipo_servico"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao listar tipos de serviço: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return nomes;
    }

    public TipoServico inserirTipoServico(String nomeNovoTipoServico) {
         if (nomeNovoTipoServico == null || nomeNovoTipoServico.trim().isEmpty()) { return null; }
         nomeNovoTipoServico = nomeNovoTipoServico.trim();

        String sql = "INSERT INTO cad_tipos_servico (nome_tipo_servico) VALUES (?)";
        Connection conn = null; PreparedStatement pstmt = null; ResultSet generatedKeys = null;
        TipoServico tipoCriado = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) { System.err.println("DAO: Falha de conexão ao inserir tipo de serviço."); return null; }
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, nomeNovoTipoServico);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    tipoCriado = new TipoServico(nomeNovoTipoServico);
                    tipoCriado.setIdTipoServico(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
             if (e.getSQLState() != null && e.getSQLState().equals("23000")) {
                 JOptionPane.showMessageDialog(null, "Tipo de Serviço '" + nomeNovoTipoServico + "' já existe.", "Erro de Duplicidade", JOptionPane.WARNING_MESSAGE);
             } else {
                 JOptionPane.showMessageDialog(null, "Erro DAO ao inserir tipo de serviço: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
                 
             }
             return null;
        } finally {
             try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException e) {}
             try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
             try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return tipoCriado;
    }

     public TipoServico buscarPorNomeExato(String nome) {
         if (nome == null || nome.trim().isEmpty()) return null;
         String sql = "SELECT id_tipo_servico, nome_tipo_servico FROM cad_tipos_servico WHERE UPPER(nome_tipo_servico) = UPPER(?)";
         TipoServico tipo = null;
         Connection conn = null; PreparedStatement pstmt = null; ResultSet rs = null;
         try {
             conn = ModuloConexao.conector();
             if (conn == null) { System.err.println("DAO: Falha de conexão ao buscar tipo de serviço por nome."); return null; }
             pstmt = conn.prepareStatement(sql);
             pstmt.setString(1, nome.trim());
             rs = pstmt.executeQuery();
             if (rs.next()) {
                 tipo = new TipoServico(rs.getString("nome_tipo_servico"));
                 tipo.setIdTipoServico(rs.getInt("id_tipo_servico"));
             }
         } catch (SQLException e) { JOptionPane.showMessageDialog(null, "Erro DAO buscar tipo de serviço p/ nome: " + e.getMessage(), "Erro SQL", 0); 
         } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) {}
             try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
             try { if (conn != null) conn.close(); } catch (SQLException e) {}
         }
         return tipo;
     }

    public boolean atualizarNomeTipoServico(int idTipoServico, String novoNome) {
        if (idTipoServico <= 0 || novoNome == null || novoNome.trim().isEmpty()) return false;
        novoNome = novoNome.trim();

        if (existeOutroTipoServicoComNome(novoNome, idTipoServico)) {
             JOptionPane.showMessageDialog(null, "Erro: Já existe outro tipo de serviço com o nome '" + novoNome + "'.", "Nome Duplicado", JOptionPane.WARNING_MESSAGE);
             return false;
        }

        String sql = "UPDATE cad_tipos_servico SET nome_tipo_servico = ? WHERE id_tipo_servico = ?";
        Connection conn = null; PreparedStatement pstmt = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) return false;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, novoNome);
            pstmt.setInt(2, idTipoServico);
            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas == 1;
        } catch (SQLException e) {
             if (e.getSQLState() != null && e.getSQLState().equals("23000")) { JOptionPane.showMessageDialog(null, "Erro SQL: Nome de tipo de serviço duplicado.", "Erro", 0); }
             else { JOptionPane.showMessageDialog(null, "Erro DAO ao atualizar tipo de serviço: " + e.getMessage(), "Erro SQL", 0);  }
             return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    private boolean existeOutroTipoServicoComNome(String nome, int idExcluir) {
        String sql = "SELECT 1 FROM cad_tipos_servico WHERE UPPER(nome_tipo_servico) = UPPER(?) AND id_tipo_servico != ?";
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
             System.err.println("DAO (existeOutroTipoServico): Erro: " + e.getMessage()); return true;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return existe;
    }

    public TipoServico buscarPorId(int idTipoServico) {
        if (idTipoServico <= 0) {
            return null;
        }
        String sql = "SELECT id_tipo_servico, nome_tipo_servico FROM cad_tipos_servico WHERE id_tipo_servico = ?";
        TipoServico tipo = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                System.err.println("DAO (TipoServicoDAO - buscarPorId): Falha de conexão.");
                return null;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idTipoServico);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                tipo = new TipoServico(rs.getString("nome_tipo_servico"));
                tipo.setIdTipoServico(rs.getInt("id_tipo_servico"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao buscar Tipo de Serviço por ID: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return tipo;
    }

    
    public String buscarNomePorId(int idTipoServico) {
        if (idTipoServico <= 0) {
            return null; 
        }
        String sql = "SELECT nome_tipo_servico FROM cad_tipos_servico WHERE id_tipo_servico = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String nome = null; 

        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                System.err.println("DAO (TipoServicoDAO - buscarNomePorId): Falha de conexão.");
                return null;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idTipoServico);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                nome = rs.getString("nome_tipo_servico");
            }
          
        } catch (SQLException e) {
            System.err.println("Erro DAO ao buscar nome do Tipo de Serviço por ID (ID: " + idTipoServico + "): " + e.getMessage());
            
             return null;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return nome;
    }
}
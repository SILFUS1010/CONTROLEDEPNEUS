package br.com.martins_borges.dal;


import br.com.martins_borges.model.Fornecedor;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;


public class FornecedorDAO {

    
    public List<String> listarNomes() {
        String sql = "SELECT nome_fornecedor FROM cad_fornecedores ORDER BY nome_fornecedor";
        List<String> nomes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ModuloConexao.conector();
            if (conn == null) return nomes;

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                nomes.add(rs.getString("nome_fornecedor"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao listar nomes de fornecedores: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return nomes;
    } 


   
public Fornecedor inserirFornecedor(String nome) {
    String sql = "INSERT INTO fornecedor (nome_fornecedor) VALUES (?)";
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet generatedKeys = null;
    Fornecedor fornecedorCriado = null;

    try {
        conn = ModuloConexao.conector();
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Não foi possível conectar ao banco de dados.", "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        pstmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
        pstmt.setString(1, nome);
        
        int affectedRows = pstmt.executeUpdate();

        if (affectedRows > 0) {
            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                fornecedorCriado = new Fornecedor();
                fornecedorCriado.setIdFornecedor(id);
                fornecedorCriado.setNomeFornecedor(nome);
            }
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro ao inserir fornecedor: " + e.getMessage(), "Erro no Banco de Dados", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
        // Em caso de erro, retornamos null
        return null; 
    } finally {
        // --- CORREÇÃO APLICADA AQUI ---
        // O código de fechamento foi movido para DENTRO do finally
        // E a variável de erro foi renomeada para 'ex'
        try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException ex) {}
        try { if (pstmt != null) pstmt.close(); } catch (SQLException ex) {}
        try { if (conn != null) conn.close(); } catch (SQLException ex) {}
    }

    // O retorno final do fornecedor criado com sucesso
    return fornecedorCriado;
}


   
    public List<String> buscarNomesParcial(String termo) {
        
        String sql = "SELECT nome_fornecedor FROM cad_fornecedores WHERE UPPER(nome_fornecedor) LIKE UPPER(?) ORDER BY nome_fornecedor";
        List<String> nomes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

       
        if (termo == null || termo.trim().isEmpty()) {
            return nomes;
        }

        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                 System.err.println("DAO (buscarNomesParcial Forn): Conexão NULL."); 
                 return nomes; 
            }

            pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, "%" + termo.trim() + "%"); 

            rs = pstmt.executeQuery();
            while (rs.next()) {
                nomes.add(rs.getString("nome_fornecedor"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao buscar nomes parciais de fornecedores: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
             
        } finally {
            
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return nomes;
    } 
    


public boolean atualizarNomeFornecedor(int idFornecedor, String novoNome) {
    if (idFornecedor <= 0 || novoNome == null || novoNome.trim().isEmpty()) {
        System.err.println("DAO: ID ou Novo Nome inválido para atualizar fornecedor.");
        return false;
    }
    novoNome = novoNome.trim();

    
    if (existeOutroFornecedorComNome(novoNome, idFornecedor)) {
         JOptionPane.showMessageDialog(null, "Erro: Já existe outro fornecedor com o nome '" + novoNome + "'.", "Nome Duplicado", JOptionPane.WARNING_MESSAGE);
         return false;
    }


    String sql = "UPDATE cad_fornecedores SET nome_fornecedor = ? WHERE id_fornecedor = ?";
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
        conn = ModuloConexao.conector();
        if (conn == null) return false;

        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, novoNome);    
        pstmt.setInt(2, idFornecedor); 

        int linhasAfetadas = pstmt.executeUpdate();
        return linhasAfetadas == 1; 

    } catch (SQLException e) {
        
         if (e.getSQLState() != null && e.getSQLState().equals("23000")) {
             JOptionPane.showMessageDialog(null, "Erro: Nome '" + novoNome + "' já existe (constraint).", "Erro SQL", JOptionPane.ERROR_MESSAGE);
         } else {
              JOptionPane.showMessageDialog(null, "Erro DAO ao atualizar fornecedor: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
              
         }
         return false;
    } finally {
        try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
}


private boolean existeOutroFornecedorComNome(String nome, int idExcluir) {
    String sql = "SELECT 1 FROM cad_fornecedores WHERE UPPER(nome_fornecedor) = UPPER(?) AND id_fornecedor != ?";
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
         System.err.println("DAO (existeOutro): Erro ao verificar nome duplicado: " + e.getMessage());
         return true; 
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
    return existe;
}

 
 public Fornecedor buscarPorNomeExato(String nome) {
     if (nome == null || nome.trim().isEmpty()) return null;
     String sql = "SELECT id_fornecedor, nome_fornecedor FROM cad_fornecedores WHERE UPPER(nome_fornecedor) = UPPER(?)";
     Fornecedor fornecedor = null;
     Connection conn = null; PreparedStatement pstmt = null; ResultSet rs = null;
     try {
         conn = ModuloConexao.conector();
         if (conn == null) return null;
         pstmt = conn.prepareStatement(sql);
         pstmt.setString(1, nome.trim());
         rs = pstmt.executeQuery();
         if (rs.next()) {
             fornecedor = new Fornecedor(rs.getString("nome_fornecedor"));
             fornecedor.setIdFornecedor(rs.getInt("id_fornecedor"));
         }
     } catch (SQLException e) {
          JOptionPane.showMessageDialog(null, "Erro DAO ao buscar fornecedor por nome: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
          
     } finally {
         try { if (rs != null) rs.close(); } catch (SQLException e) {}
         try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
         try { if (conn != null) conn.close(); } catch (SQLException e) {}
     }
     return fornecedor;
 }





} 
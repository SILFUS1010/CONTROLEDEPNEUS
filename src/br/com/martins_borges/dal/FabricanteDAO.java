package br.com.martins_borges.dal; // Pacote correto


import br.com.martins_borges.model.Fabricante; // Import do modelo Fabricante
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Necessário para RETURN_GENERATED_KEYS
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
// Import do ModuloConexao
import br.com.martins_borges.dal.ModuloConexao;

public class FabricanteDAO {

    
    public List<String> listarNomes() {
        String sql = "SELECT nome_fabricante FROM cad_fabricantes ORDER BY nome_fabricante";
        List<String> nomes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ModuloConexao.conector(); // Chama o método estático da classe ModuloConexao
            if (conn == null) {
                System.err.println("DAO: Falha de conexão ao listar nomes de fabricantes.");
                // Considerar lançar uma exceção ou retornar null para indicar falha de forma mais clara
                return nomes; // Retorna lista vazia em caso de falha de conexão
            }

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                nomes.add(rs.getString("nome_fabricante"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao listar nomes de fabricantes: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
             // Mantém para debug no console
        } finally {
            // Bloco finally para garantir o fechamento dos recursos
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Log ou ignore */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Log ou ignore */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Log ou ignore */ }
        }
        return nomes;
    } // Fim do método listarNomes


    /**
     * Insere um novo fabricante no banco de dados.
     * @param nomeNovoFabricante O nome do fabricante a ser inserido.
     * @return O objeto Fabricante criado (com ID preenchido) se sucesso, null caso contrário.
     */
    public Fabricante inserirFabricante(String nomeNovoFabricante) {
         // Validação básica de entrada
         if (nomeNovoFabricante == null || nomeNovoFabricante.trim().isEmpty()) {
             System.err.println("DAO: Tentativa de inserir fabricante com nome vazio.");
             return null;
         }
         nomeNovoFabricante = nomeNovoFabricante.trim(); // Remove espaços

        String sql = "INSERT INTO cad_fabricantes (nome_fabricante) VALUES (?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        Fabricante fabricanteCriado = null;

        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                System.err.println("DAO: Falha de conexão ao inserir fabricante.");
                return null;
            }

            // Prepara o statement pedindo as chaves geradas (ID)
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, nomeNovoFabricante);

            int affectedRows = pstmt.executeUpdate();

            // Se inseriu com sucesso, tenta obter o ID gerado
            if (affectedRows > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    fabricanteCriado = new Fabricante(nomeNovoFabricante); // Cria o objeto
                    fabricanteCriado.setIdFabricante(generatedKeys.getInt(1)); // Define o ID retornado pelo banco
                } else {
                    // Isso não deveria acontecer se affectedRows > 0, mas é bom logar
                    System.err.println("DAO: Inserção de fabricante bem-sucedida, mas não retornou ID.");
                    // Ainda retorna o objeto sem ID, mas pode indicar um problema
                    fabricanteCriado = new Fabricante(nomeNovoFabricante);
                }
            }
        } catch (SQLException e) {
             // Trata erro de violação de chave única (nome duplicado)
             if (e.getSQLState() != null && e.getSQLState().equals("23000")) {
                 JOptionPane.showMessageDialog(null, "Fabricante '" + nomeNovoFabricante + "' já existe.", "Erro de Duplicidade", JOptionPane.WARNING_MESSAGE);
             } else {
                 // Outros erros SQL
                 JOptionPane.showMessageDialog(null, "Erro DAO ao inserir fabricante: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
                  // Mantém para debug
             }
             return null; // Retorna null em caso de erro SQL
        } finally {
            // Bloco finally para fechar recursos
             try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException e) { /* Log ou ignore */ }
             try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Log ou ignore */ }
             try { if (conn != null) conn.close(); } catch (SQLException e) { /* Log ou ignore */ }
        }
        return fabricanteCriado; // Retorna o objeto criado (pode ter ID 0 se getGeneratedKeys falhar) ou null
    } // Fim do método inserirFabricante

    public boolean atualizarNomeFabricante(int idFabricante, String novoNome) {
        if (idFabricante <= 0 || novoNome == null || novoNome.trim().isEmpty()) return false;
        novoNome = novoNome.trim();

        if (existeOutroFabricanteComNome(novoNome, idFabricante)) {
             JOptionPane.showMessageDialog(null, "Erro: Já existe outro fabricante com o nome '" + novoNome + "'.", "Nome Duplicado", JOptionPane.WARNING_MESSAGE);
             return false;
        }

        String sql = "UPDATE cad_fabricantes SET nome_fabricante = ? WHERE id_fabricante = ?"; // Tabela/Colunas corretas
        Connection conn = null; PreparedStatement pstmt = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) return false;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, novoNome);
            pstmt.setInt(2, idFabricante);
            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas == 1;
        } catch (SQLException e) {
             if (e.getSQLState() != null && e.getSQLState().equals("23000")) { /* Tratar duplicidade */ JOptionPane.showMessageDialog(null, "Erro SQL: Nome duplicado.", "Erro", 0); }
             else { JOptionPane.showMessageDialog(null, "Erro DAO ao atualizar fabricante: " + e.getMessage(), "Erro SQL", 0);  }
             return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    /**
     * Verifica se OUTRO fabricante já tem o nome desejado.
     */
    private boolean existeOutroFabricanteComNome(String nome, int idExcluir) {
        String sql = "SELECT 1 FROM cad_fabricantes WHERE UPPER(nome_fabricante) = UPPER(?) AND id_fabricante != ?"; // Tabela/Colunas corretas
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
             System.err.println("DAO (existeOutroFab): Erro: " + e.getMessage()); return true;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return existe;
    }

     /**
     * Busca um Fabricante pelo nome exato (case-insensitive).
     */
     public Fabricante buscarPorNomeExato(String nome) {
         if (nome == null || nome.trim().isEmpty()) return null;
         String sql = "SELECT id_fabricante, nome_fabricante FROM cad_fabricantes WHERE UPPER(nome_fabricante) = UPPER(?)"; // Tabela/Colunas corretas
         Fabricante fabricante = null;
         Connection conn = null; PreparedStatement pstmt = null; ResultSet rs = null;
         try {
             conn = ModuloConexao.conector();
             if (conn == null) return null;
             pstmt = conn.prepareStatement(sql);
             pstmt.setString(1, nome.trim());
             rs = pstmt.executeQuery();
             if (rs.next()) {
                 fabricante = new Fabricante(rs.getString("nome_fabricante")); // Usa o modelo Fabricante
                 fabricante.setIdFabricante(rs.getInt("id_fabricante"));      // Usa o setter correto
             }
         } catch (SQLException e) { JOptionPane.showMessageDialog(null, "Erro DAO buscar fab. p/ nome: " + e.getMessage(), "Erro SQL", 0); 
         } finally {
             try { if (rs != null) rs.close(); } catch (SQLException e) {}
             try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
             try { if (conn != null) conn.close(); } catch (SQLException e) {}
         }
         return fabricante;
     }

}


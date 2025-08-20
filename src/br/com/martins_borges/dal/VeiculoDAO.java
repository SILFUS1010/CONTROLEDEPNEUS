package br.com.martins_borges.dal;

import br.com.martins_borges.model.Veiculo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VeiculoDAO {

  
    public boolean salvarPlacaVeiculo(String placa) {
        // SQL SÓ com a coluna PLACA (e talvez o status inicial)
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
            pstmt.setString(1, placa);          // Parâmetro 1: Placa
            pstmt.setString(2, "DISPONIVEL"); // Parâmetro 2: Status inicial

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            System.err.println("DAO Error: Placa '" + placa + "' já existe? " + e.getMessage());
            return false;
        } catch (SQLException e) {
            System.err.println("DAO SQL Error: Erro ao salvar placa - " + e.getMessage());
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    public boolean salvarVeiculoCompleto(Veiculo veiculo) {
        String sql = "INSERT INTO CAD_VEICULOS (FROTA, PLACA, ID_CONFIG_FK, QTD_PNEUS, DATA_CADASTRO, MEDIDA_PNEU, STATUS_VEICULO) VALUES (?, ?, ?, ?, ?, ?, ?)";
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
            pstmt.setInt(3, veiculo.getID_CONFIG_FK());
            pstmt.setInt(4, veiculo.getQTD_PNEUS());
            pstmt.setDate(5, java.sql.Date.valueOf(veiculo.getDATA_CADASTRO()));
            pstmt.setString(6, veiculo.getMEDIDA_PNEU());
            pstmt.setString(7, veiculo.getSTATUS_VEICULO());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("DAO SQL Error: Erro ao salvar veículo completo - " + e.getMessage());
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
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
                veiculo.setID_CONFIG_FK(rs.getInt("ID_CONFIG_FK"));
                veiculo.setQTD_PNEUS(rs.getInt("QTD_PNEUS"));
                veiculo.setDATA_CADASTRO(rs.getDate("DATA_CADASTRO").toLocalDate());
                veiculo.setMEDIDA_PNEU(rs.getString("MEDIDA_PNEU"));
                veiculo.setSTATUS_VEICULO(rs.getString("STATUS_VEICULO"));
            }
        } catch (SQLException e) {
            System.err.println("DAO SQL Error: Erro ao buscar veículo por placa - " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
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
                return veiculos; // Retorna lista vazia
            }
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Veiculo veiculo = new Veiculo();
                veiculo.setID(rs.getInt("ID"));
                veiculo.setFROTA(rs.getString("FROTA"));
                veiculo.setPLACA(rs.getString("PLACA"));
                veiculo.setID_CONFIG_FK(rs.getInt("ID_CONFIG_FK"));
                veiculo.setQTD_PNEUS(rs.getInt("QTD_PNEUS"));
                veiculo.setDATA_CADASTRO(rs.getDate("DATA_CADASTRO").toLocalDate());
                veiculo.setMEDIDA_PNEU(rs.getString("MEDIDA_PNEU"));
                veiculo.setSTATUS_VEICULO(rs.getString("STATUS_VEICULO"));
                veiculos.add(veiculo);
            }
        } catch (SQLException e) {
            System.err.println("DAO SQL Error: Erro ao listar veículos - " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
        return veiculos;
    }

    public boolean atualizarVeiculo(Veiculo veiculo) {
        String sql = "UPDATE CAD_VEICULOS SET FROTA = ?, PLACA = ?, ID_CONFIG_FK = ?, QTD_PNEUS = ?, DATA_CADASTRO = ?, MEDIDA_PNEU = ?, STATUS_VEICULO = ? WHERE ID = ?";
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
            pstmt.setInt(3, veiculo.getID_CONFIG_FK());
            pstmt.setInt(4, veiculo.getQTD_PNEUS());
            pstmt.setDate(5, java.sql.Date.valueOf(veiculo.getDATA_CADASTRO()));
            pstmt.setString(6, veiculo.getMEDIDA_PNEU());
            pstmt.setString(7, veiculo.getSTATUS_VEICULO());
            pstmt.setInt(8, veiculo.getID());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("DAO SQL Error: Erro ao atualizar veículo - " + e.getMessage());
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
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
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    
    public boolean frotaExiste(String frota) {
        // FORÇANDO RECOMPILAÇÃO: Adicionando este comentário para garantir que o arquivo .class seja atualizado.
        // A consulta usa COUNT(*) que é otimizada para apenas contar os registros
        // Nota: O nome da tabela é "CAD_VEICULOS" conforme o restante do código, e não "tb_veiculos".
        String sql = "SELECT COUNT(*) FROM CAD_VEICULOS WHERE frota = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                System.err.println("DAO: Falha na conexão ao verificar frota.");
                // Em um cenário real, poderia ser melhor lançar uma exceção aqui
                return false;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, frota);
            rs = pstmt.executeQuery();

            // Se o ResultSet tiver um resultado, pegamos o valor da contagem
            if (rs.next()) {
                // rs.getInt(1) pega o valor da primeira coluna do resultado (o COUNT)
                // Se a contagem for maior que 0, significa que a frota existe.
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("DAO SQL Error: Erro ao verificar se a frota existe - " + e.getMessage());
        } finally {
            // Bloco finally para garantir que os recursos sejam fechados
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
        // Retorna false por padrão ou em caso de erro.
        return false;
    }
// COLE ESTE MÉTODO DENTRO DA SUA CLASSE VeiculoDAO

public boolean excluir(int id) {
    // Comando SQL para deletar um registro com base no seu ID
    String sql = "DELETE FROM CAD_VEICULOS WHERE id = ?";
    
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
        // Abre a conexão com o banco de dados
        conn = ModuloConexao.conector();
        if (conn == null) {
            // Se a conexão falhar, não podemos continuar
            return false;
        }

        // Prepara o comando SQL, passando o ID como parâmetro para evitar SQL Injection
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);

        // Executa o comando de exclusão
        int affectedRows = pstmt.executeUpdate();

        // executeUpdate() retorna o número de linhas afetadas.
        // Se for maior que 0, significa que a exclusão funcionou.
        return affectedRows > 0;

    } catch (SQLException e) {
        // Se ocorrer um erro de SQL, imprime o erro no console
        System.err.println("Erro ao excluir veículo: " + e.getMessage());
        e.printStackTrace();
        // Retorna 'false' para indicar que a operação falhou
        return false;
    } finally {
        // Bloco 'finally' para garantir que a conexão e o statement sejam fechados,
        // mesmo que ocorra um erro.
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


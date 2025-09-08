package br.com.martins_borges.dal;

import br.com.martins_borges.model.Pneu;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types; // Importado para setNull
import java.util.ArrayList;
import java.util.Date; // Para dataRetorno
import java.util.List;
import javax.swing.JOptionPane; // Para feedback de erro

public class PneuDAO {

    public PneuDAO() {
        // O construtor não precisa inicializar a conexão globalmente se cada método
        // pega e fecha a sua própria conexão através de ModuloConexao.conector().
    }

    /**
     * Lista todos os pneus cadastrados no sistema.
     * @return Lista de objetos Pneu
     */
    public List<Pneu> listarTodosPneus() {
        // Inclui os novos campos na consulta SQL
        String sql = "SELECT ID, ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, TIPO_PNEU, MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, PROJETADO_KM, OBSERVACOES, DATA_RETORNO, status_pneu, ID_VEICULO, POSICAO_NO_VEICULO FROM cad_pneus ORDER BY FOGO";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Pneu> pneus = new ArrayList<>();
        
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Erro de Conexão com o Banco.", "Erro DAO", JOptionPane.ERROR_MESSAGE);
                return pneus;
            }
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Pneu pneu = new Pneu();
                pneu.setId(rs.getInt("ID"));
                pneu.setIdEmpresaProprietaria(rs.getInt("ID_EMPRESA_PROPRIETARIA"));
                pneu.setFogo(rs.getString("FOGO"));
                pneu.setFornecedor(rs.getString("FORNECEDOR"));
                pneu.setValor(rs.getObject("VALOR") != null ? rs.getDouble("VALOR") : null);
                pneu.setFabricante(rs.getString("FABRICANTE"));
                pneu.setTipoPneu(rs.getString("TIPO_PNEU"));
                pneu.setModelo(rs.getString("MODELO"));
                pneu.setDot(rs.getString("DOT"));
                pneu.setMedida(rs.getString("MEDIDA"));
                pneu.setProfundidade(rs.getObject("PROFUNDIDADE") != null ? rs.getDouble("PROFUNDIDADE") : null);
                
                java.sql.Date sqlDateCadastro = rs.getDate("DATA_CADASTRO");
                if (sqlDateCadastro != null) {
                    pneu.setDataCadastro(sqlDateCadastro.toLocalDate());
                } else {
                    pneu.setDataCadastro(null);
                }
                
                pneu.setnRecapagens(rs.getInt("N_RECAPAGENS"));
                // Tratamento para PROJETADO_KM que pode ser NULL no DB
                Integer projetadoKmRs = rs.getInt("PROJETADO_KM");
                pneu.setProjetadoKm(rs.wasNull() ? null : projetadoKmRs);
                
                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setDataRetorno(rs.getDate("DATA_RETORNO")); // Pode ser null
                pneu.setStatusPneu(rs.getString("status_pneu"));
                
                // Tratamento para ID_VEICULO que pode ser NULL no DB
                Integer idVeiculoRs = rs.getInt("ID_VEICULO");
                pneu.setIdVeiculo(rs.wasNull() ? null : idVeiculoRs);
                pneu.setPosicaoNoVeiculo(rs.getString("POSICAO_NO_VEICULO")); // String já trata NULL automaticamente
                
                pneus.add(pneu);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar todos os pneus: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
        
        return pneus;
    }
    
    /**
     * Busca um pneu pelo seu ID.
     * @param id O ID do pneu a ser buscado
     * @return O objeto Pneu encontrado ou null se não encontrado
     */
    public Pneu buscarPneuPorId(int id) {
        // Inclui os novos campos na consulta SQL
        String sql = "SELECT ID, ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, TIPO_PNEU, MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, PROJETADO_KM, OBSERVACOES, DATA_RETORNO, status_pneu, ID_VEICULO, POSICAO_NO_VEICULO FROM cad_pneus WHERE ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Pneu pneu = null;
        
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Erro de Conexão com o Banco.", "Erro DAO", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                pneu = new Pneu();
                pneu.setId(rs.getInt("ID"));
                pneu.setIdEmpresaProprietaria(rs.getInt("ID_EMPRESA_PROPRIETARIA"));
                pneu.setFogo(rs.getString("FOGO"));
                pneu.setFornecedor(rs.getString("FORNECEDOR"));
                pneu.setValor(rs.getObject("VALOR") != null ? rs.getDouble("VALOR") : null);
                pneu.setFabricante(rs.getString("FABRICANTE"));
                pneu.setTipoPneu(rs.getString("TIPO_PNEU"));
                pneu.setModelo(rs.getString("MODELO"));
                pneu.setDot(rs.getString("DOT"));
                pneu.setMedida(rs.getString("MEDIDA"));
                pneu.setProfundidade(rs.getObject("PROFUNDIDADE") != null ? rs.getDouble("PROFUNDIDADE") : null);
                
                java.sql.Date sqlDateCadastro = rs.getDate("DATA_CADASTRO");
                if (sqlDateCadastro != null) {
                    pneu.setDataCadastro(sqlDateCadastro.toLocalDate());
                } else {
                    pneu.setDataCadastro(null);
                }
                
                pneu.setnRecapagens(rs.getInt("N_RECAPAGENS"));
                // Tratamento para PROJETADO_KM que pode ser NULL no DB
                Integer projetadoKmRs = rs.getInt("PROJETADO_KM");
                pneu.setProjetadoKm(rs.wasNull() ? null : projetadoKmRs);
                
                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setDataRetorno(rs.getDate("DATA_RETORNO")); // Pode ser null
                pneu.setStatusPneu(rs.getString("status_pneu"));
                
                // Tratamento para ID_VEICULO que pode ser NULL no DB
                Integer idVeiculoRs = rs.getInt("ID_VEICULO");
                pneu.setIdVeiculo(rs.wasNull() ? null : idVeiculoRs);
                pneu.setPosicaoNoVeiculo(rs.getString("POSICAO_NO_VEICULO")); // String já trata NULL automaticamente
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar pneu por ID: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
        
        return pneu;
    }

    public boolean inserirPneu(Pneu pneu) {
        // Adiciona ID_VEICULO e POSICAO_NO_VEICULO (NULL) no INSERT
        String sql = "INSERT INTO cad_pneus (ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, TIPO_PNEU, "
                + "MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, "
                + "PROJETADO_KM, OBSERVACOES, status_pneu, DATA_RETORNO, ID_VEICULO, POSICAO_NO_VEICULO) " // <--- Novos campos adicionados aqui
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; // <--- Aumenta o número de VALUES
        Connection conn = null;
        PreparedStatement pstmt = null;

        if (existePneu(pneu.getIdEmpresaProprietaria(), pneu.getFogo())) {
            JOptionPane.showMessageDialog(null, "Erro: Já existe um pneu com o N° Fogo '" + pneu.getFogo() + "' para a empresa selecionada.", "Erro de Duplicidade", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Erro de Conexão com o Banco.", "Erro DAO", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, pneu.getIdEmpresaProprietaria());
            pstmt.setString(2, pneu.getFogo());
            pstmt.setString(3, pneu.getFornecedor());
            if (pneu.getValor() != null) {
                pstmt.setDouble(4, pneu.getValor());
            } else {
                pstmt.setNull(4, Types.DECIMAL);
            }
            pstmt.setString(5, pneu.getFabricante());
            pstmt.setString(6, pneu.getTipoPneu());
            pstmt.setString(7, pneu.getModelo());
            pstmt.setString(8, pneu.getDot());
            pstmt.setString(9, pneu.getMedida());
            if (pneu.getProfundidade() != null) {
                pstmt.setDouble(10, pneu.getProfundidade());
            } else {
                pstmt.setNull(10, Types.DECIMAL);
            }
            if (pneu.getDataCadastro() != null) {
                pstmt.setDate(11, java.sql.Date.valueOf(pneu.getDataCadastro()));
            } else {
                pstmt.setNull(11, Types.DATE);
            }
            pstmt.setInt(12, pneu.getnRecapagens());
            if (pneu.getProjetadoKm() != null) {
                pstmt.setInt(13, pneu.getProjetadoKm());
            } else {
                pstmt.setNull(13, Types.INTEGER);
            }
            pstmt.setString(14, pneu.getObservacoes());
            pstmt.setString(15, "ESTOQUE"); // Status inicial
            
            if (pneu.getDataRetorno() != null) { // Adiciona dataRetorno
                pstmt.setDate(16, new java.sql.Date(pneu.getDataRetorno().getTime()));
            } else {
                pstmt.setNull(16, Types.DATE);
            }
            
            pstmt.setNull(17, Types.INTEGER); // ID_VEICULO inicia como NULL
            pstmt.setNull(18, Types.VARCHAR); // POSICAO_NO_VEICULO inicia como NULL

            int adicionado = pstmt.executeUpdate();
            return adicionado > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao inserir pneu: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    public boolean existePneu(int idEmpresa, String fogoSequencial) {
        String sql = "SELECT 1 FROM cad_pneus WHERE ID_EMPRESA_PROPRIETARIA = ? AND FOGO = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existe = false;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Erro de Conexão com o Banco.", "Erro DAO", JOptionPane.ERROR_MESSAGE);
                return true; 
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idEmpresa);
            pstmt.setString(2, fogoSequencial);
            rs = pstmt.executeQuery();
            existe = rs.next();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao verificar existência do pneu: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            return true; // Em caso de erro, por segurança, consideramos que existe para evitar duplicidade
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
        return existe;
    }

    // --- NOVO MÉTODO: 1. Buscar um pneu completo pelo número de fogo ---
    public Pneu buscarPneuPorFogo(String fogo) {
        // Inclui os novos campos na consulta SQL
        String sql = "SELECT ID, ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, TIPO_PNEU, "
                + "MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, "
                + "PROJETADO_KM, OBSERVACOES, DATA_RETORNO, status_pneu, ID_VEICULO, POSICAO_NO_VEICULO "
                + "FROM cad_pneus WHERE FOGO = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Pneu pneu = null;

        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Erro de Conexão com o Banco.", "Erro DAO", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, fogo);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                pneu = new Pneu();
                pneu.setId(rs.getInt("ID"));
                pneu.setIdEmpresaProprietaria(rs.getInt("ID_EMPRESA_PROPRIETARIA"));
                pneu.setFogo(rs.getString("FOGO"));
                pneu.setFornecedor(rs.getString("FORNECEDOR"));
                pneu.setValor(rs.getObject("VALOR") != null ? rs.getDouble("VALOR") : null);
                pneu.setFabricante(rs.getString("FABRICANTE"));
                pneu.setTipoPneu(rs.getString("TIPO_PNEU"));
                pneu.setModelo(rs.getString("MODELO"));
                pneu.setDot(rs.getString("DOT"));
                pneu.setMedida(rs.getString("MEDIDA"));
                pneu.setProfundidade(rs.getObject("PROFUNDIDADE") != null ? rs.getDouble("PROFUNDIDADE") : null);
                
                java.sql.Date sqlDateCadastro = rs.getDate("DATA_CADASTRO");
                if (sqlDateCadastro != null) {
                    pneu.setDataCadastro(sqlDateCadastro.toLocalDate());
                } else {
                    pneu.setDataCadastro(null);
                }
                
                pneu.setnRecapagens(rs.getInt("N_RECAPAGENS"));
                // Tratamento para PROJETADO_KM que pode ser NULL no DB
                Integer projetadoKmRs = rs.getInt("PROJETADO_KM");
                pneu.setProjetadoKm(rs.wasNull() ? null : projetadoKmRs);
                
                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setDataRetorno(rs.getDate("DATA_RETORNO")); // Pode ser null
                pneu.setStatusPneu(rs.getString("status_pneu"));
                
                // Tratamento para ID_VEICULO que pode ser NULL no DB
                Integer idVeiculoRs = rs.getInt("ID_VEICULO");
                pneu.setIdVeiculo(rs.wasNull() ? null : idVeiculoRs);
                pneu.setPosicaoNoVeiculo(rs.getString("POSICAO_NO_VEICULO")); // String já trata NULL automaticamente
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao buscar pneu por N° Fogo: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
        return pneu;
    }

    // --- MODIFICADO MÉTODO: 2. Listar pneus por status e (opcionalmente) medida ---
    public List<Pneu> listarPneusPorStatusEMedida(String status, String medida) {
        List<Pneu> listaPneus = new ArrayList<>();
        // Inclui os novos campos na consulta SQL
        String sql = "SELECT ID, ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, TIPO_PNEU, "
                + "MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, "
                + "PROJETADO_KM, OBSERVACOES, DATA_RETORNO, status_pneu, ID_VEICULO, POSICAO_NO_VEICULO "
                + "FROM cad_pneus WHERE status_pneu = ?";
        if (medida != null && !medida.trim().isEmpty()) {
            sql += " AND MEDIDA = ?"; 
        }
        sql += " ORDER BY FOGO"; // Adicionado ORDER BY
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Erro de Conexão com o Banco.", "Erro DAO", JOptionPane.ERROR_MESSAGE);
                return listaPneus;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            if (medida != null && !medida.trim().isEmpty()) {
                pstmt.setString(2, medida);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Pneu pneu = new Pneu();
                pneu.setId(rs.getInt("ID"));
                pneu.setIdEmpresaProprietaria(rs.getInt("ID_EMPRESA_PROPRIETARIA"));
                pneu.setFogo(rs.getString("FOGO"));
                pneu.setFornecedor(rs.getString("FORNECEDOR"));
                pneu.setValor(rs.getObject("VALOR") != null ? rs.getDouble("VALOR") : null);
                pneu.setFabricante(rs.getString("FABRICANTE"));
                pneu.setTipoPneu(rs.getString("TIPO_PNEU"));
                pneu.setModelo(rs.getString("MODELO"));
                pneu.setDot(rs.getString("DOT"));
                pneu.setMedida(rs.getString("MEDIDA"));
                pneu.setProfundidade(rs.getObject("PROFUNDIDADE") != null ? rs.getDouble("PROFUNDIDADE") : null);
                java.sql.Date sqlDate = rs.getDate("DATA_CADASTRO");
                if (sqlDate != null) {
                    pneu.setDataCadastro(sqlDate.toLocalDate());
                } else {
                    pneu.setDataCadastro(null);
                }
                pneu.setnRecapagens(rs.getInt("N_RECAPAGENS"));
                // Tratamento para PROJETADO_KM que pode ser NULL no DB
                Integer projetadoKmRs = rs.getInt("PROJETADO_KM");
                pneu.setProjetadoKm(rs.wasNull() ? null : projetadoKmRs);
                
                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setDataRetorno(rs.getDate("DATA_RETORNO")); // Pode ser null
                pneu.setStatusPneu(rs.getString("status_pneu"));
                
                // Tratamento para ID_VEICULO que pode ser NULL no DB
                Integer idVeiculoRs = rs.getInt("ID_VEICULO");
                pneu.setIdVeiculo(rs.wasNull() ? null : idVeiculoRs);
                pneu.setPosicaoNoVeiculo(rs.getString("POSICAO_NO_VEICULO")); // String já trata NULL automaticamente
                
                listaPneus.add(pneu);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao listar pneus por status e medida: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
        return listaPneus;
    }

    // --- MODIFICADO MÉTODO: 3. Listar todos os pneus com um determinado status ---
    public List<Pneu> listarPneusPorStatus(String status) {
        String sql = "SELECT ID, ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, TIPO_PNEU, "
                + "MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, "
                + "PROJETADO_KM, OBSERVACOES, DATA_RETORNO, status_pneu, ID_VEICULO, POSICAO_NO_VEICULO "
                + "FROM cad_pneus WHERE status_pneu = ? ORDER BY FOGO";

        List<Pneu> listaPneus = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Erro de Conexão com o Banco.", "Erro DAO", JOptionPane.ERROR_MESSAGE);
                return listaPneus;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Pneu pneu = new Pneu();
                pneu.setId(rs.getInt("ID"));
                pneu.setIdEmpresaProprietaria(rs.getInt("ID_EMPRESA_PROPRIETARIA"));
                pneu.setFogo(rs.getString("FOGO"));
                pneu.setFornecedor(rs.getString("FORNECEDOR"));
                pneu.setValor(rs.getObject("VALOR") != null ? rs.getDouble("VALOR") : null);
                pneu.setFabricante(rs.getString("FABRICANTE"));
                pneu.setTipoPneu(rs.getString("TIPO_PNEU"));
                pneu.setModelo(rs.getString("MODELO"));
                pneu.setDot(rs.getString("DOT"));
                pneu.setMedida(rs.getString("MEDIDA"));
                pneu.setProfundidade(rs.getObject("PROFUNDIDADE") != null ? rs.getDouble("PROFUNDIDADE") : null);
                java.sql.Date sqlDate = rs.getDate("DATA_CADASTRO");
                if (sqlDate != null) {
                    pneu.setDataCadastro(sqlDate.toLocalDate());
                } else {
                    pneu.setDataCadastro(null);
                }
                pneu.setnRecapagens(rs.getInt("N_RECAPAGENS"));
                // Tratamento para PROJETADO_KM que pode ser NULL no DB
                Integer projetadoKmRs = rs.getInt("PROJETADO_KM");
                pneu.setProjetadoKm(rs.wasNull() ? null : projetadoKmRs);
                
                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setDataRetorno(rs.getDate("DATA_RETORNO")); // Pode ser null
                pneu.setStatusPneu(rs.getString("status_pneu"));
                
                // Tratamento para ID_VEICULO que pode ser NULL no DB
                Integer idVeiculoRs = rs.getInt("ID_VEICULO");
                pneu.setIdVeiculo(rs.wasNull() ? null : idVeiculoRs);
                pneu.setPosicaoNoVeiculo(rs.getString("POSICAO_NO_VEICULO")); // String já trata NULL automaticamente

                listaPneus.add(pneu);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao listar pneus por status: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
        return listaPneus;
    }
    
    // --- NOVO MÉTODO: 4. Listar pneus atualmente alocados em um veículo específico ---
    public List<Pneu> listarPneusNoVeiculo(int idVeiculo) {
        List<Pneu> pneus = new ArrayList<>();
        String sql = "SELECT ID, ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, TIPO_PNEU, "
                + "MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, "
                + "PROJETADO_KM, OBSERVACOES, DATA_RETORNO, status_pneu, ID_VEICULO, POSICAO_NO_VEICULO "
                + "FROM cad_pneus WHERE ID_VEICULO = ? ORDER BY POSICAO_NO_VEICULO";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Erro de Conexão com o Banco.", "Erro DAO", JOptionPane.ERROR_MESSAGE);
                return pneus;
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idVeiculo);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Pneu pneu = new Pneu();
                pneu.setId(rs.getInt("ID"));
                pneu.setIdEmpresaProprietaria(rs.getInt("ID_EMPRESA_PROPRIETARIA"));
                pneu.setFogo(rs.getString("FOGO"));
                pneu.setFornecedor(rs.getString("FORNECEDOR"));
                pneu.setValor(rs.getObject("VALOR") != null ? rs.getDouble("VALOR") : null);
                pneu.setFabricante(rs.getString("FABRICANTE"));
                pneu.setTipoPneu(rs.getString("TIPO_PNEU"));
                pneu.setModelo(rs.getString("MODELO"));
                pneu.setDot(rs.getString("DOT"));
                pneu.setMedida(rs.getString("MEDIDA"));
                pneu.setProfundidade(rs.getObject("PROFUNDIDADE") != null ? rs.getDouble("PROFUNDIDADE") : null);
                
                java.sql.Date sqlDateCadastro = rs.getDate("DATA_CADASTRO");
                if (sqlDateCadastro != null) {
                    pneu.setDataCadastro(sqlDateCadastro.toLocalDate());
                } else {
                    pneu.setDataCadastro(null);
                }
                
                pneu.setnRecapagens(rs.getInt("N_RECAPAGENS"));
                // Tratamento para PROJETADO_KM que pode ser NULL no DB
                Integer projetadoKmRs = rs.getInt("PROJETADO_KM");
                pneu.setProjetadoKm(rs.wasNull() ? null : projetadoKmRs);
                
                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setDataRetorno(rs.getDate("DATA_RETORNO"));
                pneu.setStatusPneu(rs.getString("status_pneu"));
                
                // ID_VEICULO e POSICAO_NO_VEICULO não serão NULL aqui, pois estamos filtrando por ID_VEICULO
                pneu.setIdVeiculo(rs.getInt("ID_VEICULO")); 
                pneu.setPosicaoNoVeiculo(rs.getString("POSICAO_NO_VEICULO")); 
                
                pneus.add(pneu);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao listar pneus no veículo: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
        return pneus;
    }

    // --- MÉTODO ATUALIZADO: 5. Atualizar a localização de um pneu (alocar a um veículo/posição) ---
    // Agora aceita um parâmetro para o status e usa a conexão fornecida (pode ser nula para criar uma nova)
    public boolean atualizarLocalizacaoPneu(int pneuId, int veiculoId, String posicaoNoVeiculo, String status) {
        String sql = "UPDATE cad_pneus SET ID_VEICULO = ?, POSICAO_NO_VEICULO = ?, status_pneu = ? WHERE ID = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean closeConnection = false;
        
        try {
            // Se a conexão for fornecida, usa a existente, senão cria uma nova
            if (conn == null) {
                conn = ModuloConexao.conector();
                closeConnection = true;
                if (conn == null) {
                    JOptionPane.showMessageDialog(null, "Erro de Conexão com o Banco.", "Erro DAO", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, veiculoId);
            pstmt.setString(2, posicaoNoVeiculo);
            pstmt.setString(3, status);
            pstmt.setInt(4, pneuId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao alocar pneu: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            // Só fecha a conexão se ela foi criada neste método
            try { 
                if (conn != null && closeConnection) { 
                    conn.close(); 
                } 
            } catch (SQLException e) { /* Ignora */ }
        }
    }

    // --- MÉTODO ATUALIZADO: 6. Remover um pneu de um veículo ---
    // Agora aceita um parâmetro para o status e usa a conexão fornecida (pode ser nula para criar uma nova)
    public boolean removerPneuDoVeiculo(int pneuId, String novoStatus) {
        String sql = "UPDATE cad_pneus SET ID_VEICULO = NULL, POSICAO_NO_VEICULO = NULL, status_pneu = ? WHERE ID = ?"; 
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean closeConnection = false;
        
        try {
            // Se a conexão for fornecida, usa a existente, senão cria uma nova
            if (conn == null) {
                conn = ModuloConexao.conector();
                closeConnection = true;
                if (conn == null) {
                    JOptionPane.showMessageDialog(null, "Erro de Conexão com o Banco.", "Erro DAO", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, novoStatus);
            pstmt.setInt(2, pneuId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao remover pneu do veículo: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            // Só fecha a conexão se ela foi criada neste método
            try { 
                if (conn != null && closeConnection) { 
                    conn.close(); 
                } 
            } catch (SQLException e) { /* Ignora */ }
        }
    }
    
    // --- NOVO MÉTODO: Buscar pneu por posição no veículo ---
    public Pneu buscarPneuPorPosicaoNoVeiculo(int veiculoId, String posicao) {
        String sql = "SELECT ID, ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, TIPO_PNEU, "
                + "MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, "
                + "PROJETADO_KM, OBSERVACOES, DATA_RETORNO, status_pneu, ID_VEICULO, POSICAO_NO_VEICULO "
                + "FROM cad_pneus WHERE ID_VEICULO = ? AND POSICAO_NO_VEICULO = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Pneu pneu = null;

        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Erro de Conexão com o Banco.", "Erro DAO", JOptionPane.ERROR_MESSAGE);
                return null;
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, veiculoId);
            pstmt.setString(2, posicao);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                pneu = new Pneu();
                pneu.setId(rs.getInt("ID"));
                pneu.setIdEmpresaProprietaria(rs.getInt("ID_EMPRESA_PROPRIETARIA"));
                pneu.setFogo(rs.getString("FOGO"));
                pneu.setFornecedor(rs.getString("FORNECEDOR"));
                pneu.setValor(rs.getObject("VALOR") != null ? rs.getDouble("VALOR") : null);
                pneu.setFabricante(rs.getString("FABRICANTE"));
                pneu.setTipoPneu(rs.getString("TIPO_PNEU"));
                pneu.setModelo(rs.getString("MODELO"));
                pneu.setDot(rs.getString("DOT"));
                pneu.setMedida(rs.getString("MEDIDA"));
                pneu.setProfundidade(rs.getObject("PROFUNDIDADE") != null ? rs.getDouble("PROFUNDIDADE") : null);
                
                java.sql.Date sqlDateCadastro = rs.getDate("DATA_CADASTRO");
                if (sqlDateCadastro != null) {
                    pneu.setDataCadastro(sqlDateCadastro.toLocalDate());
                }
                
                pneu.setnRecapagens(rs.getInt("N_RECAPAGENS"));
                Integer projetadoKmRs = rs.getInt("PROJETADO_KM");
                pneu.setProjetadoKm(rs.wasNull() ? null : projetadoKmRs);
                
                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setDataRetorno(rs.getDate("DATA_RETORNO"));
                pneu.setStatusPneu(rs.getString("status_pneu"));
                
                Integer idVeiculoRs = rs.getInt("ID_VEICULO");
                pneu.setIdVeiculo(rs.wasNull() ? null : idVeiculoRs);
                pneu.setPosicaoNoVeiculo(rs.getString("POSICAO_NO_VEICULO"));
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar pneu por posição: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
        
        return pneu;
    }


    public boolean atualizarPneu(Pneu pneu) {
        if (pneu == null || pneu.getId() <= 0) {
            return false;
        }

        String sql = "UPDATE cad_pneus SET "
                + "ID_EMPRESA_PROPRIETARIA = ?, FOGO = ?, FORNECEDOR = ?, VALOR = ?, "
                + "FABRICANTE = ?, TIPO_PNEU = ?, MODELO = ?, DOT = ?, MEDIDA = ?, "
                + "PROFUNDIDADE = ?, N_RECAPAGENS = ?, PROJETADO_KM = ?, OBSERVACOES = ?, status_pneu = ?, "
                + "DATA_RETORNO = ? " 
                + "WHERE ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                return false;
            }
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, pneu.getIdEmpresaProprietaria());
            pstmt.setString(2, pneu.getFogo());
            pstmt.setString(3, pneu.getFornecedor());
            if (pneu.getValor() != null) {
                pstmt.setDouble(4, pneu.getValor());
            } else {
                pstmt.setNull(4, Types.DECIMAL);
            }
            pstmt.setString(5, pneu.getFabricante());
            pstmt.setString(6, pneu.getTipoPneu());
            pstmt.setString(7, pneu.getModelo());
            pstmt.setString(8, pneu.getDot());
            pstmt.setString(9, pneu.getMedida());
            if (pneu.getProfundidade() != null) {
                pstmt.setDouble(10, pneu.getProfundidade());
            } else {
                pstmt.setNull(10, Types.DECIMAL);
            }
            pstmt.setInt(11, pneu.getnRecapagens());
            if (pneu.getProjetadoKm() != null) {
                pstmt.setInt(12, pneu.getProjetadoKm());
            } else {
                pstmt.setNull(12, Types.INTEGER);
            }
            pstmt.setString(13, pneu.getObservacoes());
            pstmt.setString(14, pneu.getStatusPneu());
            if (pneu.getDataRetorno() != null) {
                pstmt.setDate(15, new java.sql.Date(pneu.getDataRetorno().getTime()));
            } else {
                pstmt.setNull(15, Types.DATE);
            }
            pstmt.setInt(16, pneu.getId()); 

            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas == 1;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao atualizar pneu (ID: " + pneu.getId() + "): " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    private boolean existePneuDuplicadoParaUpdate(int idEmpresa, String fogoSequencial, int idAtual) {
        String sql = "SELECT 1 FROM cad_pneus WHERE ID_EMPRESA_PROPRIETARIA = ? AND FOGO = ? AND ID != ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existe = false;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Erro de Conexão com o Banco.", "Erro DAO", JOptionPane.ERROR_MESSAGE);
                return true;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idEmpresa);
            pstmt.setString(2, fogoSequencial);
            pstmt.setInt(3, idAtual);
            rs = pstmt.executeQuery();
            existe = rs.next();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao verificar duplicidade para update: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            return true;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
        return existe;
    }

    // --- MODIFICADO/PADRONIZADO MÉTODO: 7. Atualizar apenas o status de um pneu ---
    public boolean atualizarStatusPneu(int pneuId, String novoStatus) { 
        if (pneuId <= 0 || novoStatus == null || novoStatus.trim().isEmpty()) {
            return false;
        }
        String sql = "UPDATE cad_pneus SET status_pneu = ? WHERE ID = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Erro de Conexão com o Banco.", "Erro DAO", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, novoStatus.trim().toUpperCase());
            pstmt.setInt(2, pneuId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao atualizar status do pneu (ID: " + pneuId + "): " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    public Pneu buscarPorEmpresaEFogo(int idEmpresa, String fogoSequencial) {
        String sql = "SELECT ID, ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, TIPO_PNEU, "
                + "MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, "
                + "PROJETADO_KM, OBSERVACOES, DATA_RETORNO, status_pneu, ID_VEICULO, POSICAO_NO_VEICULO "
                + "FROM cad_pneus WHERE ID_EMPRESA_PROPRIETARIA = ? AND FOGO = ?";
        Pneu pneu = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        if (idEmpresa <= 0 || fogoSequencial == null || fogoSequencial.trim().isEmpty()) {
            return null;
        }

        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                return null;
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idEmpresa);
            pstmt.setString(2, fogoSequencial.trim());
            rs = pstmt.executeQuery();

            if (rs.next()) {
                pneu = new Pneu();
                pneu.setId(rs.getInt("ID"));
                pneu.setIdEmpresaProprietaria(rs.getInt("ID_EMPRESA_PROPRIETARIA"));
                pneu.setFogo(rs.getString("FOGO"));
                pneu.setFornecedor(rs.getString("FORNECEDOR"));
                pneu.setValor(rs.getObject("VALOR") != null ? rs.getDouble("VALOR") : null);
                pneu.setFabricante(rs.getString("FABRICANTE"));
                pneu.setTipoPneu(rs.getString("TIPO_PNEU"));
                pneu.setModelo(rs.getString("MODELO"));
                pneu.setDot(rs.getString("DOT"));
                pneu.setMedida(rs.getString("MEDIDA"));
                pneu.setProfundidade(rs.getObject("PROFUNDIDADE") != null ? rs.getDouble("PROFUNDIDADE") : null);
                java.sql.Date sqlDate = rs.getDate("DATA_CADASTRO");
                if (sqlDate != null) {
                    pneu.setDataCadastro(sqlDate.toLocalDate());
                } else {
                    pneu.setDataCadastro(null);
                }
                pneu.setnRecapagens(rs.getInt("N_RECAPAGENS"));
                // Tratamento para PROJETADO_KM que pode ser NULL no DB
                Integer projetadoKmRs = rs.getInt("PROJETADO_KM");
                pneu.setProjetadoKm(rs.wasNull() ? null : projetadoKmRs);
                
                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setDataRetorno(rs.getDate("DATA_RETORNO")); // Pode ser null
                pneu.setStatusPneu(rs.getString("status_pneu"));

                // Tratamento para ID_VEICULO que pode ser NULL no DB
                Integer idVeiculoRs = rs.getInt("ID_VEICULO");
                pneu.setIdVeiculo(rs.wasNull() ? null : idVeiculoRs);
                pneu.setPosicaoNoVeiculo(rs.getString("POSICAO_NO_VEICULO")); // String já trata NULL automaticamente
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao buscar pneu por empresa/fogo: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */  }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
        return pneu;
    }

    public List<Pneu> buscarPneusPorFiltro(int idEmpresa, String fogoParcial) {
        List<Pneu> pneusEncontrados = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT ID, ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, TIPO_PNEU, "
                + "MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, "
                + "PROJETADO_KM, OBSERVACOES, DATA_RETORNO, status_pneu, ID_VEICULO, POSICAO_NO_VEICULO "
                + "FROM cad_pneus WHERE 1=1");
        List<Object> parametros = new ArrayList<>();

        if (idEmpresa > 0) {
            sqlBuilder.append(" AND ID_EMPRESA_PROPRIETARIA = ?");
            parametros.add(idEmpresa);
        }
        if (fogoParcial != null && !fogoParcial.trim().isEmpty()) {
            sqlBuilder.append(" AND FOGO LIKE ?");
            parametros.add("%" + fogoParcial.trim() + "%");
        }
        sqlBuilder.append(" ORDER BY ID_EMPRESA_PROPRIETARIA, FOGO");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Erro de Conexão com o Banco.", "Erro DAO", JOptionPane.ERROR_MESSAGE);
                return pneusEncontrados;
            }

            pstmt = conn.prepareStatement(sqlBuilder.toString());

            for (int i = 0; i < parametros.size(); i++) {
                pstmt.setObject(i + 1, parametros.get(i));
            }

            rs = pstmt.executeQuery();
            while (rs.next()) {
                Pneu pneu = new Pneu();
                pneu.setId(rs.getInt("ID"));
                pneu.setIdEmpresaProprietaria(rs.getInt("ID_EMPRESA_PROPRIETARIA"));
                pneu.setFogo(rs.getString("FOGO"));
                pneu.setFornecedor(rs.getString("FORNECEDOR"));
                pneu.setValor(rs.getObject("VALOR") != null ? rs.getDouble("VALOR") : null);
                pneu.setFabricante(rs.getString("FABRICANTE"));
                pneu.setTipoPneu(rs.getString("TIPO_PNEU"));
                pneu.setModelo(rs.getString("MODELO"));
                pneu.setDot(rs.getString("DOT"));
                pneu.setMedida(rs.getString("MEDIDA"));
                pneu.setProfundidade(rs.getObject("PROFUNDIDADE") != null ? rs.getDouble("PROFUNDIDADE") : null);
                java.sql.Date sqlDate = rs.getDate("DATA_CADASTRO");
                if (sqlDate != null) {
                    pneu.setDataCadastro(sqlDate.toLocalDate());
                } else {
                    pneu.setDataCadastro(null);
                }
                pneu.setnRecapagens(rs.getInt("N_RECAPAGENS"));
                // Tratamento para PROJETADO_KM que pode ser NULL no DB
                Integer projetadoKmRs = rs.getInt("PROJETADO_KM");
                pneu.setProjetadoKm(rs.wasNull() ? null : projetadoKmRs);
                
                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setDataRetorno(rs.getDate("DATA_RETORNO")); // Pode ser null
                pneu.setStatusPneu(rs.getString("status_pneu"));

                // Tratamento para ID_VEICULO que pode ser NULL no DB
                Integer idVeiculoRs = rs.getInt("ID_VEICULO");
                pneu.setIdVeiculo(rs.wasNull() ? null : idVeiculoRs);
                pneu.setPosicaoNoVeiculo(rs.getString("POSICAO_NO_VEICULO")); // String já trata NULL automaticamente
                
                pneusEncontrados.add(pneu);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao buscar pneus por filtro: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
        return pneusEncontrados;
    }

    public Pneu buscarPorId(int idPneu) {
        if (idPneu <= 0) {
            return null;
        }
        String sql = "SELECT ID, ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, TIPO_PNEU, "
                + "MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, "
                + "PROJETADO_KM, OBSERVACOES, DATA_RETORNO, status_pneu, ID_VEICULO, POSICAO_NO_VEICULO "
                + "FROM cad_pneus WHERE ID = ?";
        Pneu pneu = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                return null;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idPneu);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                pneu = new Pneu();
                pneu.setId(rs.getInt("ID"));
                pneu.setIdEmpresaProprietaria(rs.getInt("ID_EMPRESA_PROPRIETARIA"));
                pneu.setFogo(rs.getString("FOGO"));
                pneu.setFornecedor(rs.getString("FORNECEDOR"));
                pneu.setValor(rs.getObject("VALOR") != null ? rs.getDouble("VALOR") : null);
                pneu.setFabricante(rs.getString("FABRICANTE"));
                pneu.setTipoPneu(rs.getString("TIPO_PNEU"));
                pneu.setModelo(rs.getString("MODELO"));
                pneu.setDot(rs.getString("DOT"));
                pneu.setMedida(rs.getString("MEDIDA"));
                pneu.setProfundidade(rs.getObject("PROFUNDIDADE") != null ? rs.getDouble("PROFUNDIDADE") : null);
                java.sql.Date sqlDate = rs.getDate("DATA_CADASTRO");
                if (sqlDate != null) {
                    pneu.setDataCadastro(sqlDate.toLocalDate());
                } else {
                    pneu.setDataCadastro(null);
                }
                pneu.setnRecapagens(rs.getInt("N_RECAPAGENS"));
                // Tratamento para PROJETADO_KM que pode ser NULL no DB
                Integer projetadoKmRs = rs.getInt("PROJETADO_KM");
                pneu.setProjetadoKm(rs.wasNull() ? null : projetadoKmRs);
                
                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setDataRetorno(rs.getDate("DATA_RETORNO")); // Pode ser null
                pneu.setStatusPneu(rs.getString("status_pneu"));

                // Tratamento para ID_VEICULO que pode ser NULL no DB
                Integer idVeiculoRs = rs.getInt("ID_VEICULO");
                pneu.setIdVeiculo(rs.wasNull() ? null : idVeiculoRs);
                pneu.setPosicaoNoVeiculo(rs.getString("POSICAO_NO_VEICULO")); // String já trata NULL automaticamente
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao buscar Pneu por ID: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
        return pneu;
    }

    public boolean excluirPneu(int id) {
        if (id <= 0) {
            return false;
        }
        String sql = "DELETE FROM cad_pneus WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                return false;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas == 1;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao excluir pneu (ID: " + id + "): " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
    }

    public void moverParaPneuExcluido(Pneu pneu) {
        // Inclui ID_VEICULO e POSICAO_NO_VEICULO para registrar o estado antes da exclusão, se necessário
        String sqlInsert = "INSERT INTO pneus_excluidos (id, status, data_retorno, id_veiculo, posicao_no_veiculo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ModuloConexao.conector(); PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {

            stmt.setInt(1, pneu.getId());
            stmt.setString(2, pneu.getStatusPneu());
            if (pneu.getDataRetorno() != null) {
                stmt.setDate(3, new java.sql.Date(pneu.getDataRetorno().getTime()));
            } else {
                stmt.setNull(3, Types.DATE);
            }
            // Novas colunas para pneus_excluidos
            if (pneu.getIdVeiculo() != null) {
                stmt.setInt(4, pneu.getIdVeiculo());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setString(5, pneu.getPosicaoNoVeiculo()); // Pode ser null
            
            stmt.executeUpdate();

            // Ajuste o nome da tabela 'ordens_servico_pneu' se necessário
            String sqlDelete = "DELETE FROM ordens_servico_pneu WHERE id_pneu_fk = ?"; // Assumindo FK 'id_pneu_fk'
            try (PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete)) {
                stmtDelete.setInt(1, pneu.getId());
                stmtDelete.executeUpdate();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao mover pneu para excluídos: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public boolean atualizarStatusERetorno(int idPneu, String novoStatus, Date dataRetorno) {
        String sql = "UPDATE cad_pneus SET status_pneu = ?, data_retorno = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;

        if (idPneu <= 0 || novoStatus == null || novoStatus.trim().isEmpty()) {
            System.err.println("DAO (PneuDAO - atualizarStatusERetorno): ID ou Status inválido.");
            return false;
        }

        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                System.err.println("DAO (PneuDAO - atualizarStatusERetorno): Falha de conexão.");
                return false;
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, novoStatus.trim().toUpperCase());

            if (dataRetorno != null) {
                pstmt.setDate(2, new java.sql.Date(dataRetorno.getTime()));
            } else {
                pstmt.setNull(2, Types.DATE);
            }
            pstmt.setInt(3, idPneu);

            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas == 1;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao atualizar status e data de retorno do pneu (ID: " + idPneu + "): " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }
    
    public boolean existeOrdemServicoComOrcamento(String numOrcamento) {
        if (numOrcamento == null || numOrcamento.trim().isEmpty()) {
            return false;
        }
        String sql = "SELECT 1 FROM ordens_servico_pneu WHERE num_orcamento = ?"; 

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean existe = false;

        try {
            conn = ModuloConexao.conector(); 
            if (conn == null) {
                 System.err.println("DAO (existeOrdemServicoComOrcamento): Falha de conexão.");
                return true; 
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, numOrcamento.trim());
            rs = pstmt.executeQuery();

            existe = rs.next(); 

        } catch (SQLException e) {
            System.err.println("DAO (existeOrdemServicoComOrcamento): Erro SQL: " + e.getMessage());
            return true; 
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return existe;
    }

    public int obterUltimoFogoPorEmpresa(int idEmpresa) {
        String sql = "SELECT MAX(CAST(FOGO AS UNSIGNED)) FROM cad_pneus WHERE ID_EMPRESA_PROPRIETARIA = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int ultimoFogo = 0;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                System.err.println("DAO (obterUltimoFogo): Falha de conexão.");
                return 0;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idEmpresa);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                ultimoFogo = rs.getInt(1);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao obter último N° Fogo: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* Ignora */ }
            try { if (conn != null) conn.close(); } catch (SQLException e) { /* Ignora */ }
        }
        return ultimoFogo;
    }
}
package br.com.martins_borges.dal;

import br.com.martins_borges.model.Pneu;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

public class PneuDAO {

    public boolean inserirPneu(Pneu pneu) {
        String sql = "INSERT INTO cad_pneus (ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, TIPO_PNEU, "
                + "MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, "
                + "PROJETADO_KM, OBSERVACOES, status_pneu) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            pstmt.setString(15, "ESTOQUE");

            int adicionado = pstmt.executeUpdate();
            return adicionado > 0;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao inserir pneu: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            
            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
            }
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
                return true;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idEmpresa);
            pstmt.setString(2, fogoSequencial);
            rs = pstmt.executeQuery();
            existe = rs.next();
        } catch (SQLException e) {
            
            return true;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
            }
        }
        return existe;
    }

    public List<Pneu> listarTodosPneus() {
        String sql = "SELECT ID, ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, TIPO_PNEU, MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, PROJETADO_KM, OBSERVACOES, status_pneu FROM cad_pneus ORDER BY ID";
        List<Pneu> listaPneus = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                return listaPneus;
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
                java.sql.Date sqlDate = rs.getDate("DATA_CADASTRO");
                if (sqlDate != null) {
                    pneu.setDataCadastro(sqlDate.toLocalDate());
                } else {
                    pneu.setDataCadastro(null);
                }
                pneu.setnRecapagens(rs.getInt("N_RECAPAGENS"));
                pneu.setProjetadoKm(rs.getObject("PROJETADO_KM") != null ? rs.getInt("PROJETADO_KM") : null);
                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setStatusPneu(rs.getString("status_pneu"));
                listaPneus.add(pneu);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao listar pneus: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
            }
        }
        return listaPneus;
    }

    /**
     * Busca um pneu pelo seu ID
     * @param id ID do pneu a ser buscado
     * @return Objeto Pneu se encontrado, null caso contrário
     */
    public Pneu buscarPneuPorId(int id) {
        if (id <= 0) {
            return null;
        }
        
        String sql = "SELECT ID, ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, TIPO_PNEU, "
                + "MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, PROJETADO_KM, OBSERVACOES, "
                + "status_pneu FROM cad_pneus WHERE ID = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                return null;
            }
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
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
                pneu.setProjetadoKm(rs.getObject("PROJETADO_KM") != null ? rs.getInt("PROJETADO_KM") : null);
                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setStatusPneu(rs.getString("status_pneu"));
                
                return pneu;
            }
            
            return null;
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar pneu por ID: " + e.getMessage(), 
                    "Erro SQL", JOptionPane.ERROR_MESSAGE);
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                // Logar o erro se necessário
            }
        }
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
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public boolean atualizarPneu(Pneu pneu) {
        if (pneu == null || pneu.getId() <= 0) {
            return false;
        }

        if (existePneuDuplicadoParaUpdate(pneu.getIdEmpresaProprietaria(), pneu.getFogo(), pneu.getId())) {
            JOptionPane.showMessageDialog(null, "Erro: Já existe OUTRO pneu com o N° Fogo '" + pneu.getFogo() + "' para a empresa selecionada.", "Erro de Duplicidade", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String sql = "UPDATE cad_pneus SET "
                + "ID_EMPRESA_PROPRIETARIA = ?, FOGO = ?, FORNECEDOR = ?, VALOR = ?, "
                + "FABRICANTE = ?, TIPO_PNEU = ?, MODELO = ?, DOT = ?, MEDIDA = ?, "
                + "PROFUNDIDADE = ?, N_RECAPAGENS = ?, PROJETADO_KM = ?, OBSERVACOES = ?, status_pneu = ? "
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
            pstmt.setInt(15, pneu.getId());

            int linhasAfetadas = pstmt.executeUpdate();
            return linhasAfetadas == 1;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao atualizar pneu (ID: " + pneu.getId() + "): " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            
            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
            }
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
                return true;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idEmpresa);
            pstmt.setString(2, fogoSequencial);
            pstmt.setInt(3, idAtual);
            rs = pstmt.executeQuery();
            existe = rs.next();
        } catch (SQLException e) {
            
            return true;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
            }
        }
        return existe;
    }

    /**
     * Lista todos os pneus que estão em estoque.
     * @return Lista de pneus em estoque
     */
    public List<Pneu> listarPneusEmEstoque() {
        return listarPneusPorStatus("ESTOQUE");
    }
    
    public boolean atualizarStatusPneu(int idPneu, String novoStatus) {
        if (idPneu <= 0 || novoStatus == null || novoStatus.trim().isEmpty()) {
            return false;
        }
        String sql = "UPDATE cad_pneus SET status_pneu = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                return false;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, novoStatus.trim().toUpperCase());
            pstmt.setInt(2, idPneu);
            return pstmt.executeUpdate() == 1;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao atualizar status do pneu (ID: " + idPneu + "): " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            
            return false;
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public Pneu buscarPorEmpresaEFogo(int idEmpresa, String fogoSequencial) {
        String sql = "SELECT * FROM cad_pneus WHERE id_empresa_proprietaria = ? AND fogo = ?";
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
                }
                pneu.setnRecapagens(rs.getInt("N_RECAPAGENS"));
                pneu.setProjetadoKm(rs.getObject("PROJETADO_KM") != null ? rs.getInt("PROJETADO_KM") : null);
                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setStatusPneu(rs.getString("status_pneu"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao buscar pneu por empresa/fogo: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
            }
        }
        return pneu;
    }

    public List<Pneu> buscarPneusPorFiltro(int idEmpresa, String fogoParcial) {
        List<Pneu> pneusEncontrados = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM cad_pneus WHERE 1=1");
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
                }
                pneu.setnRecapagens(rs.getInt("N_RECAPAGENS"));
                pneu.setProjetadoKm(rs.getObject("PROJETADO_KM") != null ? rs.getInt("PROJETADO_KM") : null);
                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setStatusPneu(rs.getString("status_pneu"));
                pneusEncontrados.add(pneu);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao buscar pneus por filtro: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
            }
        }
        return pneusEncontrados;
    }

    public Pneu buscarPorId(int idPneu) {
        if (idPneu <= 0) {
            return null;
        }
        String sql = "SELECT * FROM cad_pneus WHERE ID = ?";
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
                }
                pneu.setnRecapagens(rs.getInt("N_RECAPAGENS"));
                pneu.setProjetadoKm(rs.getObject("PROJETADO_KM") != null ? rs.getInt("PROJETADO_KM") : null);
                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setStatusPneu(rs.getString("status_pneu"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao buscar Pneu por ID: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
            }
        }
        return pneu;
    }

    public void moverParaPneuExcluido(Pneu pneu) {
        String sqlInsert = "INSERT INTO pneus_excluidos (id, status, data_retorno) VALUES (?, ?, ?)";

        try (Connection conn = ModuloConexao.conector(); PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {

            stmt.setInt(1, pneu.getId());
            stmt.setString(2, pneu.getStatusPneu());
            stmt.setDate(3, new java.sql.Date(pneu.getDataRetorno().getTime()));

            stmt.executeUpdate();

            String sqlDelete = "DELETE FROM ordens_servico_pneu WHERE id = ?";
            try (PreparedStatement stmtDelete = conn.prepareStatement(sqlDelete)) {
                stmtDelete.setInt(1, pneu.getId());
                stmtDelete.executeUpdate();
            }
        } catch (SQLException e) {
            
        }
    }

    public boolean atualizarStatus(int idPneu, String novoStatus) {
        String sql = "UPDATE cad_pneus SET status_pneu = ? WHERE id = ?";

        try (Connection conexao = ModuloConexao.conector(); PreparedStatement pst = conexao.prepareStatement(sql)) {

            pst.setString(1, novoStatus);
            pst.setInt(2, idPneu);

            int rowsAffected = pst.executeUpdate();

            return rowsAffected > 0;

        } catch (SQLException e) {
            
            JOptionPane.showMessageDialog(null, "Erro ao atualizar status do pneu no banco de dados.", "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            return false;
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

    public List<Pneu> listarPneusPorStatus(String status) {
        String sql = "SELECT * FROM cad_pneus WHERE status_pneu = ? ORDER BY FOGO";
        List<Pneu> listaPneus = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                return listaPneus; // Retorna lista vazia
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
                pneu.setProjetadoKm(rs.getObject("PROJETADO_KM") != null ? rs.getInt("PROJETADO_KM") : null);
                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setStatusPneu(rs.getString("status_pneu"));
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
    
    // COLE ESTE MÉTODO DENTRO DA SUA CLASSE PneuDAO

public List<Pneu> listarPneusPorStatusEMedida(String status, String medida) {
    String sql = "SELECT * FROM cad_pneus WHERE status_pneu = ? AND medida = ? ORDER BY FOGO";
    List<Pneu> listaPneus = new ArrayList<>();
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    try {
        conn = ModuloConexao.conector();
        if (conn == null) {
            return listaPneus; // Retorna lista vazia
        }
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, status);
        pstmt.setString(2, medida);
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
            pneu.setProjetadoKm(rs.getObject("PROJETADO_KM") != null ? rs.getInt("PROJETADO_KM") : null);
            pneu.setObservacoes(rs.getString("OBSERVACOES"));
            pneu.setStatusPneu(rs.getString("status_pneu"));
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
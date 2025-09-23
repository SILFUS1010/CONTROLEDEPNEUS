package br.com.martins_borges.dal;

import br.com.martins_borges.model.Pneu;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;

public class PneuDAO {

    public PneuDAO() {

    }

    public List<Pneu> buscarPneusPorFogoEFilial(String fogoParcial, int idEmpresa) {
        String sql = "SELECT ID, ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, "
                + "TIPO_PNEU, MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, "
                + "PROJETADO_KM, OBSERVACOES, DATA_RETORNO, status_pneu, ID_VEICULO, POSICAO_NO_VEICULO "
                + "FROM cad_pneus WHERE 1=1";

        if (idEmpresa > 0) {
            sql += " AND ID_EMPRESA_PROPRIETARIA = ?";
        }
        if (fogoParcial != null && !fogoParcial.isEmpty()) {
            sql += " AND UPPER(FOGO) LIKE ?";
        }
        sql += " ORDER BY FOGO";

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

            int paramIndex = 1;
            if (idEmpresa > 0) {
                pstmt.setInt(paramIndex++, idEmpresa);
            }
            if (fogoParcial != null && !fogoParcial.isEmpty()) {
                pstmt.setString(paramIndex, "%" + fogoParcial.toUpperCase() + "%");
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Pneu pneu = new Pneu();
                pneu.setId(rs.getInt("ID"));
                pneu.setIdEmpresaProprietaria(rs.getInt("ID_EMPRESA_PROPRIETARIA"));
                pneu.setFogo(rs.getString("FOGO"));
                pneu.setTipoPneu(rs.getString("TIPO_PNEU"));
                pneu.setStatusPneu(rs.getString("status_pneu"));

                java.sql.Date dataCadastro = rs.getDate("DATA_CADASTRO");
                if (dataCadastro != null) {
                    pneu.setDataCadastro(dataCadastro.toLocalDate());
                }

                pneu.setFornecedor(rs.getString("FORNECEDOR"));
                pneu.setValor(rs.getObject("VALOR") != null ? rs.getDouble("VALOR") : null);
                pneu.setFabricante(rs.getString("FABRICANTE"));
                pneu.setModelo(rs.getString("MODELO"));
                pneu.setDot(rs.getString("DOT"));
                pneu.setMedida(rs.getString("MEDIDA"));

                Object profundidadeObj = rs.getObject("PROFUNDIDADE");
                if (profundidadeObj != null) {
                    if (profundidadeObj instanceof Number) {
                        pneu.setProfundidade(((Number) profundidadeObj).doubleValue());
                    }
                } else {
                    pneu.setProfundidade(null);
                }

                pneu.setnRecapagens(rs.getObject("N_RECAPAGENS") != null ? rs.getInt("N_RECAPAGENS") : 0);
                pneu.setProjetadoKm(rs.getObject("PROJETADO_KM") != null ? rs.getInt("PROJETADO_KM") : null);
                pneu.setObservacoes(rs.getString("OBSERVACOES"));

                java.sql.Date dataRetornoSql = rs.getDate("DATA_RETORNO");
                if (dataRetornoSql != null) {
                    pneu.setDataRetorno(new java.util.Date(dataRetornoSql.getTime()));
                } else {
                    pneu.setDataRetorno(null);
                }

                pneu.setIdVeiculo(rs.getObject("ID_VEICULO") != null ? rs.getInt("ID_VEICULO") : null);
                pneu.setPosicaoNoVeiculo(rs.getString("POSICAO_NO_VEICULO"));

                pneus.add(pneu);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar pneus: " + e.getMessage(), "Erro de Consulta", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
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

        return pneus;
    }

    public List<Pneu> listarTodosPneus() {

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

                Integer projetadoKmRs = rs.getInt("PROJETADO_KM");
                pneu.setProjetadoKm(rs.wasNull() ? null : projetadoKmRs);

                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setDataRetorno(rs.getDate("DATA_RETORNO"));
                pneu.setStatusPneu(rs.getString("status_pneu"));

                Integer idVeiculoRs = rs.getInt("ID_VEICULO");
                pneu.setIdVeiculo(rs.wasNull() ? null : idVeiculoRs);
                pneu.setPosicaoNoVeiculo(rs.getString("POSICAO_NO_VEICULO"));

                pneus.add(pneu);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao listar todos os pneus: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
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

        return pneus;
    }

    public Pneu buscarPneuPorId(int id) {

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
            JOptionPane.showMessageDialog(null, "Erro ao buscar pneu por ID: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
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

    public boolean inserirPneu(Pneu pneu) {

        String sql = "INSERT INTO cad_pneus (ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, TIPO_PNEU, "
                + "MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, "
                + "PROJETADO_KM, OBSERVACOES, status_pneu, DATA_RETORNO, ID_VEICULO, POSICAO_NO_VEICULO) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

            if (pneu.getDataRetorno() != null) {
                pstmt.setDate(16, new java.sql.Date(pneu.getDataRetorno().getTime()));
            } else {
                pstmt.setNull(16, Types.DATE);
            }

            pstmt.setNull(17, Types.INTEGER);
            pstmt.setNull(18, Types.VARCHAR);

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

    public Pneu buscarPneuPorFogo(String fogo) {

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
            JOptionPane.showMessageDialog(null, "Erro DAO ao buscar pneu por N° Fogo: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
        }
        return pneu;
    }

    public List<Pneu> listarPneusPorNumeroFogo(String numeroFogo) {
        List<Pneu> listaPneus = new ArrayList<>();
        String sql = "SELECT ID, ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, TIPO_PNEU, "
                + "MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, "
                + "PROJETADO_KM, OBSERVACOES, DATA_RETORNO, status_pneu, ID_VEICULO, POSICAO_NO_VEICULO "
                + "FROM cad_pneus";

        if (numeroFogo != null && !numeroFogo.trim().isEmpty()) {
            sql += " WHERE FOGO LIKE ?";
        }
        sql += " ORDER BY FOGO";

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

            if (numeroFogo != null && !numeroFogo.trim().isEmpty()) {
                pstmt.setString(1, "%" + numeroFogo.trim() + "%");
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

                java.sql.Date sqlDateCadastro = rs.getDate("DATA_CADASTRO");
                if (sqlDateCadastro != null) {
                    pneu.setDataCadastro(sqlDateCadastro.toLocalDate());
                } else {
                    pneu.setDataCadastro(null);
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

                listaPneus.add(pneu);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao listar pneus por número de fogo: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
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

    public List<Pneu> listarPneusPorStatusEMedida(String status, String medida) {
        List<Pneu> listaPneus = new ArrayList<>();

        String sql = "SELECT ID, ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, TIPO_PNEU, "
                + "MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, "
                + "PROJETADO_KM, OBSERVACOES, DATA_RETORNO, status_pneu, ID_VEICULO, POSICAO_NO_VEICULO "
                + "FROM cad_pneus WHERE status_pneu = ?";
        if (medida != null && !medida.trim().isEmpty()) {
            sql += " AND MEDIDA = ?";
        }
        sql += " ORDER BY FOGO";

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

                Integer projetadoKmRs = rs.getInt("PROJETADO_KM");
                pneu.setProjetadoKm(rs.wasNull() ? null : projetadoKmRs);

                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setDataRetorno(rs.getDate("DATA_RETORNO"));
                pneu.setStatusPneu(rs.getString("status_pneu"));

                Integer idVeiculoRs = rs.getInt("ID_VEICULO");
                pneu.setIdVeiculo(rs.wasNull() ? null : idVeiculoRs);
                pneu.setPosicaoNoVeiculo(rs.getString("POSICAO_NO_VEICULO"));

                listaPneus.add(pneu);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao listar pneus por status e medida: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
        }
        return listaPneus;
    }

    public List<Pneu> listarPneusPorStatusMedidaEEmpresa(String status, String medida, int idEmpresa) {
        List<Pneu> listaPneus = new ArrayList<>();
        String sql = "SELECT ID, ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, TIPO_PNEU, "
                + "MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, "
                + "PROJETADO_KM, OBSERVACOES, DATA_RETORNO, status_pneu, ID_VEICULO, POSICAO_NO_VEICULO "
                + "FROM cad_pneus WHERE status_pneu = ? AND MEDIDA = ? AND ID_EMPRESA_PROPRIETARIA = ? ORDER BY FOGO";

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
            pstmt.setString(2, medida);
            pstmt.setInt(3, idEmpresa);
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
                Integer projetadoKmRs = rs.getInt("PROJETADO_KM");
                pneu.setProjetadoKm(rs.wasNull() ? null : projetadoKmRs);
                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setDataRetorno(rs.getDate("DATA_RETORNO"));
                pneu.setStatusPneu(rs.getString("status_pneu"));
                Integer idVeiculoRs = rs.getInt("ID_VEICULO");
                pneu.setIdVeiculo(rs.wasNull() ? null : idVeiculoRs);
                pneu.setPosicaoNoVeiculo(rs.getString("POSICAO_NO_VEICULO"));
                listaPneus.add(pneu);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao listar pneus por status, medida e empresa: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
        }
        return listaPneus;
    }

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

                Integer projetadoKmRs = rs.getInt("PROJETADO_KM");
                pneu.setProjetadoKm(rs.wasNull() ? null : projetadoKmRs);

                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setDataRetorno(rs.getDate("DATA_RETORNO"));
                pneu.setStatusPneu(rs.getString("status_pneu"));

                Integer idVeiculoRs = rs.getInt("ID_VEICULO");
                pneu.setIdVeiculo(rs.wasNull() ? null : idVeiculoRs);
                pneu.setPosicaoNoVeiculo(rs.getString("POSICAO_NO_VEICULO"));

                listaPneus.add(pneu);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao listar pneus por status: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
        }
        return listaPneus;
    }

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

                Integer projetadoKmRs = rs.getInt("PROJETADO_KM");
                pneu.setProjetadoKm(rs.wasNull() ? null : projetadoKmRs);

                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setDataRetorno(rs.getDate("DATA_RETORNO"));
                pneu.setStatusPneu(rs.getString("status_pneu"));

                pneu.setIdVeiculo(rs.getInt("ID_VEICULO"));
                pneu.setPosicaoNoVeiculo(rs.getString("POSICAO_NO_VEICULO"));

                pneus.add(pneu);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao listar pneus no veículo: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
        }
        return pneus;
    }

    public boolean atualizarLocalizacaoPneu(int pneuId, int veiculoId, String posicaoNoVeiculo, String status) {
        String sql = "UPDATE cad_pneus SET ID_VEICULO = ?, POSICAO_NO_VEICULO = ?, status_pneu = ? WHERE ID = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean closeConnection = false;

        try {

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
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
            }

            try {
                if (conn != null && closeConnection) {
                    conn.close();
                }
            } catch (SQLException e) {
            }
        }
    }

    public boolean removerPneuDoVeiculo(int pneuId, String novoStatus) {
        String sql = "UPDATE cad_pneus SET ID_VEICULO = NULL, POSICAO_NO_VEICULO = NULL, status_pneu = ? WHERE ID = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean closeConnection = false;

        try {

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
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
            }

            try {
                if (conn != null && closeConnection) {
                    conn.close();
                }
            } catch (SQLException e) {
            }
        }
    }

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
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
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
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
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
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
        }
        return existe;
    }

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
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
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
            JOptionPane.showMessageDialog(null, "Erro DAO ao buscar pneu por empresa/fogo: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                /* Ignora */            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
        }
        return pneu;
    }

    public List<Pneu> buscarPneusPorFiltro(int idEmpresa, String fogoParcial, int idVeiculoFk) {
        List<Pneu> pneusEncontrados = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT ID, ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, TIPO_PNEU, "
                + "MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, "
                + "PROJETADO_KM, OBSERVACOES, DATA_RETORNO, status_pneu, ID_VEICULO, POSICAO_NO_VEICULO "
                + "FROM cad_pneus WHERE status_pneu IN ('EM_USO', 'ESTOQUE', 'AG. CONSERTO', 'EM_SERVICO')");

        List<Object> parametros = new ArrayList<>();

        if (idEmpresa > 0) {
            sqlBuilder.append(" AND ID_EMPRESA_PROPRIETARIA = ?");
            parametros.add(idEmpresa);
        }
        if (fogoParcial != null && !fogoParcial.trim().isEmpty()) {
            sqlBuilder.append(" AND FOGO LIKE ?");
            parametros.add("%" + fogoParcial.trim() + "%");
        }

        if (idVeiculoFk > 0) {
            sqlBuilder.append(" AND ID_VEICULO = ?");
            parametros.add(idVeiculoFk);
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
                Integer projetadoKmRs = rs.getInt("PROJETADO_KM");
                pneu.setProjetadoKm(rs.wasNull() ? null : projetadoKmRs);
                pneu.setObservacoes(rs.getString("OBSERVACOES"));
                pneu.setDataRetorno(rs.getDate("DATA_RETORNO"));
                pneu.setStatusPneu(rs.getString("status_pneu"));
                Integer idVeiculoRs = rs.getInt("ID_VEICULO");
                pneu.setIdVeiculo(rs.wasNull() ? null : idVeiculoRs);
                pneu.setPosicaoNoVeiculo(rs.getString("POSICAO_NO_VEICULO"));

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
            JOptionPane.showMessageDialog(null, "Erro DAO ao buscar Pneu por ID: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
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
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
        }
    }

    public void moverParaPneuExcluido(Pneu pneu) {

        String sqlInsert = "INSERT INTO pneus_excluidos (id, status, data_retorno, id_veiculo, posicao_no_veiculo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ModuloConexao.conector(); PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {

            stmt.setInt(1, pneu.getId());
            stmt.setString(2, pneu.getStatusPneu());
            if (pneu.getDataRetorno() != null) {
                stmt.setDate(3, new java.sql.Date(pneu.getDataRetorno().getTime()));
            } else {
                stmt.setNull(3, Types.DATE);
            }

            if (pneu.getIdVeiculo() != null) {
                stmt.setInt(4, pneu.getIdVeiculo());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setString(5, pneu.getPosicaoNoVeiculo());

            stmt.executeUpdate();

            String sqlDelete = "DELETE FROM ordens_servico_pneu WHERE id_pneu_fk = ?";
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
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                /* Ignora */ }
        }
        return ultimoFogo;
    }

    public Pneu buscarUltimoPneuCadastrado() {
        String sql = "SELECT * FROM cad_pneus ORDER BY ID DESC LIMIT 1";
        Pneu pneu = null;
        try (Connection conn = ModuloConexao.conector(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

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
            JOptionPane.showMessageDialog(null, "Erro DAO ao buscar último pneu cadastrado: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
        }
        return pneu;
    }

    public List<Pneu> buscarPorEmpresasEFogo(List<Integer> idsEmpresas, String fogoParcial) {
        List<Pneu> pneusEncontrados = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT ID, ID_EMPRESA_PROPRIETARIA, FOGO, FORNECEDOR, VALOR, FABRICANTE, TIPO_PNEU, ")
                .append("MODELO, DOT, MEDIDA, PROFUNDIDADE, DATA_CADASTRO, N_RECAPAGENS, ")
                .append("PROJETADO_KM, OBSERVACOES, DATA_RETORNO, status_pneu, ID_VEICULO, POSICAO_NO_VEICULO ")
                .append("FROM cad_pneus WHERE 1=1");

        List<Object> parametros = new ArrayList<>();

        if (idsEmpresas != null && !idsEmpresas.isEmpty()) {
            sqlBuilder.append(" AND ID_EMPRESA_PROPRIETARIA IN (");
            for (int i = 0; i < idsEmpresas.size(); i++) {
                sqlBuilder.append("?");
                if (i < idsEmpresas.size() - 1) {
                    sqlBuilder.append(",");
                }
            }
            sqlBuilder.append(")");
            parametros.addAll(idsEmpresas);
        }

        if (fogoParcial != null && !fogoParcial.trim().isEmpty()) {
            sqlBuilder.append(" AND FOGO LIKE ?");
            parametros.add("%" + fogoParcial.trim() + "%");
        }

        sqlBuilder.append(" ORDER BY ID_EMPRESA_PROPRIETARIA, FOGO");

        try (Connection conn = ModuloConexao.conector(); PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {

            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Erro de Conexão com o Banco.", "Erro DAO", JOptionPane.ERROR_MESSAGE);
                return pneusEncontrados;
            }

            for (int i = 0; i < parametros.size(); i++) {
                pstmt.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
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
                    Integer projetadoKmRs = rs.getInt("PROJETADO_KM");
                    pneu.setProjetadoKm(rs.wasNull() ? null : projetadoKmRs);
                    pneu.setObservacoes(rs.getString("OBSERVACOES"));
                    pneu.setDataRetorno(rs.getDate("DATA_RETORNO"));
                    pneu.setStatusPneu(rs.getString("status_pneu"));
                    Integer idVeiculoRs = rs.getInt("ID_VEICULO");
                    pneu.setIdVeiculo(rs.wasNull() ? null : idVeiculoRs);
                    pneu.setPosicaoNoVeiculo(rs.getString("POSICAO_NO_VEICULO"));
                    pneusEncontrados.add(pneu);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao buscar pneus por múltiplos filtros: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return pneusEncontrados;
    }
}

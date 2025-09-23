package br.com.martins_borges.dal;

import br.com.martins_borges.model.Pneu;
import br.com.martins_borges.model.OrdemServicoPneu;
import br.com.martins_borges.model.TipoServico;
import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class OrdemServicoPneuDAO {

    public boolean inserirOrdemServico(OrdemServicoPneu osPneu) {
        String sql = "INSERT INTO ordens_servico_pneu (id_pneu_fk, num_orcamento, data_envio, "
                + "id_parceiro_fk, id_tipo_servico_fk, valor_servico, id_motivo_fk, "
                + "data_retorno, observacoes_servico, usuario_registro_servico) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Erro de Conexão ao salvar Ordem de Serviço.", "Erro DAO", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, osPneu.getIdPneuFk());
            pstmt.setString(2, osPneu.getNumOrcamento());
            if (osPneu.getDataEnvio() != null) {
                pstmt.setTimestamp(3, Timestamp.valueOf(osPneu.getDataEnvio()));
            } else {

                pstmt.setNull(3, Types.TIMESTAMP);
            }
            if (osPneu.getIdParceiroFk() != null) {
                pstmt.setInt(4, osPneu.getIdParceiroFk());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }
            pstmt.setInt(5, osPneu.getIdTipoServicoFk());
            if (osPneu.getValorServico() != null) {
                pstmt.setDouble(6, osPneu.getValorServico());
            } else {
                pstmt.setNull(6, Types.DECIMAL);
            }
            if (osPneu.getIdMotivoFk() != null) {
                pstmt.setInt(7, osPneu.getIdMotivoFk());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            if (osPneu.getDataRetorno() != null) {
                pstmt.setTimestamp(8, Timestamp.valueOf(osPneu.getDataRetorno()));
            } else {
                pstmt.setNull(8, Types.TIMESTAMP);
            }
            pstmt.setString(9, osPneu.getObservacoesServico());
            pstmt.setString(10, osPneu.getUsuarioRegistroServico());

            int adicionado = pstmt.executeUpdate();

            if (adicionado > 0) {
                PneuDAO pneuDAO = new PneuDAO();
                TipoServicoDAO tsDAO = new TipoServicoDAO();
                String novoStatusPneu = (osPneu.getDataRetorno() == null) ? "EM_SERVICO" : "ESTOQUE";

                if (osPneu.getIdTipoServicoFk() != 0) {
                    TipoServico ts = tsDAO.buscarPorId(osPneu.getIdTipoServicoFk());
                    if (ts != null && "DESCARTE".equalsIgnoreCase(ts.getNomeTipoServico())) {
                        novoStatusPneu = "DESCARTADO";
                    }
                }

                boolean statusAtualizado = pneuDAO.atualizarStatusPneu(osPneu.getIdPneuFk(), novoStatusPneu);
                if (!statusAtualizado) {
                    System.err.println("DAO (OrdemServicoPneuDAO): Falha ao atualizar status do pneu ID " + osPneu.getIdPneuFk());

                }
            }
            return adicionado > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao inserir Ordem de Serviço: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            
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

    public Pneu buscarPneuParaServico(int idEmpresa, String fogoSequencial) {
        PneuDAO pneuDAO = new PneuDAO();

        return pneuDAO.buscarPorEmpresaEFogo(idEmpresa, fogoSequencial);
    }

    public List<OrdemServicoPneu> listarOrdensPorEmpresaDoPneu(int idEmpresa) {
        List<OrdemServicoPneu> listaOS = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT os.* FROM ordens_servico_pneu os "
                + "INNER JOIN cad_pneus cp ON os.id_pneu_fk = cp.id "
        );

        if (idEmpresa > 0) {
            sqlBuilder.append("WHERE cp.id_empresa_proprietaria = ? ");
        }
        sqlBuilder.append("ORDER BY os.data_envio DESC, os.id_servico DESC");

        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                System.err.println("DAO (listarOrdensPorEmpresa): Falha de conexão.");
                return listaOS;
            }

            pstmt = conn.prepareStatement(sqlBuilder.toString());

            if (idEmpresa > 0) {
                pstmt.setInt(1, idEmpresa);
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                OrdemServicoPneu os = new OrdemServicoPneu();
                os.setIdServico(rs.getInt("id_servico"));
                os.setIdPneuFk(rs.getInt("id_pneu_fk"));
                os.setNumOrcamento(rs.getString("num_orcamento"));

                Timestamp dataEnvioTS = rs.getTimestamp("data_envio");
                if (dataEnvioTS != null) {
                    os.setDataEnvio(dataEnvioTS.toLocalDateTime());
                }

                os.setIdParceiroFk(rs.getObject("id_parceiro_fk") != null ? rs.getInt("id_parceiro_fk") : null);
                os.setIdTipoServicoFk(rs.getInt("id_tipo_servico_fk"));
                os.setValorServico(rs.getObject("valor_servico") != null ? rs.getDouble("valor_servico") : null);
                os.setIdMotivoFk(rs.getObject("id_motivo_fk") != null ? rs.getInt("id_motivo_fk") : null);

                Timestamp dataRetornoTS = rs.getTimestamp("data_retorno");
                if (dataRetornoTS != null) {
                    os.setDataRetorno(dataRetornoTS.toLocalDateTime());
                }

                os.setObservacoesServico(rs.getString("observacoes_servico"));
                os.setUsuarioRegistroServico(rs.getString("usuario_registro_servico"));

                Timestamp timestampRegTS = rs.getTimestamp("timestamp_registro_servico");
                if (timestampRegTS != null) {
                    os.setTimestampRegistroServico(timestampRegTS.toLocalDateTime());
                }

                listaOS.add(os);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao listar ordens de serviço por empresa: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            
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
        return listaOS;
    }

    public List<OrdemServicoPneu> buscarOrdensServicoFiltradas(int idEmpresa, String fogoPneuParcial) {
        List<OrdemServicoPneu> listaOS = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT os.* "
                + "FROM ordens_servico_pneu os "
                + "INNER JOIN cad_pneus cp ON os.id_pneu_fk = cp.id "
                + "WHERE 1=1"
        );
        List<Object> parametros = new ArrayList<>();

        if (idEmpresa > 0) {
            sqlBuilder.append(" AND cp.ID_EMPRESA_PROPRIETARIA = ?");
            parametros.add(idEmpresa);
        }
        if (fogoPneuParcial != null && !fogoPneuParcial.trim().isEmpty()) {
            sqlBuilder.append(" AND UPPER(cp.FOGO) LIKE UPPER(?)");
            parametros.add("%" + fogoPneuParcial.trim() + "%");
        }
        sqlBuilder.append(" ORDER BY os.data_envio DESC, os.id_servico DESC");

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                System.err.println("DAO (OS - buscarFiltradas): Falha de conexão.");
                return listaOS;
            }

            pstmt = conn.prepareStatement(sqlBuilder.toString());

            for (int i = 0; i < parametros.size(); i++) {
                pstmt.setObject(i + 1, parametros.get(i));
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                OrdemServicoPneu os = new OrdemServicoPneu();
                os.setIdServico(rs.getInt("id_servico"));
                os.setIdPneuFk(rs.getInt("id_pneu_fk"));
                os.setNumOrcamento(rs.getString("num_orcamento"));

                Timestamp dataEnvioTS = rs.getTimestamp("data_envio");
                if (dataEnvioTS != null) {
                    os.setDataEnvio(dataEnvioTS.toLocalDateTime());
                }

                os.setIdParceiroFk(rs.getObject("id_parceiro_fk") != null ? rs.getInt("id_parceiro_fk") : null);
                os.setIdTipoServicoFk(rs.getInt("id_tipo_servico_fk"));
                os.setValorServico(rs.getObject("valor_servico") != null ? rs.getDouble("valor_servico") : null);
                os.setIdMotivoFk(rs.getObject("id_motivo_fk") != null ? rs.getInt("id_motivo_fk") : null);

                Timestamp dataRetornoTS = rs.getTimestamp("data_retorno");
                if (dataRetornoTS != null) {
                    os.setDataRetorno(dataRetornoTS.toLocalDateTime());
                }

                os.setObservacoesServico(rs.getString("observacoes_servico"));
                os.setUsuarioRegistroServico(rs.getString("usuario_registro_servico"));

                listaOS.add(os);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro DAO ao buscar Ordens de Serviço filtradas: " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            
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
        return listaOS;
    }

    public List<OrdemServicoPneu> listarOrdensPorPneu(int idPneu) {
        List<OrdemServicoPneu> listaOS = new ArrayList<>();

        String sql = "SELECT os.* FROM ordens_servico_pneu os WHERE os.id_pneu_fk = ? ORDER BY os.data_envio DESC, os.id_servico DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        if (idPneu <= 0) {
            System.err.println("DAO (listarOrdensPorPneu): ID do pneu inválido.");
            return listaOS;
        }

        try {
            conn = br.com.martins_borges.dal.ModuloConexao.conector();
            if (conn == null) {
                System.err.println("DAO (listarOrdensPorPneu): Falha de conexão.");
                return listaOS;
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idPneu);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                OrdemServicoPneu os = new OrdemServicoPneu();
                os.setIdServico(rs.getInt("id_servico"));
                os.setIdPneuFk(rs.getInt("id_pneu_fk"));
                os.setNumOrcamento(rs.getString("num_orcamento"));

                Timestamp dataEnvioTS = rs.getTimestamp("data_envio");
                if (dataEnvioTS != null) {
                    os.setDataEnvio(dataEnvioTS.toLocalDateTime());
                }

                os.setIdParceiroFk(rs.getObject("id_parceiro_fk") != null ? rs.getInt("id_parceiro_fk") : null);
                os.setIdTipoServicoFk(rs.getInt("id_tipo_servico_fk"));
                os.setValorServico(rs.getObject("valor_servico") != null ? rs.getDouble("valor_servico") : null);
                os.setIdMotivoFk(rs.getObject("id_motivo_fk") != null ? rs.getInt("id_motivo_fk") : null);

                Timestamp dataRetornoTS = rs.getTimestamp("data_retorno");
                if (dataRetornoTS != null) {
                    os.setDataRetorno(dataRetornoTS.toLocalDateTime());
                }

                os.setObservacoesServico(rs.getString("observacoes_servico"));
                os.setUsuarioRegistroServico(rs.getString("usuario_registro_servico"));

                Timestamp timestampRegTS = rs.getTimestamp("timestamp_registro_servico");
                if (timestampRegTS != null) {
                    os.setTimestampRegistroServico(timestampRegTS.toLocalDateTime());
                }

                listaOS.add(os);
            }
        } catch (SQLException e) {

            System.err.println("Erro DAO ao listar ordens de serviço por pneu (ID: " + idPneu + "): " + e.getMessage());
            
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
        return listaOS;
    }

    public OrdemServicoPneu buscarUltimaOSAbertaPorPneu(int idPneu) {
        OrdemServicoPneu ultimaOS = null;
        String sql = "SELECT os.* FROM ordens_servico_pneu os WHERE os.id_pneu_fk = ? AND os.data_retorno IS NULL ORDER BY os.data_envio DESC LIMIT 1";

        try (Connection conn = ModuloConexao.conector(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idPneu);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                ultimaOS = new OrdemServicoPneu();

                ultimaOS.setIdServico(rs.getInt("id_servico"));
                ultimaOS.setIdPneuFk(rs.getInt("id_pneu_fk"));
                ultimaOS.setNumOrcamento(rs.getString("num_orcamento"));
                ultimaOS.setDataEnvio(rs.getTimestamp("data_envio").toLocalDateTime());
                ultimaOS.setIdParceiroFk(rs.getInt("id_parceiro_fk"));
                ultimaOS.setIdTipoServicoFk(rs.getInt("id_tipo_servico_fk"));
                ultimaOS.setValorServico(rs.getDouble("valor_servico"));
                ultimaOS.setObservacoesServico(rs.getString("observacoes_servico"));

            }
        } catch (SQLException e) {
            
        }
        return ultimaOS;
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

    public int getProximoNumeroOrcamento() {
        String sql = "SELECT MAX(CAST(num_orcamento AS SIGNED)) FROM ordens_servico_pneu";
        int proximoNumero = 1;

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                System.err.println("DAO (getProximoNumeroOrcamento): Falha de conexão.");
                return 0;
            }

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                Number maxNumber = (Number) rs.getObject(1);
                Integer maxNumeroAtual = null;
                if (maxNumber != null) {
                   
                     maxNumeroAtual = maxNumber.intValue();
                }
              

                if (maxNumeroAtual != null) {
                    proximoNumero = maxNumeroAtual + 1;
                }
               
            }

        } catch (SQLException e) {
            
            System.err.println("DAO (getProximoNumeroOrcamento): Erro SQL ao buscar próximo número de orçamento: " + e.getMessage());
             return 0;
        } catch (NumberFormatException e) {
            
            System.err.println("DAO (getProximoNumeroOrcamento): Erro ao converter num_orcamento para número. Verifique se a coluna só contém números válidos.");
             return 0;
        } finally {
          
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return proximoNumero;
    }
    
    public boolean atualizarOrdemServico(OrdemServicoPneu os) {
        String sql = "UPDATE ordens_servico_pneu SET "
                   + "id_pneu_fk = ?, num_orcamento = ?, data_envio = ?, "
                   + "id_parceiro_fk = ?, id_tipo_servico_fk = ?, valor_servico = ?, "
                   + "id_motivo_fk = ?, data_retorno = ?, observacoes_servico = ?, "
                   + "usuario_registro_servico = ? " 
                   + "WHERE id_servico = ?"; 

        Connection conn = null;
        PreparedStatement pstmt = null;
        if (os == null || os.getIdServico() <= 0) {
            System.err.println("DAO (atualizarOrdemServico): Objeto OrdemServicoPneu ou ID da OS inválido.");
            return false;
        }

        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                System.err.println("DAO (atualizarOrdemServico): Falha de conexão com o banco.");
                return false;
            }

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, os.getIdPneuFk());
            pstmt.setString(2, os.getNumOrcamento());

            if (os.getDataEnvio() != null) {
                pstmt.setTimestamp(3, Timestamp.valueOf(os.getDataEnvio()));
            } else {
                pstmt.setNull(3, Types.TIMESTAMP);
            }

            if (os.getIdParceiroFk() != null) {
                pstmt.setInt(4, os.getIdParceiroFk());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setInt(5, os.getIdTipoServicoFk());

            if (os.getValorServico() != null) {
                pstmt.setDouble(6, os.getValorServico());
            } else {
                pstmt.setNull(6, Types.DECIMAL);
            }

            if (os.getIdMotivoFk() != null) {
                pstmt.setInt(7, os.getIdMotivoFk());
            } else {
                pstmt.setNull(7, Types.INTEGER);
            }
            if (os.getDataRetorno() != null) {
                pstmt.setTimestamp(8, Timestamp.valueOf(os.getDataRetorno()));
            } else {
                pstmt.setNull(8, Types.TIMESTAMP); 
            }

            pstmt.setString(9, os.getObservacoesServico());
            pstmt.setString(10, os.getUsuarioRegistroServico());
            pstmt.setInt(11, os.getIdServico());

            int linhasAfetadas = pstmt.executeUpdate();

           
            return linhasAfetadas == 1;

        } catch (SQLException e) {
            
            JOptionPane.showMessageDialog(null, "Erro DAO ao atualizar Ordem de Serviço (ID: " + os.getIdServico() + "): " + e.getMessage(), "Erro SQL", JOptionPane.ERROR_MESSAGE);
            return false;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    
  public List<OrdemServicoPneu> listarOsComPneusEmServicoOuDescartados() {
    List<OrdemServicoPneu> listaOS = new ArrayList<>();
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
        conn = ModuloConexao.conector();
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Erro no banco ao listar OSs: Falha na conexão.", "Erro Conexão", JOptionPane.ERROR_MESSAGE);
            return listaOS;
        }

        String sql = "SELECT os.*, cp.status_pneu "
                   + "FROM ordens_servico_pneu os "
                   + "INNER JOIN cad_pneus cp ON os.id_pneu_fk = cp.id "
                   + "WHERE os.id_servico IN ( "
                   + "    SELECT MAX(os_inner.id_servico) "
                   + "    FROM ordens_servico_pneu os_inner "
                   + "    JOIN cad_pneus cp_inner ON os_inner.id_pneu_fk = cp_inner.id "
                   + "    WHERE cp_inner.status_pneu IN ('EM_SERVICO', 'DESCARTADO') "
                   + "    GROUP BY os_inner.id_pneu_fk "
                   + ") "
                   + "ORDER BY os.data_envio ASC, os.id_servico ASC";

        pstmt = conn.prepareStatement(sql);
        rs = pstmt.executeQuery();

        while (rs.next()) {
            OrdemServicoPneu os = new OrdemServicoPneu();
            os.setIdServico(rs.getInt("id_servico"));
            os.setIdPneuFk(rs.getInt("id_pneu_fk"));
            os.setNumOrcamento(rs.getString("num_orcamento"));
            Timestamp dataEnvioTS = rs.getTimestamp("data_envio");
            if (dataEnvioTS != null) os.setDataEnvio(dataEnvioTS.toLocalDateTime());
            os.setIdParceiroFk(rs.getObject("id_parceiro_fk") != null ? rs.getInt("id_parceiro_fk") : null);
            os.setIdTipoServicoFk(rs.getInt("id_tipo_servico_fk"));
            os.setValorServico(rs.getObject("valor_servico") != null ? rs.getDouble("valor_servico") : null);
            os.setIdMotivoFk(rs.getObject("id_motivo_fk") != null ? rs.getInt("id_motivo_fk") : null);
            Timestamp dataRetornoTS = rs.getTimestamp("data_retorno");
            if (dataRetornoTS != null) os.setDataRetorno(dataRetornoTS.toLocalDateTime());
            os.setObservacoesServico(rs.getString("observacoes_servico"));
            os.setUsuarioRegistroServico(rs.getString("usuario_registro_servico"));
            listaOS.add(os);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Erro no banco ao listar OSs.", "Erro SQL", JOptionPane.ERROR_MESSAGE);
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
    return listaOS;
}

    public OrdemServicoPneu buscarOrdemServicoPorId(Integer id) {
        String sql = "SELECT * FROM ordens_servico_pneu WHERE id_servico = ?";
        OrdemServicoPneu os = null;
        try (Connection con = ModuloConexao.conector();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    os = new OrdemServicoPneu();
                    os.setIdServico(rs.getInt("id_servico"));
                    os.setIdPneuFk(rs.getInt("id_pneu_fk"));
                    os.setNumOrcamento(rs.getString("num_orcamento"));

                    Timestamp dataEnvioTs = rs.getTimestamp("data_envio");
                    if (dataEnvioTs != null) {
                        os.setDataEnvio(dataEnvioTs.toLocalDateTime());
                    }

                    Timestamp dataRetornoTs = rs.getTimestamp("data_retorno");
                    if (dataRetornoTs != null) {
                        os.setDataRetorno(dataRetornoTs.toLocalDateTime());
                    }

                    os.setIdParceiroFk(rs.getInt("id_parceiro_fk"));
                    os.setIdTipoServicoFk(rs.getInt("id_tipo_servico_fk"));
                    os.setValorServico(rs.getDouble("valor_servico"));
                    
                    int idMotivoFk = rs.getInt("id_motivo_fk");
                    if (!rs.wasNull()) {
                        os.setIdMotivoFk(idMotivoFk);
                    }
                    os.setObservacoesServico(rs.getString("observacoes_servico"));
                    os.setUsuarioRegistroServico(rs.getString("usuario_registro_servico"));
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao buscar Ordem de Serviço por ID: " + e.getMessage(), "Erro no DAO", JOptionPane.ERROR_MESSAGE);
            System.err.println("Erro ao buscar Ordem de Serviço por ID: " + e.getMessage());
        }
        return os;
    }
}
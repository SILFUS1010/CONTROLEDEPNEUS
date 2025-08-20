package br.com.martins_borges.dal; 

import br.com.martins_borges.model.TipoConfiguracao; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import br.com.martins_borges.dal.ModuloConexao;
import br.com.martins_borges.dal.ModuloConexao;
public class TipoConfiguracaoDAO {

    // Método para LISTAR TODOS os tipos (usado para popular ComboBox)
    public List<TipoConfiguracao> listarTodos() {
        // Use nomes de coluna em CAIXA ALTA se você os renomeou no banco
        String sql = "SELECT ID_CONFIG, NOME_CONFIG, QTD_PNEUS_PADRAO FROM TIPOS_CONFIGURACAO ORDER BY NOME_CONFIG";
        List<TipoConfiguracao> listaTipos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ModuloConexao.conector();
            if (conn == null) {
                 System.err.println("DAO: Falha ao obter conexão - listarTodos Tipos.");
                 return listaTipos; // Retorna lista vazia
            }
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                TipoConfiguracao tipo = new TipoConfiguracao();
                // Use nomes em CAIXA ALTA para pegar do ResultSet
                tipo.setIdConfig(rs.getInt("ID_CONFIG"));
                tipo.setNomeConfig(rs.getString("NOME_CONFIG"));

                // Leitura segura da quantidade padrão (pode ser NULL no banco)
                int qtd = rs.getInt("QTD_PNEUS_PADRAO");
                if (!rs.wasNull()) { // Verifica se o valor lido era NULL no banco
                    tipo.setQtdPneusPadrao(qtd); // Usa o setter que aceita Integer
                } else {
                    tipo.setQtdPneusPadrao(null); // Define como null no objeto
                }
                // Se tiver descricao_config, adicione aqui

                listaTipos.add(tipo);
            }
        } catch (SQLException e) {
            System.err.println("DAO SQL Error: Erro ao listar tipos config - " + e.getMessage());
            
        } finally {
            // Bloco finally para fechar recursos
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return listaTipos;
    }

    // Método para BUSCAR POR ID (usado no botão Cadastrar e opcionalmente nos botões cod_X)
    public TipoConfiguracao buscarPorId(int id) {
         // Use nomes de coluna em CAIXA ALTA se você os renomeou no banco
        String sql = "SELECT ID_CONFIG, NOME_CONFIG, QTD_PNEUS_PADRAO FROM TIPOS_CONFIGURACAO WHERE ID_CONFIG = ?";
        TipoConfiguracao tipo = null; // Começa como null
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ModuloConexao.conector();
             if (conn == null) {
                 System.err.println("DAO: Falha ao obter conexão - buscarPorId Tipo.");
                 return null;
             }

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) { // Se encontrou o registro
                tipo = new TipoConfiguracao();
                 // Use nomes em CAIXA ALTA para pegar do ResultSet
                tipo.setIdConfig(rs.getInt("ID_CONFIG"));
                tipo.setNomeConfig(rs.getString("NOME_CONFIG"));

                // Leitura segura da quantidade padrão
                int qtd = rs.getInt("QTD_PNEUS_PADRAO");
                if (!rs.wasNull()) {
                    tipo.setQtdPneusPadrao(qtd);
                } else {
                    tipo.setQtdPneusPadrao(null);
                }
                 // Se tiver descricao_config, adicione aqui
            }
        } catch (SQLException e) {
            System.err.println("DAO SQL Error: Erro ao buscar tipo por ID ("+id+") - " + e.getMessage());
             
        } finally {
            // Fechar rs, pstmt, conn
             try { if (rs != null) rs.close(); } catch (SQLException e) {}
             try { if (pstmt != null) pstmt.close(); } catch (SQLException e) {}
             try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return tipo; // Retorna o objeto encontrado ou null se não encontrou/deu erro
    }

    // Adicione outros métodos se precisar (ex: salvar, atualizar, deletar tipo)

}
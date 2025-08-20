package br.com.martins_borges.dal; // Certifique-se que o pacote está correto

// Imports necessários para JDBC e Tempo
import br.com.martins_borges.model.RegistroEngate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types; 
import br.com.martins_borges.dal.ModuloConexao;


public class RegistroEngateDAO {

    
    public boolean registrarEngate(RegistroEngate registro) {

        // SQL para inserir um novo registro de engate.
        // Note que não incluímos 'id' (AUTO_INCREMENT) nem 'status_registro' (DEFAULT 'ATIVO')
        // nem as colunas de desengate (que serão preenchidas depois).
        String sql = "INSERT INTO registros_engate (" +
                     "id_cavalo_fk, id_implemento1_fk, id_dolly_fk, id_implemento2_fk, " +
                     "data_hora_engate, km_engate, usuario_engate" +
                     ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            // 1. Obter a conexão do seu ModuloConexao
            conn = ModuloConexao.conector();
            if (conn == null) {
                // Adicionar um log ou mensagem de erro mais robusta aqui seria bom
                System.err.println("DAO: Falha ao obter conexão com o banco para registrar engate.");
                return false;
            }

            // 2. Preparar o comando SQL
            pstmt = conn.prepareStatement(sql);

            // 3. Definir os valores dos parâmetros (?)
            //    Os números (1, 2, 3...) correspondem à ordem dos '?' no SQL.

            // ID do Cavalo (Obrigatório)
            pstmt.setInt(1, registro.getIdCavaloFk()); // Assumindo que getIdCavaloFk() retorna int

            // Implemento 1 (Pode ser Nulo)
            // Assumindo que os getters para IDs FK nulos retornam null ou um valor especial (como 0 ou -1)
            // Se o getter retornar um Integer, podemos fazer assim:
            if (registro.getIdImplemento1Fk() != null) { // Se usar Integer no POJO
                 pstmt.setInt(2, registro.getIdImplemento1Fk());
            } else {
                 pstmt.setNull(2, Types.INTEGER); // Define como NULL no banco
            }
            // Alternativa: Se usar int no POJO e 0 significa NULL:
            // if (registro.getIdImplemento1Fk() != 0) {
            //     pstmt.setInt(2, registro.getIdImplemento1Fk());
            // } else {
            //     pstmt.setNull(2, Types.INTEGER);
            // }

            // Dolly (Pode ser Nulo) - Use a mesma lógica do Implemento 1
             if (registro.getIdDollyFk() != null) { // Se usar Integer no POJO
                 pstmt.setInt(3, registro.getIdDollyFk());
             } else {
                 pstmt.setNull(3, Types.INTEGER);
             }

            // Implemento 2 (Pode ser Nulo) - Use a mesma lógica do Implemento 1
             if (registro.getIdImplemento2Fk() != null) { // Se usar Integer no POJO
                 pstmt.setInt(4, registro.getIdImplemento2Fk());
             } else {
                 pstmt.setNull(4, Types.INTEGER);
             }

            // Data e Hora do Engate (Obrigatório)
            // Convertendo LocalDateTime do Java para Timestamp do SQL
            if (registro.getDataHoraEngate() != null) { // Assumindo que getDataHoraEngate() retorna LocalDateTime
                 pstmt.setTimestamp(5, Timestamp.valueOf(registro.getDataHoraEngate()));
            } else {
                 // A coluna no banco é NOT NULL, então não podemos inserir NULL.
                 // Ou garantimos que o objeto RegistroEngate sempre tenha a data, ou tratamos o erro aqui.
                 System.err.println("DAO: Data/Hora de Engate não pode ser nula ao registrar.");
                 // Fechar recursos antes de retornar erro
                  try { if (pstmt != null) pstmt.close(); } catch (SQLException e2) { /* Log silencioso */ }
                  try { if (conn != null) conn.close(); } catch (SQLException e2) { /* Log silencioso */ }
                 return false;
            }

            // KM do Engate (Obrigatório)
            pstmt.setDouble(6, registro.getKmEngate()); // Assumindo que getKmEngate() retorna double ou float

            // Usuário do Engate (Pode ser Nulo)
            if (registro.getUsuarioEngate() != null && !registro.getUsuarioEngate().isEmpty()) {
                pstmt.setString(7, registro.getUsuarioEngate());
            } else {
                pstmt.setNull(7, Types.VARCHAR); // Define como NULL no banco
            }

            // 4. Executar o comando INSERT
            int linhasAfetadas = pstmt.executeUpdate();

            // 5. Verificar se o INSERT funcionou (deve retornar 1 linha afetada)
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            // Capturar erros de SQL (conexão, sintaxe, etc.)
            System.err.println("DAO SQL Error: Erro ao registrar engate - " + e.getMessage());
            
            return false;
        } catch (Exception e) {
            // Capturar outros erros inesperados
             System.err.println("DAO Error: Erro inesperado ao registrar engate - " + e.getMessage());
             
             return false;
        } finally {
            // 6. FECHAR os recursos (PreparedStatement e Connection) SEMPRE
            //    É crucial fechar para liberar recursos do banco e da aplicação.
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                System.err.println("DAO Error: Erro ao fechar PreparedStatement - " + e.getMessage());
            }
            try {
                if (conn != null) {
                    conn.close(); // Fecha a conexão obtida do ModuloConexao
                }
            } catch (SQLException e) {
                System.err.println("DAO Error: Erro ao fechar Connection - " + e.getMessage());
            }
        }
    }

 

}

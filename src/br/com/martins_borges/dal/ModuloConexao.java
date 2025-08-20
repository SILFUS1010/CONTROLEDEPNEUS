package br.com.martins_borges.dal;

import java.sql.Connection;
import java.sql.*;
public class ModuloConexao {
    
//método responsavel por estabelecer a conexão com o banco
public static Connection conector() {
java. sql. Connection conexao = null;
// a linha abaixo "chama" o driver
String driver = "com.mysql.cj.jdbc.Driver";
// Armazenando informações referente ao banco
String url="jdbc:mysql://localhost:3306/DBcontrole_de_pneus";
    String user = "root";
    String password = "";
    
try {
            // Carregar o driver
            Class.forName(driver);
            // Estabelecer a conexão
            conexao = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            System.out.println("Driver não encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Erro ao conectar: " + e.getMessage());
        }

        return conexao; // Retorna a conexão
    }
}
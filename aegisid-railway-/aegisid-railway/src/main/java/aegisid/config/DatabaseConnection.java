package aegisid.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    public static DatabaseConnection getInstance() {
        return new DatabaseConnection();
    }

    private static String env(String clave, String alternativa, String porDefecto) {
        String v = System.getenv(clave);
        if (v == null || v.isEmpty()) v = System.getenv(alternativa);
        return (v == null || v.isEmpty()) ? porDefecto : v;
    }

    public Connection getConnection() throws SQLException {
        try {
String host = System.getenv("MYSQLHOST");
String port = System.getenv("MYSQLPORT");
String db   = System.getenv("MYSQLDATABASE");
String user = System.getenv("MYSQLUSER");
String pass = System.getenv("MYSQLPASSWORD");

String url = "jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false&serverTimezone=UTC";

            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, pass);

        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver de MySQL: " + e.getMessage(), e);
        }
    }
}

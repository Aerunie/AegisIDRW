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
            // Lee las variables tanto si están en Railway como en tu PC local
            String host = env("MYSQLHOST", "MYSQL_HOST", "localhost");
            String port = env("MYSQLPORT", "MYSQL_PORT", "3306");
            String db   = env("MYSQLDATABASE", "MYSQL_DATABASE", "aegisid");
            String user = env("MYSQLUSER", "MYSQL_USER", "root");
            String pass = env("MYSQLPASSWORD", "MYSQL_PASSWORD", "");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, pass);

        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver de MySQL: " + e.getMessage(), e);
        }
    }
}

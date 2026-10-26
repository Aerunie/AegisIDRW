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
            String host = env("DB_HOST", "MYSQLHOST", "localhost");
            String port = env("DB_PORT", "MYSQLPORT", "3306");
            String db   = env("DB_NAME", "MYSQLDATABASE", "railway");
            String user = env("DB_USER", "MYSQLUSER", "root");
            String pass = env("DB_PASSWORD", "MYSQLPASSWORD", "");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + db
                    + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";

            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, pass);

        } catch (ClassNotFoundException e) {
            throw new SQLException("No se encontró el driver de MySQL: " + e.getMessage(), e);
        }
    }
}

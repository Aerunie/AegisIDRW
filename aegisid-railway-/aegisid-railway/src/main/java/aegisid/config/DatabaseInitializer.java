package aegisid.config;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebListener
public class DatabaseInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement()) {

            st.executeUpdate("CREATE TABLE IF NOT EXISTS usuarios ("
                + "id_usuario INT AUTO_INCREMENT PRIMARY KEY, "
                + "nombres VARCHAR(100), "
                + "apellidos VARCHAR(100), "
                + "dni VARCHAR(20), "
                + "telefono VARCHAR(20), "
                + "correo VARCHAR(100) NOT NULL UNIQUE, "
                + "username VARCHAR(100), "
                + "clave VARCHAR(100) NOT NULL, "
                + "rol VARCHAR(50) NOT NULL DEFAULT 'Administrador', "
                + "estado VARCHAR(15) DEFAULT 'Activo', "
                + "intentos_fallidos INT DEFAULT 0, "
                + "bloqueado BOOLEAN DEFAULT FALSE)");

            st.executeUpdate("INSERT IGNORE INTO usuarios (nombres, correo, username, clave, rol, intentos_fallidos, bloqueado) "
                + "VALUES ('Administrador', 'admin@correo.com', 'admin', '1234', 'Administrador', 0, FALSE)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS clientes ("
                + "id_cliente INT AUTO_INCREMENT PRIMARY KEY, "
                + "nombre VARCHAR(100) NOT NULL, "
                + "apellido VARCHAR(100), "
                + "dni_ruc VARCHAR(20), "
                + "correo VARCHAR(100), "
                + "telefono VARCHAR(20), "
                + "direccion VARCHAR(200), "
                + "estado VARCHAR(15) DEFAULT 'Activo')");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS proveedores ("
                + "id_proveedor INT AUTO_INCREMENT PRIMARY KEY, "
                + "razon_social VARCHAR(150) NOT NULL, "
                + "ruc VARCHAR(20), "
                + "telefono VARCHAR(20), "
                + "direccion VARCHAR(200), "
                + "correo VARCHAR(100), "
                + "estado VARCHAR(15) DEFAULT 'Activo')");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS productos ("
                + "id_producto INT AUTO_INCREMENT PRIMARY KEY, "
                + "nombre VARCHAR(150) NOT NULL, "
                + "descripcion VARCHAR(255), "
                + "precio DECIMAL(10,2) NOT NULL, "
                + "stock INT NOT NULL, "
                + "categoria VARCHAR(100), "
                + "estado VARCHAR(15) DEFAULT 'Disponible')");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS ventas ("
                + "id_venta INT AUTO_INCREMENT PRIMARY KEY, "
                + "fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "total DECIMAL(10,2) NOT NULL, "
                + "tipo_comprobante VARCHAR(50), "
                + "estado VARCHAR(20) DEFAULT 'Registrada', "
                + "id_cliente INT NOT NULL)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS detalle_venta ("
                + "id_detalle INT AUTO_INCREMENT PRIMARY KEY, "
                + "id_venta INT NOT NULL, "
                + "id_producto INT NOT NULL, "
                + "cantidad INT NOT NULL, "
                + "precio_unitario DECIMAL(10,2) NOT NULL, "
                + "subtotal DECIMAL(10,2) NOT NULL)");

            st.executeUpdate("CREATE TABLE IF NOT EXISTS menu_opciones ("
                + "id_menu INT AUTO_INCREMENT PRIMARY KEY, "
                + "nombre_opcion VARCHAR(100) NOT NULL, "
                + "descripcion VARCHAR(255), "
                + "url_enlace VARCHAR(100) NOT NULL, "
                + "rol_permitido VARCHAR(50) NOT NULL DEFAULT 'Todos')");

            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM menu_opciones")) {
                if (rs.next() && rs.getInt(1) == 0) {
                    st.executeUpdate("INSERT INTO menu_opciones (nombre_opcion, descripcion, url_enlace, rol_permitido) VALUES "
                        + "('Usuarios', 'Gestión de usuarios', 'usuarios', 'Todos'), "
                        + "('Clientes', 'Gestión de clientes', 'clientes', 'Todos'), "
                        + "('Proveedores', 'Gestión de proveedores', 'proveedores', 'Todos'), "
                        + "('Productos', 'Gestión de productos', 'productos', 'Todos'), "
                        + "('Ventas', 'Registro de ventas', 'ventas', 'Todos'), "
                        + "('Reportes', 'Reportes y análisis', 'reportes', 'Todos')");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

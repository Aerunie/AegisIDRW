package aegisid.repository;

import aegisid.config.DatabaseConnection;
import aegisid.modelo.DetalleVenta;
import aegisid.modelo.Venta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaRepository {

    public List<Venta> listarVentas() throws SQLException {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT v.id_venta, v.fecha, v.total, v.tipo_comprobante, v.estado, v.id_cliente, " +
                     "c.nombre AS nombre_cliente, c.apellido AS apellido_cliente " +
                     "FROM ventas v JOIN clientes c ON v.id_cliente = c.id_cliente ORDER BY v.id_venta DESC";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Venta v = new Venta(
                    rs.getInt("id_venta"),
                    rs.getString("fecha"),
                    rs.getDouble("total"),
                    rs.getString("tipo_comprobante"),
                    rs.getString("estado"),
                    rs.getInt("id_cliente")
                );
                v.setNombreCliente(rs.getString("nombre_cliente") + " " + rs.getString("apellido_cliente"));
                v.setDetalles(listarDetallesPorVenta(v.getId()));
                lista.add(v);
            }
        }
        return lista;
    }

    public List<DetalleVenta> listarDetallesPorVenta(int idVenta) throws SQLException {
        List<DetalleVenta> lista = new ArrayList<>();
        String sql = "SELECT d.id_detalle, d.id_venta, d.id_producto, p.nombre AS nombre_producto, d.cantidad, d.precio_unitario, d.subtotal " +
                     "FROM detalle_venta d JOIN productos p ON d.id_producto = p.id_producto WHERE d.id_venta = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new DetalleVenta(
                        rs.getInt("id_detalle"),
                        rs.getInt("id_venta"),
                        rs.getInt("id_producto"),
                        rs.getString("nombre_producto"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio_unitario")
                    ));
                }
            }
        }
        return lista;
    }

    public void registrarVentaConDetalle(Venta v, int idProducto, int cantidad, double precioUnitario) throws SQLException {
        String sqlVenta = "INSERT INTO ventas (total, tipo_comprobante, estado, id_cliente) VALUES (?, ?, 'Registrada', ?)";
        String sqlDetalle = "INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        String sqlStock = "UPDATE productos SET stock = stock - ? WHERE id_producto = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                psVenta.setDouble(1, v.getTotal());
                psVenta.setString(2, v.getTipoComprobante());
                psVenta.setInt(3, v.getIdCliente());
                
                psVenta.executeUpdate();
                try (ResultSet rs = psVenta.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idVentaGenerado = rs.getInt(1);
                        double subtotal = cantidad * precioUnitario;

                        try (PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle)) {
                            psDetalle.setInt(1, idVentaGenerado);
                            psDetalle.setInt(2, idProducto);
                            psDetalle.setInt(3, cantidad);
                            psDetalle.setDouble(4, precioUnitario);
                            psDetalle.setDouble(5, subtotal);
                            psDetalle.executeUpdate();
                        }

                        try (PreparedStatement psStock = conn.prepareStatement(sqlStock)) {
                            psStock.setInt(1, cantidad);
                            psStock.setInt(2, idProducto);
                            psStock.executeUpdate();
                        }
                    }
                }
                conn.commit();
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            }
        }
    }

    public void anularVenta(int id) throws SQLException {
        String sql = "UPDATE ventas SET estado = 'Anulada' WHERE id_venta = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
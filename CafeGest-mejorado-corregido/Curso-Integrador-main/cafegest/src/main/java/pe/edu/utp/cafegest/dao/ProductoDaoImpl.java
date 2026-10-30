package pe.edu.utp.cafegest.dao;

import pe.edu.utp.cafegest.model.Producto;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public class ProductoDaoImpl implements IProductoDao {

    private final Connection conexion;

    // Inyección de dependencias: la conexión se recibe por el constructor
    public ProductoDaoImpl(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public List<Producto> listar() throws SQLException {
        List<Producto> result = new ArrayList<>();
        // Ya no abrimos la conexión aquí, usamos this.conexion
        try (PreparedStatement p = this.conexion.prepareStatement("SELECT * FROM productos WHERE activo=TRUE ORDER BY nombre"); 
             ResultSet r = p.executeQuery()) {
            
            while(r.next()) {
                result.add(new Producto(
                    r.getInt("id"), r.getString("nombre"), r.getString("categoria"), 
                    r.getBigDecimal("precio"), r.getInt("stock"), r.getInt("minimo"), r.getBoolean("activo")
                ));
            }
        }
        return result;
    }

    @Override
    public void guardar(Integer id, String nombre, String categoria, BigDecimal precio, int stock, int minimo) throws SQLException {
        boolean nuevo = id == null;
        String sql = nuevo ? "INSERT INTO productos(nombre,categoria,precio,stock,minimo) VALUES(?,?,?,?,?)" : "UPDATE productos SET nombre=?,categoria=?,precio=?,stock=?,minimo=? WHERE id=?";
        
        try (PreparedStatement p = this.conexion.prepareStatement(sql)) {
            p.setString(1, nombre);
            p.setString(2, categoria);
            p.setBigDecimal(3, precio);
            p.setInt(4, stock);
            p.setInt(5, minimo);
            if(!nuevo) p.setInt(6, id);
            
            p.executeUpdate();
        }
    }

    @Override
    public void desactivar(int id) throws SQLException {
        try (PreparedStatement p = this.conexion.prepareStatement("UPDATE productos SET activo=FALSE WHERE id=?")) {
            p.setInt(1, id);
            p.executeUpdate();
        }
    }

    @Override
    public void ajustar(int id, int delta, String observacion) throws SQLException {
        boolean autoCommitOriginal = this.conexion.getAutoCommit();
        this.conexion.setAutoCommit(false); // Iniciamos la transacción
        
        try {
            try (PreparedStatement p = this.conexion.prepareStatement("UPDATE productos SET stock=stock+? WHERE id=? AND activo=TRUE AND stock+?>=0")) {
                p.setInt(1, delta);
                p.setInt(2, id);
                p.setInt(3, delta);
                if (p.executeUpdate() != 1) {
                    throw new SQLException("Producto inexistente o stock insuficiente");
                }
            }
            try (PreparedStatement p = this.conexion.prepareStatement("INSERT INTO movimientos(producto_id,tipo,cantidad,observacion) VALUES(?,?,?,?)")) {
                p.setInt(1, id);
                p.setString(2, delta >= 0 ? "ENTRADA" : "SALIDA");
                p.setInt(3, delta);
                p.setString(4, observacion);
                p.executeUpdate();
            }
            this.conexion.commit(); // Confirmamos los cambios
            
        } catch (Exception e) {
            this.conexion.rollback(); // Revertimos si hay error
            throw e instanceof SQLException ? (SQLException) e : new SQLException(e);
        } finally {
            this.conexion.setAutoCommit(autoCommitOriginal); // Restauramos el estado de la conexión
        }
    }
}
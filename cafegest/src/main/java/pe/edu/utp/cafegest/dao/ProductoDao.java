package pe.edu.utp.cafegest.dao;
import pe.edu.utp.cafegest.model.Producto;
import pe.edu.utp.cafegest.config.Database;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
/** DAO: encapsula las consultas JDBC para productos terminados. */
public class ProductoDao {
    public List<Producto> listar() throws SQLException {
        List<Producto> result = new ArrayList<>();
        try (Connection c=Database.open(); PreparedStatement p=c.prepareStatement("SELECT * FROM productos WHERE activo=TRUE ORDER BY nombre"); ResultSet r=p.executeQuery()) {
            while(r.next()) result.add(new Producto(r.getInt("id"),r.getString("nombre"),r.getString("categoria"),r.getBigDecimal("precio"),r.getInt("stock"),r.getInt("minimo"),r.getBoolean("activo")));
        }
        return result;
    }
    public void guardar(Integer id, String nombre, String categoria, BigDecimal precio, int stock, int minimo) throws SQLException {
        boolean nuevo=id==null;
        String sql=nuevo ? "INSERT INTO productos(nombre,categoria,precio,stock,minimo) VALUES(?,?,?,?,?)" : "UPDATE productos SET nombre=?,categoria=?,precio=?,stock=?,minimo=? WHERE id=?";
        try(Connection c=Database.open();PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1,nombre);p.setString(2,categoria);p.setBigDecimal(3,precio);p.setInt(4,stock);p.setInt(5,minimo);if(!nuevo)p.setInt(6,id);p.executeUpdate();
        }
    }
    public void desactivar(int id) throws SQLException {try(Connection c=Database.open();PreparedStatement p=c.prepareStatement("UPDATE productos SET activo=FALSE WHERE id=?")){p.setInt(1,id);p.executeUpdate();}}
    public void ajustar(int id,int delta,String observacion) throws SQLException {
        try(Connection c=Database.open()) {c.setAutoCommit(false);try {
            try(PreparedStatement p=c.prepareStatement("UPDATE productos SET stock=stock+? WHERE id=? AND activo=TRUE AND stock+?>=0")) {p.setInt(1,delta);p.setInt(2,id);p.setInt(3,delta);if(p.executeUpdate()!=1)throw new SQLException("Producto inexistente o stock insuficiente");}
            try(PreparedStatement p=c.prepareStatement("INSERT INTO movimientos(producto_id,tipo,cantidad,observacion) VALUES(?,?,?,?)")){p.setInt(1,id);p.setString(2,delta>=0?"ENTRADA":"SALIDA");p.setInt(3,delta);p.setString(4,observacion);p.executeUpdate();}
            c.commit();
        }catch(Exception e){c.rollback();throw e instanceof SQLException?(SQLException)e:new SQLException(e);}}
    }
}

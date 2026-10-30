package pe.edu.utp.cafegest.service;

import pe.edu.utp.cafegest.config.Database;
import pe.edu.utp.cafegest.dao.IVentaDao;
import pe.edu.utp.cafegest.dao.VentaDaoImpl;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

/** Registra venta y descuento de productos terminados en una sola transacción. */
public class VentaService {
    
    public record Linea(int productoId, int cantidad) {}
    
    public static void validarCantidad(int cantidad) {
        if(cantidad <= 0) throw new IllegalArgumentException("Cantidad debe ser positiva");
    }

    // --- MÉTODOS AGREGADOS PARA CONECTAR LA VISTA CON EL DAO ---

    public IVentaDao.ResumenVentas obtenerResumenHoy() throws SQLException {
        try (Connection c = Database.open()) {
            IVentaDao dao = new VentaDaoImpl(c);
            return dao.obtenerResumenHoy();
        }
    }

    public int obtenerTotalVentasHistoricas() throws SQLException {
        try (Connection c = Database.open()) {
            IVentaDao dao = new VentaDaoImpl(c);
            return dao.obtenerTotalVentasHistoricas();
        }
    }

    public List<String[]> buscarVentas(LocalDate desde, LocalDate hasta, String usuario) throws SQLException {
        try (Connection c = Database.open()) {
            IVentaDao dao = new VentaDaoImpl(c);
            return dao.buscarVentas(desde, hasta, usuario);
        }
    }

    // --- LÓGICA ORIGINAL DE VENTAS ---

    public int vender(String usuario, List<Linea> lineas) throws SQLException {
        if(lineas==null||lineas.isEmpty())throw new IllegalArgumentException("Venta vacía");
        Map<Integer,Integer> agrupado=new LinkedHashMap<>();
        for(Linea l:lineas){validarCantidad(l.cantidad());agrupado.merge(l.productoId(),l.cantidad(),Math::addExact);}
        try(Connection c=Database.open()){c.setAutoCommit(false);try {
            BigDecimal total=BigDecimal.ZERO;
            Map<Integer,BigDecimal> precios=new LinkedHashMap<>();
            for(var entry:agrupado.entrySet()){
                try(PreparedStatement p=c.prepareStatement("SELECT precio,stock FROM productos WHERE id=? AND activo=TRUE FOR UPDATE")){
                    p.setInt(1,entry.getKey());try(ResultSet r=p.executeQuery()){
                        if(!r.next())throw new SQLException("Producto no disponible: "+entry.getKey());
                        if(r.getInt("stock")<entry.getValue())throw new SQLException("Stock insuficiente para producto "+entry.getKey());
                        BigDecimal precio=r.getBigDecimal("precio");precios.put(entry.getKey(),precio);total=total.add(precio.multiply(BigDecimal.valueOf(entry.getValue())));
                    }}
            }
            int ventaId;
            try(PreparedStatement p=c.prepareStatement("INSERT INTO ventas(usuario,total) VALUES(?,?)",Statement.RETURN_GENERATED_KEYS)){
                p.setString(1,usuario);p.setBigDecimal(2,total);p.executeUpdate();try(ResultSet r=p.getGeneratedKeys()){if(!r.next())throw new SQLException("Sin ID de venta");ventaId=r.getInt(1);}
            }
            for(var entry:agrupado.entrySet()){
                int id=entry.getKey(),cantidad=entry.getValue();
                try(PreparedStatement p=c.prepareStatement("UPDATE productos SET stock=stock-? WHERE id=? AND stock>=?")){p.setInt(1,cantidad);p.setInt(2,id);p.setInt(3,cantidad);if(p.executeUpdate()!=1)throw new SQLException("Stock insuficiente");}
                try(PreparedStatement p=c.prepareStatement("INSERT INTO venta_detalle(venta_id,producto_id,cantidad,precio) VALUES(?,?,?,?)")){p.setInt(1,ventaId);p.setInt(2,id);p.setInt(3,cantidad);p.setBigDecimal(4,precios.get(id));p.executeUpdate();}
                try(PreparedStatement p=c.prepareStatement("INSERT INTO movimientos(producto_id,tipo,cantidad,observacion) VALUES(?,?,?,?)")){p.setInt(1,id);p.setString(2,"VENTA");p.setInt(3,-cantidad);p.setString(4,"Venta #"+ventaId);p.executeUpdate();}
            }
            c.commit();return ventaId;
        }catch(Exception e){c.rollback();throw e instanceof SQLException?(SQLException)e:new SQLException(e);}}
    }
}
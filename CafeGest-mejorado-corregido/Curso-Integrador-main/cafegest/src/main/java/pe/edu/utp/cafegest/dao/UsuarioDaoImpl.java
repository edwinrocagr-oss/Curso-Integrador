package pe.edu.utp.cafegest.dao;

import pe.edu.utp.cafegest.model.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.edu.utp.cafegest.service.AuthService;

public class UsuarioDaoImpl implements IUsuarioDao {
    
    private final Connection conexion;
    // Usamos Logback aquí también
    private static final Logger logger = LoggerFactory.getLogger(UsuarioDaoImpl.class);

    public UsuarioDaoImpl(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public List<Usuario> listar() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        try (PreparedStatement p = this.conexion.prepareStatement("SELECT id, usuario, rol, activo FROM usuarios ORDER BY id");
             ResultSet r = p.executeQuery()) {
            while (r.next()) {
                // Asumimos que la columna se llama 'usuario', 'rol' y 'activo' según tu AuthService
                lista.add(new Usuario(r.getInt("id"), r.getString("usuario"), r.getString("rol"), r.getBoolean("activo")));
            }
        }
        return lista;
    }

    @Override
    public void registrar(String username, String password, String rol) throws SQLException {
        // 1. Encriptamos la contraseña usando tu propio servicio de seguridad
        String hash = AuthService.hash(password);
        
        // 2. Usamos el nombre correcto de tu columna: clave_hash
        String sql = "INSERT INTO usuarios (usuario, clave_hash, rol, activo) VALUES (?, ?, ?, TRUE)";
        
        try (PreparedStatement p = this.conexion.prepareStatement(sql)) {
            p.setString(1, username);
            p.setString(2, hash); // Aquí guardamos la contraseña ya encriptada
            p.setString(3, rol);
            p.executeUpdate();
            logger.info("Nuevo usuario registrado: " + username);
        }
    }

    @Override
    public void cambiarEstado(int id, boolean activo) throws SQLException {
        try (PreparedStatement p = this.conexion.prepareStatement("UPDATE usuarios SET activo=? WHERE id=?")) {
            p.setBoolean(1, activo);
            p.setInt(2, id);
            p.executeUpdate();
            logger.info("Usuario ID " + id + " cambió su estado a activo=" + activo);
        }
    }
}
package pe.edu.utp.cafegest.dao;

import pe.edu.utp.cafegest.model.Usuario;
import java.sql.SQLException;
import java.util.List;

public interface IUsuarioDao {
    List<Usuario> listar() throws SQLException;
    void registrar(String username, String password, String rol) throws SQLException;
    void cambiarEstado(int id, boolean activo) throws SQLException; // <-- Modificado
}
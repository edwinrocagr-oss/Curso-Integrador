package pe.edu.utp.cafegest.dao;

import pe.edu.utp.cafegest.model.Comanda;
import pe.edu.utp.cafegest.model.EstadoComanda;
import java.sql.SQLException;
import java.util.List;

public interface IComandaDao {
    List<Comanda> listarActivas() throws SQLException;
    void cambiarEstado(int id, EstadoComanda actual, EstadoComanda siguiente) throws SQLException;
}
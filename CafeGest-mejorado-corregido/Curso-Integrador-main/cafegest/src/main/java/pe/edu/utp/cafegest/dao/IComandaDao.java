package pe.edu.utp.cafegest.dao;

import pe.edu.utp.cafegest.model.Comanda;
import java.sql.SQLException;
import java.util.List;

public interface IComandaDao {
    List<Comanda> listarPendientes() throws SQLException;
    void registrar(String mesa, String pedido) throws SQLException;
    void marcarCompletada(int id) throws SQLException;
}
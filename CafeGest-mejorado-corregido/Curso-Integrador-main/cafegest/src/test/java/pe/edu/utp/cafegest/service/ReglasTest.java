package pe.edu.utp.cafegest.service;
import org.junit.jupiter.api.Test;
import pe.edu.utp.cafegest.model.EstadoComanda;
import static org.junit.jupiter.api.Assertions.*;
class ReglasTest {
 @Test void contrasenaHashDistintoYVerificable(){String hash=AuthService.hash("clave-de-prueba-segura");assertNotEquals("clave-de-prueba-segura",hash);assertTrue(AuthService.verificar("clave-de-prueba-segura",hash));assertFalse(AuthService.verificar("incorrecta",hash));}
 @Test void cantidadNoPuedeSerCeroNiNegativa(){assertThrows(IllegalArgumentException.class,()->VentaService.validarCantidad(0));assertThrows(IllegalArgumentException.class,()->VentaService.validarCantidad(-2));assertDoesNotThrow(()->VentaService.validarCantidad(2));}
 @Test void comandaSoloAvanzaAlSiguienteEstado(){assertTrue(EstadoComanda.PENDIENTE.puedeAvanzarA(EstadoComanda.PREPARANDO));assertTrue(EstadoComanda.PREPARANDO.puedeAvanzarA(EstadoComanda.LISTO));assertTrue(EstadoComanda.LISTO.puedeAvanzarA(EstadoComanda.ENTREGADO));assertFalse(EstadoComanda.PENDIENTE.puedeAvanzarA(EstadoComanda.LISTO));assertFalse(EstadoComanda.ENTREGADO.puedeAvanzarA(EstadoComanda.PENDIENTE));}
}

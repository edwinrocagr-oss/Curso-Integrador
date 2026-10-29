package pe.edu.utp.cafegest.service;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ReglasTest {
 @Test void contrasenaHashDistintoYVerificable(){String hash=AuthService.hash("clave-de-prueba-segura");assertNotEquals("clave-de-prueba-segura",hash);assertTrue(AuthService.verificar("clave-de-prueba-segura",hash));assertFalse(AuthService.verificar("incorrecta",hash));}
 @Test void cantidadNoPuedeSerCeroNiNegativa(){assertThrows(IllegalArgumentException.class,()->VentaService.validarCantidad(0));assertThrows(IllegalArgumentException.class,()->VentaService.validarCantidad(-2));assertDoesNotThrow(()->VentaService.validarCantidad(2));}
}

# CaféGest — Sistema de gestión de cafetería

Proyecto académico en Java 17 + JavaFX 21, Maven y JDBC. Inventario de **productos terminados**, sin recetas ni ingredientes.

## Ejecutar
1. Instalar JDK 17 y Apache Maven (o abrir como proyecto Maven en NetBeans/IntelliJ).
2. Desde la carpeta del proyecto: `mvn clean javafx:run`
3. En el primer inicio, crear contraseña de administrador (10+ caracteres). Usuario inicial: `admin`.
4. Crear productos (ej. Café expreso, Latte, Capuchino), registrar entradas, vender y verificar el stock.

## Persistencia
Por defecto H2 guarda los datos en `~/.cafegest/cafegest.mv.db`, sin instalar PostgreSQL.
Para PostgreSQL, crear primero una base vacía `cafegest` y configurar variables de entorno:
- `CAFEGEST_DB_URL=jdbc:postgresql://localhost:5432/cafegest`
- `CAFEGEST_DB_USER=postgres`
- `CAFEGEST_DB_PASSWORD=<contraseña local>`
El esquema se crea automáticamente al iniciar. **No** usar una base que contenga tablas con los mismos nombres.

## Funcionalidades actuales
- Creación segura de administrador en el primer inicio, login y control básico de permisos.
- CRUD de productos terminados (alta, edición, baja lógica).
- Reposición y ajuste de stock con historial de movimientos en base de datos.
- Registro de venta transaccional con descuento de stock y control de insuficiencia.
- Dashboard con totales y alertas de stock bajo.
- Historial de ventas y exportación XLSX mediante Apache POI.
- Tests unitarios JUnit, DAO JDBC, patrón MVC por capas y CI GitHub Actions.

## Pendiente para el alcance completo
Administración de usuarios y roles desde interfaz, pantalla de comandas, reportes avanzados, ticket interno imprimible, pruebas de integración y evaluación de rendimiento. No afirmar que están implementados.

## Seguridad
PBKDF2-HMAC-SHA256 con sal aleatoria, consultas parametrizadas y transacciones. La contraseña inicial la define el usuario. Para uso real multiusuario falta endurecer la autorización en capa de servicio, recuperación de cuentas y controles de acceso a la base de datos.

## Evidencias Git
Crear repositorio y registrar commits sucesivos: `git init`, `git add .`, `git commit -m "feat: estructura inicial y persistencia"`. Publicar en GitHub desde el equipo del grupo. No se ha publicado automáticamente.


## Mejoras de interfaz (edición UX)

Esta versión incorpora una interfaz JavaFX más clara, con navegación lateral, tarjetas de resumen y formularios agrupados.

- Catálogo inicial de 20 productos de cafetería (bebidas calientes y frías, té, panadería, postres y sándwiches). Son productos y precios de demostración en soles; puedes modificarlos desde Productos.
- El catálogo de ejemplo se inserta solo si la base de datos no tiene productos activos. No se borran las ventas ni el inventario existentes.
- Inventario con alertas visuales de stock bajo, productos agotados y registro de entradas/salidas con motivo.
- Nueva venta con búsqueda, filtro por categoría, carrito, cantidades, subtotales, total y confirmación.
- Historial con filtro por rango de fechas y usuario, total vendido y exportación a Excel.
- Resumen con ventas del día, ingresos, productos activos y alertas de inventario.

### Ejecutar

Desde la carpeta que contiene este `pom.xml`, ejecutar `mvn clean javafx:run`. También se puede abrir como proyecto Maven en Apache NetBeans y ejecutar el proyecto.

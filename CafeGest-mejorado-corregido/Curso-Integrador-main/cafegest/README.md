# CaféGest - Sistema de gestión de cafetería

Proyecto académico de escritorio con Java 17, JavaFX, Maven y PostgreSQL. Maneja productos terminados, inventario y ventas.

## Requisitos

- JDK 17 o superior.
- Apache Maven.
- PostgreSQL 14 o superior, iniciado y accesible.
- Una base de datos vacía llamada `cafegest` (el usuario de conexión debe poder crear tablas).

## Ejecutar en Windows

Abre PowerShell en la carpeta `Curso-Integrador-main\cafegest`, la misma carpeta donde está este `pom.xml`. Configura la conexión para esa terminal y ejecuta la aplicación:

```powershell
$env:CAFEGEST_DB_URL = "jdbc:postgresql://localhost:5432/cafegest"
$env:CAFEGEST_DB_USER = "postgres"
$env:CAFEGEST_DB_PASSWORD = "TU_CONTRASENA_LOCAL"
mvn clean javafx:run
```

Reemplaza `TU_CONTRASENA_LOCAL` por la contraseña que configuraste en PostgreSQL. Estas variables duran mientras esa ventana de PowerShell siga abierta. Para probar sin abrir la interfaz, ejecuta `mvn test`.

Si tu PostgreSQL usa otro host, puerto, base de datos o usuario, actualiza `CAFEGEST_DB_URL` y `CAFEGEST_DB_USER`. Si no se define la URL, la aplicación usa `jdbc:postgresql://localhost:5432/cafegest`; no hay contraseña predeterminada.

## Configuracion del equipo

1. Instala PostgreSQL y crea una base vacía llamada `cafegest` con pgAdmin o la herramienta que uses.
2. Usa el comando de ejecución anterior. La primera vez que se inicia, CaféGest crea las tablas y agrega productos de demostración si no hay productos activos.
3. En el primer inicio de la base, crea la contraseña del administrador de CaféGest (mínimo 10 caracteres). El usuario inicial es `admin`.

No uses una base que ya tenga tablas `usuarios`, `productos`, `ventas`, `venta_detalle` o `movimientos` con otro esquema.

## Trabajar con compañeros

Para compartir ventas e inventario, todos deben conectarse a **la misma instancia PostgreSQL** accesible por la red. Quien administre esa instancia debe compartir de forma privada el host, puerto, nombre de base, usuario y contraseña. Cada integrante configura su propia terminal con esos datos:

```powershell
$env:CAFEGEST_DB_URL = "jdbc:postgresql://HOST_COMPARTIDO:5432/cafegest"
$env:CAFEGEST_DB_USER = "USUARIO_COMPARTIDO"
$env:CAFEGEST_DB_PASSWORD = "CONTRASENA_COMPARTIDA"
mvn clean javafx:run
```

Reemplacen `HOST_COMPARTIDO` y las credenciales por los valores reales del servidor. `localhost` apunta a la computadora de cada integrante; no permite compartir datos entre computadoras. El servidor debe aceptar conexiones de la red del equipo y su firewall debe permitir el puerto configurado.

**No suban contraseñas, archivos de configuración con credenciales ni datos de acceso al repositorio.** No usen una base PostgreSQL expuesta públicamente sin restringir conexiones y permisos. Los datos que existan en una base H2 anterior no se copian automáticamente a PostgreSQL.

## Funcionalidades

- Inicio de sesión y creación inicial del administrador con hash PBKDF2-HMAC-SHA256.
- Alta, edicion y baja logica de productos.
- Ajustes de inventario y registro de movimientos.
- Ventas transaccionales con validación y descuento de stock.
- Resumen, historial con filtros y exportación a Excel.

La administración de usuarios desde la interfaz, los reportes avanzados, los tickets imprimibles y las pruebas de integración siguen pendientes.

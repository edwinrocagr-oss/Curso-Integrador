# CaféGest - Sistema de gestión de cafetería

Proyecto académico de escritorio con Java 17, JavaFX, Maven, Guava y PostgreSQL. Maneja productos terminados, inventario y ventas. Guava se utiliza para mantener inmutable el catálogo de productos de demostración.

## Requisitos

- JDK 17 o superior.
- Apache Maven.
- PostgreSQL 14 o superior, iniciado y accesible.
- Una base de datos vacía llamada `cafegest` (el usuario de conexión debe poder crear tablas).

## Ejecutar en Windows

1. Copia `src/main/resources/application-local.properties.example` como `src/main/resources/application-local.properties`.
2. Edita `application-local.properties` y reemplaza `YOUR_POSTGRES_PASSWORD` por tu contraseña de PostgreSQL. Este archivo está ignorado por Git y no se debe subir.
3. Abre PowerShell en la carpeta `Curso-Integrador-main\cafegest`, la misma carpeta donde está este `pom.xml`, y ejecuta:

```powershell
mvn clean javafx:run
```

La conexión predeterminada está en `src/main/resources/application.properties` y apunta a `jdbc:postgresql://localhost:5432/cafegest` con el usuario `postgres`. Si tu PostgreSQL usa otro host, puerto, base de datos o usuario, edita esos valores en `application-local.properties`. Para probar sin abrir la interfaz, ejecuta `mvn test`.

## Configuracion del equipo

1. Instala PostgreSQL y crea una base vacía llamada `cafegest` con pgAdmin o la herramienta que uses.
2. Copia el archivo de ejemplo de configuración local y escribe la contraseña válida de PostgreSQL.
3. Ejecuta `mvn clean javafx:run`. La primera vez que se inicia, CaféGest crea las tablas y agrega productos de demostración si no hay productos activos.
4. En el primer inicio de la base, crea la contraseña del administrador de CaféGest (mínimo 10 caracteres). El usuario inicial es `admin`.

No uses una base que ya tenga tablas `usuarios`, `productos`, `ventas`, `venta_detalle` o `movimientos` con otro esquema.

## Trabajar con compañeros

Para compartir ventas e inventario, todos deben conectarse a **la misma instancia PostgreSQL** accesible por la red. Quien administre esa instancia debe compartir de forma privada el host, puerto, nombre de base, usuario y contraseña. Cada integrante copia el archivo de ejemplo como `application-local.properties`, coloca ahí esos valores y ejecuta `mvn clean javafx:run`. `localhost` apunta a la computadora de cada integrante; no permite compartir datos entre computadoras. El servidor debe aceptar conexiones de la red del equipo y su firewall debe permitir el puerto configurado.

**No suban contraseñas, archivos de configuración con credenciales ni datos de acceso al repositorio.** No usen una base PostgreSQL expuesta públicamente sin restringir conexiones y permisos. Los datos que existan en una base H2 anterior no se copian automáticamente a PostgreSQL.

## Funcionalidades

- Inicio de sesión y creación inicial del administrador con hash PBKDF2-HMAC-SHA256.
- Alta, edicion y baja logica de productos.
- Ajustes de inventario y registro de movimientos.
- Ventas transaccionales con validación y descuento de stock.
- Resumen, historial con filtros y exportación a Excel con fechas y montos numéricos, filtros de columna y total calculado.

La administración de usuarios desde la interfaz, los reportes avanzados, los tickets imprimibles y las pruebas de integración siguen pendientes.

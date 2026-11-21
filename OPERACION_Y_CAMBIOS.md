# CaféGest — mejoras para proyecto final

## Cambios realizados
- Se quitó la contraseña de `application.properties` y el archivo local del paquete entregable. Se admiten `CAFEGEST_DB_URL`, `CAFEGEST_DB_USER`, `CAFEGEST_DB_PASSWORD` por variables de entorno. **Cambiar la contraseña anterior en PostgreSQL**, pues apareció en el archivo original.
- Logs en `logs/cafegest.log` con rotación por fecha/tamaño (10 MB, 14 días, límite 200 MB).
- Validación adicional de hashes PBKDF2 malformados y nuevas pruebas unitarias.
- Scripts para respaldo, restauración y comprobación de disponibilidad PostgreSQL.

## Preparación
1. Instalar Java 17+, Maven, PostgreSQL y herramientas cliente (`pg_dump`, `pg_restore`, `pg_isready`).
2. Crear/configurar base de datos `cafegest` y credenciales propias.
3. Exportar `CAFEGEST_DB_PASSWORD` en la terminal (no subir la contraseña a Git). Alternativamente crear `application-local.properties` **solo en el equipo local**, a partir del ejemplo.
4. Ejecutar `mvn test`, `mvn package` y `mvn javafx:run` desde la carpeta que contiene `pom.xml`.
5. Para revisar logs: `tail -f logs/cafegest.log`.
6. Para comprobar disponibilidad: `./scripts/health_check.sh`. Esto comprueba disponibilidad PostgreSQL, **no toda la interfaz JavaFX**.
7. Para respaldar: `./scripts/backup_postgres.sh`; configurar `PGPASSWORD` o `.pgpass` con permisos 600, sin incrustar contraseñas en scripts.
8. Para restaurar: usar **base de datos de prueba vacía** y `./scripts/restore_postgres.sh ruta/archivo.dump --confirmar-restauracion`. Validar los datos restaurados.

## Ejemplo cron (Linux)
`0 2 * * * /ruta/absoluta/cafegest/scripts/backup_postgres.sh >> /ruta/segura/backup.log 2>&1`

**No se instaló cron automáticamente.** Configurar rutas y entorno de PostgreSQL antes de habilitarlo; realizar restauración de prueba.

## Evidencias aún pendientes
- Ejecución de Maven/JUnit y captura del resultado; validación con PostgreSQL real.
- Pruebas de roles, transacciones de ventas y casos de seguridad con datos de prueba.
- Evidencia de cron configurado, backup creado, restauración exitosa y métricas de rendimiento.
- Prueba gráfica de JavaFX en el equipo final de exposición.

# FitFlow_task_1
This is a task about a fitness tracker, but programmed in code.

## Conexión a base de datos

Al ejecutar la aplicación sin Docker, configura la URL de la base de datos con:

```
jdbc:mariadb://127.0.0.1:3308/fitflow_db
```

Puedes establecer esta cadena en la variable de entorno `SPRING_DATASOURCE_URL` antes de iniciar la aplicación.

## Autenticación y autorización GraphQL

Todas las operaciones GraphQL requieren un JWT válido en el encabezado `Authorization: Bearer …`, salvo las mutaciones `login` y `createUser`, que se mantienen abiertas para permitir el alta y la obtención de credenciales.

Los tokens deben incluir el claim `roles` con los permisos otorgados en formato `MODULO_PERMISO`.

- **Permiso de lectura (`AUDITOR`)**: habilita todas las *queries* del módulo correspondiente.
- **Permiso de escritura (`EDITOR`)**: habilita además las mutaciones del módulo.

### Mapeo de módulos

- `RUTINAS`: rutinas, usuarios y roles.
- `ACTIVIDADES`: hábitos y actividades de rutina.
- `GUIAS`: guías de bienestar.
- `PROGRESO`: registros de progreso y actividades completadas.
- `RECORDATORIOS`: recordatorios.

Para consumir los recursos protegidos, asigna al usuario un rol con el módulo indicado y el permiso adecuado, por ejemplo `GUIAS_AUDITOR` para leer guías o `GUIAS_EDITOR` para crearlas y editarlas.

# Huellario Backend

Backend REST de Huellario, implementado en TypeScript con Node.js, Express y MariaDB. La API ofrece registro e inicio de sesión de propietarios y operaciones sobre las mascotas asociadas a cada propietario.

## API REST y arquitectura

Una API REST expone recursos mediante rutas HTTP y formatos de intercambio como JSON. En Huellario, la aplicación Android envía peticiones HTTP con Retrofit; Express recibe las rutas, los controladores validan y traducen las solicitudes, los servicios aplican reglas de negocio, los repositorios ejecutan consultas parametrizadas y MariaDB conserva los datos. Las respuestas vuelven a Android como códigos HTTP y JSON.

Flujo típico: **Android/Retrofit → Express/ruta → middleware (si requiere JWT) → controlador → servicio → repositorio → MariaDB**.

El código fuente propio está en `src/` y `npm run build` lo compila a `dist/`. Producción ejecuta `dist/server.js`; los archivos `.cjs`/JSON de configuración de herramientas no son código fuente del backend.

## Requisitos

- Node.js 18 o posterior y npm.
- MariaDB con la base `huellario_db` y las tablas `users` y `pets` ya preparadas.
- Credenciales de base de datos configuradas en `.env`.

El backend no crea ni migra tablas automáticamente. El repositorio espera columnas de usuarios `id`, `name`, `email`, `password_hash` y de mascotas `id`, `user_id`, `name`, `species`, `species_detail`, `breed`, `birth_date`, `photo_url`, `created_at`. Comprueba que coincidan con el esquema existente antes de usar la API; no ejecutes scripts destructivos sobre datos reales.

Las migraciones recomendadas para la ampliación se encuentran en `migrations/` y están pensadas para aplicarlas de forma controlada sobre `huellario_db`.

## Configuración e inicio

Desde PowerShell, en esta carpeta:

```powershell
if (-not (Test-Path .env)) { Copy-Item .env.example .env }
npm install
npm run dev
```

Si `.env` ya existe, conserva ese archivo y no ejecutes el comando de copia. Completa `.env` con los datos locales de MariaDB y un `JWT_SECRET` aleatorio y privado. No compartas ni subas `.env`. Variables:

| Variable | Uso | Valor de desarrollo |
|---|---|---|
| `PORT` | Puerto HTTP de Express | `3000` |
| `DB_HOST` | Host de MariaDB | `localhost` |
| `DB_PORT` | Puerto de MariaDB | `3306` |
| `DB_NAME` | Base de datos existente | `huellario_db` |
| `DB_USER` / `DB_PASSWORD` | Credenciales de MariaDB | Configurar localmente |
| `JWT_SECRET` | Firma de tokens JWT | Sustituir por un secreto aleatorio |
| `JWT_EXPIRES_IN` | Vigencia del JWT | `1h` |

Comandos:

```powershell
npm run dev       # Desarrollo con recarga
npm run clean     # Elimina únicamente los archivos compilados de dist/
npm run build     # Compila src/ en dist/
npm test          # Pruebas automatizadas
npm start         # Ejecuta la versión compilada de dist/
```

Para producción, configura variables seguras del entorno, ejecuta `npm run build` y después `npm start`. No uses un secreto JWT de ejemplo ni expongas el servidor de desarrollo directamente a Internet.

## Endpoints

La URL base local es `http://localhost:3000/api`.

| Método | Ruta | Acceso | Función |
|---|---|---|---|
| `GET` | `/api/health` | Público | Comprueba servidor y conexión a MariaDB |
| `POST` | `/api/auth/register` | Público | Registra un propietario |
| `POST` | `/api/auth/login` | Público | Autentica y emite un JWT |
| `GET` | `/api/users/me` | JWT | Consulta el perfil del propietario autenticado |
| `PUT` | `/api/users/me` | JWT | Actualiza el nombre y correo del propietario autenticado |
| `GET` | `/api/pets` | JWT | Lista mascotas del propietario autenticado |
| `POST` | `/api/pets` | JWT | Registra una mascota para ese propietario |
| `GET` | `/api/pets/{id}` | JWT | Consulta una mascota propia |
| `PUT` | `/api/pets/{id}` | JWT | Actualiza una mascota propia |
| `DELETE` | `/api/pets/{id}` | JWT | Elimina una mascota propia |
| `POST` | `/api/pets/{id}/photo` | JWT | Sube foto multipart y actualiza `photo_url` |
| `GET` | `/api/routines` | JWT | Lista rutinas del usuario o por mascota |
| `POST` | `/api/routines` | JWT | Crea una rutina |
| `GET` | `/api/routines/daily` | JWT | Consulta programadas del día |
| `GET` | `/api/routines/history` | JWT | Consulta historial de cumplimiento |
| `POST` | `/api/routines/logs` | JWT | Registra estado de ejecución de una rutina |

Los endpoints de autenticación reciben JSON. Registro usa `fullName`, `email`, `password`; login usa `email`, `password`. Las rutas de mascotas reciben `name`, `species` (`PERRO`, `GATO`, `AVE`, `HAMSTER`, `PEZ` u `OTRO`), opcionalmente `species_detail`, `breed`, `birth_date` y `photo_url`. En la primera versión de subida de imágenes, el backend guarda archivos locales en `uploads/` y devuelve una URL pública servida desde `/uploads`.

Las rutinas usan `routine_logs` para guardar el estado de cada ocurrencia por fecha: `PENDIENTE`, `COMPLETADA` o `NO_FINALIZADA` (esta última se crea automáticamente al vencer la hora sin marcarla como completada). La hora se almacena como hora local de Colombia en formato `HH:mm:ss`. Las rutinas `SEMANAL` repiten el día de la semana de `start_date`; `DIAS_ESPECIFICOS` acepta `days_of_week` o `selected_days` como arreglo de días (0 = domingo, ..., 6 = sábado; también se aceptan nombres en español/inglés).

Aplica las migraciones nuevas `migrations/03_update_pet_species.sql` y `migrations/04_add_not_finished_routine_status.sql` una sola vez sobre la base de datos. La primera convierte registros `CUY` existentes a `HAMSTER` y agrega `PEZ` a la enumeración.

El perfil requiere `Authorization: Bearer <token>`. `GET /api/users/me` devuelve `{ "user": { "id", "fullName", "email" } }`. `PUT /api/users/me` recibe `fullName` y `email`, y devuelve `message` junto con el usuario actualizado. El identificador siempre se obtiene del JWT; la respuesta no incluye `password_hash`.

### Flujo de autenticación

1. Android envía `POST /api/auth/register` o `POST /api/auth/login`.
2. El backend guarda la contraseña con bcrypt (registro), o compara su hash con bcrypt (login), y responde con `token` y los datos públicos del usuario.
3. Android envía ese token en las rutas protegidas con la cabecera `Authorization: Bearer <token>`.
4. El middleware valida firma y expiración del JWT y obtiene el ID del propietario.
5. Las operaciones de perfil usan ese ID para consultar o actualizar únicamente al propietario autenticado. Cada operación de mascotas filtra también por ese ID; un propietario no puede acceder a mascotas de otro propietario.

## Swagger y comprobaciones manuales

Abre [http://localhost:3000/api-docs](http://localhost:3000/api-docs). En Swagger puedes registrar un usuario de prueba, iniciar sesión, pulsar **Authorize** e introducir el JWT; después puedes probar el perfil y las rutas de mascotas. Utiliza solamente datos controlados de prueba y elimina únicamente los que hayas creado de forma identificable.

Health-check:

```powershell
Invoke-RestMethod http://localhost:3000/api/health
```

La respuesta satisfactoria incluye `status: "ok"` y `database: "ok"`. Si devuelve `503`, revisa que MariaDB esté ejecutándose y que `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER` y `DB_PASSWORD` sean correctos.

## Conexión desde un teléfono Android por Wi-Fi

El servidor escucha en `0.0.0.0` en el puerto definido por `PORT` (por defecto `3000`), manteniendo disponible `localhost` en el portátil.

1. Conecta el portátil y el teléfono a la misma red Wi-Fi.
2. En Windows, ejecuta `ipconfig` y busca la dirección **IPv4** del adaptador Wi-Fi activo.
3. Configura la URL base Retrofit del teléfono con esa dirección real, por ejemplo `http://192.168.1.20:3000/api/`; reemplaza `192.168.1.20` por la IPv4 mostrada en tu equipo. Un teléfono físico no usa `10.0.2.2`.
4. Arranca el backend con `npm run dev` y prueba desde el teléfono una ruta como `/api/health`.
5. Si Windows Firewall bloquea la conexión, permite la aplicación/puerto TCP `3000` solo para el perfil **Privado** y la red local. No habilites una regla pública ni abras el puerto de MariaDB. Android debe acceder únicamente a Express.

Si la app Android usa HTTP sin TLS, cualquier permiso para tráfico HTTP sin cifrar debe limitarse al desarrollo en una red de confianza; producción debe usar HTTPS. La IP de la red local puede cambiar, así que vuelve a consultar `ipconfig` cuando el teléfono deje de conectar.

## Árbol del proyecto

```text
BACKEND/
├── .env.example
├── .gitignore
├── jest.config.cjs
├── package-lock.json
├── package.json
├── README.md
├── tsconfig.json
├── src/
│   ├── app.ts
│   ├── server.ts
│   ├── config/
│   │   ├── db.ts
│   │   └── env.ts
│   ├── controllers/
│   │   ├── auth.controller.ts
│   │   ├── pets.controller.ts
│   │   └── user.controller.ts
│   ├── docs/swagger.ts
│   ├── middlewares/auth.middleware.ts
│   ├── repositories/
│   │   ├── pet.repository.ts
│   │   └── user.repository.ts
│   ├── routes/
│   │   ├── auth.routes.ts
│   │   ├── pets.routes.ts
│   │   └── user.routes.ts
│   ├── services/
│   │   ├── auth.service.ts
│   │   ├── pet.service.ts
│   │   └── user.service.ts
│   ├── types/index.ts
│   ├── utils/
│   │   ├── errors.ts
│   │   └── validators.ts
│   └── __tests__/
│       ├── auth.service.test.ts
│       ├── pet.repository.test.ts
│       ├── swagger.test.ts
│       ├── user.repository.test.ts
│       ├── user.routes.test.ts
│       ├── user.service.test.ts
│       └── validators.test.ts
└── dist/                 # Salida generada por npm run build
```

## Responsabilidad de los archivos importantes

| Archivo | Responsabilidad |
|---|---|
| `src/server.ts` | Inicio HTTP y cierre ordenado; escucha en interfaces LAN. |
| `src/app.ts` | Middleware Express, health-check, rutas, Swagger y errores. |
| `src/config/env.ts` | Carga de variables de entorno y valores predeterminados de desarrollo. |
| `src/config/db.ts` | Pool MariaDB, consultas y prueba de conectividad. |
| `src/routes/` | Mapeo entre rutas HTTP y controladores. |
| `src/controllers/` | Validación de entrada y respuestas HTTP. |
| `src/services/` | Reglas de negocio, autenticación y autorización. |
| `src/repositories/` | Acceso parametrizado a MariaDB. |
| `src/middlewares/auth.middleware.ts` | Validación del JWT Bearer. |
| `src/utils/validators.ts` | Validación y normalización de payloads. |
| `src/utils/errors.ts` | Error HTTP y manejo de controladores asíncronos. |
| `src/docs/swagger.ts` | Definición y generación de OpenAPI. |
| `src/types/index.ts` | Tipos compartidos del backend. |
| `src/__tests__/` | Pruebas automatizadas de servicios, validación y Swagger. |
| `package.json` / `package-lock.json` | Scripts y versiones bloqueadas de dependencias. |
| `tsconfig.json` / `jest.config.cjs` | Opciones de TypeScript y Jest. |
| `.env.example` / `.gitignore` | Plantilla sin secretos y exclusión de archivos locales/generados. |

## Problemas frecuentes

- **Health-check en `503`:** confirma que MariaDB esté activo, que la base existente se llame `huellario_db` y que las credenciales de `.env` sean válidas.
- **El teléfono no conecta:** confirma misma Wi-Fi, usa la IPv4 actual del adaptador Wi-Fi, comprueba `PORT` y revisa una regla de firewall limitada a perfil Privado. No uses `localhost` desde el teléfono.
- **El registro/login falla:** revisa que `users` tenga las columnas esperadas y que el correo no exista ya; no imprimas ni compartas contraseñas o tokens.
- **Swagger no carga:** inicia el servidor desde esta carpeta y visita `http://localhost:3000/api-docs`.
- **`npm start` no encuentra `dist/server.js`:** ejecuta primero `npm run build`.

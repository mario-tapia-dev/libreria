# 📚 Libreria API

API REST para la gestión de una librería, desarrollada con **Spring Boot**. Permite administrar el catálogo de libros, los usuarios (socios) y los préstamos, con validación de datos, contraseñas cifradas y control automático de la disponibilidad de ejemplares. Además de la API, incluye una interfaz web sencilla construida con Thymeleaf.

## Tabla de contenido

- [Descripción de la API](#descripción-de-la-api)
- [Tecnologías utilizadas](#tecnologías-utilizadas)
- [Requisitos previos](#requisitos-previos)
- [Instalación y ejecución](#instalación-y-ejecución)
- [Variables de entorno](#variables-de-entorno)
- [Listado de endpoints](#listado-de-endpoints)
- [Validaciones y reglas de negocio](#validaciones-y-reglas-de-negocio)
- [Ejemplos de peticiones y respuestas](#ejemplos-de-peticiones-y-respuestas)
- [Interfaz web](#interfaz-web)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Notas y limitaciones conocidas](#notas-y-limitaciones-conocidas)

---

## Descripción de la API

Libreria API expone tres recursos principales:

| Recurso | Descripción |
|---------|-------------|
| **Libros** | Catálogo con título, autor, año de publicación y número de ejemplares disponibles. Se pueden buscar por autor o por título (búsqueda parcial, sin distinguir mayúsculas). |
| **Usuarios** | Socios registrados en la librería, con datos de contacto, rol y estado. Su contraseña se almacena cifrada con BCrypt y nunca se devuelve en las respuestas. |
| **Préstamos** | Relación entre un libro y un socio. Al registrar un préstamo se descuenta un ejemplar del libro; al registrar la devolución (o eliminar un préstamo activo) el ejemplar se recupera. |

**URL base por defecto:** `http://localhost:1501`

Los endpoints REST reciben y devuelven **JSON**. Los mensajes de error se devuelven como **texto plano**.

---

## Tecnologías utilizadas

| Tecnología | Versión | Uso |
|------------|---------|-----|
| Java | 21 | Lenguaje |
| Spring Boot | 4.1.1 | Framework principal |
| Spring Web MVC | — | API REST y controladores web |
| Spring Data JPA (Hibernate) | — | Persistencia y repositorios |
| Thymeleaf | — | Vistas HTML del lado del servidor |
| Spring Security Crypto (BCrypt) | — | Cifrado de contraseñas |
| MySQL | 8+ | Base de datos relacional |
| MySQL Connector/J | — | Driver JDBC |
| Maven (Maven Wrapper) | — | Gestión de dependencias y construcción |

> Solo se usa el módulo `spring-security-crypto` para cifrar contraseñas. **No** se incluye Spring Security, por lo que la API no tiene autenticación ni autorización.

---

## Requisitos previos

- **JDK 21** o superior
- **MySQL 8** o superior en ejecución
- **Git**
- (Opcional) Maven; no es necesario porque el proyecto incluye el Maven Wrapper (`mvnw` / `mvnw.cmd`)

Verifica tu versión de Java:

```bash
java -version
```

---

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone https://github.com/mario-tapia-dev/libreria.git
cd libreria
```

### 2. Crear la base de datos

La aplicación crea las **tablas** automáticamente (`spring.jpa.hibernate.ddl-auto=update`), pero la **base de datos** debe existir previamente:

```sql
CREATE DATABASE libreria CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configurar las credenciales

Las credenciales se leen de un archivo `.env` ubicado en la raíz del proyecto. Copia la plantilla incluida y edítala con tus datos:

**Linux / macOS**

```bash
cp .env.example .env
```

**Windows**

```bash
copy .env.example .env
```

Contenido del archivo `.env`:

```properties
DB_URL=jdbc:mysql://localhost:3306/libreria?serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=tu_contraseña
```

> El archivo `.env` está en el `.gitignore`, así que tus credenciales no se subirán al repositorio.

También puedes definir `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` como variables de entorno del sistema (consulta [Variables de entorno](#variables-de-entorno)).

### 4. Verificar `application.properties`

Comprueba que exista el archivo `src/main/resources/application.properties`. Si no está, créalo con este contenido:

```properties
spring.application.name=libreria

server.port=1501

# Cargar las variables del archivo .env
spring.config.import=optional:file:.env[.properties]

# Configuracion de MySQL
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/libreria?serverTimezone=UTC}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# Configuracion de JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

### 5. Ejecutar la aplicación

Ejecuta el comando **desde la raíz del proyecto** (la ruta del `.env` es relativa al directorio de trabajo).

**Linux / macOS**

```bash
./mvnw spring-boot:run
```

**Windows**

```bash
mvnw.cmd spring-boot:run
```

La API quedará disponible en `http://localhost:1501`.

### 6. (Opcional) Generar y ejecutar el JAR

```bash
./mvnw clean package -DskipTests
java -jar target/libreria-0.0.1-SNAPSHOT.jar
```

Ejecuta el JAR también desde la carpeta donde está el `.env`, o define las variables de entorno del sistema.

### 7. (Opcional) Ejecutar las pruebas

```bash
./mvnw test
```

> ⚠️ La prueba existente (`LibreriaApplicationTests`) levanta el contexto completo de Spring, por lo que requiere MySQL en ejecución y las variables `DB_USERNAME` y `DB_PASSWORD` configuradas.

---

## Variables de entorno

| Variable | Obligatoria | Valor por defecto | Descripción |
|----------|:-----------:|-------------------|-------------|
| `DB_URL` | No | `jdbc:mysql://localhost:3306/libreria?serverTimezone=UTC` | URL JDBC de la base de datos MySQL |
| `DB_USERNAME` | **Sí** | — | Usuario de MySQL |
| `DB_PASSWORD` | **Sí** | — | Contraseña del usuario de MySQL |

Si falta `DB_USERNAME` o `DB_PASSWORD`, la aplicación no arrancará (error al resolver el placeholder).

**Orden de prioridad:** las variables de entorno del sistema tienen prioridad sobre las del archivo `.env`.

**Linux / macOS**

```bash
export DB_URL="jdbc:mysql://localhost:3306/libreria?serverTimezone=UTC"
export DB_USERNAME="root"
export DB_PASSWORD="tu_contraseña"

./mvnw spring-boot:run
```

**Windows (PowerShell)**

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/libreria?serverTimezone=UTC"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="tu_contraseña"

.\mvnw.cmd spring-boot:run
```

Además, cualquier propiedad de Spring Boot puede sobrescribirse con su variable equivalente; por ejemplo, `SERVER_PORT=8080` cambia el puerto de la API.

> 🔒 No subas contraseñas reales al repositorio. Mantén `.env` fuera del control de versiones y comparte únicamente `.env.example`.

---

## Listado de endpoints

### Libros — `/libros`

| Método | Ruta | Descripción | Respuestas |
|--------|------|-------------|------------|
| `GET` | `/libros` | Lista todos los libros | `200` |
| `POST` | `/libros` | Registra un libro | `201`, `400` |
| `GET` | `/libros/{isbn}` | Obtiene un libro por ISBN | `200` |
| `PUT` | `/libros/{isbn}` | Actualiza un libro | `200`, `404` |
| `DELETE` | `/libros/{isbn}` | Elimina un libro | `204`, `404`, `409` |
| `GET` | `/libros/autor/{autor}` | Busca libros cuyo autor **contenga** el texto indicado | `200` |
| `GET` | `/libros/titulo/{titulo}` | Busca libros cuyo título **contenga** el texto indicado | `200` |

### Usuarios — `/usuarios`

| Método | Ruta | Descripción | Respuestas |
|--------|------|-------------|------------|
| `GET` | `/usuarios` | Lista todos los usuarios | `200` |
| `POST` | `/usuarios` | Registra un usuario | `201`, `400` |
| `GET` | `/usuarios/{id}` | Obtiene un usuario por ID | `200` |
| `PUT` | `/usuarios/{id}` | Actualiza un usuario | `200`, `404` |
| `DELETE` | `/usuarios/{id}` | Elimina un usuario | `204`, `404`, `409` |

### Préstamos — `/prestamos`

| Método | Ruta | Descripción | Respuestas |
|--------|------|-------------|------------|
| `GET` | `/prestamos` | Lista todos los préstamos | `200` |
| `POST` | `/prestamos?libroIsbn={isbn}&socioId={id}` | Registra un préstamo (descuenta un ejemplar). Los datos van como **query params**, no como JSON | `200`, `400` |
| `GET` | `/prestamos/{id}` | Obtiene un préstamo por ID | `200` |
| `PUT` | `/prestamos/{id}/devolucion` | Registra la devolución (recupera el ejemplar) | `200`, `404` |
| `DELETE` | `/prestamos/{id}` | Elimina un préstamo (si estaba activo, recupera el ejemplar) | `200` |

### Códigos de respuesta

| Código | Cuándo se devuelve |
|--------|--------------------|
| `200 OK` | Consultas, actualizaciones y operaciones de préstamos exitosas |
| `201 Created` | Creación exitosa de un libro o un usuario |
| `204 No Content` | Eliminación exitosa de un libro o un usuario (sin cuerpo) |
| `400 Bad Request` | Datos inválidos al crear un libro o usuario; error al registrar un préstamo |
| `404 Not Found` | Recurso inexistente en `PUT` o `DELETE`. **Importante:** en `PUT /libros` y `PUT /usuarios` también se usa para los errores de validación (ver [notas](#notas-y-limitaciones-conocidas)) |
| `409 Conflict` | Al eliminar un libro o usuario que tiene préstamos registrados |

---

## Validaciones y reglas de negocio

### Libros

| Campo | Regla |
|-------|-------|
| `titulo` | Obligatorio |
| `autor` | Obligatorio |
| `añoPublicacion` | Obligatorio; exactamente 4 dígitos; entre `0001` y el año actual |
| `numeroEjemplares` | No puede ser negativo |

### Usuarios

| Campo | Regla |
|-------|-------|
| `nombre` | Obligatorio |
| `correo` | Obligatorio, con formato válido y **único** (sin distinguir mayúsculas) |
| `contrasena` | Obligatoria al crear; mínimo 8 caracteres y máximo 72 bytes en UTF-8. Se guarda cifrada con BCrypt y **nunca se devuelve** en las respuestas |
| `rol` | Obligatorio |
| `estado` | Obligatorio |
| `fechaCreacion`, `fechaNacimiento`, `telefono`, `direccion` | Opcionales, sin validación de formato |

Al **actualizar** un usuario:

- `nombre`, `correo`, `rol` y `estado` siguen siendo obligatorios.
- `contrasena` es opcional: si no se envía, se conserva la actual; si se envía, se valida y se vuelve a cifrar.
- `fechaNacimiento`, `telefono` y `direccion` solo cambian si se envían con valor; si vienen vacíos o ausentes, se conserva el valor existente.
- `fechaCreacion` no se modifica.

### Préstamos

- No se puede prestar un libro o a un usuario que no existe.
- No se puede prestar un libro sin ejemplares disponibles.
- Un préstamo no puede devolverse dos veces.
- La fecha de préstamo se asigna automáticamente.
- Préstamo, devolución y eliminación son transaccionales: el stock y el préstamo se actualizan juntos.

### Eliminaciones

Un libro o un usuario **no puede eliminarse si tiene algún préstamo registrado**, incluso si ya fue devuelto. Primero hay que eliminar sus préstamos con `DELETE /prestamos/{id}`.

---

## Ejemplos de peticiones y respuestas

Los ejemplos usan `curl` y la URL base `http://localhost:1501`.

### Libros

#### Crear un libro

```bash
curl -X POST http://localhost:1501/libros \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Cien años de soledad",
    "autor": "Gabriel García Márquez",
    "añoPublicacion": "1967",
    "numeroEjemplares": 5
  }'
```

**Respuesta** `201 Created`

```json
{
  "isbn": 1,
  "titulo": "Cien años de soledad",
  "autor": "Gabriel García Márquez",
  "añoPublicacion": "1967",
  "numeroEjemplares": 5
}
```

> El campo `isbn` se genera automáticamente; no es necesario enviarlo.

**Respuesta con datos inválidos** `400 Bad Request` (texto plano)

```text
El año de publicación debe tener cuatro dígitos.
```

Otros mensajes posibles: `El título es obligatorio.`, `El autor es obligatorio.`, `El año de publicación no es válido.`, `Los ejemplares disponibles no pueden ser negativos.`

#### Listar libros

```bash
curl http://localhost:1501/libros
```

**Respuesta** `200 OK`

```json
[
  {
    "isbn": 1,
    "titulo": "Cien años de soledad",
    "autor": "Gabriel García Márquez",
    "añoPublicacion": "1967",
    "numeroEjemplares": 5
  }
]
```

#### Obtener un libro por ISBN

```bash
curl http://localhost:1501/libros/1
```

**Respuesta** `200 OK`

```json
{
  "isbn": 1,
  "titulo": "Cien años de soledad",
  "autor": "Gabriel García Márquez",
  "añoPublicacion": "1967",
  "numeroEjemplares": 5
}
```

#### Actualizar un libro

Se deben enviar todos los campos del libro.

```bash
curl -X PUT http://localhost:1501/libros/1 \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Cien años de soledad",
    "autor": "Gabriel García Márquez",
    "añoPublicacion": "1967",
    "numeroEjemplares": 8
  }'
```

**Respuesta** `200 OK`

```json
{
  "isbn": 1,
  "titulo": "Cien años de soledad",
  "autor": "Gabriel García Márquez",
  "añoPublicacion": "1967",
  "numeroEjemplares": 8
}
```

**Respuesta si el libro no existe** `404 Not Found` (texto plano)

```text
No existe un libro con ISBN: 99
```

#### Buscar por autor o título

La búsqueda es **parcial** y **no distingue mayúsculas**. Si el valor contiene espacios o caracteres especiales, debe ir codificado en la URL (por ejemplo, `%20` para el espacio).

```bash
curl http://localhost:1501/libros/autor/garcia
curl "http://localhost:1501/libros/titulo/cien%20a%C3%B1os"
```

**Respuesta** `200 OK`

```json
[
  {
    "isbn": 1,
    "titulo": "Cien años de soledad",
    "autor": "Gabriel García Márquez",
    "añoPublicacion": "1967",
    "numeroEjemplares": 8
  }
]
```

Si no hay coincidencias, se devuelve un arreglo vacío (`[]`).

#### Eliminar un libro

```bash
curl -X DELETE http://localhost:1501/libros/1 -i
```

**Respuesta** `204 No Content` (sin cuerpo)

**Respuesta si el libro no existe** `404 Not Found` (sin cuerpo)

**Respuesta si el libro tiene préstamos** `409 Conflict` (texto plano)

```text
No se puede eliminar el libro porque tiene préstamos registrados.
```

---

### Usuarios

#### Crear un usuario

```bash
curl -X POST http://localhost:1501/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Ana López",
    "correo": "ana.lopez@example.com",
    "contrasena": "secreto123",
    "rol": "SOCIO",
    "estado": "ACTIVO",
    "fechaCreacion": "2026-10-14",
    "fechaNacimiento": "1995-04-21",
    "telefono": "2291234567",
    "direccion": "Av. Principal 123, Veracruz"
  }'
```

**Respuesta** `201 Created`

```json
{
  "id": 1,
  "nombre": "Ana López",
  "correo": "ana.lopez@example.com",
  "rol": "SOCIO",
  "estado": "ACTIVO",
  "fechaCreacion": "2026-10-14",
  "fechaNacimiento": "1995-04-21",
  "telefono": "2291234567",
  "direccion": "Av. Principal 123, Veracruz"
}
```

> La contraseña se guarda cifrada con BCrypt y **no aparece** en ninguna respuesta.

**Respuestas con datos inválidos** `400 Bad Request` (texto plano)

```text
Debes proporcionar un correo válido.
```

```text
La contraseña debe tener al menos 8 caracteres.
```

```text
Ya existe un usuario con ese correo.
```

Otros mensajes posibles: `El nombre es obligatorio.`, `El rol es obligatorio.`, `El estado es obligatorio.`, `La contraseña es obligatoria.`, `La contraseña es demasiado larga; utiliza como máximo 72 bytes en UTF-8.`

#### Listar usuarios

```bash
curl http://localhost:1501/usuarios
```

**Respuesta** `200 OK`

```json
[
  {
    "id": 1,
    "nombre": "Ana López",
    "correo": "ana.lopez@example.com",
    "rol": "SOCIO",
    "estado": "ACTIVO",
    "fechaCreacion": "2026-10-14",
    "fechaNacimiento": "1995-04-21",
    "telefono": "2291234567",
    "direccion": "Av. Principal 123, Veracruz"
  }
]
```

#### Obtener un usuario por ID

```bash
curl http://localhost:1501/usuarios/1
```

#### Actualizar un usuario

En este ejemplo no se envía `contrasena`, por lo que se conserva la actual.

```bash
curl -X PUT http://localhost:1501/usuarios/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Ana López Pérez",
    "correo": "ana.lopez@example.com",
    "rol": "SOCIO",
    "estado": "ACTIVO",
    "telefono": "2297654321",
    "direccion": "Calle Reforma 45, Veracruz"
  }'
```

**Respuesta** `200 OK`

```json
{
  "id": 1,
  "nombre": "Ana López Pérez",
  "correo": "ana.lopez@example.com",
  "rol": "SOCIO",
  "estado": "ACTIVO",
  "fechaCreacion": "2026-10-14",
  "fechaNacimiento": "1995-04-21",
  "telefono": "2297654321",
  "direccion": "Calle Reforma 45, Veracruz"
}
```

**Respuesta si el usuario no existe** `404 Not Found` (texto plano)

```text
No existe un usuario con ID: 99
```

#### Eliminar un usuario

```bash
curl -X DELETE http://localhost:1501/usuarios/1 -i
```

**Respuesta** `204 No Content` (sin cuerpo)

**Respuesta si el usuario no existe** `404 Not Found` (sin cuerpo)

**Respuesta si el usuario tiene préstamos** `409 Conflict` (texto plano)

```text
No se puede eliminar el usuario porque tiene préstamos registrados.
```

---

### Préstamos

#### Registrar un préstamo

Los datos se envían como **parámetros de consulta** (no como cuerpo JSON).

```bash
curl -X POST "http://localhost:1501/prestamos?libroIsbn=1&socioId=1"
```

**Respuesta** `200 OK`

```json
{
  "id": 1,
  "libro": {
    "isbn": 1,
    "titulo": "Cien años de soledad",
    "autor": "Gabriel García Márquez",
    "añoPublicacion": "1967",
    "numeroEjemplares": 4
  },
  "socio": {
    "id": 1,
    "nombre": "Ana López Pérez",
    "correo": "ana.lopez@example.com",
    "rol": "SOCIO",
    "estado": "ACTIVO",
    "fechaCreacion": "2026-10-14",
    "fechaNacimiento": "1995-04-21",
    "telefono": "2297654321",
    "direccion": "Calle Reforma 45, Veracruz"
  },
  "fechaPrestamo": "2026-10-14T08:15:30.412",
  "fechaDevolucion": null
}
```

> En este ejemplo el libro tenía 5 ejemplares; `numeroEjemplares` bajó a 4 al registrar el préstamo.

**Respuestas de error** `400 Bad Request` (texto plano)

```text
No existe un libro con ISBN: 99
```

```text
No existe un usuario con ID: 99
```

```text
No hay ejemplares disponibles de este libro.
```

#### Listar préstamos

```bash
curl http://localhost:1501/prestamos
```

**Respuesta** `200 OK`: arreglo con los préstamos (misma estructura del ejemplo anterior).

#### Obtener un préstamo por ID

```bash
curl http://localhost:1501/prestamos/1
```

#### Registrar la devolución

```bash
curl -X PUT http://localhost:1501/prestamos/1/devolucion
```

**Respuesta** `200 OK`

```json
{
  "id": 1,
  "libro": {
    "isbn": 1,
    "titulo": "Cien años de soledad",
    "autor": "Gabriel García Márquez",
    "añoPublicacion": "1967",
    "numeroEjemplares": 5
  },
  "socio": {
    "id": 1,
    "nombre": "Ana López Pérez",
    "correo": "ana.lopez@example.com",
    "rol": "SOCIO",
    "estado": "ACTIVO",
    "fechaCreacion": "2026-10-14",
    "fechaNacimiento": "1995-04-21",
    "telefono": "2297654321",
    "direccion": "Calle Reforma 45, Veracruz"
  },
  "fechaPrestamo": "2026-10-14T08:15:30.412",
  "fechaDevolucion": "2026-10-14T09:02:11.087"
}
```

**Respuestas de error** `404 Not Found` (texto plano)

```text
No existe un préstamo con ID: 99
```

```text
Este préstamo ya fue devuelto.
```

#### Eliminar un préstamo

```bash
curl -X DELETE http://localhost:1501/prestamos/1
```

**Respuesta** `200 OK` (texto plano)

```text
Se eliminó el préstamo con ID: 1
```

Si el préstamo no existe (también `200 OK`):

```text
No se pudo eliminar el préstamo con ID: 99
```

---

## Interfaz web

El proyecto incluye vistas HTML (Thymeleaf) para gestionar la información desde el navegador, sin necesidad de herramientas como Postman o `curl`. Los mensajes de éxito y de error (por ejemplo, validaciones) se muestran en pantalla.

| Ruta | Descripción |
|------|-------------|
| `http://localhost:1501/` o `/vista/libros` | Gestión de libros |
| `http://localhost:1501/vista/usuarios` | Gestión de usuarios |
| `http://localhost:1501/vista/prestamos` | Gestión de préstamos y devoluciones |

Estas vistas utilizan rutas `POST` internas (`/vista/.../guardar`, `/vista/.../{id}/actualizar`, `/vista/.../{id}/eliminar`, `/vista/prestamos/{id}/devolucion`) que redirigen de vuelta a la pantalla correspondiente. No forman parte de la API REST.

---

## Estructura del proyecto

```text
libreria/
├── pom.xml
├── mvnw / mvnw.cmd
├── .env.example                                  # Plantilla de credenciales
├── .env                                          # Credenciales locales (no se versiona)
└── src/
    ├── main/
    │   ├── java/com/api/libreria/
    │   │   ├── LibreriaApplication.java
    │   │   ├── controllers/
    │   │   │   ├── LibroController.java          # API REST de libros
    │   │   │   ├── UsuarioController.java        # API REST de usuarios
    │   │   │   ├── PrestamoController.java       # API REST de préstamos
    │   │   │   ├── LibroVistaController.java     # Vistas web de libros
    │   │   │   ├── UsuarioVistaController.java   # Vistas web de usuarios
    │   │   │   └── PrestamoVistaController.java  # Vistas web de préstamos
    │   │   ├── models/                           # Entidades JPA
    │   │   │   ├── LibroModel.java
    │   │   │   ├── UsuarioModel.java
    │   │   │   └── PrestamoModel.java
    │   │   ├── repositories/                     # Repositorios Spring Data
    │   │   └── services/                         # Lógica de negocio y validaciones
    │   └── resources/
    │       ├── application.properties
    │       └── templates/                        # Plantillas Thymeleaf
    └── test/
```

### Modelo de datos

- **libros**: `isbn` (PK, autogenerado), `titulo`, `autor`, `añoPublicacion`, `numeroEjemplares`
- **usuarios**: `id` (PK, autogenerado), `nombre`, `correo`, `contrasena` (hash BCrypt, no nula), `rol`, `estado`, `fechaCreacion`, `fechaNacimiento`, `telefono`, `direccion`
- **prestamos**: `id` (PK, autogenerado), `libro_isbn` (FK → libros), `socio_id` (FK → usuarios), `fechaPrestamo`, `fechaDevolucion` (nula mientras el préstamo está activo)

---

## Notas y limitaciones conocidas

- **Sin autenticación ni autorización:** todos los endpoints son públicos. Las contraseñas se cifran, pero no existe un endpoint de inicio de sesión ni protección de rutas.
- **`PUT` y los códigos de error:** `PUT /libros/{isbn}` y `PUT /usuarios/{id}` devuelven `404` ante *cualquier* error, incluidos los de validación (por ejemplo, un correo duplicado o un año inválido). Lee el mensaje de la respuesta para distinguir la causa real.
- **Consulta de un recurso inexistente:** `GET /libros/{isbn}`, `GET /usuarios/{id}` y `GET /prestamos/{id}` no devuelven `404` cuando el registro no existe, sino una respuesta `200` sin datos.
- **Búsquedas con texto en blanco:** `/libros/autor/{autor}` y `/libros/titulo/{titulo}` lanzan un error no controlado si el valor está en blanco (por ejemplo, `%20`), que se traduce en un `500`.
- **Préstamos:** `DELETE /prestamos/{id}` siempre responde `200`; el éxito o el fallo se indica solo en el texto. `POST /prestamos` no distingue entre recursos inexistentes y falta de ejemplares más que por el mensaje (`400` en ambos casos).
- **Fechas como texto:** `fechaCreacion` y `fechaNacimiento` de los usuarios se guardan como texto sin formato impuesto; se recomienda usar `AAAA-MM-DD`.
- **Validación limitada:** al crear un libro no se recortan espacios de `titulo` y `autor`, y no se impiden libros duplicados.

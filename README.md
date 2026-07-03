# TechStore API

Microservicio RESTful en Java con Spring Boot para administrar un catalogo de productos de la tienda ficticia TechStore Chile.

## Caracteristicas

- CRUD de productos mediante endpoints REST.
- Listado completo de productos.
- Autenticacion JWT.
- Persistencia en PostgreSQL con Docker.
- Empaquetado como JAR ejecutable con Maven.
- Pruebas automatizadas con Spring Boot Test y H2 en memoria.

## Estructura principal

```text
src/main/java/cl/techstore/api/
|-- controller/
|   |-- AuthController.java
|   `-- ProductoController.java
|-- dto/
|   |-- LoginRequest.java
|   |-- LoginResponse.java
|   `-- ProductoDTO.java
|-- model/
|   `-- Producto.java
|-- repository/
|   `-- ProductoRepository.java
|-- security/
|   |-- JwtFilter.java
|   |-- JwtUtil.java
|   `-- SecurityConfig.java
|-- service/
|   `-- ProductoService.java
`-- TechstoreApiApplication.java
```

## Requisitos

- Java 17
- Maven 3.9+
- Docker Desktop

## Levantar el proyecto con Docker

Desde la carpeta raiz `techstore-api`:

```bash
docker compose up --build
```

Servicios disponibles:

- API: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

## Credenciales por defecto

Autenticacion JWT:

- Usuario: `admin`
- Contrasena: `Admin12345`

Base de datos PostgreSQL:

- Base de datos: `techstore`
- Usuario: `techstore`
- Contrasena: `techstore`

## Datos iniciales

Al levantar PostgreSQL se ejecuta el script [init.sql](file:///d:/Duoc/2026/Java%20Dise%C3%B1o%20y%20construccion%20de%20soluciones%20nativas%20en%20nube/Prueba%203/Techstore-app/techstore-api/docker/postgres/init.sql), que crea la tabla `products` y carga 5 productos de ejemplo.

## Autenticacion

Endpoint de login:

```http
POST /auth/login
Content-Type: application/json
```

Body:

```json
{
  "username": "admin",
  "password": "Admin12345"
}
```

Respuesta esperada:

```json
{
  "token": "JWT_AQUI",
  "tipo": "Bearer",
  "expiracion": 3600000
}
```

Para consumir endpoints protegidos:

```http
Authorization: Bearer TU_TOKEN
```

## Endpoints

### Productos

- `GET /api/productos` -> lista todos los productos.
- `GET /api/productos/{id}` -> obtiene un producto por id.
- `POST /api/productos` -> crea un producto.
- `PUT /api/productos/{id}` -> actualiza un producto.
- `DELETE /api/productos/{id}` -> elimina un producto.

## Codigos HTTP esperados

- `GET /api/productos` -> `200 OK`
- `POST /api/productos` -> `201 Created`
- `PUT /api/productos/{id}` -> `200 OK`
- `DELETE /api/productos/{id}` -> `204 No Content`
- `GET /api/productos/{id}` con id inexistente -> `404 Not Found`

## Ejemplo de producto

```json
{
  "nombre": "Tablet Samsung Galaxy Tab A9",
  "descripcion": "Tablet de prueba",
  "precio": 129990,
  "stock": 14,
  "categoria": "Tablets",
  "activo": true
}
```

## Ejecutar sin Docker

Primero debes tener PostgreSQL disponible y configurar:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `APP_SECURITY_USERNAME`
- `APP_SECURITY_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_MS`

Luego puedes ejecutar:

```bash
mvn spring-boot:run
```

## Comandos Maven

Compilar:

```bash
mvn compile
```

Ejecutar pruebas:

```bash
mvn test
```

Generar JAR:

```bash
mvn clean package -DskipTests
```

Ejecutar el JAR:

```bash
java -jar target/techstore-api-0.0.1-SNAPSHOT.jar
```

## Pruebas

El proyecto incluye:

- pruebas de integracion para login JWT.
- pruebas de integracion para CRUD de productos.
- perfil `test` con H2 en memoria en `src/test/resources/application-test.properties`.

## Archivos importantes

- [pom.xml](file:///d:/Duoc/2026/Java%20Dise%C3%B1o%20y%20construccion%20de%20soluciones%20nativas%20en%20nube/Prueba%203/Techstore-app/techstore-api/pom.xml)
- [docker-compose.yml](file:///d:/Duoc/2026/Java%20Dise%C3%B1o%20y%20construccion%20de%20soluciones%20nativas%20en%20nube/Prueba%203/Techstore-app/techstore-api/docker-compose.yml)
- [Dockerfile](file:///d:/Duoc/2026/Java%20Dise%C3%B1o%20y%20construccion%20de%20soluciones%20nativas%20en%20nube/Prueba%203/Techstore-app/techstore-api/Dockerfile)
- [application.properties](file:///d:/Duoc/2026/Java%20Dise%C3%B1o%20y%20construccion%20de%20soluciones%20nativas%20en%20nube/Prueba%203/Techstore-app/techstore-api/src/main/resources/application.properties)

## Estado actual

El proyecto fue validado con exito en:

- compilacion Maven
- pruebas automatizadas
- ejecucion real con Docker
- login JWT
- CRUD completo contra PostgreSQL

# TechStore API

Microservicio RESTful desarrollado con Java 17 y Spring Boot para la gestion de productos de TechStore Chile.

## Estado del proyecto

El proyecto se encuentra operativo y validado tanto en entorno local como en AWS.

- Autenticacion JWT funcionando.
- CRUD de productos funcionando.
- Borrado logico implementado.
- Auditoria asincrona con SQS y Lambda validada.
- Despliegue en AWS con RDS, ECS Fargate, ALB y API Gateway validado.
- Workflow de GitHub Actions preparado para CI/CD.

Por tratarse de un repositorio publico, la URL desplegada y las credenciales activas no se versionan en este documento.

## Arquitectura actual

La solucion quedo compuesta por los siguientes servicios:

- `API Gateway HTTP API` para exponer la API publicamente.
- `Application Load Balancer` como punto de entrada hacia ECS.
- `Amazon ECS Fargate` para ejecutar el contenedor de la API.
- `Amazon RDS PostgreSQL` para persistencia.
- `Amazon ECR` para almacenar la imagen Docker.
- `Amazon SQS` para la auditoria asincrona.
- `AWS Lambda` para consumir mensajes de auditoria.
- `Amazon CloudWatch Logs` para trazabilidad y monitoreo.

Flujo general:

```text
Cliente -> API Gateway -> ALB -> ECS Fargate -> RDS PostgreSQL
                               |
                               -> SQS -> Lambda -> CloudWatch Logs
```

## Caracteristicas principales

- CRUD REST de productos.
- Endpoints protegidos con JWT bajo `/api/productos`.
- Login publico en `/auth/login`.
- Persistencia en PostgreSQL.
- Borrado logico de productos con campo `activo`.
- Auditoria de operaciones `POST`, `PUT` y `DELETE`.
- Pruebas automatizadas con Spring Boot Test y H2.
- Dockerfile y `docker-compose.yml` para entorno local.

## Estructura principal

```text
src/main/java/cl/techstore/api/
|-- config/
|   `-- SqsConfig.java
|-- controller/
|   |-- AuthController.java
|   `-- ProductoController.java
|-- dto/
|   |-- AuditoriaEvento.java
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
|   |-- AuditoriaService.java
|   `-- ProductoService.java
`-- TechstoreApiApplication.java
```

## Requisitos

- Java 17
- Maven 3.9+
- Docker Desktop
- Cuenta AWS Academy Learner Lab para despliegue en nube

## Configuracion por variables de entorno

La aplicacion utiliza variables de entorno para base de datos, seguridad y AWS.

Variables principales:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `APP_SECURITY_USERNAME`
- `APP_SECURITY_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_MS`
- `AWS_REGION`
- `AUDIT_QUEUE_URL`

Valores por defecto locales definidos en `src/main/resources/application.properties`:

- `APP_SECURITY_USERNAME=local-admin@example.com`
- `APP_SECURITY_PASSWORD=change-me-local-password`
- `JWT_EXPIRATION_MS=3600000`
- `AWS_REGION=us-east-1`

## Ejecucion local con Docker

Desde la carpeta raiz del proyecto:

```bash
docker compose up --build
```

Servicios disponibles:

- API: `http://localhost:8080`
- PostgreSQL: `localhost:5432`

Credenciales locales por defecto:

- Usuario API: `local-admin@example.com`
- Contrasena API: `change-me-local-password`
- Base de datos: `techstore`
- Usuario DB: `techstore`
- Contrasena DB: `techstore`

## Ejecucion local sin Docker

Primero debes tener PostgreSQL disponible y configurar las variables de entorno necesarias. Luego puedes iniciar la aplicacion con:

```bash
mvn spring-boot:run
```

## Datos iniciales

El script [`docker/postgres/init.sql`](docker/postgres/init.sql) crea la tabla `products` y carga 5 productos de ejemplo.

Este script fue utilizado tambien para inicializar la base PostgreSQL desplegada en RDS.

## Autenticacion

Endpoint publico:

```http
POST /auth/login
Content-Type: application/json
```

Body de ejemplo:

```json
{
  "username": "usuario-configurado@example.com",
  "password": "tu-password-configurada"
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

## Endpoints principales

- `POST /auth/login` -> login publico.
- `GET /api/productos` -> lista productos activos.
- `GET /api/productos/{id}` -> obtiene un producto activo por id.
- `POST /api/productos` -> crea un producto y registra auditoria.
- `PUT /api/productos/{id}` -> actualiza un producto y registra auditoria.
- `DELETE /api/productos/{id}` -> realiza borrado logico y registra auditoria.

## Codigos HTTP esperados

- `POST /auth/login` -> `200 OK`
- `GET /api/productos` con token -> `200 OK`
- `GET /api/productos` sin token -> `403 Forbidden`
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

## Auditoria asincrona

Cada vez que se crea, modifica o elimina logicamente un producto, la API envia un mensaje JSON a la cola SQS configurada en `AUDIT_QUEUE_URL`.

Ejemplo de evento:

```json
{
  "accion": "CREAR",
  "productoId": 12,
  "usuario": "usuario-configurado@example.com",
  "fecha": "2026-07-03T20:49:39Z"
}
```

La cola es consumida por una funcion Lambda que registra la auditoria en CloudWatch Logs.

## Despliegue en AWS

Recursos utilizados en la version desplegada:

- `Amazon RDS PostgreSQL`
- `Amazon ECR` repositorio `techstore-api`
- `Amazon ECS Fargate`
- `Application Load Balancer`
- `Amazon API Gateway HTTP API`
- `Amazon SQS` cola `techstore-audit-queue`
- `AWS Lambda` funcion `techstore-audit-logger`
- `LabRole` como rol obligatorio del laboratorio

Estado validado del despliegue:

- Login publico operativo.
- Rutas protegidas con JWT operativas.
- CRUD operativo.
- Auditoria asincrona validada.
- Target Group healthy en ECS.

## CI/CD

Se incluyo el workflow [`deploy.yml`](.github/workflows/deploy.yml) para automatizar:

- ejecucion de pruebas Maven
- build de imagen Docker
- push a Amazon ECR
- actualizacion del servicio ECS

Para ejecutarlo en GitHub Actions se deben configurar estos secretos:

- `AWS_ACCOUNT_ID`
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_SESSION_TOKEN`
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `APP_SECURITY_USERNAME`
- `APP_SECURITY_PASSWORD`
- `JWT_SECRET`
- `AUDIT_QUEUE_URL`

Nota: en AWS Academy las credenciales son temporales, por lo que estos secretos deben actualizarse cada vez que se reinicia la sesion del laboratorio.

La URL publica de API Gateway, el endpoint real de RDS y cualquier credencial efectiva deben mantenerse fuera del repositorio y gestionarse mediante variables de entorno o `GitHub Secrets`.

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

## Pruebas automatizadas

El proyecto incluye:

- pruebas de integracion para login JWT
- pruebas de integracion para CRUD de productos
- perfil `test` con H2 en memoria en `src/test/resources/application-test.properties`

Resultado local validado:

- `mvn test` exitoso
- `mvn clean package -DskipTests` exitoso

## Archivos importantes

- [`pom.xml`](pom.xml)
- [`Dockerfile`](Dockerfile)
- [`docker-compose.yml`](docker-compose.yml)
- [`task-def.example.json`](task-def.example.json)
- [`src/main/resources/application.properties`](src/main/resources/application.properties)
- [`.github/workflows/deploy.yml`](.github/workflows/deploy.yml)

## Nota de seguridad

Antes de subir el proyecto a GitHub, revisa que no se publiquen secretos reales de AWS o contrasenas productivas en archivos versionados.

Este repositorio utiliza `task-def.example.json` como plantilla publica y deja `task-def.json` ignorado en `.gitignore` para evitar subir credenciales o configuraciones sensibles.

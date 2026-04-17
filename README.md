# Parking System

Sistema de gestión de estacionamiento desarrollado con Spring Boot y Thymeleaf.

## Tecnologías

- Java 21
- Spring Boot 4.0.5
- Spring Security (BCrypt, roles)
- Spring Data JPA + H2 (en memoria)
- Thymeleaf + Thymeleaf Security Extras
- Lombok

## Funcionalidades

- Autenticación con dos roles: `ADMIN` y `WORKER`
- Registro de entrada y salida de vehículos
- Cálculo de costo con precio congelado por 5 minutos
- Historial de salidas con filtro por fecha
- Gestión de usuarios y tarifa (solo admin)
- Eliminación de registros con restricción por tiempo y rol

## Credenciales por defecto

| Usuario | Contraseña | Rol   |
|---------|-----------|-------|
| admin   | Admin123  | Admin |
| worker  | Worker123 | Worker |

## Cómo ejecutar

```bash
./mvnw spring-boot:run
```

Abrir en `http://localhost:8080`

Consola H2 disponible en `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:parkingdb`
- Usuario: `sa`
- Contraseña: *(vacío)*

## Roadmap

| Fase | Estado | Descripción |
|------|--------|-------------|
| v1 - Thymeleaf | Completo | App web monolítica con Spring Boot |
| v2 - API REST | Pendiente | Migración a REST + documentación Swagger |
| v3 - React | Pendiente | Frontend React consumiendo la API |
| v4 - Desktop | Pendiente | App de escritorio con Java Swing |
# Parking System

Sistema de gestión de estacionamiento con backend en Spring Boot (API REST) y frontend en React.

## Tecnologías

**Backend**
- Java 21
- Spring Boot 4.0.5
- Spring Security (BCrypt, roles, sesión por cookie)
- Spring Data JPA + H2 (en memoria)
- Lombok

**Frontend**
- React 19 + Vite
- react-router-dom v7
- API nativa `fetch` con `credentials: 'include'`

## Funcionalidades

- Autenticación con dos roles: `ADMIN` y `WORKER`
- Registro de entrada y salida de vehículos
- Edición de patente en vehículos activos
- Cálculo de costo con precio congelado por 5 minutos
- Historial de salidas con filtro por fecha
- Gestión de usuarios y tarifa (solo admin)
- Eliminación de registros con restricción por tiempo y rol

## API REST

| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| POST | `/api/auth/login` | Público | Iniciar sesión |
| GET | `/api/auth/me` | Sesión | Obtener usuario actual |
| POST | `/api/auth/logout` | Sesión | Cerrar sesión |
| GET | `/api/parking` | ADMIN/WORKER | Vehículos activos |
| GET | `/api/parking/settings` | ADMIN/WORKER | Costo por minuto |
| POST | `/api/parking/entry` | ADMIN/WORKER | Registrar entrada |
| PUT | `/api/parking/{id}` | ADMIN/WORKER | Editar patente |
| POST | `/api/parking/calculate/{plate}` | ADMIN/WORKER | Calcular costo |
| DELETE | `/api/parking/calculate/{plate}` | ADMIN/WORKER | Cancelar cotización |
| POST | `/api/parking/exit` | ADMIN/WORKER | Registrar salida |
| DELETE | `/api/parking/{plate}` | ADMIN/WORKER | Eliminar entrada |
| GET | `/api/history` | ADMIN/WORKER | Historial (opcional `?date=`) |
| DELETE | `/api/history/{id}` | ADMIN | Eliminar registro |
| GET | `/api/admin` | ADMIN | Usuarios y tarifa |
| POST | `/api/admin` | ADMIN | Crear usuario |
| PUT | `/api/admin/{id}` | ADMIN | Habilitar/deshabilitar |
| DELETE | `/api/admin/{id}` | ADMIN | Eliminar usuario |
| PUT | `/api/admin/cost` | ADMIN | Actualizar tarifa |

## Credenciales por defecto

| Usuario | Contraseña | Rol |
|---------|-----------|------|
| admin | Admin123 | Admin |
| worker | Worker123 | Worker |

## Cómo ejecutar

**Backend** (terminal 1):
```bash
./mvnw spring-boot:run
```
El backend corre en `http://localhost:8080`

**Frontend** (terminal 2):
```bash
cd parking-frontend
npm run dev
```
El frontend corre en `http://localhost:3000`

Consola H2: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:parkingdb`
- Usuario: `sa`
- Contraseña: *(vacío)*

## Estructura del proyecto

```
parking/
├── src/                        # Backend Spring Boot
│   └── main/java/com/javier/parking/
│       ├── config/             # SecurityConfig, GlobalExceptionHandler
│       ├── controller/rest/    # Controladores REST
│       ├── dto/                # DTOs
│       ├── model/              # Entidades JPA
│       ├── repository/         # Repositorios
│       └── service/            # Lógica de negocio
├── parking-frontend/           # Frontend React + Vite
│   └── src/
│       ├── api.js              # Cliente HTTP
│       ├── context/            # AuthContext, AuthProvider
│       ├── components/         # Navbar, Layout, ProtectedRoute
│       └── pages/              # Login, Dashboard, History, Admin
└── pom.xml
```

## Notas

- El frontend se comunica con el backend a través del proxy de Vite (puerto 3000 redirige a 8080)
- La autenticación usa sesión por cookie (`JSESSIONID`), no JWT
- CSRF desactivado para todas las rutas `/api/**`
- CORS configurado para `http://localhost:3000`

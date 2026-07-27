# Parking System

Sistema de gestión de estacionamiento con backend en Spring Boot (API REST) y frontend en React.

## Tecnologías

**Backend**
- Java 21
- Spring Boot 4.0.5
- Spring Security (BCrypt, roles, sesión por cookie)
- Spring Data JPA + H2 (desarrollo) / PostgreSQL (producción)
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

### Desarrollo local

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

### Deploy en Render (free tier)

Deploy manual sin Blueprint (Blueprint requiere plan pago).

**Pasos:**

1. **Subir el código a GitHub**

2. **Crear PostgreSQL Database** en Render:
   - New > PostgreSQL
   - Plan: **Free**
   - Copiar la **Internal Database URL**

3. **Crear Web Service** en Render:
   - New > Web Service
   - Connect tu repositorio GitHub
   - Runtime: **Docker**
   - Instance Type: **Free**
   - Variables de entorno:
     - `SPRING_PROFILES_ACTIVE` = `prod`
     - `DATABASE_URL` = *(la URL del paso anterior)*
   - Click **Create Web Service**

4. **Esperar** el primer deploy (~5-10 min). El servicio se duerme tras 15 min de inactividad y tarda ~30s en despertar.

**Limitaciones del free tier:**
- 512 MB RAM, 0.1 CPU compartido
- Se duerme tras 15 min sin tráfico (cold start de 30-60s)
- PostgreSQL expira después de 90 días
- 750 horas de compute al mes

**Credenciales (se crean automáticamente al iniciar):**

| Usuario | Contraseña | Rol |
|---------|-----------|------|
| admin | Admin123 | Admin |
| worker | Worker123 | Worker |

**Arquitectura Docker (multi-stage):**

| Etapa | Herramienta | Qué hace |
|-------|-------------|----------|
| Stage 1 | Node 20 Alpine | Compila el frontend React → `dist/` |
| Stage 2 | Maven + JDK 21 | Copia `dist/` a `static/`, compila el JAR |
| Stage 3 | JRE 21 Alpine | Ejecuta el JAR con perfil `prod` |

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

- En desarrollo, el frontend usa el proxy de Vite (puerto 3000 → 8080)
- En producción, Spring Boot sirve el frontend y la API desde el mismo puerto
- La autenticación usa sesión por cookie (`JSESSIONID`), no JWT
- CSRF desactivado para todas las rutas `/api/**`
- CORS configurable via propiedad `cors.allowed-origins` (default: `http://localhost:3000`)
- Perfiles de Spring: `default` (H2 local) y `prod` (PostgreSQL en Render)

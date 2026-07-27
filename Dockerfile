# ============================================
# Stage 1: Compilar el frontend React
# ============================================
FROM node:20-alpine AS frontend-build
WORKDIR /app/parking-frontend

# Copiar solo package*.json primero para aprovechar cache de capas
COPY parking-frontend/package*.json ./
RUN npm ci

# Copiar todo el codigo del frontend y compilar
COPY parking-frontend/ ./
RUN npm run build

# ============================================
# Stage 2: Compilar el backend Spring Boot
# ============================================
FROM maven:3.9-eclipse-temurin-21 AS backend-build
WORKDIR /app

# Copiar pom.xml y descargar dependencias (cache de capas)
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Copiar codigo fuente del backend
COPY src/ ./src/

# Copiar el build del frontend al directorio static de Spring Boot
COPY --from=frontend-build /app/parking-frontend/dist/ src/main/resources/static/

# Empaquetar el JAR (sin tests para rapido)
RUN mvn package -DskipTests -B

# ============================================
# Stage 3: Imagen de runtime (ligera)
# ============================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copiar el JAR compilado
COPY --from=backend-build /app/target/*.jar app.jar

# Puerto que Render asigna via variable de entorno PORT
EXPOSE ${PORT:-8080}

# Render provee DATABASE_URL en formato postgresql://user:pass@host:port/dbname
# Spring Boot necesita formato JDBC: jdbc:postgresql://user:pass@host:port/dbname
# El siguiente comando convierte automaticamente el formato
ENTRYPOINT ["sh", "-c", "\
  JDBC_URL=$(echo $DATABASE_URL | sed 's|^postgresql://|jdbc:postgresql://|') && \
  java -jar -Dspring.profiles.active=prod -Dspring.datasource.url=\"$JDBC_URL\" app.jar"]

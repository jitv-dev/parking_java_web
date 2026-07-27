# ============================================
# Stage 1: Compilar el frontend React
# ============================================
FROM node:20-alpine AS frontend-build
WORKDIR /app/parking-frontend

COPY parking-frontend/package*.json ./
RUN npm ci

COPY parking-frontend/ ./
RUN npm run build

# ============================================
# Stage 2: Compilar el backend Spring Boot
# ============================================
FROM maven:3.9-eclipse-temurin-21 AS backend-build
WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -B

COPY src/ ./src/
COPY --from=frontend-build /app/parking-frontend/dist/ src/main/resources/static/

RUN mvn package -DskipTests -B

# ============================================
# Stage 3: Imagen de runtime (ligera)
# ============================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=backend-build /app/target/*.jar app.jar

# Render asigna el puerto via variable de entorno PORT
EXPOSE ${PORT:-8080}

# Limitar JVM a 384MB (Render free tier da 512MB total)
ENV JAVA_OPTS="-Xmx384m -Xms256m"

# Render provee DATABASE_URL en formato postgresql://
# Spring Boot necesita jdbc:postgresql://
ENTRYPOINT ["sh", "-c", "\
  JDBC_URL=$(echo $DATABASE_URL | sed 's|^postgresql://[^@]*@|jdbc:postgresql://|') && \
  DB_USER=$(echo $DATABASE_URL | sed -n 's|.*://\\([^:]*\\):.*|\\1|p') && \
  DB_PASS=$(echo $DATABASE_URL | sed -n 's|.*://[^:]*:\\([^@]*\\)@.*|\\1|p') && \
  java $JAVA_OPTS -jar -Dspring.profiles.active=prod \
    -Dspring.datasource.url=\"$JDBC_URL\" \
    -Dspring.datasource.username=\"$DB_USER\" \
    -Dspring.datasource.password=\"$DB_PASS\" \
    app.jar"]

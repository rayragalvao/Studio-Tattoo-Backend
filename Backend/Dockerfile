# ── Stage 1: Build ──────────────────────────────────────────
FROM maven:3.9-amazoncorretto-21 AS build

WORKDIR /app

# Copia pom.xml e baixa dependências (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código fonte e gera o JAR
COPY src ./src
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

# ── Stage 2: Run ─────────────────────────────────────────────
FROM amazoncorretto:21-alpine

WORKDIR /app

# Copia o JAR gerado no stage anterior
COPY --from=build /app/target/orcana-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

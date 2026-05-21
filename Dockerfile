FROM maven:3-amazoncorretto-21 AS builder
LABEL authors="jupiterfrito"

WORKDIR /build
COPY . .
RUN mvn clean package -DskipTests -Dcheckstyle.skip=true

FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]
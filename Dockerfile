
FROM eclipse-temurin:23-jdk-alpine AS build


WORKDIR /app


COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .


RUN ./mvnw dependency:go-offline -B


COPY src src
RUN ./mvnw package -DskipTests


FROM eclipse-temurin:23-jdk-alpine
WORKDIR /app


COPY --from=build /app/target/payment-service-0.0.1-SNAPSHOT.jar app.jar


ENTRYPOINT ["java","-jar","app.jar"]

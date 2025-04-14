FROM maven:3.8.7-amazoncorretto-17 AS build
WORKDIR /app
COPY . .
WORKDIR /app/crud
RUN mvn clean package -DskipTests

FROM amazoncorretto:17.0.13
WORKDIR /app
COPY --from=build /app/crud/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
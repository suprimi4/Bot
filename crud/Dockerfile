FROM maven:3.8.7-amazoncorretto-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -pl crud -am -DskipTests

FROM amazoncorretto:17.0.13
WORKDIR /app
COPY --from=build /app/crud/target/crud-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
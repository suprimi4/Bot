FROM maven:3.8.7-amazoncorretto-17 AS build
WORKDIR /app
COPY . .
WORKDIR /app/TimeToGoBot
RUN mvn clean package -DskipTests

FROM amazoncorretto:17.0.13
WORKDIR /app
COPY --from=build /app/TimeToGoBot/target/TimeToGoBot-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
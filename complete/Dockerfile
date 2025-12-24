FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copy everything including mvnw and .mvn
COPY . .

# Make mvnw executable
RUN chmod +x mvnw

# Use the wrapper
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

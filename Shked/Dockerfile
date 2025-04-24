# build stage
FROM eclipse-temurin:22-jdk-alpine as builder
WORKDIR /app
COPY pom.xml ./
COPY mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:resolve
COPY . .
RUN ./mvnw package -DskipTests


# run stage
FROM eclipse-temurin:22-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
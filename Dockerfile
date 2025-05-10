FROM openjdk:21

WORKDIR app/
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

RUN ./mvnw dependency:go-offline

CMD ["./mvnw", "spring-boot:run"]

# docker build --platform linux/amd64 -t spring-helloworld .
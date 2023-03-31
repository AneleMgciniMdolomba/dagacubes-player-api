# Dagacubes Player API Spring Boot app

[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

A DagaCubes Player Transactions API using [Spring Boot](http://projects.spring.io/spring-boot/).

## Requirements

For building and running the application you need:

- [JDK 11](https://www.oracle.com/java/technologies/downloads/#java11)
- [Maven 3](https://maven.apache.org)
- [Spring Boot 2.7.9](https://docs.spring.io/spring-boot/docs/2.7.9/reference/html/)

# Running the application locally
There are several ways to run a Spring Boot application on your local machine.
- Clone this project

### IDE
- Simplest way is to open this project on your favourite IDE and
run `main` method inside `co.za.anele.dagacubesplayerapi.DagaCubesPlayerApiApplication`

### CMD
- cd inside project and build the project ```mvn clean package```
- Once built successfully, you can run:
```sbtshell
     mvn spring-boot:run -Drun.arguments="spring.profiles.active=dev"
```

## Accessing Swagger
- You can explore the api on [swagger](http://localhost:8080/swagger-ui/index.html)

Released under the Apache License 2.0. See the [LICENSE](https://github.com/AneleMgciniMdolomba/dagacubes-player-api/blob/master/LICENSE) file.
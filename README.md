# Greenfield

## Technologies

- gRPC

- Protocol Buffer

- gson

- Jersey 1.19

- AspectJ

## Requirements

- Java 1.8 

- Gradle >= 7.4.2 

## Run, Compile and Build

> You can use `gradle` or `./gradlew`  


### Run

Running gradle tasks will automatically compile the necessary files.

**Mosquitto**

- Start mosquitto broker
    
    `sudo service mosquitto start` or `sudo systemctl start mosquitto`

- Stop mosquitto broker

    `sudo service mosquitto stop` or `sudo systemctl stop mosquitto`

- Status mosquitto broker

    `sudo service mosquitto status` or `sudo systemctl status mosquitto`

**Cleaning Robot**

- Start one cleaning robot:
    `gradle runCleaningRobotClient --console=plain` or `./gradlew runCleaningRobotClient --console=plain``

**Administrator Server**

- Start administrator server:
   
   `gradle runAdministratorServer --console=plain` or `./gradlew runAdministratorServer --console=plain` 

**Administrator Client**

- Start administrator client:
   
   `gradle runAdministratorClient --console=plain` or `./gradlew runAdministratorClient --console=plain` 

### Compile 

First of all run `gradle` or `./gradlew` to check that everything is OK.

**Compile <small>[java with aspectj]</small>:**

*Note that `.aj` files must be on `src/main/aspectj` folder.*

- `gradle :compileJava` or `./gradlew :compileJava`

> If you want to put `.aj` file on the same directory of `.java` files, 
you should replace on `build.gradle` file `compileJava.ajc.options.compilerArgs = ["-sourceroots", "../../../src/main/aspectj"]` 
with `compileJava.ajc.options.compilerArgs=["-sourceroots", sourceSets.main.java.sourceDirectories.getAsPath()]`

### Build

- `gradle build` or `./gradlew build`




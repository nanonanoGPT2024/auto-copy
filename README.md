# Spring Boot Application

## Prerequisites
- JDK 17
- Maven
- MySQL Server

## Database Setup
1. Install and start MySQL Server.
2. Create a database named `mydb`.
3. Ensure the database credentials in `src/main/resources/application-dev.properties` are correct (default: username `root`, password `pass123`).

## Running the Application (Dev)
1. Clone the repository.
2. Navigate to the project directory.
3. Run the following command to start the application with the dev profile:
   ```
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```
4. The application will start on the default port (typically 8080).

## Building JAR
Run the following command to build the JAR file:
```
mvn clean package
```
The JAR file will be generated in the `target/` directory as `spring-0.0.1-SNAPSHOT.jar`.

## Running JAR (Production)
Run the following command to execute the JAR file:
```
java -jar target/scheduler-0.0.1-SNAPSHOT.jar --DB_HOST=prod-host --DB_PORT=3306 --DB_NAME=proddb --DB_USERNAME=produser --DB_PASSWORD=prodpass --SERVER_PORT=9090 app.folder.storage.data_recording= test app.folder.storage.data_recording_new=new_path
```
Note: Ensure the production database configuration is set in `application.properties` if different from dev.

## Running with Custom Port

To run the application with a custom port, use the following commands:

For development:
```
mvn spring-boot:run --spring.profiles.active=dev 


# build 
mvn clean package -Pdev


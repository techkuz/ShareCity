## Steps to Setup the Spring Boot Back-end app

0. **Requirements**
    ```
    Java 8+
    Maven (or use Maven Wrapper bundled with application)
    ```

1. **Clone the application**

    ```bash
    git clone https://github.com/trthhrtz/ShareCity.git
    cd junction-bytom-app
    ```

2. **Create PostgreSQL database**

    ```
    create database in postgresql bytom_app
    ```
    
3. **Create PSQL user**
    ```
    login:bytom
    password:bytom
    ```
    Or change to desired in `bytomapp-server/src/main/java/application.properties`

4. **Run the app**
    
    You can run the spring boot app by typing the following command -
    
    ```bash
    mvn spring-boot:run
    ```
    
    The server will start on port 5000.
    	
5. **Add the default Roles**

    The spring boot app uses role based authorization powered by spring security. Please execute the following sql queries in the database to insert the `USER` and `ADMIN` roles.
    
    ```sql
    INSERT INTO bytom_app.roles (name) VALUES ('ROLE_USER');
    INSERT INTO bytom_app.roles (name) VALUES ('ROLE_ADMIN');
    INSERT INTO bytom_app.roles (name) VALUES ('ROLE_BUSINESS');
    INSERT INTO bytom_app.roles (name) VALUES ('ROLE_STARTUP');
    INSERT INTO bytom_app.roles (name) VALUES ('ROLE_CORPORATE');
    ```
     
     Any new user who signs up to the app is assigned the `ROLE_USER` by default.
     

## Steps to Setup the React Front-end app

0. **Requirements**

    ```
    node.js 10.13
    ```

1. **Go to the `bytom-app-client` folder**

    ```bash
    cd bytom-app-client
    ```

2. **Use the following command to install the dependencies and start the application**

    ```bash
    npm install && npm start
    ```

    The front-end server will start on port `3000`.

# **Farm Manager Server**  

Farm Manager Server is a backend system designed to collect data about crops and fields, to make resource planning easier, and to allow farmers to compare how different solutions used in farming influence yields.



## **2. Tech Stack & Dependencies**  


- **Spring Boot**

- **Spring Data JPA**

- **OAuth2 / Spring Security** 

- **MySQL** 

- **Lombok** 

- **SpringDoc OpenAPI**



## **3. Database Structure & ERD**  

A separate ERD diagram is available in project files.  


## **4. API Documentation**  

The API documentation is generated using **SpringDoc OpenAPI** and can be accessed at:  [Swagger UI](http://localhost:8080/swagger-ui.html) when the application is running.


## **5. Security**  

- **OAuth2 authentication** is used to protect API endpoints.  

- The authentication server for this application is available at:  [Farm Manager Authorization Server](https://github.com/ZERO-S-PLACE/FarmManagerAuthorizationServer)  

- The authentication system has been customized to allow distinguished users.  

- A manual on how to register, login and obtain tokens is available in API documentation.  


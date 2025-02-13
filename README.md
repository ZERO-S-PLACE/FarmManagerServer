# **Farm Manager Server**  

Farm Manager Server is a backend system designed to collect data about crops and fields, making resource planning easier. It helps estimate the effectiveness of different farming solutions by providing insights into crop management and field usage.  

Built with **Spring Boot** and **Spring Data JPA**, the application ensures efficient data handling and security using OAuth2 authentication for user access control.  

## **2. Tech Stack & Dependencies**  

- **Spring Boot** – Core framework for building REST APIs  
- **Spring Data JPA** – ORM for database interactions  
- **OAuth2 / Spring Security** – Authentication & authorization  
- **MySQL** – Relational database for storing farm-related data  
- **Lombok** – Reduces boilerplate code  
- **SpringDoc OpenAPI** – API documentation with Swagger UI  

## **3. Database Structure & ERD**  

A separate ERD diagram provides an overview of how different entities relate (e.g., `Crop`, `FieldPart`, `Subside`, `User`).  
The database follows a normalized structure to prevent data duplication and improve efficiency.  

## **4. API Documentation** *(Work in Progress)*  

The API documentation is generated using **SpringDoc OpenAPI** and can be accessed at:  

- [Swagger UI](http://localhost:8080/swagger-ui.html)  
- [OpenAPI JSON](http://localhost:8080/v3/api-docs)

  when application is running.

## **5. Security**  

- **OAuth2 authentication** is used to protect API endpoints.  
- The authentication server for this application is available at:  
  [Farm Manager Authorization Server](https://github.com/ZERO-S-PLACE/FarmManagerAuthorizationServer)  
- The authentication system has been customized to allow user differentiation.  
- A manual on how to connect to this application is available in a separate file.  

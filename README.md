# ParcelApp
Parcel Application Microservices. Java 17. Spring Boot 3.0.5 (Maven).

Application consists of 6 microservices:

Service-registry - host of Eureka Server, allows to registrate other services; port:8761

Gateway - Spring Cloud Gateway, provides routing and one entry point to other services; port:4000

Authorization Department - MVC, Spring Security provides api for authorization and authentication, Role-based environment with JWT tokens; port:4001

Courier Dept. - MVC, provides api related to couriers; port:4002

Order Dept. - MVC, api for managing orders; port:4003

Tracking Dept. - stub for tracking couriers by coordinates (check config folder); port:4004

Also used: Kafka Message brocker, Liquibase, PostgreSQL, JUnit/Mockito testing (checkout Auth. Dept. microservice), Swagger Openapi.

To check api routes/parameters doc. hit - http://localhost:{port}/swagger-ui/index.html#/ All api's available via 4000 port of Gateway.

\# Smart Event \& Venue Management Platform



A microservices-based Event \& Venue Management Platform built using Spring Boot, Spring Cloud, MySQL, and React.js.



\## Project Overview



The platform allows customers to discover events, check venue information, reserve seats, make payments, cancel bookings, and receive notifications.



The system follows a microservices architecture with service discovery, centralized configuration, API Gateway, and independent databases for services.



\## Technology Stack



\### Backend



\- Java 17

\- Spring Boot

\- Spring MVC

\- Spring Data JPA

\- Hibernate

\- REST APIs

\- MySQL

\- Maven



\### Microservices



\- Eureka Server

\- Config Server

\- API Gateway

\- Customer Service

\- Venue Service

\- Event Service

\- Booking Service

\- Payment Service

\- Notification Service

\- Admin Service



\### Frontend



\- React.js

\- HTML

\- CSS

\- JavaScript



\### Spring Cloud



\- Spring Cloud Eureka

\- Spring Cloud Gateway

\- Spring Cloud Config

\- OpenFeign

\- Resilience4j



\### DevOps / Tools



\- Git

\- GitHub

\- Docker

\- Eclipse

\- Postman

\- MySQL Workbench



\---



\# Architecture



```text

&#x20;                        ┌──────────────────┐

&#x20;                        │      React       │

&#x20;                        │     Frontend     │

&#x20;                        └────────┬─────────┘

&#x20;                                 │

&#x20;                                 ▼

&#x20;                        ┌──────────────────┐

&#x20;                        │   API Gateway    │

&#x20;                        │      :8080       │

&#x20;                        └────────┬─────────┘

&#x20;                                 │

&#x20;            ┌────────────────────┼────────────────────┐

&#x20;            │                    │                    │

&#x20;            ▼                    ▼                    ▼

&#x20;     ┌─────────────┐      ┌─────────────┐      ┌─────────────┐

&#x20;     │  Customer   │      │    Venue    │      │    Event    │

&#x20;     │   Service   │      │   Service   │      │   Service   │

&#x20;     │    :8081    │      │    :8082    │      │    :8083    │

&#x20;     └──────┬──────┘      └──────┬──────┘      └──────┬──────┘

&#x20;            │                    │                    │

&#x20;            ▼                    ▼                    ▼

&#x20;       Customer DB           Venue DB              Event DB



&#x20;            ┌────────────────────┼────────────────────┐

&#x20;            │                    │                    │

&#x20;            ▼                    ▼                    ▼

&#x20;     ┌─────────────┐      ┌─────────────┐      ┌─────────────┐

&#x20;     │   Booking   │      │   Payment   │      │Notification │

&#x20;     │   Service   │      │   Service   │      │   Service   │

&#x20;     │    :8084    │      │    :8085    │      │    :8086    │

&#x20;     └──────┬──────┘      └──────┬──────┘      └──────┬──────┘

&#x20;            │                    │                    │

&#x20;            ▼                    ▼                    ▼

&#x20;       Booking DB            Payment DB          Notification DB





&#x20;                        ┌──────────────────┐

&#x20;                        │   Admin Service  │

&#x20;                        │      :8087       │

&#x20;                        └──────────────────┘





&#x20;     ┌──────────────────┐          ┌──────────────────┐

&#x20;     │  Eureka Server   │          │  Config Server   │

&#x20;     │      :8761       │          │      :8888       │

&#x20;     └──────────────────┘          └──────────────────┘







\# Smart Event \& Venue Management Platform



A microservices-based Event \& Venue Management Platform built using Spring Boot, Spring Cloud, MySQL, and React.js.



\## Microservices



\- Eureka Server

\- Config Server

\- API Gateway

\- Customer Service

\- Venue Service

\- Event Service

\- Booking Service

\- Payment Service

\- Notification Service

\- Admin Service



\## Service Ports



| Service | Port |

|---|---:|

| Eureka Server | 8761 |

| Config Server | 8888 |

| API Gateway | 8080 |

| Customer Service | 8081 |

| Venue Service | 8082 |

| Event Service | 8083 |

| Booking Service | 8084 |

| Payment Service | 8085 |

| Notification Service | 8086 |

| Admin Service | 8087 |



\## Current Progress



\- Eureka Server completed

\- Config Server completed

\- API Gateway completed

\- Customer Service completed

\- Venue Service completed


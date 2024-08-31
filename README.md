# Mentorship Management System

A Spring-based microservices project for managing mentorship relationships and scheduling meetings.

## Features

- User roles: Mentor and Mentee
- Mentorship request and acceptance system
- Meeting scheduling with calendar integration
- OAuth2 authentication (Google and GitHub)
- Secure API endpoints
- Asynchronous communication using Kafka
- File storage using Google Storage API
- JWT and Refresh session based Token 

## Architecture

This project is built using a microservices architecture with the following components:

1. API Gateway
2. Authentication Service
3. Mentorship Service
4. Profile Service
5. Eureka Server (for service discovery)

## Technologies Used

- Spring Boot
- Spring Cloud (for microservices)
- Spring Security
- Spring Data JPA
- Kafka for event-driven architecture
- Google OAuth 2.0 and GitHub OAuth 2.0
- Google Cloud Storage API
- PostgreSQL (or your specific database)
- Eureka for service discovery
- API Gateway


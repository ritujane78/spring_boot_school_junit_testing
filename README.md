# Spring Boot School App -- MVC & JUnit Testing

This project demonstrates comprehensive testing of a Spring Boot MVC
School Management Application using:

-   Spring Boot 3.5.9
-   Java 21
-   JUnit 5
-   Mockito
-   MockMvc
-   MySQL (Production Database)
-   H2 (Testing Database)
-   Spring Data JPA
-   Thymeleaf

------------------------------------------------------------------------

## Project Overview

The application manages:

-   Students
-   Math Grades
-   Science Grades
-   History Grades

The testing suite validates:

-   Service Layer Business Logic
-   Controller Layer HTTP Requests
-   Database Integration
-   Grade Validation Rules
-   Error Handling & Edge Cases


@TestPropertySource("/application-test.properties")

Ensures isolation from production DB and faster execution.

------------------------------------------------------------------------

## Testing Structure

### 1. StudentAndGradeServiceTest

-   Integration Test
-   Uses @SpringBootTest
-   Uses H2 database
-   SQL setup and teardown using JdbcTemplate
-   Covers:
    -   Create student
    -   Delete student
    -   Create grade
    -   Delete grade
    -   Validation rules
    -   Get gradebook
    -   Get student information

### 2. GradebookControllerTest

-   Web Layer Test
-   Uses @AutoConfigureMockMvc
-   Uses MockMvc for HTTP simulation
-   Uses @Mock StudentAndGradeService
-   Covers:
    -   GET "/"
    -   POST "/"
    -   Delete student endpoint
    -   Student information endpoint
    -   Create grade endpoint
    -   Delete grade endpoint
    -   Error scenarios

------------------------------------------------------------------------

## Key Testing Features

-   JUnit 5 Annotations (@Test, @BeforeEach, @AfterEach, @BeforeAll)
-   Spring Boot Testing Support
-   Mockito mocking with maven-surefire-plugin javaagent
-   MockMvc for controller testing
-   JdbcTemplate for SQL execution

------------------------------------------------------------------------

## Run Tests

Run all tests:

mvn clean test

Run specific test:

mvn -Dtest=StudentAndGradeServiceTest test

------------------------------------------------------------------------

## Architecture Tested

Controller → Service → Repository → Database


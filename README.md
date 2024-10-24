# Gym CRM Application for EPAM Laboratory
## Overview
This is a CRM application which is designed to help gym users maintain their activities. The main actors are Trainee and Trainer. It allows users to manage activities, manage profiles and much more. App supports selection, adding and updation of records. 
### Key features
* **User Profiles:** Users can register a new account and manage it
* **Authentication:** All endpoints except of account creaction are highly secured
* **Training Overview:** Both Trainers and Trainees can view their trainings and sort them by different criterions
## Navigation
1. [Technology Stack](#technology-stack)
2. [Installation](#installation)
3. [Usage](#usage)
## Technology Stack
This project utilizes different technologies and dependencies:
- Java 21
- Spring Boot 3.3.4
- Spring Data JPA
- Spring Security
- JWT tokens
- Lombok
- PostgreSQL
- Aspect-Oriented Programming (AOP)
- Spring Boot Actuator for monitoring
- Spring Boot DevTools for development
- OpenAPI
- Prometheus
- JUnit and Mockito
And other various technologies
## Installation
To run this project locally, follow these steps:
### Prerequisites:
- Java 21 installed
- Maven installed
- PostgreSQL database running
### Steps:
1. Clone the repository:
```bash
  git clone https://github.com/username/yourgym.git
```
2. Navigate to the project directory:
```bash
  cd gymcrm
```
3. Cnfigure PostgreSQL database
4. Build and install dependencies:
```bash
  ./mvnw clean install
```
5.Run the application:
```bash
  ./mvnw spring-boot:run
```
## Usage
### For Unauthenticated Users:
- Register Trainee or Trainer account
- Login to have an access to other endpoints
For Authenticated Users:
- Logout, change password
- View and update trainer profile
- View, update and delete trainee profile
- Create trainings
- Find information about trainings
- View available training types

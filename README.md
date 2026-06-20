# Secure Email Management System

A full-stack email management project built with **Spring Boot, React, MySQL/H2, Spring Security, JWT, and Jakarta Mail**.

This project is designed for a Java/Spring Boot GitHub portfolio. It demonstrates REST API development, authentication, role-based authorization, email integration, database persistence, React frontend integration, and production-style project structure.


## Description

Secure Email Management System is a full-stack Java project that allows authenticated users to compose, send, receive, and track emails. It uses Spring Boot REST APIs, JWT security, role-based admin access, database-backed email history, and a React frontend. The project demonstrates secure backend design, frontend-backend integration, database persistence, and external email server integration using SMTP and IMAP.

## Features

- User registration and login
- JWT based authentication
- Role based access: `USER` and `ADMIN`
- Compose and send emails from the web UI
- Store sent email history in the database
- Sync received emails from an IMAP inbox when mail credentials are configured
- Admin can view users, enable/disable accounts, and view email logs
- H2 database for quick local run
- MySQL support using Docker Compose
- React frontend using Vite and Axios

## Tech Stack

### Backend

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA / Hibernate
- JWT using JJWT
- Jakarta Mail / JavaMailSender
- H2 database for local demo
- MySQL for real database profile
- Maven

### Frontend

- React 18
- Vite
- React Router
- Axios
- Plain CSS

## Project Structure

```text
secure-email-system/
  backend/                 Spring Boot REST API
  frontend/                React UI
  docker-compose.yml       MySQL local setup
  README.md
```

## Default Demo Users

The backend creates these users automatically on startup:

```text
Admin:
email: admin@example.com
password: admin123

User:
email: user@example.com
password: user123
```

## Run Backend with H2 Database

Open PowerShell from the project root:

```powershell
cd backend
mvn spring-boot:run
```

Backend will run on:

```text
http://localhost:8080
```

H2 console:

```text
http://localhost:8080/h2-console
```

H2 JDBC URL:

```text
jdbc:h2:mem:emaildb
```

## Run Frontend

Open another PowerShell window:

```powershell
cd frontend
npm install
npm run dev
```

Frontend will run on:

```text
http://localhost:5173
```

## Run with MySQL

From project root:

```powershell
docker compose up -d
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

MySQL details:

```text
Host: localhost
Port: 3306
Database: email_system
Username: email_user
Password: email_password
```

## Configure Real SMTP Sending

For Gmail, use an App Password instead of your normal Gmail password.

Set environment variables before starting backend:

```powershell
$env:MAIL_HOST="smtp.gmail.com"
$env:MAIL_PORT="587"
$env:MAIL_USERNAME="your-email@gmail.com"
$env:MAIL_PASSWORD="your-app-password"
$env:MAIL_FROM="your-email@gmail.com"
```

Then run:

```powershell
cd backend
mvn spring-boot:run
```

If SMTP is not configured, the app runs in demo mode and stores email records as `DEMO_SAVED` instead of failing.

## Configure Inbox Sync using IMAP

Set these variables only if you want to test receiving emails:

```powershell
$env:MAIL_RECEIVE_ENABLED="true"
$env:MAIL_RECEIVE_PROTOCOL="imaps"
$env:MAIL_RECEIVE_HOST="imap.gmail.com"
$env:MAIL_RECEIVE_PORT="993"
$env:MAIL_RECEIVE_USERNAME="your-email@gmail.com"
$env:MAIL_RECEIVE_PASSWORD="your-app-password"
```

## Sample Login Request

```json
{
  "email": "user@example.com",
  "password": "user123"
}
```

## Sample Send Email Request

```json
{
  "to": "friend@example.com",
  "subject": "Hello from Spring Boot",
  "body": "This email was sent from my full-stack secure email system."
}
```

Email sending is integrated using Spring Boot Mail and Gmail SMTP.
For security, Gmail credentials are passed using environment variables:

MAIL_USERNAME=your-gmail-address
MAIL_PASSWORD=your-gmail-app-password

The project supports real email delivery using SMTP and stores email history in the database.

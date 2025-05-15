# 💱 Currency Converter Microservices

This project consists of two Spring Boot microservices (`main-service` and `rate-service`) that allow currency conversion based on real-time exchange rates from a third-party API. All conversions are persisted in a PostgreSQL database.

---

## 📦 Services Overview

| Service       | Port  | Description                                   |
|---------------|-------|-----------------------------------------------|
| `main-service`| 8080  | Accepts currency conversion requests          |
| `rate-service`| 8081  | Fetches real-time exchange rates              |
| `postgres`    | 5433  | Stores conversion logs                        |

---

## 🧰 Technologies

- Java 17 + Spring Boot 3
- Spring Data JDBC
- PostgreSQL 15
- Docker + Docker Compose
- RESTful API design
- ExchangeRate-API integration
- Token-based security via API Key + Secret

---

## 🛠️ Setup Instructions

### 1. Clone the Repository

git clone https://github.com/WangariNelly/currency_converterApi.git
cd your-repo

---
### 2. Create .env File
Create a .env file in the root directory with the following content:
```env

SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/mydbexample
SPRING_DATASOURCE_USERNAME=yourusername
SPRING_DATASOURCE_PASSWORD=yourpassword

EXCHANGE_API_KEY=yourapikeyprovidedbyProvider

SECURITY_API_KEY=yourapikey
SECURITY_API_SECRET=yoursecretkey
APP_API_KEY=${SECURITY_API_KEY}
APP_API_SECRET=${SECURITY_API_SECRET}

RATE_SERVICE_URL=http://rate-service:8081/api/v1/rate

MAIN_PORT=8080
RATE_PORT=8081

LOGGING_LEVEL=DEBUG
API_KEY_HEADER=X-API-KEY
--- 
```

### 3. Build the Docker Images
3. Run the Application with Docker Compose
````
           docker-compose up --build
````
This will:

     Start PostgreSQL at localhost:5433
     Run rate-service on localhost:8081
     Run main-service on localhost:8080

### 🚀 API Testing (via Postman or Curl)
1. main-service Health Check
````
GET http://localhost:8080/api/v1/status
Header: X-API-KEY: <your-key>
````

2. rate-service Health Check
````
GET http://localhost:8081/api/v1/rate/status
Header: X-API-KEY: <your-key>

````
   3. Convert Currency
````
POST http://localhost:8080/api/v1/convert
Headers:
  Content-Type: application/json
  X-API-KEY: <your-key>
  ````
````
Body:
{
  "from": "USD",
  "to": "KES",
  "amount": 100
}
````
````
### 🗂️ Project Structure

├── mainService/
│   └── Dockerfile
├── rateService/
│   └── Dockerfile
├── docker-compose.yml
├── .env
└── README.md
````
#### 📝 Notes            
✅ Features Completed
 Two independent Spring Boot services

 PostgreSQL for persistence

 Real-time currency exchange API integration

 Dockerized microservices

 Environment-based config using .env

 Secure internal communication (API key & secret)

 Health checks and error handling

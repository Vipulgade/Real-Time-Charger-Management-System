Real-Time Charger Management System

📌 Project Overview

This project implements a Real-Time Charger Management System that enables EV chargers to communicate via OCPP 1.6 WebSocket server. The system tracks and updates charger statuses, logs charging transactions, and provides a REST API for monitoring.

🔹 Key Features

WebSocket-based OCPP 1.6 Communication

Handle BootNotification, Heartbeat, StatusNotification, StartTransaction, and StopTransaction requests.

Charger Status Tracking

Store real-time charger status: Available, Charging, Faulted, Unavailable.

Auto-mark chargers as Unavailable if no heartbeat is received for 5+ minutes.

Transaction Logging

Store charging session details in a database.

Provide REST APIs to retrieve transaction history (filtered by charger ID & time range).

Authentication

Secure API endpoints using JWT authentication.

High Concurrency & Fault Tolerance

Handle 100+ concurrent WebSocket connections.

Prevent data loss during charger disconnections.

🛠️ Setup Instructions

1️⃣ Clone the Repository

git clone https://github.com/yourusername/real-time-charger-management.git
cd real-time-charger-management

2️⃣ Configure Database

Modify the application.properties file to set up your database connection:

spring.datasource.url=jdbc:mysql://localhost:3306/charger_db
spring.datasource.username=root
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update

3️⃣ Build & Run the Application

mvn clean install
mvn spring-boot:run

🔥 API Endpoints & Expected Payloads

🔹 Charger Management

✅ List All Chargers

GET /api/chargers

Response:

[
  {"chargerId": 1, "status": "Available"},
  {"chargerId": 2, "status": "Charging"}
]

✅ Get Charger Status by ID

GET /api/chargers/{chargerId}

Response:

{"chargerId": 1, "status": "Available"}

🔹 Transaction Management

✅ Start a Transaction

POST /api/transactions/{chargerId}/start

Request:

{
  "sessionId": "ABC123"
}

Response:

{"message": "Transaction started"}

✅ Stop a Transaction

POST /api/transactions/{transactionId}/end

Response:

{"message": "Transaction ended"}

✅ Get Transaction History

GET /api/transactions/charger/{chargerId}

Response:

[
  {"transactionId": 101, "chargerId": 1, "status": "Completed"},
  {"transactionId": 102, "chargerId": 1, "status": "In Progress"}
]

🔌 WebSocket Communication (OCPP 1.6)

📡 WebSocket Endpoint:

ws://localhost:8080/ocpp

🔹 BootNotification

Client Request:

{
  "messageType": "BootNotification",
  "chargerId": 1,
  "model": "EVChargerX",
  "vendor": "ChargerCo"
}

Server Response:

{
  "status": "Accepted",
  "currentTime": "2025-03-30T12:00:00Z",
  "interval": 300
}

🔹 Heartbeat

Client Request:

{
  "messageType": "Heartbeat",
  "chargerId": 1
}

Server Response:

{
  "currentTime": "2025-03-30T12:05:00Z"
}

🔹 StartTransaction

Client Request:

{
  "messageType": "StartTransaction",
  "chargerId": 1,
  "transactionId": "TXN-456",
  "timestamp": "2025-03-30T12:10:00Z"
}

Server Response:

{
  "transactionId": "TXN-456",
  "status": "Accepted"
}

🚀 Future Enhancements

Implement Load Balancing for WebSocket connections.

Add OAuth2 authentication for better security.

Store charger location details in the database.

📌 Contributors

Vipul Gade (GitHub)

📄 License

This project is licensed under the MIT License. Feel free to use and modify it!

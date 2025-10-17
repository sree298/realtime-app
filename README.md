# Realtime App

A real-time web application built with **Spring Boot**, **MySQL**, and **Kubernetes**, containerized using **Docker** and deployed via **Helm**. This repository includes a CI/CD pipeline example using **Jenkins** and manifests to deploy to Kubernetes and workflow.

---

## 🚀 Project Overview

- **Backend:** Spring Boot (Java, Maven)
- **Database:** MySQL
- **Containerization:** Docker
- **Orchestration:** Kubernetes (manifests in `k8s/`) and a Helm chart (`realtime-app-chart/`)
- **CI/CD:** Jenkinsfile included
- **Static frontend:** `src/main/resources/static/index.html` (simple UI)

---

## 📁 Project Structure

```
realtime-app/
├── Dockerfile
├── Jenkinsfile
├── pom.xml
├── k8s/
│   ├── mysql-deployment.yaml
│   └── realtime-app-deployment.yaml
├── realtime-app-chart/
│   ├── Chart.yaml
│   ├── values.yaml
│   └── templates/
│       ├── app.yaml
│       ├── mysql.yaml
│       └── namespace.yaml
├── src/
│   ├── main/java/com/example/realtimeapp/
│   │   ├── controller/
│   │   │   ├── ApiController.java
│   │   │   └── UserController.java
│   │   ├── model/User.java
│   │   ├── repository/UserRepository.java
│   │   └── RealtimeAppApplication.java
│   └── main/resources/
│       ├── application.properties
│       └── static/index.html
```

---

## ⚙️ Prerequisites

Make sure you have the following installed locally:

- Java 17+ (or the version used in `pom.xml`)
- Maven 3.6+
- Docker
- kubectl (for Kubernetes)
- Helm 3+ (if using the Helm chart)
- A Kubernetes cluster (Minikube, kind, EKS, GKE, etc.)
- MySQL (or use the included Kubernetes MySQL manifest)
- (Optional) Jenkins for CI/CD

---

## 🔧 Configuration

Open `src/main/resources/application.properties`. Current values detected:

```
spring.datasource.url=jdbc:mysql://mysql:3306/realtime_db
spring.datasource.username=root
spring.datasource.password=sree!
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Optional health endpoints (useful for K8s probes)
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always

```

Key settings you may change before running locally:

- `spring.datasource.url` — currently set to use `mysql:3306/realtime_db`. Change to `jdbc:mysql://localhost:3306/realtime_db` if running MySQL locally.
- `spring.datasource.username` and `spring.datasource.password` — set credentials as needed.
- `server.port` — default Spring Boot port (8080) can be changed here.

---

## 🧩 Build & Run Locally (without Docker)

1. Build the project:

```bash
mvn clean package -DskipTests
```

2. Run the JAR:

```bash
java -jar target/*.jar
```

The app will start on `http://localhost:8080` unless `server.port` is changed.

---

## 🐳 Build & Run with Docker

1. Build Docker image (from repository root):

```bash
docker build -t realtime-app:latest -f Dockerfile .
```

2. Run container (example, with local MySQL):

```bash
docker run -d --name realtime-app -p 8080:8080   --env SPRING_DATASOURCE_URL="jdbc:mysql://host.docker.internal:3306/realtime_db"   --env SPRING_DATASOURCE_USERNAME=root   --env SPRING_DATASOURCE_PASSWORD=yourpassword   realtime-app:latest
```

If using Docker Compose / Kubernetes, prefer using the provided manifests instead.

---

## ☸️ Kubernetes Deployment (manifests)

There are k8s manifests in the `k8s/` directory. Example to deploy all resources (namespace may be defined in manifests):

```bash
kubectl apply -f k8s/
```

Check pods and services:

```bash
kubectl get pods
kubectl get svc
kubectl logs <pod-name>
```

If the MySQL pod is part of the same namespace, ensure the `spring.datasource.url` points to `jdbc:mysql://mysql:3306/realtime_db` which matches the MySQL service name defined in the manifests.

---

## 📦 Helm Chart

A basic Helm chart exists in `realtime-app-chart/`. To install with Helm:

```bash
helm install realtime-app ./realtime-app-chart --values ./realtime-app-chart/values.yaml
```

To upgrade after changes:

```bash
helm upgrade realtime-app ./realtime-app-chart --values ./realtime-app-chart/values.yaml
```

To uninstall:

```bash
helm uninstall realtime-app
```

---

## 🔁 CI/CD - Jenkinsfile

The repository includes a `Jenkinsfile` that can be used as a starting point for a pipeline. A typical pipeline would:

1. Checkout code
2. Build with Maven
3. Build Docker image and push to registry
4. Deploy/upgrade Helm chart to Kubernetes

Make sure Jenkins agent has Docker and Helm installed and has access to the Kubernetes cluster credentials (`kubeconfig`) and docker registry credentials (for push).

---

## 🧪 Database (MySQL) Setup

### Using local MySQL
1. Create the database and user (example):

```sql
CREATE DATABASE realtime_db;
CREATE USER 'appuser'@'%' IDENTIFIED BY 'apppassword';
GRANT ALL PRIVILEGES ON realtime_db.* TO 'appuser'@'%';
FLUSH PRIVILEGES;
```

2. Ensure `application.properties` uses these credentials.

### Using Kubernetes manifest
The chart/manifests include a `mysql` deployment and service. The application properties default to `jdbc:mysql://mysql:3306/realtime_db` so the service name `mysql` is used for internal DNS in the cluster.

---

## 🔌 API Endpoints (examples & testing)

The project contains `ApiController.java` and `UserController.java`. Without reading method-level code, typical endpoints you can try (adjust according to actual controller mappings):

- `GET /api/health` — health check or basic API info
- `GET /api/users` — list users
- `GET /api/users/<built-in function id>` — get user
- `POST /api/users` — create user (JSON body)
- `PUT /api/users/<built-in function id>` — update user
- `DELETE /api/users/<built-in function id>` — delete user

### Example CURL requests

```bash
# List users
curl http://localhost:8080/api/users

# Create user (example payload)
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice","email":"alice@example.com"}'
```

> If you want exact endpoint paths and request/response shapes, I can extract them from the controller sources and paste them into the README. (I can do this now if you want.)

---

## ✅ Useful Commands Summary

```bash
# Build
mvn clean package -DskipTests

# Docker build
docker build -t realtime-app:latest .

# Kubernetes deploy
kubectl apply -f k8s/

# Helm install
helm install realtime-app ./realtime-app-chart
```

---

## 🧾 Troubleshooting

- Check logs: `kubectl logs <pod>` or `docker logs <container>`
- Database connection errors: verify MySQL credentials, service name, network access
- Port conflicts: ensure `8080` is free or change `server.port` in `application.properties`

---

## ✍️ License & Author

**Author:** Srinivasa Rao  
**License:** 

---
# 🧠 Realtime App

A full-stack **Spring Boot + MySQL** backend application demonstrating real-time data management, containerization with **Docker**, and deployment on **Kubernetes**. and **Project workflow**

---

## 📘 Overview

This project provides RESTful APIs to manage user data using **Spring Boot**, **Spring Data JPA**, and **MySQL**.
It includes CI/CD automation through **Jenkins**, and ready-to-use manifests for **Kubernetes** deployment.

---

## ⚙️ Architecture Flow

**Request → Controller → Repository → Database**

1. **Client Request:** A user sends an HTTP request (e.g., POST `/api/users`).
2. **DispatcherServlet:** Spring Boot routes the request to the correct controller.
3. **Controller Layer:** `UserController` handles the request and calls the repository.
4. **Repository Layer:** `UserRepository` (Spring Data JPA) saves data via Hibernate.
5. **Database:** Data is persisted in MySQL.
6. **Response:** The saved data is serialized and returned as JSON.

### 📊 Sequence Diagram

```
Client --> Tomcat (Embedded)
Tomcat --> DispatcherServlet
DispatcherServlet --> UserController#createUser()
UserController --> UserRepository.save()
UserRepository --> Hibernate/JPA
Hibernate --> MySQL (INSERT)
MySQL --> Hibernate --> UserRepository --> Controller --> DispatcherServlet --> Client (JSON)
```

---

## 🏗️ Project Structure

```
src/
 └── main/java/com/example/realtimeapp/
     ├── RealtimeAppApplication.java     # Main Spring Boot entry point
     ├── controller/
     │    ├── UserController.java        # API endpoints for user management
     │    └── ApiController.java         # Health/root endpoints
     ├── model/
     │    └── User.java                  # JPA Entity
     ├── repository/
     │    └── UserRepository.java        # Data access layer
resources/
 └── application.properties              # DB & JPA configurations
```

---

## 🚀 How the Application Starts

1. `RealtimeAppApplication.main()` executes first.
2. Spring Boot auto-configures components (DataSource, Repositories, Controllers).
3. Embedded Tomcat starts on **port 8080**.
4. App is ready to accept requests.

---

## 💾 API Endpoints

| Method | Endpoint     | Description           |
| ------ | ------------ | --------------------- |
| GET    | `/api/users` | Retrieve all users    |
| POST   | `/api/users` | Create new user       |
| GET    | `/api`       | API root/health check |

### Example Request

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "Srinivas", "email": "srinivas@example.com"}'
```

---

## 🧩 Local Setup

### Prerequisites

* JDK 17+
* Maven 3.8+
* MySQL running locally or in Docker
* (Optional) Docker & kubectl for deployment

### Build & Run Locally

```bash
# Build
mvn clean package -DskipTests

# Run
java -jar target/realtime-app.jar
```

App will be available at:
👉 [http://localhost:8080/api](http://localhost:8080/api)

---

## 🐳 Docker Setup

### Build Image

```bash
docker build -t <your-dockerhub-username>/realtime-app:latest .
```

### Run Container

```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/realtime_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=sree! \
  <your-dockerhub-username>/realtime-app:latest
```

---

## ☸️ Kubernetes Deployment

Kubernetes manifests are available in the `k8s/` directory.

### Step-by-step Deployment

```bash
# Create Namespace
kubectl create namespace realtime

# Deploy MySQL
kubectl apply -f k8s/mysql-deployment.yaml -n realtime

# Deploy Application
kubectl apply -f k8s/realtime-app-deployment.yaml -n realtime

# Check Pods & Services
kubectl get pods -n realtime
kubectl get svc -n realtime
```

Access the application using NodePort service:

```
http://<node-ip>:31265/api
```

---

## ⚙️ Jenkins CI/CD

A `Jenkinsfile` is included for automated pipeline:

1. Checkout source
2. Build JAR using Maven
3. Build & push Docker image (`IMAGE_NAME=srinu298/realtime-app`)
4. Deploy to Kubernetes cluster

Pipeline stages:

```
Build → Docker Build → Push → Deploy
```

---

## 🗄️ Database Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://mysql:3306/realtime_db
spring.datasource.username=root
spring.datasource.password=sree!
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

For Kubernetes, ensure:

* MySQL Service name = `mysql`
* Same namespace (`realtime`)
* PVC attached for persistent storage

---

## 🧠 Key Technologies

* **Spring Boot 3.x**
* **Spring Data JPA (Hibernate)**
* **MySQL 8**
* **Docker**
* **Jenkins**
* **Kubernetes (YAML + Helm chart)**

---

## ✅ Health Check

The application exposes Spring Actuator endpoints:

```
GET /actuator/health
GET /actuator/info
```

You can configure readiness/liveness probes in K8s for production.

---

## 👨‍💻 Author

**Srinivasa Rao P**
📧 [srinivasarao2455@gmail.com](mailto:srinivas@example.com)
🔗 [LinkedIn](#) • 

---

## 📄 License




# E-commerce Microservices Application

## 🚀 Project Overview

This repository hosts a conceptual e-commerce backend built on a **Microservices Architecture** using Java and the Spring Cloud ecosystem. The primary objective is to demonstrate how core business capabilities (product catalog, inventory, and order processing) can be segregated into independently deployable and scalable services.

## ⚙️ Technology Stack

| Component | Technology | Role in Project |
| :--- | :--- | :--- |
| **Backend Framework** | Java, Spring Boot | Foundation for building RESTful microservices. |
| **Architecture** | Spring Cloud | Provides tools for service discovery, gateway routing, and resilience. |
| **Service Discovery** | **Netflix Eureka** | **Crucial for locating services** dynamically by name instead of hardcoded IP/port. |
| **API Gateway** | Spring Cloud Gateway | Single entry point for all client requests, providing routing and load balancing. |
| **Data Persistence** | Spring Data JPA / (Planned: PostgreSQL/MongoDB) | To be implemented for transactional and non-transactional data storage per service. |
| **Build Tool** | Maven (or Gradle) | Dependency management and build automation. |

## 📐 Microservices Architecture

The application is decomposed into the following core services:

| Service Name | Repository Folder | Primary Responsibility | Interacts With |
| :--- | :--- | :--- | :--- |
| **`eureka-server`** | (Dedicated Module) | The central service registry. | All microservices (as a client) |
| **`api-gateway`** | `api-gateway` | Routes external traffic to internal services. | All microservices (via Eureka) |
| **`product-service`** | `product-service` | Manages the product catalog (CRUD operations). | - |
| **`inventory-service`** | `inventory-service` | Manages stock quantities and checks availability. | - |
| **`order-service`** | `order-service` | Handles order creation and checkout logic. | `inventory-service` (for stock deduction) |


## 💻 Getting Started (Setup)
Since the project is in its early stages, the following steps assume you are setting up the full environment, including the necessary infrastructure components (like Eureka, which is a key missing piece).

Prerequisites
Java Development Kit (JDK) 17 or higher

Apache Maven

A preferred IDE (IntelliJ IDEA, Eclipse, VS Code)
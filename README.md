# Online Shopping System (COMP603 Project)

![Java](https://img.shields.io/badge/Java-21-orange)
![Maven](https://img.shields.io/badge/Maven-3.x-blue)
![Swing](https://img.shields.io/badge/GUI-Swing-green)
![Derby](https://img.shields.io/badge/Database-Apache%20Derby-red)

A comprehensive desktop-based Online Shopping System developed for the **COMP603 Program Design & Construction** course. This project demonstrates the application of **Object-Oriented Programming (OOP)** principles, **Java Swing GUI**, and **Embedded Database** integration, strictly adhering to the course requirements.

## 📋 Project Overview

This application allows users to browse products, manage a shopping cart, and place orders, while providing administrators with tools to manage inventory. It is designed to be **robust, bug-free, and easy to run** without manual configuration.

### Key Features
*   **User Interface (GUI)**: A user-friendly Swing interface with distinct panels for Login, Product Browsing, Cart, and Admin Dashboard.
*   **Database Integration**: Uses **Apache Derby (Embedded)** for persistent storage of Users, Products, and Orders. The database is **automatically initialized** on the first run (Zero-Configuration).
*   **Functionality**:
    *   **Authentication**: Secure login/register for Customers and Admins.
    *   **Shopping**: Add/remove items, view cart, and checkout.
    *   **Admin Tools**: Add/Edit/Delete products and manage orders.
*   **Unit Testing**: Includes **JUnit 5** test cases covering critical business logic (Authentication, Cart calculations, Models).

## 🛠 Technical Highlights (Marking Criteria)

This project implements the following technical requirements:

### 1. Object-Oriented Design
*   **Encapsulation**: All models (`User`, `Product`) use private fields with public getters/setters.
*   **Inheritance**: `PhysicalProduct` and `DigitalProduct` extend the abstract `Product` class.
*   **Polymorphism**: The `PaymentStrategy` interface allows different payment behaviors (`CreditCardStrategy`, `WalletStrategy`) to be used interchangeably.
*   **Design Patterns**:
    *   **DAO Pattern**: Separates data access logic (`UserDAO`, `ProductDAO`) from business logic.
    *   **Strategy Pattern**: Used for the payment system.
    *   **Singleton/Static Factory**: Used in `DBManager` for database connections.

### 2. Database
*   **DBMS**: Apache Derby (v10.15.2.0).
*   **Connection**: JDBC is used for all database interactions.
*   **Auto-Setup**: `DBManager` checks for table existence and runs `schema.sql` automatically.

### 3. Quality & Robustness
*   **Error Handling**: Comprehensive `try-catch` blocks for SQL and IO exceptions.
*   **Input Validation**: GUI forms validate user input (e.g., non-empty fields, numeric prices) before processing.

## 🚀 Quick Start

### Prerequisites
*   **JDK 21** (Required).
*   **Maven** (Recommended for building).
*   *Compatible with NetBeans 23.*

### How to Run
1.  **Clone the repository**:
    ```bash
    git clone <repository-url>
    cd my_onlineshopingSystem
    ```

2.  **Build the project**:
    ```bash
    mvn clean install
    ```

3.  **Run the application**:
    ```bash
    mvn exec:java -Dexec.mainClass="com.comp603.shopping.ShoppingApp"
    ```
    *Note: The `shopping_db` folder will be automatically created in the project root.*

## 🧪 Testing

The project includes **14 Unit Tests** (exceeding the minimum of 5) covering:
*   `AuthServiceTest`: Login, Registration, Logout logic.
*   `ProductTest`: Model integrity and inheritance.
*   `ShoppingCartTest`: Total calculation and item management.
*   `WalletStrategyTest`: Payment logic.

Run tests using:
```bash
mvn test
```

## 📂 Project Structure

*   `com.comp603.shopping`
    *   `gui/`: Swing UI components (MainFrame, Panels, Dialogs).
    *   `models/`: Domain entities (User, Product, Order).
    *   `dao/`: Data Access Objects (JDBC implementation).
    *   `services/`: Business logic and Strategies.
    *   `config/`: Database configuration (`DBManager`).

## 👥 Contribution

**Group ID**: [Your Group ID]

| Student ID | Name | Contribution |
| :--- | :--- | :--- |
| [ID 1] | [Name 1] | [Brief description of work] |
| [ID 2] | [Name 2] | [Brief description of work] |

*(Update this table with your actual group details)*

## 📜 License

This project is for educational purposes under the COMP603 course at AUT.

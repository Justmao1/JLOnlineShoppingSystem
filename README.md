# Online Shopping System

![Java](https://img.shields.io/badge/Java-21-orange)
![Maven](https://img.shields.io/badge/Maven-3.x-blue)
![Swing](https://img.shields.io/badge/GUI-Swing-green)
![Derby](https://img.shields.io/badge/Database-Apache%20Derby-red)

A comprehensive desktop-based Online Shopping System built with Java Swing, featuring user authentication, product browsing, shopping cart management, and an admin dashboard.

## Core Features

*   **User Authentication**: Secure login and registration system for customers and administrators.
*   **Product Management**: Browse products with images, prices, and stock status. Support for both Physical and Digital products.
*   **Shopping Cart**: Add items to cart, update quantities, and view total costs in real-time.
*   **Wishlist**: Save favorite items for later.
*   **Checkout System**: Secure checkout with mock payment processing (Credit Card & Wallet).
*   **Order History**: View past orders and track their status.
*   **Admin Dashboard**: comprehensive tools for admins to manage products (add/edit/delete) and process orders (ship/cancel).
*   **Embedded Database**: Zero-configuration setup using Apache Derby; the database initializes automatically on first run.

## Tech Stack

*   **Language**: Java 21
*   **Build Tool**: Maven
*   **GUI Framework**: Java Swing (javax.swing)
*   **Database**: Apache Derby (Embedded, v10.15.2.0)
*   **Testing**: JUnit 5

## Prerequisites

Before you begin, ensure you have the following installed:

*   **Java Development Kit (JDK) 21** or higher.
*   **Maven** (for building and running the project).
*   **IDE** (Optional but recommended): IntelliJ IDEA, Eclipse, or VS Code.

## Quick Start

Follow these steps to get the project running on your local machine.

### 1. Clone the Repository

```bash
git clone <repository-url>
cd my_onlineshopingSystem
```

### 2. Build the Project

Use Maven to clean and install dependencies:

```bash
mvn clean install
```

### 3. Run the Application

You can run the application directly using the Maven Exec plugin:

```bash
mvn exec:java -Dexec.mainClass="com.comp603.shopping.ShoppingApp"
```

*Note: The application will automatically create the `shopping_db` folder in your project directory and initialize the database with seed data on the first run.*

## Project Structure

*   `/src/main/java/com/comp603/shopping/`
    *   `ShoppingApp.java`: The main entry point of the application.
    *   `gui/`: Contains all Swing UI components (Frames, Panels, Dialogs).
    *   `models/`: Data models (POJOs) representing User, Product, Order, etc.
    *   `dao/`: Data Access Objects for database interactions.
    *   `services/`: Business logic services (AuthService, PaymentStrategy).
    *   `config/`: Database configuration and initialization logic (`DBManager`).
*   `/src/main/resources/`: Contains `schema.sql` for database initialization.
*   `/shopping_db/`: The embedded Derby database files (created at runtime).

## Contributing

Contributions are welcome! If you find a bug or want to add a feature:

1.  Fork the repository.
2.  Create a new branch (`git checkout -b feature/AmazingFeature`).
3.  Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4.  Push to the branch (`git push origin feature/AmazingFeature`).
5.  Open a Pull Request.

## License

This project is licensed under the **MIT License**.

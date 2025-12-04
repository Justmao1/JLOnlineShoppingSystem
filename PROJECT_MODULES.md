# Project Module Breakdown

## I. Project Overview

*   **A. Project Goal:** The **Online Shopping System** is a comprehensive desktop application designed to facilitate e-commerce operations. It enables users to browse products, manage a shopping cart, place orders, and track their history, while providing administrators with tools to manage inventory and fulfill orders.
*   **B. Architectural Style:** The project follows a **Layered Architecture** (Presentation, Business/Service, Data Access). It separates concerns by isolating the Swing GUI (Presentation) from the Business Logic (Services) and Data Persistence (DAOs).

## II. Core Module Breakdown

### 1. Root Package (`com.comp603.shopping`)
*   **Primary Responsibility:** Serves as the entry point and container for application-wide utilities.
*   **Key Components:**

| Component | Role/Responsibility |
| :--- | :--- |
| `ShoppingApp.java` | **Entry Point**. Initializes the Swing GUI on the Event Dispatch Thread and sets up the Look and Feel. |
| `CheckUsers.java` | **Utility**. A standalone script to verify and list users currently in the database (for debugging). |
| `TestMyAccount.java` | **Utility**. A standalone script to test backend logic (DAOs) without the GUI. |

### 2. Configuration (`com.comp603.shopping.config`)
*   **Primary Responsibility:** Manages application configuration and infrastructure setup, specifically the database connection.
*   **Key Components:**

| Component | Role/Responsibility |
| :--- | :--- |
| `DBManager` | **Database Connection Factory**. Manages the JDBC connection to the embedded Apache Derby database. It also handles **auto-initialization** by running `schema.sql` if the database is empty. |

### 3. Data Access Layer (`com.comp603.shopping.dao`)
*   **Primary Responsibility:** Handles all interactions with the database. It abstracts SQL queries from the rest of the application.
*   **Key Components:**

| Component | Role/Responsibility |
| :--- | :--- |
| `UserDAO` | Manages user registration, profile updates, and balance management. |
| `ProductDAO` | Handles product search, retrieval, stock updates, and administration (add/edit/delete). |
| `OrderDAO` | Manages order creation, history retrieval, cancellation, and status updates. |
| `ShoppingCartDAO` | Persists cart items for users, allowing their cart to be saved across sessions. |
| `WishlistDAO` | Manages the user's wishlist items. |

### 4. Graphical User Interface (`com.comp603.shopping.gui`)
*   **Primary Responsibility:** The Presentation Layer. Handles all user input and renders the application state.
*   **Key Sub-Packages:**

#### a. Main Container (`com.comp603.shopping.gui`)
| Component | Role/Responsibility |
| :--- | :--- |
| `MainFrame` | **Main Controller**. The primary `JFrame` that holds the application. It uses a `CardLayout` to switch between different views (Login, Products, Checkout, etc.) and manages the global `AuthService` and `ShoppingCart` state. |

#### b. Panels (`com.comp603.shopping.gui.panels`)
| Component | Role/Responsibility |
| :--- | :--- |
| `LoginPanel` | Handles user login and delegates registration to `RegisterDialog`. |
| `ProductListPanel` | Displays the catalog of products using `ProductCard` components and a `CarouselPanel`. |
| `CheckoutPanel` | Manages the checkout process, including payment method selection (Credit Card/Wallet). |
| `AdminDashboard` | The administrative interface for managing products and orders. |
| `OrderHistoryPanel` | Displays past orders and allows cancellation. |
| `ProfilePanel` | Allows users to view and edit their personal details. |
| `WalletPanel` | Manages user balance and saved payment methods. |

#### c. Dialogs (`com.comp603.shopping.gui.dialogs`)
| Component | Role/Responsibility |
| :--- | :--- |
| `CartDialog` | Displays the current shopping cart in a modal window, allowing quantity adjustments. |
| `ProductDialog` | A form for Admins to add or edit products. |
| `OrderDetailDialog` | Shows detailed information about a specific order. |
| `RegisterDialog` | Handles new user registration. |

#### d. Components (`com.comp603.shopping.gui.components`)
| Component | Role/Responsibility |
| :--- | :--- |
| `ProductCard` | A reusable UI component representing a single product in the grid view. |
| `CarouselPanel` | A custom component for displaying featured products in a rotating carousel. |

### 5. Domain Models (`com.comp603.shopping.models`)
*   **Primary Responsibility:** Defines the core business entities (POJOs) used throughout the application.
*   **Key Components:**

| Component | Role/Responsibility |
| :--- | :--- |
| `User` | Represents a registered user (Customer or Admin). |
| `Product` | Base class for items. Extended by `PhysicalProduct` and `DigitalProduct`. |
| `Order` / `OrderItem` | Represents a completed transaction and its contents. |
| `ShoppingCart` / `CartItem` | Represents the temporary state of items a user intends to purchase. |

### 6. Services (`com.comp603.shopping.services`)
*   **Primary Responsibility:** Contains business logic that doesn't fit strictly into DAOs or UI.
*   **Key Components:**

| Component | Role/Responsibility |
| :--- | :--- |
| `AuthService` | Manages the current user session (login, logout, current user tracking). |
| `PaymentStrategy` | **Strategy Pattern**. Defines the contract for payment processing. Implemented by `CreditCardStrategy` and `WalletStrategy`. |

## III. Data Flow and Interaction

### A. Request Lifecycle (Example: "Place Order")
1.  **User Action**: User clicks "Confirm Payment" in `CheckoutPanel`.
2.  **Controller/UI**: `CheckoutPanel` validates input and invokes the selected `PaymentStrategy`.
3.  **Service**: `PaymentStrategy` (e.g., `WalletStrategy`) verifies funds and deducts the amount.
4.  **Data Access**:
    *   `OrderDAO` is called to insert the new `Order` and `OrderItem` records into the database.
    *   `ProductDAO` is called to decrease the stock quantity of purchased items.
    *   `ShoppingCartDAO` is called to clear the user's cart.
5.  **Feedback**: The UI displays a success message and redirects the user to the Product List.

### B. Inter-Module Dependencies
*   **GUI** depends on **Services** (for session/logic) and **DAOs** (for data).
*   **Services** depend on **Models** and sometimes **DAOs**.
*   **DAOs** depend on **DBManager** (for connection) and **Models** (for mapping results).
*   **MainFrame** acts as the central coordinator, holding instances of `AuthService` and `ShoppingCart` and passing them to child panels.

## IV. Technology Integration

*   **A. Database:**
    *   **Implementation**: **JDBC** (Java Database Connectivity) is used for direct SQL interaction.
    *   **Engine**: **Apache Derby** (Embedded). The database files are stored locally in the `shopping_db` directory.
    *   **Initialization**: The `DBManager` checks for the existence of tables on startup and executes `schema.sql` if needed, ensuring a "Zero Configuration" experience.

*   **B. External Services:**
    *   **Payment**: The system uses a **Mock Payment Implementation** via the Strategy Pattern. No actual external payment gateway (Stripe/PayPal) is connected; logic is handled internally for demonstration purposes.

# Guided Tour Management System

![Java](https://img.shields.io/badge/Java-OOP-ED8B00?logo=openjdk&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit5-Testing-25A162?logo=junit5&logoColor=white)
![Design Patterns](https://img.shields.io/badge/Design_Patterns-MVC_%7C_Strategy_%7C_State-success)

Full-stack Java desktop application for guided tour reservations and management. Developed to demonstrate advanced Object-Oriented Programming (OOP) principles, Clean Architecture, and GoF Design Patterns.

## 📌 Project Overview
This system provides a robust platform for managing the entire lifecycle of cultural tours, with strict role-based access control (Administrators, Guides/Volunteers, and Clients). The architecture relies on standard design patterns to ensure scalability, maintainability, and a clean separation of concerns, abstracting the business logic from the console-based UI and data persistence layers.

## ⚙️ Key Features & Architecture
* **MVC Architecture:** Strictly separates domain entities (Model), console interactions (View), and business logic coordination (Controller).
* **Behavioral Design Patterns:**
  * **Strategy Pattern:** Dynamically manages UI menus and available commands based on the active user role (`MenuConfiguratoreStrategy`, `MenuFruitoreStrategy`).
  * **State Pattern:** Encapsulates the lifecycle of a guided tour (`StatoProposta`, `StatoConfermata`, `StatoEffettuata`, etc.), handling automatic state transitions based on bookings and chronological constraints.
* **Repository Pattern:** Decouples the business logic from the physical storage using interface-driven repositories.
* **Data Persistence (JSON):** Engineered a local data layer using Gson, implementing Custom Type Adapters to serialize and deserialize polymorphic states and Java 8 Time APIs (`LocalDate`, `LocalTime`).
* **Unit Testing:** Comprehensive test suite built with JUnit 5 to validate state transitions, booking logic, and data loading.

## 📂 Repository Structure
```text
├── src/progettoUnibs/
│   ├── Main.java                 # Application entry point
│   ├── controller/               # Business logic and repositories
│   ├── model/                    # Domain entities and state pattern implementations
│   ├── utils/                    # Utilities and Gson Custom Adapters
│   └── view/                     # Console UI components
├── lib/                          # External dependencies (Gson, JUnit Console)
├── test/                         # JUnit 5 integration and domain unit tests
└── README.md
```

## 🚀 How to Run

**1. Compilation**
From the project root, compile the source code into a `bin/` directory:
```bash
# Linux / macOS
javac -cp "lib/*" -d bin $(find src -name "*.java")

# Windows
javac -cp "lib/*" -d bin src/progettoUnibs/Main.java src/progettoUnibs/**/*.java
```

**2. Execution**
Run the console application:
```bash
# Linux / macOS
java -cp "bin:lib/*" progettoUnibs.Main

# Windows
java -cp "bin;lib/*" progettoUnibs.Main
```

**3. Running the Test Suite**
Execute the JUnit 5 tests via the standalone console:
```bash
javac -cp "lib/*:bin" -d bin test/*.java
java -jar lib/junit-platform-console-standalone-1.10.0.jar --class-path "bin:lib/*" --select-package ""
```

## 📄 Project Documentation & UML
For a comprehensive overview of the software engineering design phase, including use case diagrams and textual use cases mapping the actors' interactions (Configuratore, Volontario, Fruitore), please refer to the official documentation:
* [**Software Engineering UML Documentation (PDF)**](./docs/Software_Engineering_UML_Documentation.pdf)
``` *(Nota: assicurati che il nome del file nel link corrisponda a quello che hai effettivamente caricato).*

In questo modo, la tua documentazione di progettazione (UML e casi d'uso) diventa facilmente accessibile, rafforzando ulteriormente l'aspetto professionale e accademico del tuo lavoro[cite: 8].

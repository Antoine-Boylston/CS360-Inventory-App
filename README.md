# SmartStock Inventory Tracker | Android Application

**Course:** CS 360 – Mobile Application Development  
**Author:** Antoine Boylston  

---

## Project Overview

SmartStock is a lightweight Android inventory tracking application designed to help individuals and small teams monitor stock levels quickly and easily. The app allows users to add items, update quantities, and receive SMS alerts when inventory reaches zero.

The goal of the project was to design a simple, user-centered application that solves a practical problem without unnecessary complexity.

This project demonstrates mobile development concepts including UI design, local data management, RecyclerView implementation, and runtime permission handling.

---

## Technologies Used

- Java
- Android Studio
- RecyclerView
- SQLite Database
- Android SDK
- XML Layout Design

---
  
## Key Concepts Demonstrated

- Mobile UI design using Android layouts
- Dynamic data display using RecyclerView
- Local database management
- Runtime permission handling
- User-centered design improvements based on usability feedback

---

## What I Learned

Through developing this application I learned the importance of designing software around real user behavior rather than assumptions. Early testing revealed that terminology and workflows that made sense from a development perspective were confusing to users. Simplifying the interface and focusing on core functionality significantly improved usability.

This project also strengthened my understanding of structuring Android applications, managing UI components, and handling permissions responsibly.

---

## Screenshots

*(Add screenshots here)*

---
  

## User Needs and App Purpose

The app was created to address a common problem: keeping track of inventory in a simple and accessible way. Many inventory systems are overly complex for small operations or personal use. SmartStock focuses on essential functionality so users can quickly see what items they have, update quantities, and be notified when stock runs out.

By prioritizing clarity and ease of use, the app helps reduce mistakes and ensures users can manage inventory without needing specialized training.

---

## Screens and Features

The application includes several key screens and features that support a user-centered design:

- Inventory list displaying all tracked items  
- Ability to add new inventory items  
- Quantity controls for increasing or decreasing stock  
- Visual indicators for low or empty inventory  
- Optional SMS alerts when stock reaches zero  

The interface was intentionally designed to be simple and intuitive. During development, usability feedback helped refine the terminology and workflow so the app would make sense to users without technical or industry-specific knowledge.

---

## Development Approach

The application was developed incrementally, focusing on one feature at a time. Core components such as database integration, RecyclerView display, and user interactions were built and tested individually before being combined into the final application.

Organizing the project this way made debugging easier and ensured that each part of the app worked reliably before moving forward. This approach is useful for future development because it encourages modular design and reduces complexity during troubleshooting.

---

## Testing and Validation

Testing involved repeatedly performing the primary user actions within the app, including:

- Adding inventory items  
- Updating quantities  
- Preventing quantities from dropping below zero  
- Verifying SMS alerts trigger correctly  
- Confirming the app behaves properly if SMS permissions are denied  

Testing is critical in mobile development because applications must handle a wide range of user behaviors and device configurations. Through testing, usability improvements were identified and stability issues were resolved.

---

## Challenges and Innovation

One of the biggest challenges during development was ensuring the interface was intuitive for users who were unfamiliar with maintenance or technical terminology. Initial designs included terms that made sense from a developer perspective but were confusing during user testing.

Feedback from real-world testing led to simplifying the design, clarifying language, and removing unnecessary screens. This iteration improved the user experience and reinforced the importance of user-centered design.

---

## Key Strength of the Project

A strong aspect of this project is the integration of core Android development practices such as structured UI design, dynamic list handling with RecyclerView, local data management, and responsible runtime permission handling for SMS functionality.

These components demonstrate an understanding of both technical implementation and practical usability considerations.

---

## Project Structure
src/main/java – Application logic and activities

src/main/res – Layout files and UI resources

AndroidManifest.xml – App configuration and permissions

---

## Future Improvements

Future development could expand the application with features such as:

- Cloud synchronization  
- Barcode scanning  
- Custom low-inventory thresholds  
- Data export functionality  
- Multi-user support  

These improvements would allow the app to scale from a simple inventory tracker to a more robust business tool.

---

## Repository Purpose

This repository serves as a portfolio artifact demonstrating the design and development of a functional Android mobile application as part of the SNHU Computer Science program.

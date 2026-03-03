---
description: Best practices for clean and maintainable code in Java
applyTo: '**/*.java'
---

# Clean code best practices 

## Variables and naming
- Name variables and functions descriptively to enhance readability.
- Use named constants instead of magic numbers or strings.
## Functions and complexity
- Keep functions small and focused on a single task.
- Avoid nested structures to reduce complexity.
- Use early returns to minimize indentation.
## Classes and modules
- Avoid primitive obsession by defining types.
- Favor composition over inheritance.
- Keep dependencies to a minimum.
- Use adapter pattern to decouple from external systems.
- Maintain a shared module (folder...) for common utilities and types.
## Error handling and comments
- Handle errors gracefully with try-catch blocks and meaningful messages.
- Write comments to explain the "why" behind complex logic, not the "what".
## General principles
- Keep it simple and avoid over-engineering.
- Try to keep it DRY (Don't Repeat Yourself) by reusing code where applicable.

## Java specific guidelines

- Use packages to organize code logically.
- Favor composition over inheritance.
- Use interfaces to define contracts.
- Follow Java naming conventions (CamelCase for classes, camelCase for methods and variables).
- Use `final` for constants and immutable fields.
- Use `Optional` to represent nullable values instead of `null`.
- Use `try-with-resources` for automatic resource management.
- Declare types for data structures in their own classes or records in separate files.
- Use enums and immutable value objects to represent fixed sets of constant values.
- Define interfaces for class contract behavior in their own files and program to interfaces rather than implementations.
- Avoid `null` where possible; prefer `Optional` and clear default values.
- Leverage Java's standard utility types and collections (e.g., `Optional`, `Stream`, `List`, `Map`) appropriately.
- Use appropriate concurrency tools (e.g., `CompletableFuture`, `ExecutorService`, or virtual threads) and handle exceptions explicitly when running asynchronous tasks.

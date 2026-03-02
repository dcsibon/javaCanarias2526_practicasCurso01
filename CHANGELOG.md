# Changelog

All notable changes to this project are documented in this file.

## [0.1.0] - 2026-03-02

### Added
- Rocket management CRUD API with `POST /rockets`, `GET /rockets`, `GET /rockets/{id}`, `PUT /rockets/{id}`, and `DELETE /rockets/{id}`.
- Input validation for rocket `name`, `range`, and `capacity` according to business constraints.
- Centralized API error handling for validation and not-found scenarios.
- End-to-end test suite covering all acceptance criteria from `specs/rockets.spec.md`.

### Changed
- Project version updated from `0.0.1-SNAPSHOT` to `0.1.0` for release.

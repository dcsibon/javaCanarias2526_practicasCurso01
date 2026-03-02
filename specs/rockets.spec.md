# Rocket Management API Specification
## Problem Description
- As a travel operations manager, I want to create rocket records with valid name, range, and capacity so that available rockets can be configured in AstroBookings.
- As a booking platform administrator, I want to update existing rocket records so that rocket details stay accurate as fleet capabilities change.
- As a booking platform administrator, I want to view and remove rocket records so that only current, bookable rockets are exposed to downstream booking flows.

## Solution Overview
- Provide a REST API endpoint set to create, read, update, and delete rocket resources for AstroBookings.
- Validate incoming rocket data against the business rules: required name, range restricted to `suborbital|orbital|moon|mars`, and capacity constrained to 1-10 passengers.
- Persist rocket records in the application data store and return clear success/error responses so client applications can manage rockets reliably.

## Acceptance Criteria
- [ ] When a client sends a create request with valid `name`, `range`, and `capacity`, the system shall store a new rocket and return a successful creation response.
- [ ] When a create or update request omits `name`, the system shall reject the request and return a validation error indicating `name` is required.
- [ ] When a create or update request provides `range` outside `suborbital`, `orbital`, `moon`, or `mars`, the system shall reject the request with a validation error.
- [ ] When a create or update request provides `capacity` below 1 or above 10, the system shall reject the request with a validation error.
- [ ] Where a rocket exists, when a client requests that rocket by identifier, the system shall return the rocket details including `name`, `range`, and `capacity`.
- [ ] Where no rocket exists for a requested identifier, when a client performs a read, update, or delete request, the system shall return a not-found response.
- [ ] When a client sends an update request with valid fields for an existing rocket, the system shall persist the new values and return a successful update response.
- [ ] When a client requests the rocket collection, the system shall return a list of all stored rockets.
- [ ] When a client deletes an existing rocket, the system shall remove it from storage and return a successful deletion response.

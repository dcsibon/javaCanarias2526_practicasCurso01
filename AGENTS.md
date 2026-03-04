# Agents Instructions

## Product Overview
- AstroBookings Rockets API manages rocket records for booking operations.
- It exposes CRUD endpoints under `/rockets` using REST.
- It validates payload fields and returns consistent API errors.

## Technical Implementation

### Tech Stack
- Language: **Java 17**
- Framework: **Spring Boot 3.3.5**
- Database: **None (in-memory repository)**
- Security: **Input validation and global API error handling**
- Testing: **Spring Boot Test (JUnit 5)**
- Logging: **Spring Boot default logging (Logback)**

### Development workflow
```bash
# Set up the project
mvn -version

# Build/Compile the project
mvn clean compile

# Run the project
mvn spring-boot:run

# Test the project
mvn test

# Deploy the project
mvn clean package
java -jar target/rockets-api-0.1.0.jar
```

### Folder structure
```text
.                         # Project root
├── AGENTS.md             # This file with instructions for AI agents
├── README.md             # The main human documentation file
├── pom.xml               # Maven config and dependencies
├── CHANGELOG.md          # Release notes
├── prompts/              # Prompt files for project tasks
├── specs/                # Markdown specifications
├── instrucciones/        # Exercise instructions
├── src/main/java/        # Application source code
├── src/test/java/        # Test source code
└── target/               # Build output
```

## Environment
- Code and documentation must be in English.
- Chat responses must be in the language of the user prompt.
- Sacrifice grammar for conciseness in responses.
- This is a windows environment using git bash terminal.
- My default branch is `main`.

## AI Agents Structure

This project defines agents, prompts and skills stored in the `.agents` directory.

```text
.agents/
├── agents/             # Agent role definitions
│   ├── coder.md
│   └── dev-ops.md
│
├── skills/             # Reusable agent capabilities
│   ├── commit-changes.md
│   └── releasing-version/
│       ├── SKILL.md
│       └── sem-ver.md
│
└── prompts/            # Entry prompts used to trigger workflows
    └── resolve-issue.md
```

### Agents

Agents represent specialized roles in the development workflow.

An **agent** performs a specific task using the project context, available tools, and defined steps.

After completing its task, the agent performs a **handoff**, passing the updated context and responsibility to the next agent in the workflow.

**Coder**
- Implements the solution described in a GitHub issue.
- Reads the implementation plan from the issue body.
- Creates a feature branch.
- Writes the minimal code necessary to complete the tasks.
- Updates the issue checklist and commits the changes.

**Handoff**
- After completing the implementation, hand off the workflow to the **DevOps** agent to prepare the release.

---

**DevOps**
- Acts as a DevOps engineer responsible for integrating the implementation.
- Updates documentation.
- Merges changes into the default branch.
- Prepares the release process.

**Uses Skill**
- `releasing-version` for documentation updates, changelog generation and semantic versioning.

### Skills

Skills define reusable procedures that agents can execute.

**commit-changes**
- Commits pending changes in the repository.
- Ensures a clean working directory before starting work.
- Uses conventional commit messages.

**releasing-version**
- Updates project documentation
- Generates changelog entries
- Applies semantic versioning rules.

Additional documentation used by this skill:
- `sem-ver.md`

### Prompts

Prompts act as entry points to start workflows.

**resolve-issue**

- Starts the development workflow for resolving a GitHub issue.
- Triggers the implementation process handled by the agents.
- The issue ID should be provided so the agents can read the implementation plan.

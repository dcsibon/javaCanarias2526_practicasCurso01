---
name: releasing-version
description: >
  Updates documentation, generates changelogs, and handles versioning.
  To be used for automating release tasks in a git repository.
---

# Releasing Version Skill

When asked for releasing a version, follow these steps:

1. **Update documentation**:
- Update [AGENTS.md](/AGENTS.md) to reflect recent changes:
    - tech stack,
    - setup/dev instructions,
    - folder structure.
- Update other relevant project files if applicable (e.g., `package.json`, additional docs).

2. **Ensure changes are committed**:
- Check for uncommitted changes with `git status`.
- Commit all pending changes, grouping them logically by type of change (e.g., docs, fixes, features).
- Use conventional commit messages to keep commit history readable.

3. **Generate changelog**:
- Read the commit history to determine the nature of changes included in the release.  [oai_citation:2‡3-3-4-sem-ver.md](sediment://file_000000000b0c720ab4c9a19244d07693)
- Update [CHANGELOG.md](/CHANGELOG.md) by adding entries based on the commit history.

4. **Determine next version using SemVer**:
- Use **Semantic Versioning (MAJOR.MINOR.PATCH)** principles:  [oai_citation:3‡3-3-4-sem-ver.md](sediment://file_000000000b0c720ab4c9a19244d07693)
    - Increment **MAJOR** for breaking changes (commit type with `!`, e.g., `<type>!:`).
    - Increment **MINOR** for new features (`feat:`).
    - Increment **PATCH** for bug fixes (`fix:`).
    - Ignore other commit types for version increments.

5. **Finalize release (merge + tag)**:
- If there is an issue/ticket id in context, include `Close #ID` in the relevant commit message.
- Merge the work into the default branch.
- Create a git tag for the new version (e.g., `vMAJOR.MINOR.PATCH`).
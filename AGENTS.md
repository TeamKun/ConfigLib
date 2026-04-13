# Agent Instructions

This repository contains the ConfigLib library itself.

For repository maintenance, bug fixes, tests, and implementation work, read:

- `docs/agents/repository.md`
- `docs/agents/testing.md`

Do not treat `.claude/skills/configlib` or `.claude/skills/commandlib` as
repository-maintenance instructions. Those skills are for downstream library
users who want to generate or understand usage code.

Prefer focused Gradle verification for touched modules. Do not rewrite unrelated
code, generated files, or user changes.

# Contributing Guidelines

This document describes the key shared practices to follow when contributing.

## 1. Branching Strategy
- `main`: production-ready code
- `dev`: integration branch
- `feature/<jira-id>-short-description`: new features or fixes

All changes must be made through Pull Requests.

## 2. Commit Messages
- Follow the convention:
`<type>(<scope>): <short description>`
- Examples:
- `feat(api): add user registration`
- `fix(auth): handle token expiration`

## 3. Code Style
### Backend (Java / Spring)
- Follow standard Java conventions
- Use meaningful class and method names
- Keep methods small and focused
- Prefer constructor injection
- Avoid hardcoded configuration values

### Frontend
- Use consistent formatting
- Prefer small, reusable components
- Avoid inline styles when possible

## 4. Testing
- New features must include tests
- All tests must pass before opening a Pull Request
- CI pipeline must be green

## 5. Pull Requests
- One logical change per Pull Request
- Link the related Jira issue (e.g. `TQS-17`)
- Ensure CI and Quality Gates pass before requesting review

## 6. Documentation
- Keep documentation up to date when adding new features

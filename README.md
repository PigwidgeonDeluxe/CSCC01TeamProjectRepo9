# PROJECT STRUCTURE
## Branches
There are two main branches `master` and `develop`

`master`

- Main branch of the project
- Code base is result of `develop` being merged into `master`
    - Code is merged after pre-defined milestones (defined on JIRA)
    - DO NOT MERGE ANY BRANCH OTHER THAN `develop` INTO `master`
        - With the exception of `hotfix/` branches
- `master` code is guaranteed to be stable after peer-review

`develop`

- Develop branch of the project
- All features and fixes are merged into `develop` upon completion
- `develop` is merged into `master` after pre-defined milestones are met

---

## Branching Prefixes
Every new branch should be prefixed with one of the following prefixes:

1. `feature/`
    - New features; components that do not already exist in the project
2. `fix/`
    - Fixes to bugs identified in JIRA
    - Fixes should be suffixed with the appropriate JIRA ticket number
        - Example: `fix/pr-001`
3. `hotfix/`
    - Fixes to bugs identified in the `master` branch
    - To fix:
        1. Branch the `hotfix/` branch off of `master`
        2. Fix the bug on the `hotfix/` branch
        3. Merge the `hotfix/` into both `develop` and `master`

---

## Merging
Ensure that your code is reviewed before merging into develop. NEVER merge a branch into `develop` yourself. Always submit a pull request to another team member to review code and let your reviewer merge into `develop`

Merge steps:

1. Submit a pull request to a team member to review your code
2. Team member who you submitted pull request to should review code and check for errors or practices that go against coding principles
3. Fix any issues with code if there are any and re-assign the pull request to the team member
4. The team member who reviewed your code merges your code into `develop`

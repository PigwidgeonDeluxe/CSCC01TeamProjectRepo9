# UTSC Search Engine

## Installation

Prerequisites:

  - NodeJS
  - Angular CLI
  - Maven
  - Java

Building:

  - Frontend:
    - `cd` to `src/main/webapp/`
    - `npm install`
    - Build with `ng serve -o`
      - App opens on `http://localhost:4200`

  - Backend:
    - Build with `mvn clean install tomcat7:run`
    - Backend runs on `http://localhost:8080`

---

## Directory Structure
This section will keep track and explain the logic behind the folder structure.
Folder structures will be subject to change as new folder structures will be created and modified

---

`docs`
- contains the various documentation related to the project
- each section of documentation is separated into the phase of which it was created in
    - i.e. `phase-1`, `phase-2`, `phase-3`, etc...

`phase-1`

- contains the documentation created in phase-1 of the project:
    - `competition.md`
        - describes competing products to our project and reasons why our product is better
    - `process.md`
        - describes the process the team uses to work together and outlines various tools used to complete the project
    - `summary.md`
        - a summary of the high-level overview of the project
    - `UX-UI_websiteMockup.pdf`
        - a high-level mockup of the UI/UX components of the project
    - `user_stories.md`
        - a collection of the various user stories to be used in the project
    - `Personas.pdf`
        - a collection of the personas to be used in the project
    - `team_photo.jpg`
        - a photograph of the team

`phase-2`

- contains the CRC cards for the various classes created in phase-2 of the project
    - `CRC-cards.md`
        - the various CRC cards to be used for the classes in the project

---

## Special Files

This section describes the files found at the root level and outlines their purpose

`BRANCHING_RULES.md`

- located at `docs/BRANCHING_RULES.md`
- outlines the branching rules for pushing and merging branches to the origin repo
    - documentation for internal use
## SearchIndexedFiles

**Class Name**: SearchIndexedFiles
**Parent Class**: None
**Subclasses**: None

**Responsibilities**:

- SQL simple search
- search by file type
- search by user type
- search by uploader

**Collaborators**:

- FileIndexing

---

## UserCreation

**Class Name**: UserCreation
**Parent Class**: None
**Subclasses**: None

**Responsibilities**:

- create student user
- create instructor user

**Collaborators**:

- UserDatabase
- UserAuthentication

---

## UserDatabase

**Class Name**: UserDatabase
**Parent Class**: None
**Subclasses**: None

**Responsibilities**:

- add user
- delete user
- get user
- set user as either student or instructor

**Collaborators**:

- UserCreation
- UserAccount
- UserAuthentication

---

## UserAccount

**Class Name**: UserAccount
**Parent Class**: None
**Subclasses**: StudentAccount, InstructorAccount

**Responsibilities**:

- edit account info
- view account info

**Collaborators**:

- UserDatabase

---

## StudentAccount

**Class Name**: StudentAccount
**Parent Class**: UserAccount
**Subclasses**: None

**Responsibilities**:

- edit account info
- view account info

**Collaborators**:

- UserDatabase

---

## InstructorAccount

**Class Name**: InstructorAccount
**Parent Class**: UserAccount
**Subclasses**: None

**Responsibilities**:

- edit account info
- view account info

**Collaborators**:

- UserDatabase

---

## FileStoring

**Class Name**: FileStoring
**Parent Class**: None
**Subclasses**: None

**Responsibilities**:

- store uploaded files in a database

**Collaborators**:

- UserDatabase

---

## FileIndexing

**Class Name**: FileIndexing
**Parent Class**: None
**Subclasses**: None

**Responsibilities**:

- index files

**Collaborators**:

- UserDatabase
- UserPasswordDatabase

---

## UserAuthentication

**Class Name**: UserAuthentication
**Parent Class**: None
**Subclasses**: None

**Responsibilities**:

- compare input password with stored password

**Collaborators**:

- UserPasswordDatabase

---

## UserPasswordDatabase

**Class Name**: UserPasswordDatabase
**Parent Class**: None
**Subclasses**: None

**Responsibilities**:

- add password
- get password
- delete password

**Collaborators**:

- UserAuthentication

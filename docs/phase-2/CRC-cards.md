## <a id="searchIndexedFiles">SearchIndexedFiles</a>

**Class Name**: SearchIndexedFiles
<br>
**Parent Class**: None
<br>
**Subclasses**: None

**Responsibilities**:

- SQL simple search
- search by file type
- search by user type
- search by uploader

**Collaborators**:

- <a href="#fileIndexing">FileIndexing</a>

---

## <a id="userCreation">UserCreation</a>

**Class Name**: UserCreation
<br>
**Parent Class**: None
<br>
**Subclasses**: None

**Responsibilities**:

- create student user
- create instructor user

**Collaborators**:

- <a href="#userDatabase">UserDatabase</a>
- <a href="#userAuthentication">UserAuthentication</a>

---

## <a id="userDatabase">UserDatabase</a>

**Class Name**: UserDatabase
<br>
**Parent Class**: None
<br>
**Subclasses**: None

**Responsibilities**:

- add user
- delete user
- get user
- set user as either student or instructor

**Collaborators**:

- <a href="#userCreation">UserCreation</a>
- <a href="#userAccount">UserAccount</a>
- <a href="#userAuthentication">UserAuthentication</a>

---

## <a id="userAccount">UserAccount</a>

**Class Name**: UserAccount
<br>
**Parent Class**: None
<br>
**Subclasses**: StudentAccount, InstructorAccount

**Responsibilities**:

- edit account info
- view account info

**Collaborators**:

- <a href="#userDatabase">UserDatabase</a>

---

## <a id="studentAccount">StudentAccount</a>

**Class Name**: StudentAccount
<br>
**Parent Class**: UserAccount
<br>
**Subclasses**: None

**Responsibilities**:

- edit account info
- view account info

**Collaborators**:

- <a href="#userDatabase">UserDatabase</a>

---

## <a id="instructorAccount">InstructorAccount</a>

**Class Name**: InstructorAccount
<br>
**Parent Class**: UserAccount
<br>
**Subclasses**: None

**Responsibilities**:

- edit account info
- view account info

**Collaborators**:

- <a href="#userDatabase">UserDatabase</a>

---

## <a id="fileStoring">FileStoring</a>

**Class Name**: FileStoring
<br>
**Parent Class**: None
<br>
**Subclasses**: None

**Responsibilities**:

- store uploaded files in a database

**Collaborators**:

- <a href="#userDatabase">UserDatabase</a>

---

## <a id="fileIndexing">FileIndexing</a>

**Class Name**: FileIndexing
<br>
**Parent Class**: None
<br>
**Subclasses**: None

**Responsibilities**:

- index files

**Collaborators**:

- <a href="#userDatabase">UserDatabase</a>
- <a href="#userPasswordDatabase">UserPasswordDatabase</a>

---

## <a id="userAuthentication">UserAuthentication</a>

**Class Name**: UserAuthentication
<br>
**Parent Class**: None
<br>
**Subclasses**: None

**Responsibilities**:

- compare input password with stored password

**Collaborators**:

- <a href="#userPasswordDatabase">UserPasswordDatabase</a>

---

## <a id="userPasswordDatabase">UserPasswordDatabase</a>

**Class Name**: UserPasswordDatabase
<br>
**Parent Class**: None
<br>
**Subclasses**: None

**Responsibilities**:

- add password
- get password
- delete password

**Collaborators**:

- <a href="#userAuthentication">UserAuthentication</a>

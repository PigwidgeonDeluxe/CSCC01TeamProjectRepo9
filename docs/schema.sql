CREATE TABLE user (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id TEXT,
  user_type TEXT,
  created_on INTEGER,
  user_name TEXT,
  profile_image TEXT
);

CREATE TABLE file (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  file BLOB,
  file_name TEXT,
  file_type TEXT,
  file_size INTEGER,
  user_id INTEGER,
  uploaded_on INTEGER
);

CREATE TABLE following (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  user_id TEXT,
  following_user_id TEXT,
);

CREATE TABLE comments (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  file_id INTEGER,
  user_id INTEGER,
  comment TEXT,
  date INTEGER
);
CREATE TABLE IF NOT EXISTS GameEvents (
  Id          INTEGER PRIMARY KEY,
  Name        TEXT    NOT NULL,
  AuthorName  TEXT    NOT NULL,
  GameName    TEXT    NOT NULL,
  Description TEXT,
  StartTime   TEXT    NOT NULL,
  EndTime     TEXT,
  Hidden      INTEGER NOT NULL
);
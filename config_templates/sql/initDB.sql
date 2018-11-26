DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS cityes;
DROP TABLE IF EXISTS groups;
DROP TABLE IF EXISTS projects;
DROP SEQUENCE IF EXISTS user_seq;
DROP SEQUENCE IF EXISTS city_seq;
DROP SEQUENCE IF EXISTS group_seq;
DROP SEQUENCE IF EXISTS project_seq;
DROP TYPE IF EXISTS user_flag;

CREATE TYPE user_flag AS ENUM ('active', 'deleted', 'superuser');
CREATE SEQUENCE user_seq START 100000;
CREATE TABLE users (
  id        INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
  full_name TEXT NOT NULL,
  email     TEXT NOT NULL,
  flag      user_flag NOT NULL,
  city      integer,
  foreign key (city) references cityes(id)
);

CREATE UNIQUE INDEX email_idx ON users (email);

CREATE SEQUENCE city_seq START 100000;
CREATE TABLE cityes (
  id        INTEGER PRIMARY KEY DEFAULT nextval('city_seq'),
  city_name TEXT NOT NULL
);

CREATE SEQUENCE group_seq START 100000;
CREATE TABLE groupps (
  id        INTEGER PRIMARY KEY DEFAULT nextval('group_seq'),
  group_name TEXT NOT NULL
);

CREATE SEQUENCE project_seq START 100000;
CREATE TABLE projects (
  id        INTEGER PRIMARY KEY DEFAULT nextval('project_seq'),
  project_name TEXT NOT NULL,
  groupp     integer,
  FOREIGN KEY (groupp) REFERENCES groupps(id)
);
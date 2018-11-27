DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS cities CASCADE;
DROP TABLE IF EXISTS groups CASCADE;
DROP TABLE IF EXISTS projects CASCADE;
DROP SEQUENCE IF EXISTS user_seq CASCADE;
DROP SEQUENCE IF EXISTS city_seq CASCADE;
DROP SEQUENCE IF EXISTS group_seq CASCADE;
DROP SEQUENCE IF EXISTS project_seq CASCADE;
DROP TYPE IF EXISTS user_flag;
DROP TYPE IF EXISTS group_type CASCADE;

CREATE SEQUENCE city_seq START 100000;
CREATE TABLE cities (
  id        INTEGER PRIMARY KEY DEFAULT nextval('city_seq'),
  city_name TEXT NOT NULL,
  city_code TEXT NOT NULL
);

CREATE TYPE user_flag AS ENUM ('active', 'deleted', 'superuser');
CREATE SEQUENCE user_seq START 100000;
CREATE TABLE users (
  id        INTEGER PRIMARY KEY DEFAULT nextval('user_seq'),
  full_name TEXT NOT NULL,
  email     TEXT NOT NULL,
  flag      user_flag NOT NULL,
  city      integer,
  foreign key (city) references cities(id)  ON DELETE CASCADE
);
CREATE UNIQUE INDEX email_idx ON users (email);

CREATE SEQUENCE project_seq START 100000;
CREATE TABLE projects (
  id        INTEGER PRIMARY KEY DEFAULT nextval('project_seq'),
  project_name TEXT NOT NULL
);

CREATE TYPE group_type AS ENUM ('FINISHED', 'CURRENT', 'PLANNED');
CREATE SEQUENCE group_seq START 100000;
CREATE TABLE groups (
  id        INTEGER PRIMARY KEY DEFAULT nextval('group_seq'),
  group_name TEXT NOT NULL,
  group_type group_type NOT NULL,
  project   integer,
  FOREIGN KEY (project) REFERENCES projects(id) ON DELETE CASCADE
);

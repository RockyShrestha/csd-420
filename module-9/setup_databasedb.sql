-- Rakesh Shrestha
-- CSD420-T301 Advanced Java Programming
-- Module 9.2 Programming Assignment
-- Date: 11/07/2026
--
-- Creates the student1 account and the databasedb database for the
-- Module 9 JDBC test programs. Run it while logged in as root:
--
--   cd "C:\Program Files\MySQL\MySQL Server 5.7\bin"
--   mysql -u root -p
--   source C:/csd/csd-420/module-9/setup_databasedb.sql
--
-- Adapted from Commands_Used.txt by Professor Darrell Payne.

-- See which accounts already exist.
SELECT User, Host FROM mysql.user;

-- Remove an old student1 account if one is left over from a past attempt.
DROP USER IF EXISTS 'student1'@'localhost';

-- Create the account the Java programs log in with.
CREATE USER 'student1'@'localhost' IDENTIFIED BY 'pass';

-- Create the database.
DROP DATABASE IF EXISTS databasedb;
CREATE DATABASE databasedb;

-- Give student1 full rights on databasedb so JDBC can create tables.
GRANT ALL PRIVILEGES ON databasedb.* TO 'student1'@'localhost';
FLUSH PRIVILEGES;

-- Confirm the setup.
SHOW DATABASES;
SHOW GRANTS FOR 'student1'@'localhost';

-- Rakesh Shrestha
-- CSD420-T301 Advanced Java Programming
-- Module 10.2 Programming Assignment
-- November 15, 2026
--
-- Purpose: Builds and fills the fans table on my home MySQL server so I
-- have data to test with. The Java program never runs this script and
-- never creates or deletes the table. Run it once in MySQL Workbench
-- while logged in as root.

-- These three lines are only needed if databasedb and student1 are not
-- set up yet from the earlier modules.

CREATE DATABASE IF NOT EXISTS databasedb;
CREATE USER IF NOT EXISTS 'student1'@'localhost' IDENTIFIED BY 'pass';
GRANT ALL PRIVILEGES ON databasedb.* TO 'student1'@'localhost';

USE databasedb;

CREATE TABLE IF NOT EXISTS fans (
    ID INT PRIMARY KEY,
    firstname VARCHAR(25),
    lastname VARCHAR(25),
    favoriteteam VARCHAR(25)
);

INSERT INTO fans (ID, firstname, lastname, favoriteteam) VALUES
    (1, 'Maya', 'Lopez', 'Cubs'),
    (2, 'Owen', 'Reed', 'Royals'),
    (3, 'Priya', 'Nair', 'Cardinals'),
    (4, 'Sam', 'Carter', 'Dodgers'),
    (5, 'Elena', 'Brooks', 'Red Sox');

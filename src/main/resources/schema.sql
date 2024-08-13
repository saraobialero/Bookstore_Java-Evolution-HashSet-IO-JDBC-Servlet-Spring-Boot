DROP DATABASE IF EXISTS bookshop_db;

-- Create the database if it doesn't exist
CREATE DATABASE IF NOT EXISTS bookshop_db;

-- Switch to the newly created database
USE bookshop_db;

CREATE TABLE IF NOT EXISTS books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    publication_year DATE,
    description TEXT,
    ISBN VARCHAR(13) UNIQUE NOT NULL,
    available BOOLEAN NOT NULL DEFAULT TRUE
);
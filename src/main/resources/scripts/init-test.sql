CREATE DATABASE IF NOT EXISTS CloudFileStorageTest;
CREATE USER IF NOT EXISTS CFS_Test_User IDENTIFIED BY 'password';
CREATE TABLE IF NOT EXISTS users (
    id int AUTO_INCREMENT,
    username varchar(20) UNIQUE NOT NULL,
    password varchar(100) NOT NULL,
    role varchar(20) NOT NULL,
    PRIMARY KEY (id)
);
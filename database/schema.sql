-- Pay My Buddy

CREATE DATABASE IF NOT EXISTS pay_my_buddy_db;
USE pay_my_buddy_db;

-- Tables
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS user_connections;
DROP TABLE IF EXISTS users;

-- Users
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL
);

-- User Connections
CREATE TABLE user_connections (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  connection_id INT NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (connection_id) REFERENCES users(id),
  UNIQUE (user_id, connection_id)
);

-- Transactions
CREATE TABLE transactions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  sender_id INT NOT NULL,
  receiver_id INT NOT NULL,
  description VARCHAR(255),
  amount_in_cents BIGINT NOT NULL,
  status VARCHAR(50) NOT NULL,
  FOREIGN KEY (sender_id) REFERENCES users(id),
  FOREIGN KEY (receiver_id) REFERENCES users(id)
);

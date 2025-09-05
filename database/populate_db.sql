-- Pay My Buddy - Données de test

USE pay_my_buddy_db;

-- Utilisateurs de démo => password3
INSERT INTO users (username, email, password, balance_in_cents)
VALUES
('testuser3', 'test3@example.com', '$2a$10$PSGnSL..lxfxqtcuC.g5W.iucBHd/jVbn5u8AZJBQO7HwIeC3chNy', 10000),
('testuser7', 'test7@example.com', '$2a$10$3uFU/EQdSU26OlzIYywlP.YIxWRxDruPVIzXYRhCLsoPgICfieVQe', 10000);

-- Connexions entre utilisateurs
INSERT INTO user_connections (user_id, connection_id) VALUES
(1, 2), -- test3 -> test7

-- Transactions de démo
INSERT INTO transactions (sender_id, receiver_id, description, amount_in_cents, status)
VALUES 
(1, 2, 'Déjeuner', 500, 'SUCCESS'),
(2, 1, 'Partage Uber', 200, 'SUCCESS'),
(1, 3, 'Café', 150, 'SUCCESS');

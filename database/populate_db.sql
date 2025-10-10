-- Pay My Buddy - Données de test

USE pay_my_buddy_db;

-- Utilisateurs de démo => password3
INSERT INTO users (username, email, password, balance_in_cents) VALUES
('testuser3', 'test3@example.com', '$2a$10$PSGnSL..lxfxqtcuC.g5W.iucBHd/jVbn5u8AZJBQO7HwIeC3chNy', 10000),
('testuser7', 'test7@example.com', '$2a$10$3uFU/EQdSU26OlzIYywlP.YIxWRxDruPVIzXYRhCLsoPgICfieVQe', 10000);

-- User connections
INSERT INTO user_connections (user_id, connection_id) VALUES
(1, 2),
(2, 1);


-- Transactions
INSERT INTO transactions (sender_id, receiver_id, description, amount_in_cents) VALUES 
(1, 2, 'Déjeuner', 500),
(2, 1, 'Partage Uber', 200),
(1, 2, 'Café', 150),
(2, 1, 'Cinéma', 800),
(1, 2, 'Courses partagées', 1200),
(2, 1, 'Restaurant', 1500),
(1, 2, 'Taxi', 600),
(2, 1, 'Boissons', 300),
(1, 2, 'Pizza', 900),
(2, 1, 'Musée', 400),
(1, 2, 'Petit-déjeuner', 250),
(2, 1, 'Livres', 750),
(1, 2, 'Cadeau anniversaire', 2000),
(2, 1, 'Sport', 350),
(1, 2, 'Apéro', 450);

-- Pay My Buddy - Données de test

USE pay_my_buddy_db;

-- Utilisateurs de démo
INSERT INTO users (username, email, password, balance_in_cents)
VALUES
('alice',   'alice@example.com',   'password123', 1000),
('bob',     'bob@example.com',     'password123', 1000),
('charlie', 'charlie@example.com', 'password123', 1000);

-- Connexions entre utilisateurs
INSERT INTO user_connections (user_id, connection_id) VALUES
(1, 2), -- Alice -> Bob
(1, 3), -- Alice -> Charlie
(2, 1), -- Bob -> Alice
(3, 1); -- Charlie -> Alice

-- Transactions de démo
INSERT INTO transactions (sender_id, receiver_id, description, amount_in_cents, status)
VALUES 
(1, 2, 'Déjeuner', 500, 'SUCCESS'),
(2, 1, 'Partage Uber', 200, 'SUCCESS'),
(1, 3, 'Café', 150, 'SUCCESS');

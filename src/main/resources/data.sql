USE bookshop_db_v4;

-- Inserimento dei libri
INSERT INTO books (title, author, publication_year, description, ISBN, quantity, available) VALUES
('To Kill a Mockingbird', 'Harper Lee', '1960-07-11', 'The unforgettable novel of a childhood in a sleepy Southern town and the crisis of conscience that rocked it.', '9780446310789', 5, true),
('1984', 'George Orwell', '1949-06-08', 'A dystopian social science fiction novel and cautionary tale.', '9780451524935', 3, true),
('Pride and Prejudice', 'Jane Austen', '1813-01-28', 'A romantic novel of manners.', '9780141439518', 4, true),
('The Great Gatsby', 'F. Scott Fitzgerald', '1925-04-10', 'A novel of the Jazz Age.', '9780743273565', 2, true),
('Moby-Dick', 'Herman Melville', '1851-10-18', 'The saga of Captain Ahab and his monomaniacal pursuit of the white whale.', '9780142437247', 3, true),
('The Catcher in the Rye', 'J.D. Salinger', '1951-07-16', 'A controversial novel originally published for adults, it has since become popular with adolescent readers for its themes of teenage angst and alienation.', '9780316769174', 4, true),
('The Hobbit', 'J.R.R. Tolkien', '1937-09-21', 'A fantasy novel and children's book by English author J. R. R. Tolkien.', '9780547928227', 5, true);

-- Inserimento degli utenti (password hashate con bcrypt per sicurezza)
INSERT INTO users (name, surname, email, password) VALUES
('Mario', 'Rossi', 'mario.rossi@email.com', '$2a$10$51OJj1/YmoNSFPeJeh7VweEbnJf6O7KYA/bqbYS3Xg2dvA8EjB/Ky'),
('Anna', 'Bianchi', 'anna.bianchi@email.com', '$2a$10$CwPtbVrLNHPNi6gK4lvXJu5oOGGPNM3IUyGEG1tIGCwEOlT2nTMEm'),
('Luca', 'Verdi', 'luca.verdi@email.com', '$2a$10$Y9zd3s/uTT1Ff4tZ9.Ey2eFJRvb1YQD3mkhZ0CPlLcx4lojKh4aIG'),
('Giulia', 'Neri', 'giulia.neri@email.com', '$2a$10$8.iubUNjOK5GQMNgBNuzBOLq5XbARsmsCaSY4Oo4Md5VDd4WZKPKy');

-- Inserimento dei prestiti di libri
INSERT INTO users_has_books (user_id, book_id, quantity, borrow_date, return_date) VALUES
(1, 1, 1, '2023-05-01 10:00:00', NULL),
(1, 3, 1, '2023-05-02 14:30:00', '2023-05-15 11:45:00'),
(2, 2, 1, '2023-05-03 09:15:00', NULL),
(2, 4, 1, '2023-05-04 16:20:00', NULL),
(3, 5, 1, '2023-05-05 13:00:00', '2023-05-20 10:30:00'),
(3, 6, 1, '2023-05-06 11:45:00', NULL),
(4, 7, 2, '2023-05-07 15:10:00', NULL);

-- Aggiornamento delle quantità disponibili dei libri in base ai prestiti
UPDATE books SET quantity = quantity - 1 WHERE id IN (1, 2, 4, 6);
UPDATE books SET quantity = quantity - 2 WHERE id = 7;
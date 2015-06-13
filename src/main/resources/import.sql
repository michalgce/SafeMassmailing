INSERT INTO subscriber(first_name, last_name, mail) VALUES ('Michał', 'Witkowski', 'michal@gmail.com');
INSERT INTO subscriber(first_name, last_name, mail) VALUES ('Sandra', 'Kamela', 'sandra@gmail.com');

INSERT INTO groups(name) VALUES ('Gliwice');
INSERT INTO groups(name) VALUES ('Toszek');

INSERT INTO smtp_configuration(address, name, password, port, user_name) VALUES ('localhost', 'Localhost SMTP server', null, '1025', null);
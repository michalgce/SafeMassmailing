INSERT INTO subscriber(first_name, last_name, mail) VALUES ('Michał', 'Witkowski', 'michal@gmail.com');
INSERT INTO subscriber(first_name, last_name, mail) VALUES ('Sandra', 'Kamela', 'sandra@gmail.com');

INSERT INTO groups(name) VALUES ('Gliwice');
INSERT INTO groups(name) VALUES ('Toszek');

INSERT INTO smtp_configuration(address, name, password, port, user_name, active) VALUES ('localhost', 'Localhost SMTP server', null, '1025', null, false);
INSERT INTO smtp_configuration(address, name, password, port, user_name, active) VALUES ('mailtrap.io', 'Mailtrap SMTP server', '5ce43a85af510b', '25', '390016a9248e2506b', true);

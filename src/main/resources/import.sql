INSERT INTO subscriber(first_name, last_name, mail) VALUES ('Michal', 'Witkowski', 'michalgce@gmail.com');
INSERT INTO subscriber(first_name, last_name, mail) VALUES ('Sandra', 'Kamela', 'kamela.sandra@gmail.com');

INSERT INTO groups(name) VALUES ('Gliwice');
INSERT INTO groups(name) VALUES ('Toszek');

INSERT INTO smtp_configuration(address, name, password, port, user_name, active) VALUES ('localhost', 'Localhost SMTP server', null, '1025', null, false);
INSERT INTO smtp_configuration(address, name, password, port, user_name, active) VALUES ('mailtrap.io', 'Mailtrap SMTP server', '5ce43a85af510b', '25', '390016a9248e2506b', false);
INSERT INTO smtp_configuration(address, name, password, port, user_name, active) VALUES ('in-v3.mailjet.com', 'Mail JET Server', '284613132b1d294d9c6b827bd53f6fba', '587', '6de07ce015e8704241c073054bab8035', false);
INSERT INTO smtp_configuration(address, name, password, port, user_name, active) VALUES ('poczta.o2.pl', 'Poczta O2', 'pracamgr2015', 587, 'mgr.praca@o2.pl', true);

INSERT INTO subscriber_group (subscriber_id, group_id) VALUES (1, 1);

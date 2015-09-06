INSERT INTO subscriber(first_name, last_name, mail) VALUES ('Michal', 'Witkowski', 'michalgce@gmail.com');
INSERT INTO subscriber(first_name, last_name, mail) VALUES ('Sandra', 'Kamela', 'kamela.sandra@gmail.com');

INSERT INTO groups(name) VALUES ('Gliwice');
INSERT INTO groups(name) VALUES ('Toszek');

INSERT INTO smtp_configuration(address, name, password, port, user_name, active) VALUES ('localhost', 'Localhost SMTP server', null, '1025', null, false);
INSERT INTO smtp_configuration(address, name, password, port, user_name, active) VALUES ('mailtrap.io', 'Mailtrap SMTP server', '5ce43a85af510b', '25', '390016a9248e2506b', false);
INSERT INTO smtp_configuration(address, name, password, port, user_name, active) VALUES ('in-v3.mailjet.com', 'Mail JET Server', '284613132b1d294d9c6b827bd53f6fba', '587', '6de07ce015e8704241c073054bab8035', true);
INSERT INTO smtp_configuration(address, name, password, port, user_name, active) VALUES ('poczta.o2.pl', 'Poczta O2', 'pracamgr2015', 587, 'mgr.praca@o2.pl', false);
INSERT INTO smtp_configuration(address, name, password, port, user_name, active) VALUES ('smtp.gmail.com', 'gMail', '!Objectivec1', 587, 'pracamgr2015aei@gmail.com', false);
INSERT INTO smtp_configuration(address, name, password, port, user_name, active) VALUES ('smtp.poczta.onet.pl', 'onet.pl', 'pracamgr2015', 587, 'magisterska.praca@onet.pl', false);
INSERT INTO smtp_configuration(address, name, password, port, user_name, active) VALUES ('smtp-mail.outlook.com', 'outlook.com', '!Objectivec1', 587, 'praca.magisterska2015@outlook.com', false);
INSERT INTO smtp_configuration(address, name, password, port, user_name, active) VALUES ('poczta.t.pl', 't.pl', 'pracamgr2015', 587, 'mgr.praca@t.pl', false);
INSERT INTO smtp_configuration(address, name, password, port, user_name, active) VALUES ('poczta.os.pl', 'os.pl', 'pracamgr2015', 587, 'mgr.praca10@os.pl', false);
INSERT INTO smtp_configuration(address, name, password, port, user_name, active) VALUES ('poczta.os.pl', 'os.pl #2', 'pracamgr2015', 587, 'mgr.praca14@adresik.com', false);
INSERT INTO smtp_configuration(address, name, password, port, user_name, active) VALUES ('serwer1429092.home.pl', 'serwer1429092.home.pl', 'pracamgr2015', 587, 'mgr.praca@serwer1429092.home.pl', false);
INSERT INTO smtp_configuration(address, name, password, port, user_name, active) VALUES ('mail.michalgce.ehost.pl', 'mail.michalgce.ehost.pl', 'pracamgr2015', 587, 'mgr.praca@michalgce.ehost.pl', false);
INSERT INTO smtp_configuration(address, name, password, port, user_name, active) VALUES ('smtp.mandrillapp.com', 'smtp.mandrillapp.com', 'qwCaqWI8xM2ZpJ4o08mBFw', 587, 'michalgce@gmail.com', false);

INSERT INTO subscriber_group (subscriber_id, group_id) VALUES (1, 1);

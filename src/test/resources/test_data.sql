INSERT INTO users (email, password, role, first_name, second_name, patronymic, birth_day)
VALUES ('testAdmin@gmail.com', 'password', 'ROLE_ADMIN', 'Admin',
        'Testov', 'Adminovich', '1980-01-15'),
       ('user2@example.com', 'password', 'ROLE_USER', 'User',
        'Second', 'Userovich', '1990-05-20'),
       ('user3@example.com', 'password', 'ROLE_USER', 'User',
        'Third', 'Userovich', '1995-11-10'),
       ('user4@example.com', 'password', 'ROLE_USER', 'User',
        'Fourth', 'Userovich', '1985-03-25'),
       ('user5@example.com', 'password', 'ROLE_USER', 'User',
        'Fifth', 'Userovich', '1978-07-05'),
       ('user6@example.com', 'password', 'ROLE_USER', 'User',
        'Sixth', 'Userovich', '2000-09-18'),
       ('user7@example.com', 'password', 'ROLE_USER', 'User',
        'Seventh', 'Userovich', '1992-02-01'),
       ('user8@example.com', 'password', 'ROLE_USER', 'User',
        'Eighth', 'Userovich', '1988-06-30'),
       ('user9@example.com', 'password', 'ROLE_USER', 'User',
        'Ninth', 'Userovich', '1975-12-12'),
       ('user10@example.com', 'password', 'ROLE_USER', 'User',
        'Tenth', 'Userovich', '1998-04-08');

INSERT INTO phone_numbers (phone_number, user_id)
VALUES ('+7 (111) 111-11-11', 1),
       ('+7 (112) 222-22-22', 1),
       ('81111111111', 1),

       ('+7 (113) 333-33-33', 2),
       ('+7 (114) 444-44-44', 2),
       ('81122222222', 2),

       ('+7 (115) 555-55-55', 3),
       ('+7 (116) 666-66-66', 3),
       ('81133333333', 3),

       ('+7 (117) 777-77-77', 4),
       ('+7 (118) 888-88-88', 4),
       ('81144444444', 4),

       ('+7 (119) 999-99-99', 5),
       ('+7 (120) 000-00-00', 5),
       ('81155555555', 5),

       ('+7 (121) 111-11-11', 6),
       ('+7 (122) 222-22-22', 6),
       ('81166666666', 6),

       ('+7 (123) 333-33-33', 7),
       ('+7 (124) 444-44-44', 7),
       ('81177777777', 7),

       ('+7 (125) 555-55-55', 8),
       ('+7 (126) 666-66-66', 8),
       ('81188888888', 8),

       ('+7 (127) 777-77-77', 9),
       ('+7 (128) 888-88-88', 9),
       ('81199999999', 9),

       ('+7 (129) 999-99-99', 10),
       ('+7 (130) 000-00-00', 10),
       ('81100000000', 10);
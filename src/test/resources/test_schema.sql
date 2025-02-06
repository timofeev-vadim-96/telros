create table if not exists users
(
    id       bigserial primary key,
    first_name varchar(50),
    second_name varchar(50),
    patronymic varchar(50),
    birth_day date,
    email    varchar(255) not null unique,
    password varchar(255) not null,
    role     varchar(255) not null
        constraint users_role_check
            check (role IN ('ROLE_ADMIN', 'ROLE_USER'))
);

create table if not exists phone_numbers
(
    id       bigserial primary key,
    phone_number varchar(20) not null unique,
    user_id bigint references users (id)
);
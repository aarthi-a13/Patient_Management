create table patients
(
    id              serial
        primary key,
    first_name      varchar not null,
    last_name       varchar not null,
    date_of_birth   date    not null,
    contact_number  varchar(20),
    medical_history text,
    created_at      timestamp with time zone default CURRENT_TIMESTAMP,
    updated_at      timestamp with time zone default CURRENT_TIMESTAMP
);

alter table patients
    owner to admin;


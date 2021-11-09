create table result_description
(
    id          integer not null
        constraint result_description_pkey
            primary key,
    description varchar(255)
);
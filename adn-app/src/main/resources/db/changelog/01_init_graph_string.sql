create table graph_string
(
    id  bigserial
        constraint graph_string_pkey
            primary key,
    dna varchar(255)
);

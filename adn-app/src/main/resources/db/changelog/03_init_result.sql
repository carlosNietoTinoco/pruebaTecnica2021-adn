create table result
(
    graph_string_id bigint not null
        constraint result_pkey
            primary key
        constraint fk9ha159vcg8lukjejliksp3a93
            references graph_string,
    id              integer
        constraint fk41loq97jnr15m86bl1vl03q1m
            references result_description
);
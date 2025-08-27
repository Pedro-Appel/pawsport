create table Pet
(
    id         uuid   not null,
    birthdate  date,
    active     bool,
    breed      varchar(36),
    color      varchar(36),
    conditions varchar(255),
    name       varchar(36),
    species    varchar(36),
    weight     float4 not null,
    primary key (id)
);
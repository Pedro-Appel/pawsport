-- docker run -p 5432:5432 --name postgres -e POSTGRES_USER=quarkus -e POSTGRES_PASSWORD=quarkus -d postgres:17
create sequence Vaccine_SEQ start with 1 increment by 1;

create table Vaccine
(
    id             bigint not null,
    date           date,
    expirationDate date,
    petId          varchar(36),
    type           varchar(36),
    vetId          varchar(36),
    primary key (id)
);
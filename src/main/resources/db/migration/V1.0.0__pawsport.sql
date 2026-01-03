
    create sequence Treatment_SEQ start with 1 increment by 1;

    create sequence Vaccine_SEQ start with 1 increment by 1;

    create table Pet (
        active boolean not null,
        birthdate date,
        weight float4 not null,
        id uuid not null,
        breed varchar(36),
        color varchar(36),
        conditions varchar(255),
        name varchar(255),
        species varchar(255),
        primary key (id)
    );

    create table Treatment (
        endDate date,
        startDate date,
        id bigint not null,
        petId varchar(36),
        type varchar(255),
        vetId varchar(36),
        primary key (id)
    );

    create table Vaccine (
        active boolean not null,
        date date,
        expirationDate date,
        id bigint not null,
        petId varchar(36),
        type varchar(255),
        vetId varchar(36),
        primary key (id)
    );

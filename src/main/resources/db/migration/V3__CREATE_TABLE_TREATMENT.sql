create sequence Treatment_SEQ start with 1 increment by 1;

create table Treatment
(
    id        bigint not null,
    type      varchar(150),
    startDate date,
    endDate   date,
    active    bool default true,
    petId     varchar(36),
    vetId     varchar(36),
    primary key (id)
);
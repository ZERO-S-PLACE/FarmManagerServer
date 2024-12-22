create table field_group
(
    id                 varchar(36)  not null primary key,
    field_group_name   varchar(100) not null,
    description        text,
    version            integer      not null,
    created_date       timestamp    not null,
    last_modified_date timestamp    not null,
    user_id            varchar(36)  not null,
    foreign key (user_id) references user (id) ON DELETE CASCADE
) engine = InnoDB;

create table field_part
(
    id                 varchar(36)    not null primary key,
    field_part_name    varchar(100)   not null,
    area               decimal(10, 2) not null,
    is_archived        boolean        not null,
    description        text,
    version            integer        not null,
    created_date       timestamp      not null,
    last_modified_date timestamp      not null,
    field_id           varchar(36)    not null,
    foreign key (field_id) references field (id) ON DELETE CASCADE
) engine = InnoDB;

alter table field
    add column field_group_id varchar(36) not null,
    add constraint foreign key (field_group_id) references field_group (id) ON DELETE CASCADE;
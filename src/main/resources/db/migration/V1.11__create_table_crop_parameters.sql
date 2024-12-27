create table crop_parameters
(
    id                 varchar(36)   not null primary key,
    version            integer       not null,
    created_date       timestamp     not null,
    last_modified_date timestamp     not null,
    crop_type          varchar(40)   not null,
    name               varchar(100)  not null,
    created_by         varchar(36)   not null,
    resource_type      varchar(30)   not null,
    comment            text          not null,
    pollution          decimal(5, 2) not null,
    gluten_content     decimal(10, 2),
    protein_content    decimal(10, 2),
    falling_number     decimal(10, 2),
    density            decimal(10, 2),
    humidity           decimal(10, 2),
    oil_content        decimal(10, 2),
    sugar_content      decimal(10, 2)
) engine = InnoDB;

alter table harvest
    add column crop_parameters_id varchar(36) not null;
alter table harvest
    add constraint foreign key (crop_parameters_id) references crop_parameters (id);

alter table crop_sale
    add column crop_parameters_id varchar(36) not null;
alter table crop_sale
    add constraint foreign key (crop_parameters_id) references crop_parameters (id);
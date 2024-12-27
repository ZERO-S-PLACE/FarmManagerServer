create table farming_machine
(
    id                 varchar(36)  not null primary key,
    version            integer      not null,
    created_date       timestamp    not null,
    last_modified_date timestamp    not null,
    producer           varchar(100) not null,
    model              varchar(100) not null,
    description        text,
    created_by         varchar(36)  not null
) engine = InnoDB;

create table fertilizer
(
    id                     varchar(36)   not null primary key,
    version                integer       not null,
    created_date           timestamp     not null,
    last_modified_date     timestamp     not null,
    name                   varchar(100)  not null,
    producer               varchar(100),
    is_natural_fertilizer  boolean       not null,
    organic_matter_percent decimal(5, 2) not null,
    n_percent              decimal(5, 2) not null,
    p_percent              decimal(5, 2) not null,
    k_percent              decimal(5, 2) not null,
    ca_percent             decimal(5, 2) not null,
    mg_percent             decimal(5, 2) not null,
    s_percent              decimal(5, 2) not null,
    na_percent             decimal(5, 2) not null,
    cl_percent             decimal(5, 2) not null,
    fe_percent             decimal(5, 2) not null,
    b_percent              decimal(5, 2) not null,
    created_by             varchar(36)   not null
) engine = InnoDB;

create table spray
(
    id                 varchar(36)  not null primary key,
    version            integer      not null,
    created_date       timestamp    not null,
    last_modified_date timestamp    not null,
    name               varchar(100) not null,
    producer           varchar(100),
    spray_type         varchar(100) not null,
    description        text,
    created_by         varchar(36)  not null
) engine = InnoDB;

create table species
(
    id                 varchar(36)  not null primary key,
    version            integer      not null,
    created_date       timestamp    not null,
    last_modified_date timestamp    not null,
    name               varchar(100) not null,
    family             varchar(100),
    description        text,
    created_by         varchar(36)  not null
) engine = InnoDB;

create table plant
(
    id                 varchar(36)  not null primary key,
    version            integer      not null,
    created_date       timestamp    not null,
    last_modified_date timestamp    not null,
    variety            varchar(100) not null,
    registration_date  date         not null,
    production_company varchar(100) not null,
    country_of_origin  varchar(100) not null,
    description        text         not null,
    species_id         varchar(36)  not null,
    foreign key (species_id) references species (id),
    created_by         varchar(36)  not null
) engine = InnoDB;

create table subside
(
    id                          varchar(36)  not null primary key,
    version                     integer      not null,
    created_date                timestamp    not null,
    last_modified_date          timestamp    not null,
    name                        varchar(100) not null,
    description                 text         not null,
    year_of_subside             date         not null,
    subside_value_per_area_unit decimal(10, 2),
    created_by                  varchar(36)  not null
) engine = InnoDB;

create table crop_sale
(
    id                 varchar(36)    not null primary key,
    version            integer        not null,
    created_date       timestamp      not null,
    last_modified_date timestamp      not null,
    resource_type      varchar(30)    not null,
    date_sold          date           not null,
    sold_to            varchar(100)   not null,
    amount_sold        decimal(10, 2) not null,
    price_per_unit     decimal(10, 2) not null,
    unit               varchar(100)   not null
) engine = InnoDB;

create table fertilizer_application
(
    id                        varchar(36)    not null primary key,
    version                   integer        not null,
    created_date              timestamp      not null,
    last_modified_date        timestamp      not null,
    date_started              date           not null,
    date_finished             date           not null,
    is_external_service       boolean        not null,
    external_service_price    decimal(10, 2) not null,
    fuel_consumption_per_unit decimal(10, 2) not null,
    fuel_price                decimal(10, 2) not null,
    is_planned_operation      boolean        not null,
    farming_machine_id        varchar(36)    not null,
    foreign key (farming_machine_id) references farming_machine (id),
    fertilizer_id             varchar(36)    not null,
    foreign key (fertilizer_id) references fertilizer (id),
    quantity_per_area_unit    decimal(10, 2) not null,
    price_per_unit            decimal(10, 2) not null
) engine = InnoDB;

create table cultivation
(
    id                        varchar(36)    not null primary key,
    version                   integer        not null,
    created_date              timestamp      not null,
    last_modified_date        timestamp      not null,
    date_started              date           not null,
    date_finished             date           not null,
    is_external_service       boolean        not null,
    external_service_price    decimal(10, 2) not null,
    fuel_consumption_per_unit decimal(10, 2) not null,
    fuel_price                decimal(10, 2) not null,
    is_planned_operation      boolean        not null,
    farming_machine_id        varchar(36)    not null,
    foreign key (farming_machine_id) references farming_machine (id),
    depth                     decimal(5, 2)  not null,
    cultivation_type          varchar(100)   not null
) engine = InnoDB;


create table spray_application
(
    id                                varchar(36)    not null primary key,
    version                           integer        not null,
    created_date                      timestamp      not null,
    last_modified_date                timestamp      not null,
    date_started                      date           not null,
    date_finished                     date           not null,
    is_external_service               boolean        not null,
    external_service_price            decimal(10, 2) not null,
    fuel_consumption_per_unit         decimal(10, 2) not null,
    fuel_price                        decimal(10, 2) not null,
    is_planned_operation              boolean        not null,
    farming_machine_id                varchar(36)    not null,
    foreign key (farming_machine_id) references farming_machine (id),
    spray_id                          varchar(36)    not null,
    foreign key (spray_id) references spray (id),
    fertilizer_id                     varchar(36)    not null,
    foreign key (fertilizer_id) references fertilizer (id),
    quantity_per_area_unit            decimal(10, 2) not null,
    price_per_unit                    decimal(10, 2) not null,
    fertilizer_quantity_per_area_unit decimal(10, 2) not null,
    fertilizer_price_per_unit         decimal(10, 2) not null
) engine = InnoDB;

create table harvest
(
    id                        varchar(36)    not null primary key,
    version                   integer        not null,
    created_date              timestamp      not null,
    last_modified_date        timestamp      not null,
    date_started              date           not null,
    date_finished             date           not null,
    is_external_service       boolean        not null,
    external_service_price    decimal(10, 2) not null,
    fuel_consumption_per_unit decimal(10, 2) not null,
    fuel_price                decimal(10, 2) not null,
    is_planned_operation      boolean        not null,
    farming_machine_id        varchar(36)    not null,
    foreign key (farming_machine_id) references farming_machine (id),
    resource_type             varchar(30)    not null,
    quantity_per_area_unit    decimal(10, 2) not null

) engine = InnoDB;

create table seeding
(
    id                        varchar(36)    not null primary key,
    version                   integer        not null,
    created_date              timestamp      not null,
    last_modified_date        timestamp      not null,
    date_started              date           not null,
    date_finished             date           not null,
    is_external_service       boolean        not null,
    external_service_price    decimal(10, 2) not null,
    fuel_consumption_per_unit decimal(10, 2) not null,
    fuel_price                decimal(10, 2) not null,
    is_planned_operation      boolean        not null,
    farming_machine_id        varchar(36)    not null,
    foreign key (farming_machine_id) references farming_machine (id),
    depth                     decimal(10, 2) not null,
    row_spacing               decimal(10, 2) not null,
    quantity_per_area_unit    decimal(10, 2) not null,
    germination_rate          decimal(10, 2) not null,
    material_purity           decimal(10, 2) not null,
    thousand_seeds_mass       decimal(10, 2) not null,
    seeds_per_area_unit       decimal(10, 2) not null,
    seeds_cost_per_unit       decimal(10, 2) not null
) engine = InnoDB;


create table crop
(
    id                 varchar(36) not null primary key,
    version            integer     not null,
    created_date       timestamp   not null,
    last_modified_date timestamp   not null,
    field_part_id      varchar(36) not null,
    foreign key (field_part_id) references field_part (id),
    is_main_crop       varchar(20) not null,
    work_finished      boolean     not null,
    is_fully_sold      boolean,
    date_destroyed     date

) engine = InnoDB;


alter table cultivation
    add column crop_id varchar(36) not null;
alter table cultivation
    add constraint foreign key (crop_id) references crop (id);

alter table fertilizer_application
    add column crop_id varchar(36) not null;
alter table fertilizer_application
    add constraint foreign key (crop_id) references crop (id);

alter table spray_application
    add column crop_id varchar(36) not null;
alter table spray_application
    add constraint foreign key (crop_id) references crop (id);

alter table harvest
    add column crop_id varchar(36) not null;
alter table harvest
    add constraint foreign key (crop_id) references crop (id);

alter table seeding
    add column crop_id varchar(36) not null;
alter table seeding
    add constraint foreign key (crop_id) references crop (id);

alter table crop_sale
    add column crop_id varchar(36) not null;
alter table crop_sale
    add constraint foreign key (crop_id) references crop (id);

create table crop_plant
(
    crop_id  varchar(36) not null,
    plant_id varchar(36) not null,
    primary key (crop_id, plant_id),
    foreign key (crop_id) references crop (id),
    foreign key (plant_id) references plant (id)

) engine = InnoDB;

create table crop_subside
(
    crop_id    varchar(36) not null,
    subside_id varchar(36) not null,
    primary key (crop_id, subside_id),
    foreign key (crop_id) references crop (id),
    foreign key (subside_id) references subside (id)

) engine = InnoDB;

create table subside_species
(
    species_id varchar(36) not null,
    subside_id varchar(36) not null,
    primary key (species_id, subside_id),
    foreign key (species_id) references species (id),
    foreign key (subside_id) references subside (id)

) engine = InnoDB;

create table seeding_plant
(
    seeding_id varchar(36) not null,
    plant_id   varchar(36) not null,
    primary key (seeding_id, plant_id),
    foreign key (seeding_id) references seeding (id),
    foreign key (plant_id) references plant (id)
) engine = InnoDB;
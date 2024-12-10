create table user
(
    id varchar(36) not null primary key,
    first_name varchar(20) not null,
    second_name varchar(20),
    last_name varchar(40) not null,
    email varchar(255) not null unique ,
    address varchar(400),
    city varchar(100),
    zip_code varchar(10),
    phone_number varchar(12),
    username varchar(36) not null unique,
    password varchar(255) not null,
    version integer not null ,
    created_date timestamp not null ,
    last_modified_date timestamp not null

) engine = InnoDB;

create table field
(
    id varchar(36) not null primary key,
    field_name varchar(100) not null,
    area decimal(10,2) not null ,
    surveying_plots text,
    description text,
    is_own_field boolean not null ,
    is_archived boolean not null ,
    property_tax decimal(10,2),
    rent decimal(10,2),
    version integer not null ,
    created_date timestamp not null ,
    last_modified_date timestamp not null ,
    user_id varchar(36) not null ,
    foreign key (user_id) references user (id) ON DELETE CASCADE
) engine = InnoDB;


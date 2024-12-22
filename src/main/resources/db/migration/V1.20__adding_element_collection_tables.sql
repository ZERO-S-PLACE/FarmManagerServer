create table farming_machine_supported_operation_types(
    farming_machine_id varchar(36) not null,
 foreign key (farming_machine_id) references farming_machine (id) ,
    supported_operation_types varchar(100) not null
);

   create table spray_active_substances(
                                           spray_id varchar(36) not null,
                                           foreign key (spray_id) references farm_server_db.spray (id) ,
                                           active_substances text not null

   )
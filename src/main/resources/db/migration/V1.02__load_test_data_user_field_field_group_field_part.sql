insert into user (first_name, last_name, email, username,password,
                  id,created_date,last_modified_date,version)
values ('Test','User1','00@gmail.com','TestUser1','password'
       ,UUID(),now(),now(),0 );

insert into user (first_name, last_name, email, username,password,
                  id,created_date,last_modified_date,version)
values ('Test','User2','11@gmail.com','TestUser2','password'
       ,UUID(),now(),now(),0 );
insert into field_group( field_group_name, id, created_date, last_modified_date, version,user_id)
values ('fieldGroup1U1',UUID(),now(),now(),0,(select id from user where username='TestUser1'));

insert into field_group( field_group_name, id, created_date, last_modified_date, version,user_id)
values ('fieldGroup2U1',UUID(),now(),now(),0,(select id from user where username='TestUser1'));

insert into field_group( field_group_name, id, created_date, last_modified_date, version,user_id)
values ('DEFAULT',UUID(),now(),now(),0,(select id from user where username='TestUser2'));

insert into field (field_name, area, is_own_field, is_archived,
                   id, version, created_date, last_modified_date,
                   user_id,
                   field_group_id)
values ('field1U1G1',1,true,false
           ,UUID(),0,now(),now(),
        (select id from user where username='TestUser1'),
        (select id from field_group where field_group_name='fieldGroup1U1'));

insert into field (field_name, area, is_own_field, is_archived,
                   id, version, created_date, last_modified_date,
                   user_id,
                   field_group_id)
values ('field2U1G1',1,true,false
           ,UUID(),0,now(),now(),
        (select id from user where username='TestUser1'),
        (select id from field_group where field_group_name='fieldGroup1U1'));

insert into field (field_name, area, is_own_field, is_archived,
                   id, version, created_date, last_modified_date,
                   user_id,
                   field_group_id)
values ('field3U1G2',1,true,false
           ,UUID(),0,now(),now(),
        (select id from user where username='TestUser1'),
        (select id from field_group where field_group_name='fieldGroup2U1'));

insert into field (field_name, area, is_own_field, is_archived,
                   id, version, created_date, last_modified_date,
                   user_id,
                   field_group_id)
values ('field4U1G2',1,true,false
           ,UUID(),0,now(),now(),
        (select id from user where username='TestUser1'),
        (select id from field_group where field_group_name='fieldGroup2U1'));

insert into field (field_name, area, is_own_field, is_archived,
                   id, version, created_date, last_modified_date,
                   user_id,
                   field_group_id)
values ('field1U2',1,true,false
           ,UUID(),0,now(),now(),
        (select id from user where username='TestUser2'),
        (select id from field_group where field_group_name='DEFAULT'));

insert into field (field_name, area, is_own_field, is_archived,
                   id, version, created_date, last_modified_date,
                   user_id,
                   field_group_id)
values ('field2U2',1,true,false
           ,UUID(),0,now(),now(),
        (select id from user where username='TestUser2'),
        (select id from field_group where field_group_name='DEFAULT'));

insert into field (field_name, area, is_own_field, is_archived,
                   id, version, created_date, last_modified_date,
                   user_id,
                   field_group_id)
values ('field3U2',1,true,false
           ,UUID(),0,now(),now(),
        (select id from user where username='TestUser2'),
        (select id from field_group where field_group_name='DEFAULT'));

insert into field_part( field_part_name, area, is_archived,
                        id,version, created_date, last_modified_date,
                        field_id)
values ('WHOLE',(select area from field where field_name='field1U1G1'),false
           ,UUID(),0,now(),now(),
        (select id from field where field_name='field1U1G1'));

insert into field_part( field_part_name, area, is_archived,
                        id,version, created_date, last_modified_date,
                        field_id)
values ('WHOLE',(select area from field where field_name='field2U1G1'),false
           ,UUID(),0,now(),now(),
        (select id from field where field_name='field2U1G1'));

insert into field_part( field_part_name, area, is_archived,
                        id,version, created_date, last_modified_date,
                        field_id)
values ('WHOLE',(select area from field where field_name='field3U1G2'),false
           ,UUID(),0,now(),now(),
        (select id from field where field_name='field3U1G2'));

insert into field_part( field_part_name, area, is_archived,
                        id,version, created_date, last_modified_date,
                        field_id)
values ('WHOLE',(select area from field where field_name='field4U1G2'),false
           ,UUID(),0,now(),now(),
        (select id from field where field_name='field4U1G2'));

insert into field_part( field_part_name, area, is_archived,
                        id,version, created_date, last_modified_date,
                        field_id)
values ('WHOLE',(select area from field where field_name='field1U2'),false
           ,UUID(),0,now(),now(),
        (select id from field where field_name='field1U2'));

insert into field_part( field_part_name, area, is_archived,
                        id,version, created_date, last_modified_date,
                        field_id)
values ('WHOLE',(select area from field where field_name='field2U2'),false
           ,UUID(),0,now(),now(),
        (select id from field where field_name='field2U2'));

insert into field_part( field_part_name, area, is_archived,
                        id,version, created_date, last_modified_date,
                        field_id)
values ('WHOLE',(select area from field where field_name='field3U2'),false
           ,UUID(),0,now(),now(),
        (select id from field where field_name='field3U2'));

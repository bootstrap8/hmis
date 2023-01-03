insert into hbq_dic_info(field_name,field_desc,enum_type) values('foo','测试枚举1','enum');
insert into hbq_dic_ext_kv(field_name,enum_key,enum_value) values('foo','1','一');
insert into hbq_dic_ext_kv(field_name,enum_key,enum_value) values('foo','2','二');
insert into hbq_dic_ext_kv(field_name,enum_key,enum_value) values('foo','3','三');
insert into hbq_dic_ext_kv(field_name,enum_key,enum_value) values('foo','4','四');

insert into hbq_dic_info(field_name,field_desc,enum_type) values('bar','测试枚举2','sql');
insert into hbq_dic_ext_sql(field_name,enum_sql) values('bar','select id AS "key",name AS "value" from hbq_bar order by name');

insert into hbq_bar(id,name) values(15,'a');
insert into hbq_bar(id,name) values(16,'b');
insert into hbq_bar(id,name) values(101,'c');
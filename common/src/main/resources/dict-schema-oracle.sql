create table hbq_dic_info
(
    field_name varchar2(200) primary key,
    field_desc varchar2(500),
    enum_type  varchar2(100) default 'enum'
);

create table hbq_dic_ext_kv
(
    field_name varchar2(200),
    enum_key   varchar2(500),
    enum_value varchar2(500)
);

create table hbq_dic_ext_sql
(
    field_name varchar2(200),
    enum_sql   varchar2(500)
);

create table hbq_bar
(
    id   number(10),
    name varchar2(500)
);
create table `hbq_dic_info`
(
    `field_name` varchar(200) primary key,
    `field_desc` varchar(500),
    `enum_type` varchar(100) default 'enum'
);

create table `hbq_dic_ext_kv`
(
    `field_name` varchar(200),
    `enum_key`   varchar(500),
    `enum_value` varchar(500)
);

create table `hbq_dic_ext_sql`
(
    `field_name` varchar(200),
    `enum_sql`   varchar(500)
);

create table `hbq_bar`
(
    `id`   numeric(10),
    `name` varchar(500)
);
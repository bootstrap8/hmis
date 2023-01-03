create table `hbq_route_config`(
    `route_id` varchar(100) primary key,
    `uri` varchar(500),
    `predicates` varchar(1000),
    `filters` varchar(1000),
    `route_order` numeric(10)
);
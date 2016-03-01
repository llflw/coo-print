drop table if exists p_user;
create table p_user
(
  client_id character varying(8) not null,
  user_id character varying(32),
  user_name character varying(32),
  passwd character varying(32),
  email character varying(64),
  tel character varying(11),
  company character varying(64),
  other character varying(64),
  register_dt timestamp without time zone,
  first_order_dt timestamp without time zone,
  constraint p_user_pk primary key (client_id)
);

drop table if exists p_address;
create table p_address
(
  user_id character varying(10) not null,
  address_id integer not null,
  addressee character varying(32) not null,
  company character varying(64),
  province character varying(10) not null,
  city character varying(10) not null,
  district character varying(10) not null,
  detail_address character varying(100) not null,
  tel character varying(11) not null,
  email character varying(64) not null,
  constraint p_address_pk primary key (user_id, address_id)
);


drop table if exists p_order;
create table p_order
(
  order_id character varying(10) not null,
  order_dt timestamp without time zone not null,
  user_id character varying(10) not null,
  address_id integer not null,
  order_price numeric not null,
  order_status integer not null default 0,
  user_memo text,
  oper_memo text,
  constraint order_pk primary key (order_id)
);

drop table if exists p_order_detail;
create table p_order_detail
(
  order_id character varying(10) not null,
  file_name character varying(64) not null,
  print_cnt integer not null default 1,
  metaril_id integer not null,
  color character varying(10),
  finish character varying(10),
  layer character varying(10),
  fill character varying(10),
  zoom character varying(10),
  price numeric not null,
  constraint order_detail_pk primary key (order_id, file_name)
);


drop table if exists m_properties;
create table m_properties
(
 prop_type character varying(10) not null,
 prop_name character varying(32) not null,
 prop_price numeric not null,
 constraint m_properties_pk primary key (prop_type, prop_name)
);
insert into m_properties values('material',  'PLA塑料', 15);
insert into m_properties values('material',  '光敏树脂', 15);
insert into m_properties values('material',  '彩色/柔性光敏树脂', 15);
insert into m_properties values('material',  '尼龙', 15);
insert into m_properties values('material',  '尼龙+玻纤', 15);
insert into m_properties values('material',  '砂岩', 15);
insert into m_properties values('material',  '不锈钢', 15);
insert into m_properties values('color', '原色', 11);
insert into m_properties values('color', '蓝色', 11);
insert into m_properties values('color', '红色', 11);
insert into m_properties values('color', '白色', 11);
insert into m_properties values('color', '透明', 11);
insert into m_properties values('color', '彩色/柔性', 11);
insert into m_properties values('color', '淡黄色', 11);
insert into m_properties values('color', '全彩', 11);
insert into m_properties values('finish',  '无', 10);
insert into m_properties values('finish',  '喷砂', 10);
insert into m_properties values('finish',  '打磨', 10);
insert into m_properties values('finish',  '上色', 10);
insert into m_properties values('finish',  '喷镀', 10);
insert into m_properties values('finish',  '抛光', 10);
insert into m_properties values('finish',  '电镀', 10);
insert into m_properties values('layer',  '0.02', 1);
insert into m_properties values('layer',  '0.025', 2);
insert into m_properties values('layer',  '0.05', 3);
insert into m_properties values('layer',  '0.1', 3);
insert into m_properties values('layer',  '0.2', 3);
insert into m_properties values('layer',  '0.3', 3);
insert into m_properties values('fill',  '15', 1);
insert into m_properties values('fill',  '20', 2);
insert into m_properties values('fill',  '30', 3);
insert into m_properties values('fill',  '100', 4);
insert into m_properties values('zoom',  '50', 1);
insert into m_properties values('zoom',  '100', 2);
insert into m_properties values('zoom',  '200', 3);




drop table if exists m_material;
create table m_material
(
 material_id integer not null,
 material_name character varying(32) not null,
 tech character varying(32) not null,
 v_color character varying(32) not null,
 v_finish character varying(32) not null,
 v_layer character varying(32) not null,
 v_fill character varying(32) not null,
 v_zoom character varying(32) not null, 
 constraint m_material_pk primary key (material_id)
);
insert into m_material values(1, 'PLA塑料', 'FDM', '原色,蓝色,红色,白色','无,上色', '0.3,0.2,','15,20,30','100,50,200');
insert into m_material values(2, '光敏树脂', 'SLA', '白色,透明','喷砂,无,打磨,上色,电镀', '0.1,0.05,0.025','100','100,50,200');
insert into m_material values(3, '彩色/柔性光敏树脂', 'Polyjet', '彩色/柔性','喷砂,无,打磨,上色,电镀', '0.05,0.025','100','100,50,200');
insert into m_material values(4, '尼龙', 'SLS', '白色','喷砂,无,打磨,上色','0.1','100','100,50,200');
insert into m_material values(5, '尼龙+玻纤', 'SLS', '淡黄色','喷砂,无,打磨,上色', '0.1','100','100,50,200');
insert into m_material values(6, '砂岩', '3DP', '全彩','无,打磨', '0.1','100','100,50,200');
insert into m_material values(7, '不锈钢', 'SLM','-','喷砂,无,打磨,抛光,电镀','0.1,0.02','100','100,50,200');


drop table if exists m_max_available;
create table m_max_available
(
  max_id integer not null,
  material_name character varying(32) not null,
  max_x integer not null,
  max_y integer not null,
  max_z integer not null,
  constraint m_max_availabe_pk primary key (max_id)
);

insert into m_max_available values(1, 'PLA塑料',230, 150, 140);
insert into m_max_available values(2, '光敏树脂',800, 600, 400);
insert into m_max_available values(3, '光敏树脂',298, 203, 185);
insert into m_max_available values(4, '彩色/柔性光敏树脂',250, 250, 200);
insert into m_max_available values(5, '尼龙',700, 580, 380);
insert into m_max_available values(6, '尼龙+玻纤',700, 580, 380);
insert into m_max_available values(7, '砂岩',381, 254, 203);
insert into m_max_available values(8, '不锈钢',280, 250, 250);


drop sequence if exists seq_client_id;
CREATE SEQUENCE seq_client_id
   INCREMENT 1
   START 1
   MINVALUE 1
   MAXVALUE 99999;


commit;



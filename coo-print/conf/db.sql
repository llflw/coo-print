drop table if exists p_user;
create table p_user
(
  client_id character varying(10) not null,
  user_id character varying(10),
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


drop table if exists m_material;
create table m_material
(
 material_id integer not null,
 material_name character varying(32) not null,
 tech character varying(32) not null,
 max_able_1 integer not null,
 max_able_2 integer not null,
 max_able_3 integer not null,
 constraint m_material_pk primary key (material_id)
);
insert into m_material values(1, 'PLA塑料', 'FDM', 230, 150, 140);
insert into m_material values(2, '光敏树脂', 'SLA', 800, 600, 400);
insert into m_material values(3, '彩色/柔性光敏树脂', 'Polyjet', 250, 250, 200);
insert into m_material values(4, '尼龙', 'SLS', 700, 580, 380);
insert into m_material values(5, '尼龙+玻纤', 'SLS', 700, 580, 380);
insert into m_material values(6, '砂岩', '3DP', 381, 254, 203);
insert into m_material values(7, '不锈钢', 'SLM', 280, 250, 250);


drop table if exists m_color;
create table m_color
(
 color_id integer not null,
 color_name character varying(32) not null,
 constraint m_color_pk primary key (color_id)
);
insert into m_color values(1,'原色');
insert into m_color values(2,'蓝色');
insert into m_color values(3,'红色');
insert into m_color values(4,'白色');
insert into m_color values(5,'透明');
insert into m_color values(6,'彩色/柔性');
insert into m_color values(7,'淡黄色');
insert into m_color values(8,'全彩');



drop table if exists m_finish;
create table m_finish
(
 finish_id integer not null,
 finish_name character varying(32) not null,
 constraint m_finish_pk primary key (finish_id)
);

insert into m_finish values(1, '无');
insert into m_finish values(2, '喷砂');
insert into m_finish values(3, '打磨');
insert into m_finish values(4, '上色');
insert into m_finish values(5, '喷镀');
insert into m_finish values(6, '抛光');
insert into m_finish values(7, '电镀');


drop table if exists m_layer;
create table m_layer
(
 layer_id integer not null,
 layer_name character varying(32) not null,
 constraint m_layer_pk primary key (layer_id)
);

insert into m_layer values(1, '0.02 mm');
insert into m_layer values(2, '0.025 mm');
insert into m_layer values(3, '0.05 mm');
insert into m_layer values(4, '0.1 mm');
insert into m_layer values(5, '0.2 mm');
insert into m_layer values(6, '0.3 mm');


drop table if exists m_fill;
create table m_fill
(
 fill_id integer not null,
 fill_name character varying(32) not null,
 constraint m_fill_pk primary key (fill_id)
);

insert into m_fill values(1, '15%');
insert into m_fill values(2, '20%');
insert into m_fill values(3, '30%');
insert into m_fill values(4, '100%');

drop table if exists m_zoom;
create table m_zoom
(
 zoom_id integer not null,
 zoom_name character varying(32) not null,
 constraint m_zoom_pk primary key (zoom_id)
);


insert into m_zoom values(1, '50%');
insert into m_zoom values(2, '100%');
insert into m_zoom values(3, '200%');


commit;



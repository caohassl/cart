CREATE TABLE bussiness_cart
(
  cart_id INT(11) unsigned PRIMARY KEY NOT NULL AUTO_INCREMENT,
  USER_ID VARCHAR(100)  NOT NULL COMMENT '用户id',
  OFFICE_ID VARCHAR(100),
  bussiness_type VARCHAR(10) NOT NULL COMMENT '收藏类型',
  bussiness_id VARCHAR(50) NOT NULL COMMENT '案例，法规，司法观点ID',
  bussiness_status VARCHAR(4) COMMENT '0-收藏，1一检索，2-删除',
  bussiness_title VARCHAR(100),
  bussiness_content VARCHAR(500),
  bussiness_basic_info VARCHAR(3000) COMMENT '收藏文件基本信息',
  score INT(11),
  cst_create_time DATETIME NOT NULL,
  cst_update_time DATETIME NOT NULL
);
CREATE INDEX IDX_CART_STATUS ON web_cart (USER_ID , OFFICE_ID, bussiness_status, );


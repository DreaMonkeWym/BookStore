
#创建bookstore数据库
create table bookstore;

#创建管理员表
CREATE TABLE `admin` (
	`adminname` VARCHAR(20) NOT NULL,
	`adminpassword` VARCHAR(100) NOT NULL,
	`avatar` VARCHAR(100) NULL DEFAULT NULL,
	PRIMARY KEY (`adminname`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

#创建书籍详情表
CREATE TABLE `bookdetail` (
	`bookid` VARCHAR(50) NOT NULL,
	`typeid` VARCHAR(20) NOT NULL,
	`avatar` VARCHAR(100) NULL DEFAULT NULL,
	`descri` VARCHAR(10000) NOT NULL,
	`isbn` VARCHAR(20) NOT NULL,
	`price` VARCHAR(20) NOT NULL,
	`quantity` VARCHAR(100) NOT NULL,
	`publicationtime` VARCHAR(20) NOT NULL,
	`author` VARCHAR(50) NOT NULL,
	`soldout` VARCHAR(1000) NULL DEFAULT NULL,
	`glance` VARCHAR(1000) NULL DEFAULT NULL,
	`bookname` VARCHAR(100) NOT NULL,
	PRIMARY KEY (`bookid`),
	INDEX `bookdetail_typeid` (`typeid`),
	INDEX `index_isbn` (`isbn`),
	INDEX `index_bookname` (`bookname`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

#创建书籍类别表
CREATE TABLE `booktype` (
	`typeid` VARCHAR(20) NOT NULL,
	`typename` VARCHAR(50) NOT NULL,
	PRIMARY KEY (`typeid`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

#创建购物车表
CREATE TABLE `cart` (
	`cartid` VARCHAR(50) NOT NULL,
	`username` VARCHAR(20) NOT NULL,
	`bookid` VARCHAR(20) NOT NULL,
	`quantity` VARCHAR(500) NOT NULL,
	`payment` TINYINT(1) NOT NULL,
	PRIMARY KEY (`cartid`),
	INDEX `index_username` (`username`),
	INDEX `index_bookid` (`bookid`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

#创建评论表
CREATE TABLE `comment` (
	`commentid` VARCHAR(50) NOT NULL,
	`bookid` VARCHAR(20) NOT NULL,
	`content` VARCHAR(5000) NOT NULL,
	`username` VARCHAR(20) NOT NULL,
	PRIMARY KEY (`commentid`),
	INDEX `comment_bookid` (`bookid`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

#创建订单表
CREATE TABLE `orders` (
	`orderid` VARCHAR(20) NOT NULL,
	`username` VARCHAR(20) NOT NULL,
	`cartid` VARCHAR(20) NOT NULL,
	PRIMARY KEY (`orderid`, `cartid`),
	INDEX `orders_bookid` (`username`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

#创建上次浏览表
CREATE TABLE `recentview` (
	`username` VARCHAR(50) NOT NULL,
	`typeid` VARCHAR(20) NOT NULL,
	PRIMARY KEY (`username`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

#创建用户表
CREATE TABLE `user` (
	`username` VARCHAR(20) NOT NULL,
	`password` VARCHAR(100) NOT NULL,
	`favor` VARCHAR(10) NULL DEFAULT NULL,
	`avatar` VARCHAR(100) NOT NULL,
	PRIMARY KEY (`username`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB
;

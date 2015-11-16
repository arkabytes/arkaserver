CREATE TABLE users (
    id int unsigned primary key auto_increment,
    username varchar(50) NOT NULL,
    password varchar(255) NOT NULL,
    primary_email varchar(255) NOT NULL,
    secondary_email varchar(255) DEFAULT NULL,
    name varchar(255) DEFAULT NULL,
    phone varchar(255) DEFAULT NULL,
    icon varchar(255) DEFAULT NULL,
    web varchar(255) DEFAULT NULL
) ENGINE=InnoDB;

CREATE TABLE virtual_domains (
    id int unsigned primary key auto_increment,
    name varchar(50) NOT NULL,
    user_id int unsigned
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `virtual_users` (
    id int unsigned primary key auto_increment,
    domain_id int(11) NOT NULL,
    email varchar(50) NOT NULL,
    password varchar(50) NOT NULL
) ENGINE=InnoDB;

# MYSQL

nano /etc/mysql/mysql.conf.d/mysqld.cnf

bind-address            = 0.0.0.0

/etc/init.d/mysql restart
 

mysql -uroot

```sql

CREATE USER 'team'@'%' IDENTIFIED BY 'team1234';

GRANT ALL PRIVILEGES ON *.* TO 'team'@'%' WITH GRANT OPTION;

CREATE DATABASE krish;

USE krish;

create table products (id int, name varchar(255), price int, create_ts timestamp DEFAULT CURRENT_TIMESTAMP , update_ts timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP );


create table users (registertime int, userid varchar(255), regionid varchar(255), gender varchar(20), create_ts timestamp DEFAULT CURRENT_TIMESTAMP , update_ts timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP );

 


 insert into products (id, name, price) values(1,'moto phone', 1000);

select * from products;

 update products set price=2200 where id=1;
 
select * from products;

```




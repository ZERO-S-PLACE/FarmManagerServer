DROP DATABASE IF EXISTS farm_server_db;
DROP USER IF EXISTS `farm_server_admin`@`%`;
DROP USER IF EXISTS `farm_server_user`@`%`;
CREATE DATABASE IF NOT EXISTS farm_server_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS `farm_server_admin`@`%` IDENTIFIED WITH mysql_native_password BY 'password';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, REFERENCES, INDEX, ALTER, EXECUTE, CREATE VIEW, SHOW VIEW, CREATE ROUTINE, ALTER ROUTINE, EVENT, TRIGGER ON `farm_server_db`.* TO `farm_server_admin`@`%`;
CREATE USER IF NOT EXISTS `farm_server_user`@`%` IDENTIFIED WITH mysql_native_password BY 'password';
GRANT SELECT, INSERT, UPDATE, DELETE, SHOW VIEW ON `farm_server_db`.* TO `farm_server_user`@`%`;
FLUSH PRIVILEGES;
/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

CREATE DATABASE trainerinvoice;
USE trainerinvoice;

CREATE TABLE IF NOT EXISTS `adress` (
  `USER_ID` int(11) NOT NULL,
  `ADRESS_TYPE` varchar(31) NOT NULL,
  `ADRESS1` varchar(255) DEFAULT NULL,
  `ADRESS2` varchar(255) DEFAULT NULL,
  `ZIP` varchar(45) DEFAULT NULL,
  `CITY` varchar(155) DEFAULT NULL,
  `UPDATED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `CREATED` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`USER_ID`),
  CONSTRAINT `one2one_adress_user` FOREIGN KEY (`USER_ID`) REFERENCES `USERDATA` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `adress` DISABLE KEYS */;
/*!40000 ALTER TABLE `adress` ENABLE KEYS */;

CREATE TABLE IF NOT EXISTS `article` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `price` double NOT NULL,
  `title` varchar(50) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `report_ressource` varchar(45) NOT NULL DEFAULT '/reports/mtv_gross_buchholz.jrxml',
  `UPDATED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `CREATED` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `fk_artivle_user` (`user_id`),
  CONSTRAINT `fk_artivle_user` FOREIGN KEY (`user_id`) REFERENCES `USERDATA` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `article` DISABLE KEYS */;
/*!40000 ALTER TABLE `article` ENABLE KEYS */;

CREATE TABLE IF NOT EXISTS `banking_connection` (
  `USER_ID` int(11) NOT NULL,
  `OWNER_TYPE` varchar(31) NOT NULL,
  `bank_name` varchar(255) DEFAULT NULL,
  `BIC` varchar(255) DEFAULT NULL,
  `IBAN` varchar(255) DEFAULT NULL,
  `UPDATED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `CREATED` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`USER_ID`),
  UNIQUE KEY `banking_connection_UNIQUE` (`USER_ID`,`OWNER_TYPE`,`IBAN`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `banking_connection` DISABLE KEYS */;
/*!40000 ALTER TABLE `banking_connection` ENABLE KEYS */;

CREATE TABLE IF NOT EXISTS `invoice` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `invoicedate` datetime NOT NULL,
  `invoiceid` varchar(150) NOT NULL,
  `user_id` int(11) NOT NULL,
  `signImagePath` varchar(255) DEFAULT NULL,
  `UPDATED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `CREATED` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `fk_invoice_1_idx` (`user_id`),
  CONSTRAINT `fk_invoice_1` FOREIGN KEY (`user_id`) REFERENCES `USERDATA` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `invoice` DISABLE KEYS */;
/*!40000 ALTER TABLE `invoice` ENABLE KEYS */;

CREATE TABLE IF NOT EXISTS `invoice_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `start_time` datetime NOT NULL,
  `end_time` varchar(45) NOT NULL,
  `article_id` int(11) NOT NULL,
  `participants` varchar(15) DEFAULT NULL,
  `sum_price` decimal(7,2) NOT NULL,
  `rechnung_id` int(11) DEFAULT NULL,
  `invoice_id` int(11) DEFAULT NULL,
  `UPDATED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `CREATED` timestamp NOT NULL DEFAULT current_timestamp(),
  `title` varchar(100) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `pricePerHour` decimal(7,2) NOT NULL,
  `report` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_invoiceitem_article` (`article_id`),
  CONSTRAINT `fk_invoiceitem_article` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=318 DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `invoice_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `invoice_item` ENABLE KEYS */;

CREATE TABLE IF NOT EXISTS `USERDATA` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `login` varchar(45) NOT NULL,
  `prename` varchar(45) NOT NULL,
  `surname` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `principal_id` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `family_name` varchar(255) DEFAULT NULL,
  `given_name` varchar(255) DEFAULT NULL,
  `UPDATED` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `CREATED` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `login_UNIQUE` (`login`),
  UNIQUE KEY `principalIdUnique` (`principal_id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `USERDATA` DISABLE KEYS */;
/*!40000 ALTER TABLE `USERDATA` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;

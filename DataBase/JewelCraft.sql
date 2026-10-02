-- MySQL dump 10.13  Distrib 9.7.0, for Win64 (x86_64)
--
-- Host: localhost    Database: JewelCraft
-- ------------------------------------------------------
-- Server version	9.7.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `JewelCraft`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `JewelCraft` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `JewelCraft`;

--
-- Table structure for table `addresses`
--

DROP TABLE IF EXISTS `addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `addresses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `city` varchar(60) NOT NULL,
  `country` varchar(60) NOT NULL,
  `default_address` bit(1) NOT NULL,
  `full_name` varchar(100) NOT NULL,
  `line1` varchar(255) NOT NULL,
  `line2` varchar(255) DEFAULT NULL,
  `phone` varchar(15) NOT NULL,
  `pincode` varchar(10) NOT NULL,
  `state` varchar(60) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1fa36y2oqhao3wgg2rw1pi459` (`user_id`),
  CONSTRAINT `FK1fa36y2oqhao3wgg2rw1pi459` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `addresses`
--

LOCK TABLES `addresses` WRITE;
/*!40000 ALTER TABLE `addresses` DISABLE KEYS */;
INSERT INTO `addresses` VALUES (1,'Bengaluru','India',_binary '','Priya Sharma','12 MG Road',NULL,'9876500005','560001','Karnataka',5),(2,'mumbai','India',_binary '','anil nayak','w2e3r4etry','etrywre','9969079783','400093','Maharashtra',7);
/*!40000 ALTER TABLE `addresses` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK9emlp6m95v5er2bcqkjsw48he` (`user_id`),
  CONSTRAINT `FKg5uhi8vpsuy0lgloxk2h4w5o6` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (1,5),(2,7);
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cart_items`
--

DROP TABLE IF EXISTS `cart_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `quantity` int NOT NULL,
  `cart_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6oue0maw421roerltnxn16a38` (`cart_id`,`product_id`),
  KEY `FK1re40cjegsfvw58xrkdp6bac6` (`product_id`),
  CONSTRAINT `FK1re40cjegsfvw58xrkdp6bac6` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`),
  CONSTRAINT `FK99e0am9jpriwxcm6is7xfedy3` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cart_items`
--

LOCK TABLES `cart_items` WRITE;
/*!40000 ALTER TABLE `cart_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `cart_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `name` varchar(80) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKt8o6pivur7nn124jehx7cygw5` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES (1,_binary '','2026-10-01 21:31:09.170021','Engagement, cocktail and everyday rings',NULL,'Rings'),(2,_binary '','2026-10-01 21:31:09.171404','Statement necklaces and delicate layers',NULL,'Necklaces'),(3,_binary '','2026-10-01 21:31:09.172406','Studs, drops, jhumkas and hoops',NULL,'Earrings'),(4,_binary '','2026-10-01 21:31:09.173497','Chain, tennis and charm bracelets',NULL,'Bracelets'),(5,_binary '','2026-10-01 21:31:09.173497','Traditional and contemporary bangles',NULL,'Bangles'),(6,_binary '','2026-10-01 21:31:09.174665','Pendants for every occasion',NULL,'Pendants'),(7,_binary '','2026-10-01 21:31:09.174665','Gold, silver and platinum chains',NULL,'Chains'),(8,_binary '','2026-10-01 21:31:09.175802','Bridal sets and wedding essentials',NULL,'Wedding Jewellery'),(9,_binary '','2026-10-01 21:31:09.175802','Certified diamond pieces',NULL,'Diamond Jewellery'),(10,_binary '','2026-10-01 21:31:09.176932','Hallmarked gold jewellery',NULL,'Gold Jewellery'),(11,_binary '','2026-10-01 21:31:09.177976','Sterling silver designs',NULL,'Silver Jewellery');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_profiles`
--

DROP TABLE IF EXISTS `customer_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_profiles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `anniversary_date` date DEFAULT NULL,
  `date_of_birth` date DEFAULT NULL,
  `gender` enum('FEMALE','MALE','OTHER','PREFER_NOT_TO_SAY') DEFAULT NULL,
  `marketing_opt_in` bit(1) NOT NULL,
  `preferred_metal` enum('GOLD','PLATINUM','ROSE_GOLD','SILVER','WHITE_GOLD') DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKcw536j6opmbegf01k4hykv2vs` (`user_id`),
  CONSTRAINT `FK69orkdj1un5rh845ngvvmd1xs` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_profiles`
--

LOCK TABLES `customer_profiles` WRITE;
/*!40000 ALTER TABLE `customer_profiles` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `customer_requests`
--

DROP TABLE IF EXISTS `customer_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `customer_requests` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `message` text NOT NULL,
  `response` text,
  `status` enum('CLOSED','IN_PROGRESS','OPEN','RESOLVED') NOT NULL,
  `subject` varchar(150) NOT NULL,
  `type` enum('COMPLAINT','CUSTOMIZATION','EXCHANGE','QUERY','RETURN') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `handled_by` bigint DEFAULT NULL,
  `order_id` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKeoi44io6lbvql6isksp2mioe5` (`handled_by`),
  KEY `FK9lws84dkiavdhwjoew12nsgq7` (`order_id`),
  KEY `FKqe0imu5govd3weqx6p6wo1uou` (`user_id`),
  CONSTRAINT `FK9lws84dkiavdhwjoew12nsgq7` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FKeoi44io6lbvql6isksp2mioe5` FOREIGN KEY (`handled_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKqe0imu5govd3weqx6p6wo1uou` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `customer_requests`
--

LOCK TABLES `customer_requests` WRITE;
/*!40000 ALTER TABLE `customer_requests` DISABLE KEYS */;
/*!40000 ALTER TABLE `customer_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `deliveries`
--

DROP TABLE IF EXISTS `deliveries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deliveries` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `accepted_at` datetime(6) DEFAULT NULL,
  `assigned_at` datetime(6) DEFAULT NULL,
  `delivered_at` datetime(6) DEFAULT NULL,
  `notes` varchar(500) DEFAULT NULL,
  `picked_up_at` datetime(6) DEFAULT NULL,
  `status` enum('ACCEPTED','ASSIGNED','DELIVERED','FAILED','OUT_FOR_DELIVERY','PICKED_UP') NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `delivery_partner_id` bigint NOT NULL,
  `order_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKk36n9p5v7dd96hpgkwybvbogt` (`order_id`),
  KEY `FK22vo448rq4qmka73j9ml2i7vw` (`delivery_partner_id`),
  CONSTRAINT `FK22vo448rq4qmka73j9ml2i7vw` FOREIGN KEY (`delivery_partner_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK7isx0rnbgqr1dcofd5putl6jw` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deliveries`
--

LOCK TABLES `deliveries` WRITE;
/*!40000 ALTER TABLE `deliveries` DISABLE KEYS */;
INSERT INTO `deliveries` VALUES (1,'2026-10-01 21:31:48.736987','2026-10-01 21:31:48.585471','2026-10-01 21:31:48.922295',NULL,'2026-10-01 21:31:48.813711','DELIVERED','2026-10-01 21:31:48.924670',4,1),(2,'2026-10-01 22:05:08.241762','2026-10-01 22:05:08.065134','2026-10-01 22:05:08.302246',NULL,'2026-10-01 22:05:08.267913','DELIVERED','2026-10-01 22:05:08.308489',4,2),(3,'2026-10-01 22:40:27.826889','2026-10-01 22:39:59.436646','2026-10-01 22:40:55.363108',NULL,'2026-10-01 22:40:38.863006','DELIVERED','2026-10-01 22:40:55.364298',4,3),(4,'2026-10-01 23:30:56.307676','2026-10-01 23:30:56.126298','2026-10-01 23:30:56.421036',NULL,'2026-10-01 23:30:56.350932','DELIVERED','2026-10-01 23:30:56.429371',4,5);
/*!40000 ALTER TABLE `deliveries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `delivery_partner_profiles`
--

DROP TABLE IF EXISTS `delivery_partner_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `delivery_partner_profiles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `driving_license_number` varchar(20) DEFAULT NULL,
  `emergency_contact_name` varchar(100) DEFAULT NULL,
  `emergency_contact_phone` varchar(15) DEFAULT NULL,
  `service_city` varchar(60) NOT NULL,
  `service_pincode` varchar(6) NOT NULL,
  `vehicle_number` varchar(15) DEFAULT NULL,
  `vehicle_type` enum('BICYCLE','BIKE','CAR','SCOOTER','VAN') NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKbdjw0t4aje9y6xkknx5njvjpb` (`user_id`),
  CONSTRAINT `FKiibfeh8ub1gt7k2p18wc2qam8` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `delivery_partner_profiles`
--

LOCK TABLES `delivery_partner_profiles` WRITE;
/*!40000 ALTER TABLE `delivery_partner_profiles` DISABLE KEYS */;
INSERT INTO `delivery_partner_profiles` VALUES (1,'KA0120190012345',NULL,NULL,'Bengaluru','560001','KA01AB1234','BIKE',4);
/*!40000 ALTER TABLE `delivery_partner_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `email_otps`
--

DROP TABLE IF EXISTS `email_otps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `email_otps` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `attempts` int NOT NULL,
  `code_hash` varchar(255) NOT NULL,
  `consumed` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(120) NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `purpose` enum('EMAIL_VERIFICATION') NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_email_otps_email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `email_otps`
--

LOCK TABLES `email_otps` WRITE;
/*!40000 ALTER TABLE `email_otps` DISABLE KEYS */;
/*!40000 ALTER TABLE `email_otps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `inventory`
--

DROP TABLE IF EXISTS `inventory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `inventory` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `low_stock_threshold` int NOT NULL,
  `quantity` int NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `version` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKce3rbi3bfstbvvyne34c1dvyv` (`product_id`),
  CONSTRAINT `FKq2yge7ebtfuvwufr6lwfwqy9l` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `inventory`
--

LOCK TABLES `inventory` WRITE;
/*!40000 ALTER TABLE `inventory` DISABLE KEYS */;
INSERT INTO `inventory` VALUES (1,3,5,'2026-10-01 23:30:55.623471',1,1),(2,3,10,'2026-10-01 21:31:20.705146',1,2),(3,3,9,'2026-10-01 21:31:09.225607',0,3),(4,3,4,'2026-10-01 21:31:09.231622',0,4),(5,3,3,'2026-10-01 21:31:09.239640',0,5),(6,3,15,'2026-10-01 21:31:09.247133',0,6),(7,3,8,'2026-10-01 21:31:09.253268',0,7),(8,3,10,'2026-10-01 21:31:09.259827',0,8),(9,3,2,'2026-10-01 21:31:09.266690',0,9),(10,3,5,'2026-10-01 22:47:40.079271',1,10),(11,3,18,'2026-10-01 22:05:07.580886',1,11),(12,3,25,'2026-10-01 21:31:09.287108',0,12),(13,3,9,'2026-10-01 21:31:09.292314',0,13),(14,3,5,'2026-10-01 21:31:09.298028',0,14),(15,3,2,'2026-10-01 21:31:09.305457',0,15),(16,3,3,'2026-10-01 22:31:38.320506',1,16);
/*!40000 ALTER TABLE `inventory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `metal_rates`
--

DROP TABLE IF EXISTS `metal_rates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `metal_rates` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `metal_type` enum('GOLD','PLATINUM','ROSE_GOLD','SILVER','WHITE_GOLD') NOT NULL,
  `price_per_gram` decimal(12,2) NOT NULL,
  `purity` varchar(20) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKc37ty5haul3wpxcp8yp4b4ctw` (`metal_type`,`purity`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `metal_rates`
--

LOCK TABLES `metal_rates` WRITE;
/*!40000 ALTER TABLE `metal_rates` DISABLE KEYS */;
INSERT INTO `metal_rates` VALUES (1,'GOLD',7800.00,'24K','2026-10-01 21:31:09.162216'),(2,'GOLD',7300.00,'22K','2026-10-01 21:31:49.663066'),(3,'GOLD',5850.00,'18K','2026-10-01 21:31:09.164690'),(4,'ROSE_GOLD',5850.00,'18K','2026-10-01 21:31:09.164690'),(5,'WHITE_GOLD',5950.00,'18K','2026-10-01 21:31:09.165828'),(6,'SILVER',95.00,'999','2026-10-01 21:31:09.165828'),(7,'SILVER',88.00,'925','2026-10-01 21:31:09.166830'),(8,'PLATINUM',3200.00,'950','2026-10-01 21:31:09.167833');
/*!40000 ALTER TABLE `metal_rates` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `offers`
--

DROP TABLE IF EXISTS `offers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `offers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `code` varchar(30) NOT NULL,
  `description` varchar(500) DEFAULT NULL,
  `discount_percent` decimal(5,2) NOT NULL,
  `end_date` date NOT NULL,
  `max_discount_amount` decimal(12,2) DEFAULT NULL,
  `min_order_amount` decimal(12,2) NOT NULL,
  `start_date` date NOT NULL,
  `title` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK4l5ela8l289hpo39s7f3fe7k4` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `offers`
--

LOCK TABLES `offers` WRITE;
/*!40000 ALTER TABLE `offers` DISABLE KEYS */;
INSERT INTO `offers` VALUES (1,_binary '','WELCOME10','Up to Rs. 2,000 off on orders above Rs. 10,000',10.00,'2027-10-01',2000.00,10000.00,'2026-09-30','10% off your first order'),(2,_binary '','FESTIVE5','5% off on all jewellery, no minimum',5.00,'2027-01-01',NULL,0.00,'2026-09-30','Festive 5% off');
/*!40000 ALTER TABLE `offers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `line_total` decimal(12,2) NOT NULL,
  `metal_details` varchar(60) DEFAULT NULL,
  `product_image` varchar(500) DEFAULT NULL,
  `product_name` varchar(150) NOT NULL,
  `quantity` int NOT NULL,
  `tax_rate` decimal(5,2) NOT NULL,
  `unit_price` decimal(12,2) NOT NULL,
  `order_id` bigint NOT NULL,
  `product_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKbioxgbv59vetrxe0ejfubep1w` (`order_id`),
  KEY `FKocimc7dtr037rh4ls4l95nlfi` (`product_id`),
  CONSTRAINT `FKbioxgbv59vetrxe0ejfubep1w` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FKocimc7dtr037rh4ls4l95nlfi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,69750.00,'22K GOLD · 4.500 g',NULL,'Floral Gold Ring',2,3.00,34875.00,1,2),(2,37440.00,'22K GOLD · 2.400 g',NULL,'Om Gold Pendant',2,3.00,18720.00,2,11),(3,3012.00,'925 SILVER · 24.000 g',NULL,'Oxidised Silver Anklets',1,3.00,3012.00,3,16),(4,170100.00,'22K GOLD · 22.000 g','/uploads/products/b8310867-e949-4d58-9060-6ef65b092d95.jpg','Twisted Gold Bangle Pair',1,3.00,170100.00,4,10),(5,70540.00,'18K WHITE GOLD · 3.200 g',NULL,'Solitaire Diamond Ring',1,3.00,70540.00,5,1);
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_tracking_events`
--

DROP TABLE IF EXISTS `order_tracking_events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_tracking_events` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `actor_name` varchar(120) DEFAULT NULL,
  `actor_role` varchar(30) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `event` enum('CANCELLED','CONFIRMED','DELIVERED','DELIVERY_ACCEPTED','DELIVERY_ASSIGNED','DELIVERY_FAILED','ORDER_PLACED','OUT_FOR_DELIVERY','PACKED','PAYMENT_RECEIVED','PAYMENT_REFUNDED','PROCESSING','SHIPPED') NOT NULL,
  `message` varchar(500) DEFAULT NULL,
  `order_status` enum('CANCELLED','CONFIRMED','DELIVERED','OUT_FOR_DELIVERY','PACKED','PLACED','PROCESSING','SHIPPED') NOT NULL,
  `order_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_tracking_order` (`order_id`),
  CONSTRAINT `FKpiysrnkv9a30xirabwtyd0poj` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_tracking_events`
--

LOCK TABLES `order_tracking_events` WRITE;
/*!40000 ALTER TABLE `order_tracking_events` DISABLE KEYS */;
INSERT INTO `order_tracking_events` VALUES (1,'Priya Sharma','CUSTOMER','2026-10-01 21:31:20.696606','ORDER_PLACED','We have received your order JC261001-922997.','PLACED',1),(2,'JewelCraft','SYSTEM','2026-10-01 21:31:48.926782','DELIVERED','Delivered. We hope you love your jewellery!','DELIVERED',1),(3,'Priya Sharma','CUSTOMER','2026-10-01 22:05:07.568634','ORDER_PLACED','We have received your order JC261001-281782.','PLACED',2),(4,'JewelCraft','SYSTEM','2026-10-01 22:05:08.310002','DELIVERED','Delivered. We hope you love your jewellery!','DELIVERED',2),(5,'anil nayak','CUSTOMER','2026-10-01 22:31:38.304384','ORDER_PLACED','We have received your order JC261001-564179.','PLACED',3),(6,'JewelCraft','SYSTEM','2026-10-01 22:40:55.365301','DELIVERED','Delivered. We hope you love your jewellery!','DELIVERED',3),(7,'Priya Sharma','CUSTOMER','2026-10-01 22:47:40.075816','ORDER_PLACED','We have received your order JC261001-972296.','PLACED',4),(8,'Priya Sharma','CUSTOMER','2026-10-01 23:30:55.607191','ORDER_PLACED','We have received your order JC261001-980269.','PLACED',5),(9,'Rahul Verma','STAFF','2026-10-01 23:30:55.842463','CONFIRMED','Your order is confirmed and the pieces are reserved for you.','CONFIRMED',5),(10,'Rahul Verma','STAFF','2026-10-01 23:30:55.887800','PROCESSING','Our artisans are preparing and quality-checking your jewellery.','PROCESSING',5),(11,'Rahul Verma','STAFF','2026-10-01 23:30:55.920484','PACKED','Packed in tamper-proof, fully insured packaging.','PACKED',5),(12,'JewelCraft','SYSTEM','2026-10-01 23:30:56.134649','DELIVERY_ASSIGNED','Arjun Singh will deliver your order.','PACKED',5),(13,'Arjun Singh','DELIVERY_PARTNER','2026-10-01 23:30:56.314450','DELIVERY_ACCEPTED','Arjun Singh accepted the delivery and is heading to our store.','PACKED',5),(14,'Arjun Singh','DELIVERY_PARTNER','2026-10-01 23:30:56.356085','SHIPPED','Picked up by Arjun Singh – your order is on its way.','SHIPPED',5),(15,'Arjun Singh','DELIVERY_PARTNER','2026-10-01 23:30:56.391107','OUT_FOR_DELIVERY','Arjun Singh is out for delivery and will reach you today.','OUT_FOR_DELIVERY',5),(16,'Arjun Singh','DELIVERY_PARTNER','2026-10-01 23:30:56.429372','DELIVERED','Delivered. We hope you love your jewellery!','DELIVERED',5);
/*!40000 ALTER TABLE `order_tracking_events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `cancel_reason` varchar(500) DEFAULT NULL,
  `coupon_code` varchar(30) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `delivery_charge` decimal(12,2) NOT NULL,
  `discount` decimal(12,2) NOT NULL,
  `order_number` varchar(30) NOT NULL,
  `ship_city` varchar(255) DEFAULT NULL,
  `ship_country` varchar(255) DEFAULT NULL,
  `ship_full_name` varchar(255) DEFAULT NULL,
  `ship_line1` varchar(255) DEFAULT NULL,
  `ship_line2` varchar(255) DEFAULT NULL,
  `ship_phone` varchar(255) DEFAULT NULL,
  `ship_pincode` varchar(255) DEFAULT NULL,
  `ship_state` varchar(255) DEFAULT NULL,
  `status` enum('CANCELLED','CONFIRMED','DELIVERED','OUT_FOR_DELIVERY','PACKED','PLACED','PROCESSING','SHIPPED') NOT NULL,
  `subtotal` decimal(12,2) NOT NULL,
  `tax` decimal(12,2) NOT NULL,
  `total_amount` decimal(12,2) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKnthkiu7pgmnqnu86i2jyoe2v7` (`order_number`),
  KEY `FK32ql8ubntj5uh44ph9659tiih` (`user_id`),
  CONSTRAINT `FK32ql8ubntj5uh44ph9659tiih` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,NULL,'WELCOME10','2026-10-01 21:31:20.696606',0.00,2000.00,'JC261001-922997','Bengaluru','India','Priya Sharma','12 MG Road',NULL,'9876500005','560001','Karnataka','DELIVERED',69750.00,2032.50,69782.50,'2026-10-01 21:31:48.926782',5),(2,NULL,'FESTIVE5','2026-10-01 22:05:07.568634',0.00,1872.00,'JC261001-281782','Bengaluru','India','Priya Sharma','12 MG Road',NULL,'9876500005','560001','Karnataka','DELIVERED',37440.00,1067.04,36635.04,'2026-10-01 22:05:08.310002',5),(3,NULL,NULL,'2026-10-01 22:31:38.304384',99.00,0.00,'JC261001-564179','mumbai','India','anil nayak','w2e3r4etry','etrywre','9969079783','400093','Maharashtra','DELIVERED',3012.00,90.36,3201.36,'2026-10-01 22:40:55.365301',7),(4,NULL,NULL,'2026-10-01 22:47:40.075816',0.00,0.00,'JC261001-972296','Bengaluru','India','Priya Sharma','12 MG Road',NULL,'9876500005','560001','Karnataka','PLACED',170100.00,5103.00,175203.00,'2026-10-01 22:47:40.075816',5),(5,NULL,NULL,'2026-10-01 23:30:55.595836',0.00,0.00,'JC261001-980269','Bengaluru','India','Priya Sharma','12 MG Road',NULL,'9876500005','560001','Karnataka','DELIVERED',70540.00,2116.20,72656.20,'2026-10-01 23:30:56.437696',5);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` decimal(12,2) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `gateway_order_id` varchar(60) DEFAULT NULL,
  `method` enum('COD','RAZORPAY') NOT NULL,
  `paid_at` datetime(6) DEFAULT NULL,
  `status` enum('FAILED','PENDING','REFUNDED','SUCCESS') NOT NULL,
  `transaction_id` varchar(60) DEFAULT NULL,
  `order_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK8vo36cen604as7etdfwmyjsxt` (`order_id`),
  CONSTRAINT `FK81gagumt0r8y3rmudcgpbk42l` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,69782.50,'2026-10-01 21:31:20.699052',NULL,'COD','2026-10-01 21:31:48.922295','SUCCESS',NULL,1),(2,36635.04,'2026-10-01 22:05:07.577591',NULL,'COD','2026-10-01 22:05:08.302246','SUCCESS',NULL,2),(3,3201.36,'2026-10-01 22:31:38.315859','order_Tiie4nmRWO26YJ','RAZORPAY','2026-10-01 22:32:05.160027','SUCCESS','pay_TiieBcyvk01vH0',3),(4,175203.00,'2026-10-01 22:47:40.077903','order_TiiuzEwk1ePHKU','RAZORPAY',NULL,'PENDING',NULL,4),(5,72656.20,'2026-10-01 23:30:55.602364',NULL,'COD','2026-10-01 23:30:56.421036','SUCCESS',NULL,5);
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `product_images`
--

DROP TABLE IF EXISTS `product_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_images` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `image_url` varchar(500) NOT NULL,
  `main_image` bit(1) NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqnq71xsohugpqwf3c9gxmsuy` (`product_id`),
  CONSTRAINT `FKqnq71xsohugpqwf3c9gxmsuy` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_images`
--

LOCK TABLES `product_images` WRITE;
/*!40000 ALTER TABLE `product_images` DISABLE KEYS */;
INSERT INTO `product_images` VALUES (1,'/uploads/products/b8310867-e949-4d58-9060-6ef65b092d95.jpg',_binary '',10);
/*!40000 ALTER TABLE `product_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `description` text,
  `discount` decimal(12,2) NOT NULL,
  `featured` bit(1) NOT NULL,
  `final_price` decimal(12,2) NOT NULL,
  `image_url` varchar(500) DEFAULT NULL,
  `making_charges` decimal(12,2) NOT NULL,
  `metal_price` decimal(12,2) NOT NULL,
  `metal_type` enum('GOLD','PLATINUM','ROSE_GOLD','SILVER','WHITE_GOLD') NOT NULL,
  `name` varchar(150) NOT NULL,
  `purity` varchar(20) NOT NULL,
  `sku` varchar(40) DEFAULT NULL,
  `stone_charges` decimal(12,2) NOT NULL,
  `tax` decimal(5,2) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `weight` decimal(10,3) NOT NULL,
  `category_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKfhmd06dsmj6k0n90swsh8ie9g` (`sku`),
  KEY `FKog2rp4qthbtt2lfyhfo32lsw9` (`category_id`),
  CONSTRAINT `FKog2rp4qthbtt2lfyhfo32lsw9` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,_binary '','2026-10-01 21:31:09.205253','A brilliant-cut solitaire diamond in a classic four-prong white gold setting.',0.00,_binary '',72656.20,NULL,6500.00,5950.00,'WHITE_GOLD','Solitaire Diamond Ring','18K','JC-W0001',45000.00,3.00,'2026-10-01 21:31:09.205253',3.200,1),(2,_binary '','2026-10-01 21:31:09.218589','Hand-finished floral motif ring in hallmarked 22K gold.',500.00,_binary '\0',36616.50,NULL,3200.00,7300.00,'GOLD','Floral Gold Ring','22K','JC-G0002',0.00,3.00,'2026-10-01 21:31:49.664085',4.500,1),(3,_binary '','2026-10-01 21:31:09.223601','An infinity band with micro-set diamonds in warm rose gold.',0.00,_binary '',22948.40,NULL,2400.00,5850.00,'ROSE_GOLD','Rose Gold Infinity Band','18K','JC-R0003',3500.00,3.00,'2026-10-01 21:31:09.223601',2.800,1),(4,_binary '','2026-10-01 21:31:09.230620','Traditional temple necklace with Lakshmi motifs, perfect for festive wear.',2000.00,_binary '',225776.00,NULL,16800.00,7300.00,'GOLD','Temple Gold Necklace','22K','JC-G0004',0.00,3.00,'2026-10-01 21:31:49.665089',28.000,2),(5,_binary '','2026-10-01 21:31:09.237638','A graduated line of round diamonds set in 18K white gold.',5000.00,_binary '',168791.25,NULL,9500.00,5950.00,'WHITE_GOLD','Diamond Line Necklace','18K','JC-W0005',85000.00,3.00,'2026-10-01 21:31:09.237638',12.500,9),(6,_binary '','2026-10-01 21:31:09.244984','Freshwater pearl drops suspended from polished gold hooks.',0.00,_binary '\0',26841.80,NULL,2800.00,5850.00,'GOLD','Pearl Drop Earrings','18K','JC-G0006',2200.00,3.00,'2026-10-01 21:31:09.244984',3.600,3),(7,_binary '','2026-10-01 21:31:09.251760','Bell-shaped jhumkas with fine filigree work.',0.00,_binary '',67011.80,NULL,5200.00,7300.00,'GOLD','Classic Gold Jhumkas','22K','JC-G0007',0.00,3.00,'2026-10-01 21:31:49.666156',8.200,3),(8,_binary '','2026-10-01 21:31:09.258725','Timeless round diamond studs, 0.30 ct total.',0.00,_binary '\0',42961.30,NULL,3000.00,5950.00,'WHITE_GOLD','Diamond Stud Earrings','18K','JC-W0008',28000.00,3.00,'2026-10-01 21:31:09.258725',1.800,9),(9,_binary '','2026-10-01 21:31:09.265676','A flexible line of diamonds for everyday sparkle.',3000.00,_binary '',128554.30,NULL,7500.00,5950.00,'WHITE_GOLD','Tennis Bracelet','18K','JC-W0009',62000.00,3.00,'2026-10-01 21:31:09.265676',9.800,4),(10,_binary '','2026-10-01 21:31:09.272128','A pair of twisted rope bangles in 22K gold.',1500.00,_binary '\0',175203.00,'/uploads/products/b8310867-e949-4d58-9060-6ef65b092d95.jpg',11000.00,7300.00,'GOLD','Twisted Gold Bangle Pair','22K','JC-G0010',0.00,3.00,'2026-10-01 22:46:04.536040',22.000,5),(11,_binary '','2026-10-01 21:31:09.277690','A small Om pendant, ideal as a gift.',0.00,_binary '\0',19281.60,NULL,1200.00,7300.00,'GOLD','Om Gold Pendant','22K','JC-G0011',0.00,3.00,'2026-10-01 21:31:49.666156',2.400,6),(12,_binary '','2026-10-01 21:31:09.286042','Sterling silver heart pendant with a cubic zirconia accent.',0.00,_binary '\0',1567.66,NULL,650.00,88.00,'SILVER','Heart Silver Pendant','925','JC-S0012',300.00,3.00,'2026-10-01 21:31:09.286042',6.500,11),(13,_binary '','2026-10-01 21:31:09.290951','A sturdy 20-inch rope chain in 22K gold.',0.00,_binary '\0',95172.00,NULL,4800.00,7300.00,'GOLD','Rope Gold Chain','22K','JC-G0013',0.00,3.00,'2026-10-01 21:31:49.667236',12.000,7),(14,_binary '','2026-10-01 21:31:09.297011','Matching platinum wedding bands with a brushed finish.',0.00,_binary '',45011.00,NULL,8500.00,3200.00,'PLATINUM','Platinum Couple Bands','950','JC-P0014',0.00,3.00,'2026-10-01 21:31:09.297011',11.000,8),(15,_binary '','2026-10-01 21:31:09.303453','Necklace and earrings set with kundan and polki work.',5000.00,_binary '',400052.00,NULL,28000.00,7300.00,'GOLD','Bridal Kundan Set','22K','JC-G0015',15000.00,3.00,'2026-10-01 21:31:49.667236',48.000,8),(16,_binary '','2026-10-01 21:31:09.310471','A pair of oxidised silver anklets with ghungroo detailing.',0.00,_binary '\0',3102.36,NULL,900.00,88.00,'SILVER','Oxidised Silver Anklets','925','JC-S0016',0.00,3.00,'2026-10-01 21:31:09.310471',24.000,11);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reviews`
--

DROP TABLE IF EXISTS `reviews`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reviews` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `comment` varchar(1000) DEFAULT NULL,
  `created_at` datetime(6) NOT NULL,
  `rating` int NOT NULL,
  `title` varchar(100) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `product_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK1nv3auyahyyy79hvtrcqgtfo9` (`product_id`,`user_id`),
  KEY `FKcgy7qjc1r99dp117y9en6lxye` (`user_id`),
  CONSTRAINT `FKcgy7qjc1r99dp117y9en6lxye` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKpl51cejpw4gy5swfar8br9ngi` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reviews`
--

LOCK TABLES `reviews` WRITE;
/*!40000 ALTER TABLE `reviews` DISABLE KEYS */;
INSERT INTO `reviews` VALUES (1,'Beautiful finish, exactly as shown.','2026-10-01 21:31:49.135194',5,'Lovely','2026-10-01 21:31:49.135194',2,5),(2,'Exquisite finish, arrived beautifully packed.','2026-10-01 22:05:08.539018',5,'Beautiful','2026-10-01 22:05:08.539018',11,5);
/*!40000 ALTER TABLE `reviews` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` enum('ADMIN','CUSTOMER','DELIVERY_PARTNER','STAFF','STORE_MANAGER') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKofx66keruapi6vyqpv6f2or37` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (5,'ADMIN'),(1,'CUSTOMER'),(4,'DELIVERY_PARTNER'),(2,'STAFF'),(3,'STORE_MANAGER');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `staff_profiles`
--

DROP TABLE IF EXISTS `staff_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `staff_profiles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `department` enum('BILLING','CUSTOMER_SERVICE','DESIGN_WORKSHOP','INVENTORY','QUALITY_CHECK','SALES') DEFAULT NULL,
  `designation` varchar(60) DEFAULT NULL,
  `employee_code` varchar(20) NOT NULL,
  `experience_years` int NOT NULL,
  `previous_employer` varchar(100) DEFAULT NULL,
  `qualification` varchar(100) DEFAULT NULL,
  `store_branch` varchar(80) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKc4gbrs0qv8x1wm0vy1gk1h028` (`employee_code`),
  UNIQUE KEY `UKiqbe2ysx5v1al3px8acr6l03a` (`user_id`),
  CONSTRAINT `FK5i49sugfwky79ag0q798r8tck` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `staff_profiles`
--

LOCK TABLES `staff_profiles` WRITE;
/*!40000 ALTER TABLE `staff_profiles` DISABLE KEYS */;
INSERT INTO `staff_profiles` VALUES (1,NULL,'Store Manager','JC-SM-0001',8,'Heritage Jewellers','MBA, Retail Management','MG Road, Bengaluru',2),(2,'SALES','Jewellery Consultant','JC-ST-0001',3,NULL,'B.Com','MG Road, Bengaluru',3);
/*!40000 ALTER TABLE `staff_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `user_accounts`
--

DROP TABLE IF EXISTS `user_accounts`;
/*!50001 DROP VIEW IF EXISTS `user_accounts`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `user_accounts` AS SELECT 
 1 AS `id`,
 1 AS `username`,
 1 AS `full_name`,
 1 AS `role`,
 1 AS `password_bcrypt`,
 1 AS `phone`,
 1 AS `email_verified`,
 1 AS `approval_status`,
 1 AS `enabled`,
 1 AS `created_at`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) NOT NULL,
  `email` varchar(120) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `updated_at` datetime(6) NOT NULL,
  `role_id` bigint NOT NULL,
  `approval_status` varchar(20) NOT NULL DEFAULT 'APPROVED',
  `email_verified` bit(1) NOT NULL DEFAULT b'1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK6dotkott2kjsp8vw4d0m25fb7` (`email`),
  KEY `FKp56c1712k691lhsyewcssf40f` (`role_id`),
  CONSTRAINT `FKp56c1712k691lhsyewcssf40f` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2026-10-01 21:31:08.884641','admin@jewelcraft.com',_binary '','Admin','User','$2a$10$2esE1Xa0ii.JIzqpfOHyoueckzL.83FSEbDDsDEduuv0LBcArjp6G','9876500001','2026-10-01 21:31:08.884641',5,'APPROVED',_binary ''),(2,'2026-10-01 21:31:08.955516','manager@jewelcraft.com',_binary '','Meera','Kapoor','$2a$10$w4.Ip1odm1hoDtEw1U5POucGjPi3wWYRJVb6X2mNBdOineL9uVyeu','9876500002','2026-10-01 21:31:08.955516',3,'APPROVED',_binary ''),(3,'2026-10-01 21:31:09.027367','staff@jewelcraft.com',_binary '','Rahul','Verma','$2a$10$CMvbOkaW06FSsCMhHNHQL.DsJ8J81MpAtnqFV7OuMJgUB2iPfoE4K','9876500003','2026-10-01 21:31:09.027367',2,'APPROVED',_binary ''),(4,'2026-10-01 21:31:09.093465','delivery@jewelcraft.com',_binary '','Arjun','Singh','$2a$10$Aewy7WKDvhoil8L77SiUP.tLUonasoNgrTCcASW9ZDC84r1kir1iS','9876500004','2026-10-01 21:31:09.093465',4,'APPROVED',_binary ''),(5,'2026-10-01 21:31:09.159079','customer@jewelcraft.com',_binary '','Priya','Sharma','$2a$10$kmao9iOt/wsvINO7sPsMv.kT0onuYIc1Iv9gZOgRjJGatIOy3CzyO','9876500005','2026-10-01 21:31:09.159079',1,'APPROVED',_binary ''),(7,'2026-10-01 22:30:20.692634','anilnayak9969@gmail.com',_binary '','anil','nayak','$2a$10$3zljTpr1zo7lGxBSB4S1duoYfjC4Zn9lIOen0Ln5GFItkbSN1vRyW','9969079783','2026-10-01 22:30:20.692634',1,'APPROVED',_binary '');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wishlist`
--

DROP TABLE IF EXISTS `wishlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wishlist` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKrcuy9aqx9c6q56x1xdoty8r3q` (`user_id`),
  CONSTRAINT `FKtrd6335blsefl2gxpb8lr0gr7` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wishlist`
--

LOCK TABLES `wishlist` WRITE;
/*!40000 ALTER TABLE `wishlist` DISABLE KEYS */;
INSERT INTO `wishlist` VALUES (1,5),(2,7);
/*!40000 ALTER TABLE `wishlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wishlist_items`
--

DROP TABLE IF EXISTS `wishlist_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wishlist_items` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `added_at` datetime(6) NOT NULL,
  `product_id` bigint NOT NULL,
  `wishlist_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK1tt7y773rvi7jkh499ipw7r8w` (`wishlist_id`,`product_id`),
  KEY `FKqxj7lncd242b59fb78rqegyxj` (`product_id`),
  CONSTRAINT `FKp1ce2cs62p57h0uw8ydtdmbx8` FOREIGN KEY (`wishlist_id`) REFERENCES `wishlist` (`id`),
  CONSTRAINT `FKqxj7lncd242b59fb78rqegyxj` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wishlist_items`
--

LOCK TABLES `wishlist_items` WRITE;
/*!40000 ALTER TABLE `wishlist_items` DISABLE KEYS */;
/*!40000 ALTER TABLE `wishlist_items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'JewelCraft'
--

--
-- Current Database: `JewelCraft`
--

USE `JewelCraft`;

--
-- Final view structure for view `user_accounts`
--

/*!50001 DROP VIEW IF EXISTS `user_accounts`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = cp850 */;
/*!50001 SET character_set_results     = cp850 */;
/*!50001 SET collation_connection      = cp850_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `user_accounts` AS select `u`.`id` AS `id`,`u`.`email` AS `username`,trim(concat(`u`.`first_name`,' ',coalesce(`u`.`last_name`,''))) AS `full_name`,`r`.`name` AS `role`,`u`.`password` AS `password_bcrypt`,`u`.`phone` AS `phone`,(`u`.`email_verified` = 0x01) AS `email_verified`,`u`.`approval_status` AS `approval_status`,(`u`.`enabled` = 0x01) AS `enabled`,`u`.`created_at` AS `created_at` from (`users` `u` join `roles` `r` on((`r`.`id` = `u`.`role_id`))) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed

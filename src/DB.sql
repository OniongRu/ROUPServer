-- MySQL dump 10.13  Distrib 8.0.22, for Win64 (x86_64)
--
-- Host: localhost    Database: test
-- ------------------------------------------------------
-- Server version	8.0.22

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
-- Table structure for table `program`
--

DROP TABLE IF EXISTS `program`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `program` (
  `program_id` int NOT NULL AUTO_INCREMENT,
  `program_name` varchar(50) DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`program_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `program_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `program`
--

LOCK TABLES `program` WRITE;
/*!40000 ALTER TABLE `program` DISABLE KEYS */;
INSERT INTO `program` VALUES (1,'Minecraft',1),(2,'Minecraft',1),(3,'Minecraft',1),(4,'Minecraft',1),(5,'Minecraft',1),(6,'Minecraft',1),(7,'Minecraft',1),(8,'Minecraft',1),(9,'Minecraft',1),(10,'Minecraft',1),(11,'Minecraft',1),(12,'Minecraft',1),(13,'Minecraft',1),(14,'Minecraft',1),(15,'Minecraft',1),(16,'Minecraft',1),(17,'Minecraft',1),(18,'Minecraft',1),(19,'Minecraft',1),(20,'Minecraft',1),(21,'Minecraft',1),(22,'Minecraft',1),(23,'Minecraft',1),(24,'Minecraft',1),(25,'Minecraft',1),(26,'Minecraft',1),(27,'Minecraft',1),(28,'Minecraft',1),(29,'Minecraft',1),(30,'Minecraft',1),(31,'Minecraft',1),(32,'Minecraft',1),(33,'Minecraft',1),(34,'Minecraft',1),(35,'Minecraft',1),(36,'Minecraft',1),(37,'Minecraft',1);
/*!40000 ALTER TABLE `program` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `resourceusage`
--

DROP TABLE IF EXISTS `resourceusage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `resourceusage` (
  `resource_id` int NOT NULL AUTO_INCREMENT,
  `date_using` datetime DEFAULT NULL,
  `cpuUsage` float DEFAULT NULL,
  `ramUsage` float DEFAULT NULL,
  `program_id` int DEFAULT NULL,
  `thread_amount` int DEFAULT NULL,
  PRIMARY KEY (`resource_id`),
  KEY `program_id` (`program_id`),
  CONSTRAINT `resourceusage_ibfk_1` FOREIGN KEY (`program_id`) REFERENCES `program` (`program_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `resourceusage`
--

LOCK TABLES `resourceusage` WRITE;
/*!40000 ALTER TABLE `resourceusage` DISABLE KEYS */;
INSERT INTO `resourceusage` VALUES (1,'0000-00-00 00:00:00',0,0,1,NULL),(2,'2020-11-10 22:38:36',10,20,1,NULL),(3,NULL,10,20,1,NULL),(4,'0000-00-00 00:00:00',0,0,2,NULL),(5,'0000-00-00 00:00:00',0,0,2,NULL),(6,'0000-00-00 00:00:00',0,0,2,NULL);
/*!40000 ALTER TABLE `resourceusage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `user_name` varchar(50) DEFAULT NULL,
  `creationTime` datetime DEFAULT NULL,
  `login` varchar(50) DEFAULT NULL,
  `password` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'Goose',NULL,'GooseLogin','GeeseTheBest'),(2,'Goose',NULL,'GooseLogin','GeeseTheBest'),(3,'Goose',NULL,'GooseLogin','GeeseTheBest'),(4,'Goose',NULL,'GooseLogin','GeeseTheBest'),(5,'Goose',NULL,'GooseLogin','GeeseTheBest'),(6,'Goose',NULL,'GooseLogin','GeeseTheBest'),(7,'Goose',NULL,'GooseLogin','GeeseTheBest'),(8,'Goose',NULL,'GooseLogin','GeeseTheBest'),(9,'Goose',NULL,'GooseLogin','GeeseTheBest'),(10,'Goose',NULL,'GooseLogin','GeeseTheBest'),(11,'Goose',NULL,'GooseLogin','GeeseTheBest'),(12,'Goose',NULL,'GooseLogin','GeeseTheBest'),(13,'Goose',NULL,'GooseLogin','GeeseTheBest'),(14,'Goose',NULL,'GooseLogin','GeeseTheBest'),(15,'Goose',NULL,'GooseLogin','GeeseTheBest'),(16,'Goose',NULL,'GooseLogin','GeeseTheBest'),(17,'Goose',NULL,'GooseLogin','GeeseTheBest'),(18,'Goose',NULL,'GooseLogin','GeeseTheBest'),(19,'Goose',NULL,'GooseLogin','GeeseTheBest'),(20,'Goose',NULL,'GooseLogin','GeeseTheBest'),(21,'Goose',NULL,'GooseLogin','GeeseTheBest'),(22,'Goose',NULL,'GooseLogin','GeeseTheBest'),(23,'Goose',NULL,'GooseLogin','GeeseTheBest'),(24,'Goose',NULL,'GooseLogin','GeeseTheBest'),(25,'Goose',NULL,'GooseLogin','GeeseTheBest'),(26,'Goose',NULL,'GooseLogin','GeeseTheBest'),(27,'Goose',NULL,'GooseLogin','GeeseTheBest'),(28,'Goose',NULL,'GooseLogin','GeeseTheBest'),(29,'Goose',NULL,'GooseLogin','GeeseTheBest'),(30,'Goose',NULL,'GooseLogin','GeeseTheBest'),(31,'Goose',NULL,'GooseLogin','GeeseTheBest'),(32,'Goose',NULL,'GooseLogin','GeeseTheBest'),(33,'Goose',NULL,'GooseLogin','GeeseTheBest'),(34,'Goose',NULL,'GooseLogin','GeeseTheBest'),(35,'Goose',NULL,'GooseLogin','GeeseTheBest'),(36,'Goose',NULL,'GooseLogin','GeeseTheBest'),(37,'Goose',NULL,'GooseLogin','GeeseTheBest'),(38,'Goose',NULL,'GooseLogin','GeeseTheBest'),(39,'Goose',NULL,'GooseLogin','GeeseTheBest'),(40,'Goose',NULL,'GooseLogin','GeeseTheBest'),(41,'Goose',NULL,'GooseLogin','GeeseTheBest'),(42,'Goose',NULL,'GooseLogin','GeeseTheBest');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2020-11-11 14:38:41

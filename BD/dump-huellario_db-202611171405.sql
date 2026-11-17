/*M!999999\- enable the sandbox mode */ 
-- MariaDB dump 10.19-11.7.2-MariaDB, for Win64 (AMD64)
--
-- Host: localhost    Database: huellario_db
-- ------------------------------------------------------
-- Server version	13.0.2-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*M!100616 SET @OLD_NOTE_VERBOSITY=@@NOTE_VERBOSITY, NOTE_VERBOSITY=0 */;

--
-- Table structure for table `pets`
--

DROP TABLE IF EXISTS `pets`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `pets` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `user_id` int(10) unsigned NOT NULL,
  `name` varchar(80) NOT NULL,
  `species` enum('PERRO','GATO','AVE','HAMSTER','PEZ','OTRO') NOT NULL DEFAULT 'OTRO',
  `species_detail` varchar(80) DEFAULT NULL,
  `breed` varchar(100) DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `photo_url` varchar(500) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `fk_pets_user` (`user_id`),
  CONSTRAINT `fk_pets_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pets`
--

LOCK TABLES `pets` WRITE;
/*!40000 ALTER TABLE `pets` DISABLE KEYS */;
INSERT INTO `pets` VALUES
(1,1,'Orión','GATO',NULL,'Siamés','2025-06-13',NULL,'2026-11-10 01:40:11'),
(3,1,'Roxi','PERRO',NULL,'Criolla','2018-08-13',NULL,'2026-11-17 02:45:56');
/*!40000 ALTER TABLE `pets` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `routine_logs`
--

DROP TABLE IF EXISTS `routine_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `routine_logs` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `routine_id` int(10) unsigned NOT NULL,
  `scheduled_date` date NOT NULL,
  `status` enum('PENDIENTE','COMPLETADA','NO_FINALIZADA','OMITIDA') NOT NULL DEFAULT 'PENDIENTE',
  `completed_at` datetime DEFAULT NULL,
  `notes` varchar(500) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_routine_date` (`routine_id`,`scheduled_date`),
  KEY `idx_logs_date` (`scheduled_date`),
  KEY `idx_logs_status` (`status`),
  CONSTRAINT `fk_routine_logs_routine` FOREIGN KEY (`routine_id`) REFERENCES `routines` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `routine_logs`
--

LOCK TABLES `routine_logs` WRITE;
/*!40000 ALTER TABLE `routine_logs` DISABLE KEYS */;
INSERT INTO `routine_logs` VALUES
(1,4,'2026-11-17','NO_FINALIZADA',NULL,NULL,'2026-11-17 18:48:53'),
(2,7,'2026-11-17','NO_FINALIZADA',NULL,NULL,'2026-11-17 18:50:06');
/*!40000 ALTER TABLE `routine_logs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `routines`
--

DROP TABLE IF EXISTS `routines`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `routines` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `pet_id` int(10) unsigned NOT NULL,
  `name` varchar(100) NOT NULL,
  `category` enum('ALIMENTACION','AGUA','HIGIENE','EJERCICIO','MEDICACION','LIMPIEZA','SOCIALIZACION','OTRO') NOT NULL DEFAULT 'OTRO',
  `description` text DEFAULT NULL,
  `scheduled_time` time NOT NULL,
  `frequency` enum('DIARIA','SEMANAL','DIAS_ESPECIFICOS','UNICA') NOT NULL,
  `days_of_week` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`days_of_week`)),
  `start_date` date NOT NULL,
  `end_date` date DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `idx_routines_pet` (`pet_id`),
  KEY `idx_routines_active` (`is_active`),
  KEY `idx_routines_dates` (`start_date`,`end_date`),
  CONSTRAINT `fk_routines_pet` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `routines`
--

LOCK TABLES `routines` WRITE;
/*!40000 ALTER TABLE `routines` DISABLE KEYS */;
INSERT INTO `routines` VALUES
(3,1,'Bañar','HIGIENE','Usar el shampoo de tapa azul','03:00:00','SEMANAL',NULL,'2026-11-09',NULL,1,'2026-11-10 19:15:37','2026-11-10 19:15:37'),
(4,1,'Pasear','EJERCICIO',NULL,'08:00:00','DIARIA',NULL,'2026-11-10',NULL,1,'2026-11-10 19:22:08','2026-11-10 19:22:08'),
(6,3,'Cepillar','LIMPIEZA',NULL,'10:00:00','UNICA',NULL,'2026-11-16',NULL,1,'2026-11-17 02:47:16','2026-11-17 02:47:16'),
(7,3,'Desayuno','ALIMENTACION',NULL,'08:00:00','DIARIA',NULL,'2026-11-17',NULL,1,'2026-11-17 18:49:37','2026-11-17 18:49:37'),
(8,3,'Cena','ALIMENTACION',NULL,'18:00:00','DIARIA',NULL,'2026-11-17',NULL,1,'2026-11-17 18:49:56','2026-11-17 18:49:56');
/*!40000 ALTER TABLE `routines` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES
(1,'Laura Sofia Zuñiga Garcia','sofiazunigag2007@gmail.com','$2b$12$lKbJILWvnmjVLsMSeZQ60uXYd48V5CHxl0wRQYcaGiJfdbe4xbci.','2026-10-27 13:50:21'),
(2,'Anyi Daniela Ruano','anyi.ruano@correiunivalle.edu.co','$2b$12$52LufOaKEBDy8IDUfI8NNOn7PioEQ5wSKn2qHcPA1bpBDFSdwrbNu','2026-10-27 13:52:53'),
(3,'Pepito Pérez','pepito@gmail.com','$2b$12$z6Xgt7XS.0iUDJzhBJqkj.fGBxTvndNg2x9KSAstvb8CLiBsgqdNa','2026-10-27 14:03:13');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'huellario_db'
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*M!100616 SET NOTE_VERBOSITY=@OLD_NOTE_VERBOSITY */;

-- Dump completed on 2026-11-17 14:05:55

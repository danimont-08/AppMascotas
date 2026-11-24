-- Crea las tablas de rutinas en huellario_db, ya con la columna priority.
-- No borra ni modifica las tablas users y pets. Si las tablas ya existen, no hace nada.

CREATE TABLE IF NOT EXISTS `routines` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `pet_id` int(10) unsigned NOT NULL,
  `name` varchar(100) NOT NULL,
  `category` enum('ALIMENTACION','AGUA','HIGIENE','EJERCICIO','MEDICACION','LIMPIEZA','SOCIALIZACION','OTRO') NOT NULL DEFAULT 'OTRO',
  `description` text DEFAULT NULL,
  `priority` enum('ALTA','MEDIA','BAJA') NOT NULL DEFAULT 'MEDIA',
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
  KEY `idx_routines_priority` (`priority`),
  CONSTRAINT `fk_routines_pet` FOREIGN KEY (`pet_id`) REFERENCES `pets` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `routine_logs` (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

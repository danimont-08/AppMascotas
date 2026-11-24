-- Actualiza una tabla `pets` antigua al esquema que espera el backend.
-- Agrega species_detail y las especies AVE, HAMSTER y PEZ. No borra datos.

ALTER TABLE `pets`
  MODIFY COLUMN `species` enum('PERRO','GATO','AVE','HAMSTER','PEZ','OTRO') NOT NULL DEFAULT 'OTRO',
  ADD COLUMN IF NOT EXISTS `species_detail` varchar(80) DEFAULT NULL AFTER `species`;

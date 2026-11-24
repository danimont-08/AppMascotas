-- Agrupa las categorías de rutinas en 5 categorías generales.
--   AGUA -> ALIMENTACION, LIMPIEZA -> HIGIENE, MEDICACION -> SALUD,
--   EJERCICIO y SOCIALIZACION -> ACTIVIDAD.
-- Conserva todas las rutinas; solo cambia su categoría.

-- 1. Permitir temporalmente las categorías antiguas y las nuevas
ALTER TABLE `routines` MODIFY COLUMN `category`
  enum('ALIMENTACION','AGUA','HIGIENE','EJERCICIO','MEDICACION','LIMPIEZA','SOCIALIZACION','SALUD','ACTIVIDAD','OTRO')
  NOT NULL DEFAULT 'OTRO';

-- 2. Pasar las rutinas existentes a su categoría general
UPDATE `routines` SET `category` = 'ALIMENTACION' WHERE `category` = 'AGUA';
UPDATE `routines` SET `category` = 'HIGIENE'      WHERE `category` = 'LIMPIEZA';
UPDATE `routines` SET `category` = 'SALUD'        WHERE `category` = 'MEDICACION';
UPDATE `routines` SET `category` = 'ACTIVIDAD'    WHERE `category` IN ('EJERCICIO', 'SOCIALIZACION');

-- 3. Dejar solo las categorías generales
ALTER TABLE `routines` MODIFY COLUMN `category`
  enum('ALIMENTACION','HIGIENE','SALUD','ACTIVIDAD','OTRO')
  NOT NULL DEFAULT 'OTRO';

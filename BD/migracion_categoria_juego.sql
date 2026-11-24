-- Agrega la categoría JUEGO (jugar y divertirse) a las rutinas.
-- Ejecutar después de migracion_categorias_generales.sql. No modifica rutinas existentes.

ALTER TABLE `routines` MODIFY COLUMN `category`
  enum('ALIMENTACION','HIGIENE','SALUD','ACTIVIDAD','JUEGO','OTRO')
  NOT NULL DEFAULT 'OTRO';

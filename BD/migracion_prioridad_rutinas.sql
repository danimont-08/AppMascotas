-- Agrega la prioridad a las rutinas (ALTA, MEDIA, BAJA).
-- Ejecutar una sola vez sobre huellario_db, después de importar el dump.
-- Las rutinas existentes quedan con prioridad MEDIA.

ALTER TABLE `routines`
  ADD COLUMN `priority` enum('ALTA','MEDIA','BAJA') NOT NULL DEFAULT 'MEDIA' AFTER `description`;

CREATE INDEX `idx_routines_priority` ON `routines` (`priority`);

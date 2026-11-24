-- Agrega el peso (en kg) a las mascotas. Opcional; no modifica datos existentes.

ALTER TABLE `pets`
  ADD COLUMN IF NOT EXISTS `weight_kg` decimal(5,2) DEFAULT NULL AFTER `birth_date`;

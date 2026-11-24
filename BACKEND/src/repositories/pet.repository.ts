
// Ejecuta las consultas parametrizadas de mascotas y limita cada operación al propietario.
import { query, queryOne } from '../config/db';
import { PetRecord, PetPayload } from '../types';

export const petRepository = {
  async findByUser(userId: number): Promise<PetRecord[]> {
    return query<PetRecord>('SELECT * FROM pets WHERE user_id = ? ORDER BY created_at DESC', [userId]);
  },

  async findByIdAndUser(id: number, userId: number): Promise<PetRecord | null> {
    return queryOne<PetRecord>('SELECT * FROM pets WHERE id = ? AND user_id = ?', [id, userId]);
  },

  async create(userId: number, petData: PetPayload): Promise<number> {
    const result = await query<any>(
      `INSERT INTO pets (user_id, name, species, species_detail, breed, birth_date, weight_kg, photo_url)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?)`,
      [
        userId,
        petData.name,
        petData.species,
        petData.species_detail ?? null,
        petData.breed ?? null,
        petData.birth_date ?? null,
        petData.weight_kg ?? null,
        petData.photo_url ?? null,
      ],
    );

    const insertId = (result as any)?.insertId ?? (result as any)?.[0]?.insertId ?? null;
    if (!insertId) {
      throw new Error('MariaDB no devolvió el identificador de la mascota creada');
    }

    return Number(insertId);
  },

  async update(id: number, userId: number, petData: Partial<PetPayload>): Promise<PetRecord | null> {
    const existing = await this.findByIdAndUser(id, userId);
    if (!existing) return null;

    const updates = [
      petData.name !== undefined ? 'name = ?' : null,
      petData.species !== undefined ? 'species = ?' : null,
      petData.species_detail !== undefined ? 'species_detail = ?' : null,
      petData.breed !== undefined ? 'breed = ?' : null,
      petData.birth_date !== undefined ? 'birth_date = ?' : null,
      petData.weight_kg !== undefined ? 'weight_kg = ?' : null,
      petData.photo_url !== undefined ? 'photo_url = ?' : null,
    ].filter(Boolean) as string[];

    if (!updates.length) {
      return existing;
    }

    const values: unknown[] = [];
    if (petData.name !== undefined) values.push(petData.name);
    if (petData.species !== undefined) values.push(petData.species);
    if (petData.species_detail !== undefined) values.push(petData.species_detail ?? null);
    if (petData.breed !== undefined) values.push(petData.breed ?? null);
    if (petData.birth_date !== undefined) values.push(petData.birth_date ?? null);
    if (petData.weight_kg !== undefined) values.push(petData.weight_kg ?? null);
    if (petData.photo_url !== undefined) values.push(petData.photo_url ?? null);

    values.push(id, userId);

    await query(
      `UPDATE pets SET ${updates.join(', ')} WHERE id = ? AND user_id = ?`,
      values,
    );

    return this.findByIdAndUser(id, userId);
  },

  async remove(id: number, userId: number): Promise<boolean> {
    const result = await query<any>(
      'DELETE FROM pets WHERE id = ? AND user_id = ?',
      [id, userId],
    );

    const affectedRows = (result as any)?.affectedRows ?? (result as any)?.[0]?.affectedRows ?? 0;
    return Number(affectedRows) > 0;
  },
};

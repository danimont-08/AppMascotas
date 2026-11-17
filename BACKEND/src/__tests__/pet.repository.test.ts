// Comprueba que las consultas de mascotas siempre incluyen el propietario autenticado.
import * as database from '../config/db';
import { petRepository } from '../repositories/pet.repository';
import { pool } from '../config/db';

describe('petRepository ownership filters', () => {
  afterAll(async () => {
    await pool.end();
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('scopes pet lookup to both pet id and owner id', async () => {
    const queryOne = jest.spyOn(database, 'queryOne').mockResolvedValueOnce(null);

    await petRepository.findByIdAndUser(18, 7);

    expect(queryOne).toHaveBeenCalledWith(
      'SELECT * FROM pets WHERE id = ? AND user_id = ?',
      [18, 7],
    );
  });

  it('scopes the pet list to the authenticated owner', async () => {
    const query = jest.spyOn(database, 'query').mockResolvedValueOnce([]);

    await expect(petRepository.findByUser(7)).resolves.toEqual([]);

    expect(query).toHaveBeenCalledWith(
      'SELECT * FROM pets WHERE user_id = ? ORDER BY created_at DESC',
      [7],
    );
  });

  it('returns the insert id when MariaDB returns a result object', async () => {
    const query = jest.spyOn(database, 'query').mockResolvedValueOnce({ insertId: 42 } as any);

    await expect(petRepository.create(7, { name: 'Orión', species: 'GATO' })).resolves.toBe(42);

    expect(query).toHaveBeenCalledWith(
      expect.stringContaining('INSERT INTO pets'),
      [7, 'Orión', 'GATO', null, null, null, null],
    );
  });

  it('scopes pet deletion to both pet id and owner id', async () => {
    const query = jest.spyOn(database, 'query').mockResolvedValueOnce([{ affectedRows: 1 }]);

    await expect(petRepository.remove(18, 7)).resolves.toBe(true);

    expect(query).toHaveBeenCalledWith(
      'DELETE FROM pets WHERE id = ? AND user_id = ?',
      [18, 7],
    );
  });
});

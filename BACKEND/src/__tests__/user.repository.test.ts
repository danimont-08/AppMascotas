import * as database from '../config/db';
import { pool } from '../config/db';
import { userRepository } from '../repositories/user.repository';

describe('userRepository profile access', () => {
  afterAll(async () => {
    await pool.end();
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('selects only public columns for the profile', async () => {
    const queryOne = jest.spyOn(database, 'queryOne').mockResolvedValueOnce({
      id: 7,
      name: 'Sofia',
      email: 'sofia@example.com',
    });

    await expect(userRepository.findProfileById(7)).resolves.toEqual({
      id: 7,
      name: 'Sofia',
      email: 'sofia@example.com',
    });
    expect(queryOne).toHaveBeenCalledWith(
      'SELECT id, name, email FROM users WHERE id = ?',
      [7],
    );
  });
});

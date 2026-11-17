import { userRepository } from '../repositories/user.repository';
import { userService } from '../services/user.service';
import { pool } from '../config/db';

describe('userService profile operations', () => {
  afterAll(async () => {
    await pool.end();
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('returns only public profile fields', async () => {
    jest.spyOn(userRepository, 'findProfileById').mockResolvedValueOnce({
      id: 7,
      name: 'Sofia',
      email: 'sofia@example.com',
    });

    await expect(userService.getProfile(7)).resolves.toEqual({
      user: {
        id: 7,
        fullName: 'Sofia',
        email: 'sofia@example.com',
      },
    });
  });

  it('returns 404 when the requested profile does not exist', async () => {
    jest.spyOn(userRepository, 'findProfileById').mockResolvedValueOnce(null);

    await expect(userService.getProfile(7)).rejects.toMatchObject({ statusCode: 404 });
  });

  it('rejects an email already owned by another user', async () => {
    jest.spyOn(userRepository, 'findProfileById').mockResolvedValueOnce({
      id: 7,
      name: 'Sofia',
      email: 'sofia@example.com',
    });
    jest.spyOn(userRepository, 'findByEmail').mockResolvedValueOnce({
      id: 8,
      name: 'Other user',
      email: 'taken@example.com',
      password_hash: 'not-returned',
    });
    const update = jest.spyOn(userRepository, 'update');

    await expect(
      userService.updateProfile(7, { fullName: 'Sofia', email: 'taken@example.com' }),
    ).rejects.toMatchObject({ statusCode: 409 });
    expect(update).not.toHaveBeenCalled();
  });

  it('maps a duplicate-email database race to 409', async () => {
    jest.spyOn(userRepository, 'findProfileById').mockResolvedValueOnce({
      id: 7,
      name: 'Sofia',
      email: 'sofia@example.com',
    });
    jest.spyOn(userRepository, 'findByEmail').mockResolvedValueOnce(null);
    jest.spyOn(userRepository, 'update').mockRejectedValueOnce({ code: 'ER_DUP_ENTRY', errno: 1062 });

    await expect(
      userService.updateProfile(7, { fullName: 'Sofia', email: 'new@example.com' }),
    ).rejects.toMatchObject({ statusCode: 409 });
  });

  it('updates and returns the normalized public profile', async () => {
    const findProfileById = jest.spyOn(userRepository, 'findProfileById')
      .mockResolvedValueOnce({ id: 7, name: 'Sofia', email: 'sofia@example.com' })
      .mockResolvedValueOnce({ id: 7, name: 'Sofia Zuñiga', email: 'new@example.com' });
    jest.spyOn(userRepository, 'findByEmail').mockResolvedValueOnce(null);
    const update = jest.spyOn(userRepository, 'update').mockResolvedValueOnce(true);

    await expect(
      userService.updateProfile(7, { fullName: ' Sofia Zuñiga ', email: 'NEW@EXAMPLE.COM' }),
    ).resolves.toEqual({
      message: 'Perfil actualizado correctamente',
      user: {
        id: 7,
        fullName: 'Sofia Zuñiga',
        email: 'new@example.com',
      },
    });

    expect(update).toHaveBeenCalledWith(7, { name: 'Sofia Zuñiga', email: 'new@example.com' });
    expect(findProfileById).toHaveBeenCalledTimes(2);
  });
});

// Comprueba el contrato de registro y el rechazo de correos duplicados.
import bcrypt from 'bcryptjs';
import { authService } from '../services/auth.service';
import { userRepository } from '../repositories/user.repository';
import { pool } from '../config/db';

describe('authService', () => {
  afterAll(async () => {
    await pool.end();
  });

  it('registers a user and returns token and user payload', async () => {
    jest.spyOn(userRepository, 'findByEmail').mockResolvedValueOnce(null);
    jest.spyOn(userRepository, 'create').mockResolvedValueOnce(42);
    jest.spyOn(userRepository, 'findById').mockResolvedValueOnce({
      id: 42,
      name: 'Ana García',
      email: 'ana@ejemplo.com',
      password_hash: await bcrypt.hash('secreto123', 12),
    });

    const result = await authService.register({
      fullName: 'Ana García',
      email: 'ana@ejemplo.com',
      password: 'secreto123',
    });

    expect(result.token).toBeTruthy();
    expect(result.user).toMatchObject({
      id: 42,
      fullName: 'Ana García',
      email: 'ana@ejemplo.com',
    });
  });

  it('rejects duplicate email during registration', async () => {
    jest.spyOn(userRepository, 'findByEmail').mockResolvedValueOnce({
      id: 1,
      name: 'Test User',
      email: 'test@ejemplo.com',
      password_hash: 'hash',
    });

    await expect(
      authService.register({
        fullName: 'Test User',
        email: 'test@ejemplo.com',
        password: 'secreto123',
      }),
    ).rejects.toMatchObject({ statusCode: 409 });
  });

  it('authenticates a user and returns the same public payload', async () => {
    jest.spyOn(userRepository, 'findByEmail').mockResolvedValueOnce({
    id: 8,
    name: 'Ana García',
    email: 'ana@ejemplo.com',
    password_hash: await bcrypt.hash('secreto123', 12),
    });

    const result = await authService.login({
    email: 'ana@ejemplo.com',
    password: 'secreto123',
    });

    expect(result.token).toBeTruthy();
    expect(result.user).toEqual({
    id: 8,
    fullName: 'Ana García',
    email: 'ana@ejemplo.com',
    });
  });

  it('rejects invalid login credentials', async () => {
    jest.spyOn(userRepository, 'findByEmail').mockResolvedValueOnce({
    id: 8,
    name: 'Ana García',
    email: 'ana@ejemplo.com',
    password_hash: await bcrypt.hash('secreto123', 12),
    });

    await expect(
    authService.login({ email: 'ana@ejemplo.com', password: 'incorrecta' }),
    ).rejects.toMatchObject({ statusCode: 401 });
  });
});

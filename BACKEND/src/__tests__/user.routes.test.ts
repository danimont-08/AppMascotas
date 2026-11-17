import * as jwt from 'jsonwebtoken';
import request from 'supertest';
import app from '../app';
import { pool } from '../config/db';
import { env } from '../config/env';
import { petService } from '../services/pet.service';
import { userService } from '../services/user.service';

jest.mock('../services/user.service', () => ({
  userService: {
    getProfile: jest.fn(),
    updateProfile: jest.fn(),
  },
}));

jest.mock('../services/pet.service', () => ({
  petService: {
    listByUser: jest.fn(),
  },
}));

const mockedUserService = userService as jest.Mocked<typeof userService>;
const mockedPetService = petService as jest.Mocked<typeof petService>;
const tokenForUser = (userId: number) => jwt.sign({ userId }, env.jwtSecret);

describe('authenticated owner routes', () => {
  afterAll(async () => {
    await pool.end();
  });

  afterEach(() => {
    jest.clearAllMocks();
  });

  it.each([
    ['missing', undefined],
    ['invalid', 'Bearer invalid-token'],
  ])('rejects a %s token for the profile route', async (_label, authorization) => {
    const call = request(app).get('/api/users/me');
    if (authorization) {
      call.set('Authorization', authorization);
    }

    await call.expect(401);
    expect(mockedUserService.getProfile).not.toHaveBeenCalled();
  });

  it('loads the profile using the authenticated user ID and returns public fields', async () => {
    mockedUserService.getProfile.mockResolvedValueOnce({
      user: { id: 7, fullName: 'Sofia', email: 'sofia@example.com' },
    });

    const response = await request(app)
      .get('/api/users/me')
      .set('Authorization', `Bearer ${tokenForUser(7)}`)
      .expect(200);

    expect(response.body).toEqual({
      user: { id: 7, fullName: 'Sofia', email: 'sofia@example.com' },
    });
    expect(mockedUserService.getProfile).toHaveBeenCalledWith(7);
  });

  it('updates the JWT owner and ignores body identity or password fields', async () => {
    mockedUserService.updateProfile.mockResolvedValueOnce({
      message: 'Perfil actualizado correctamente',
      user: { id: 7, fullName: 'Sofia Zuñiga', email: 'sofia@example.com' },
    });

    await request(app)
      .put('/api/users/me')
      .set('Authorization', `Bearer ${tokenForUser(7)}`)
      .send({
        user_id: 8,
        password_hash: 'should-not-be-saved',
        fullName: 'Sofia Zuñiga',
        email: 'sofia@example.com',
      })
      .expect(200);

    expect(mockedUserService.updateProfile).toHaveBeenCalledWith(7, {
      fullName: 'Sofia Zuñiga',
      email: 'sofia@example.com',
    });
  });

  it('rejects invalid profile data before calling the service', async () => {
    await request(app)
      .put('/api/users/me')
      .set('Authorization', `Bearer ${tokenForUser(7)}`)
      .send({ fullName: 'S', email: 'bad-email' })
      .expect(400);

    expect(mockedUserService.updateProfile).not.toHaveBeenCalled();
  });

  it('requests only the authenticated owner’s pets', async () => {
    mockedPetService.listByUser.mockResolvedValueOnce([]);

    await request(app)
      .get('/api/pets')
      .set('Authorization', `Bearer ${tokenForUser(7)}`)
      .expect(200, []);

    expect(mockedPetService.listByUser).toHaveBeenCalledWith(7);
  });
});

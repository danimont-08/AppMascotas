// Implementa el registro, el inicio de sesión, el hash de contraseñas y la emisión de JWT.
import bcrypt from 'bcryptjs';
import * as jwt from 'jsonwebtoken';
import { env } from '../config/env';
import { userRepository } from '../repositories/user.repository';
import { RegisterPayload, LoginPayload, SafeUser } from '../types';
import { ApiError } from '../utils/errors';

export const authService = {
  async register(payload: RegisterPayload) {
    const normalizedEmail = payload.email.trim().toLowerCase();
    const existing = await userRepository.findByEmail(normalizedEmail);

    if (existing) {
      throw new ApiError(409, 'El correo electrónico ya está registrado');
    }

    const passwordHash = await bcrypt.hash(payload.password, 12);
    const userId = await userRepository.create({
      name: payload.fullName.trim(),
      email: normalizedEmail,
      passwordHash,
    });

    const user = await userRepository.findById(Number(userId));
    if (!user) {
      throw new ApiError(500, 'No se pudo completar el registro del usuario');
    }

    const userPayload: SafeUser = {
      id: user.id,
      fullName: user.name,
      email: user.email,
    };

    const token = jwt.sign(
      { userId: user.id, email: user.email },
      env.jwtSecret,
      { expiresIn: env.jwtExpiresIn as jwt.SignOptions['expiresIn'] },
    );

    return {
      token,
      message: 'Operación realizada correctamente',
      user: userPayload,
    };
  },

  async login(payload: LoginPayload) {
    const normalizedEmail = payload.email.trim().toLowerCase();
    const user = await userRepository.findByEmail(normalizedEmail);

    if (!user) {
      throw new ApiError(401, 'Credenciales inválidas');
    }

    const isPasswordValid = await bcrypt.compare(payload.password, user.password_hash);
    if (!isPasswordValid) {
      throw new ApiError(401, 'Credenciales inválidas');
    }

    const token = jwt.sign(
      { userId: user.id, email: user.email },
      env.jwtSecret,
      { expiresIn: env.jwtExpiresIn as jwt.SignOptions['expiresIn'] },
    );

    return {
      token,
      message: 'Operación realizada correctamente',
      user: {
        id: user.id,
        fullName: user.name,
        email: user.email,
      },
    };
  },
};

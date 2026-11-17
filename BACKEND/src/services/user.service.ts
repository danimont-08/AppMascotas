import { userRepository } from '../repositories/user.repository';
import { ApiError } from '../utils/errors';

const isDuplicateEntryError = (error: unknown): boolean => {
  if (typeof error !== 'object' || error === null) {
    return false;
  }

  const databaseError = error as { code?: unknown; errno?: unknown };
  return databaseError.code === 'ER_DUP_ENTRY' || databaseError.errno === 1062;
};

export const userService = {
  async getProfile(userId: number) {
    const user = await userRepository.findProfileById(userId);
    if (!user) {
      throw new ApiError(404, 'Usuario no encontrado');
    }

    return {
      user: {
        id: user.id,
        fullName: user.name,
        email: user.email,
      },
    };
  },

  async updateProfile(userId: number, payload: { fullName: string; email: string }) {
    const normalizedEmail = payload.email.trim().toLowerCase();
    const name = payload.fullName.trim();

    const currentUser = await userRepository.findProfileById(userId);
    if (!currentUser) {
      throw new ApiError(404, 'Usuario no encontrado');
    }

    if (normalizedEmail !== currentUser.email) {
      const existing = await userRepository.findByEmail(normalizedEmail);
      if (existing && existing.id !== userId) {
        throw new ApiError(409, 'El correo electrónico ya está en uso por otro usuario');
      }
    }

    try {
      await userRepository.update(userId, { name, email: normalizedEmail });
    } catch (error) {
      if (isDuplicateEntryError(error)) {
        throw new ApiError(409, 'El correo electrónico ya está en uso por otro usuario');
      }
      throw error;
    }

    const updatedUser = await userRepository.findProfileById(userId);
    if (!updatedUser) {
      throw new ApiError(404, 'Usuario no encontrado');
    }

    return {
      message: 'Perfil actualizado correctamente',
      user: {
        id: updatedUser.id,
        fullName: updatedUser.name,
        email: updatedUser.email,
      },
    };
  },
};

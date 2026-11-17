import { Response, NextFunction, Request } from 'express';
import { AuthenticatedRequest } from '../types';
import { userService } from '../services/user.service';
import { ApiError } from '../utils/errors';
import { validateUpdateProfileInput } from '../utils/validators';

export const getProfile = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const authReq = req as AuthenticatedRequest;
    const userId = Number(authReq.user?.id);
    if (!userId) {
      throw new ApiError(401, 'Usuario no autenticado');
    }

    const result = await userService.getProfile(userId);
    res.status(200).json(result);
  } catch (error) {
    next(error);
  }
};

export const updateProfile = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const authReq = req as AuthenticatedRequest;
    const userId = Number(authReq.user?.id);
    if (!userId) {
      throw new ApiError(401, 'Usuario no autenticado');
    }

    const validation = validateUpdateProfileInput(req.body);
    if (!validation.valid) {
      throw new ApiError(400, validation.message ?? 'Los datos enviados no son válidos');
    }

    const { fullName, email } = req.body;
    const result = await userService.updateProfile(userId, { fullName, email });
    res.status(200).json(result);
  } catch (error) {
    next(error);
  }
};

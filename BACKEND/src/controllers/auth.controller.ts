// Valida solicitudes de autenticación y traduce sus resultados a respuestas HTTP.
import { Request, Response, NextFunction } from 'express';
import { authService } from '../services/auth.service';
import { validateLoginInput, validateRegisterInput } from '../utils/validators';
import { ApiError } from '../utils/errors';

export const register = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const validation = validateRegisterInput(req.body);
    if (!validation.valid) {
      throw new ApiError(400, validation.message ?? 'Los datos enviados no son válidos');
    }

    const result = await authService.register(req.body);
    res.status(201).json(result);
  } catch (error) {
    next(error);
  }
};

export const login = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const validation = validateLoginInput(req.body);
    if (!validation.valid) {
      throw new ApiError(400, validation.message ?? 'Los datos enviados no son válidos');
    }

    const result = await authService.login(req.body);
    res.status(200).json(result);
  } catch (error) {
    next(error);
  }
};

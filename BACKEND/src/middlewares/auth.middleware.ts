// Verifica el JWT Bearer y añade la identidad del propietario a la solicitud.
import { NextFunction, Request, RequestHandler, Response } from 'express';
import * as jwt from 'jsonwebtoken';
import { env } from '../config/env';
import { AuthenticatedRequest } from '../types';
import { ApiError } from '../utils/errors';

export const authenticate: RequestHandler = (req: Request, res: Response, next: NextFunction) => {
  const authReq = req as AuthenticatedRequest;
  const header = authReq.headers.authorization;

  if (!header || !header.startsWith('Bearer ')) {
    return next(new ApiError(401, 'Token de autenticación requerido'));
  }

  const token = header.split(' ')[1];

  try {
    const decoded = jwt.verify(token, env.jwtSecret) as { userId: number; email?: string };
    authReq.user = {
      id: decoded.userId,
      email: decoded.email,
    };
    return next();
  } catch (error) {
    return next(new ApiError(401, 'Token inválido o expirado'));
  }
};

import { NextFunction, Request, Response } from 'express';
import { AuthenticatedRequest } from '../types';
import { routineService } from '../services/routine.service';
import { ApiError } from '../utils/errors';

const getBogotaDate = (): string =>
  new Intl.DateTimeFormat('en-CA', { timeZone: 'America/Bogota' }).format(new Date());

export const listRoutines = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const authReq = req as AuthenticatedRequest;
    const petId = req.query.petId ? Number(req.query.petId) : undefined;
    const routines = await routineService.listByUser(Number(authReq.user?.id), petId);
    res.status(200).json(routines);
  } catch (error) {
    next(error);
  }
};

export const createRoutine = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const authReq = req as AuthenticatedRequest;
    const routine = await routineService.create(Number(authReq.user?.id), {
      ...req.body,
      pet_id: Number(req.body.pet_id),
      days_of_week: req.body.days_of_week ?? req.body.selected_days,
    });
    res.status(201).json(routine);
  } catch (error) {
    next(error);
  }
};

export const getRoutineById = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const authReq = req as AuthenticatedRequest;
    const routine = await routineService.getById(Number(authReq.user?.id), Number(req.params.id));
    res.status(200).json(routine);
  } catch (error) {
    next(error);
  }
};

export const updateRoutine = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const authReq = req as AuthenticatedRequest;
    const routine = await routineService.update(Number(authReq.user?.id), Number(req.params.id), {
      ...req.body,
      ...(req.body.days_of_week === undefined && req.body.selected_days !== undefined
        ? { days_of_week: req.body.selected_days }
        : {}),
    });
    res.status(200).json(routine);
  } catch (error) {
    next(error);
  }
};

export const deleteRoutine = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const authReq = req as AuthenticatedRequest;
    await routineService.remove(Number(authReq.user?.id), Number(req.params.id));
    res.status(204).send();
  } catch (error) {
    next(error);
  }
};

export const getDailyRoutineSummary = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const authReq = req as AuthenticatedRequest;
    const date = typeof req.query.date === 'string'
      ? req.query.date
      : getBogotaDate();
    const petId = req.query.petId ? Number(req.query.petId) : undefined;

    if (petId !== undefined && Number.isNaN(petId)) {
      throw new ApiError(400, 'El identificador de la mascota es inválido');
    }

    const routines = await routineService.getDaily(Number(authReq.user?.id), date, petId);
    res.status(200).json({ date, petId: petId ?? null, routines });
  } catch (error) {
    next(error);
  }
};

export const getRoutineHistory = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const authReq = req as AuthenticatedRequest;
    const today = getBogotaDate();
    const from = typeof req.query.from === 'string' ? req.query.from : today;
    const to = typeof req.query.to === 'string' ? req.query.to : today;
    const petId = req.query.petId ? Number(req.query.petId) : undefined;

    const history = await routineService.getHistory(Number(authReq.user?.id), from, to, petId);
    res.status(200).json({ from, to, petId: petId ?? null, history });
  } catch (error) {
    next(error);
  }
};

export const updateRoutineLog = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const authReq = req as AuthenticatedRequest;
    const payload = {
      ...req.body,
      routine_id: Number(req.params.id ?? req.body.routine_id),
    };

    const log = await routineService.setLogStatus(Number(authReq.user?.id), payload);
    res.status(200).json(log);
  } catch (error) {
    next(error);
  }
};

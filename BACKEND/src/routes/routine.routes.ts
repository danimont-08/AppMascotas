import { Router } from 'express';
import {
  createRoutine,
  deleteRoutine,
  getDailyRoutineSummary,
  getRoutineById,
  getRoutineHistory,
  listRoutines,
  updateRoutine,
  updateRoutineLog,
} from '../controllers/routine.controller';
import { authenticate } from '../middlewares/auth.middleware';

const router = Router();
router.use(authenticate);

router.get('/daily', getDailyRoutineSummary);
router.get('/history', getRoutineHistory);
router.post('/logs', updateRoutineLog);
router.put('/logs/:id', updateRoutineLog);
router.get('/', listRoutines);
router.post('/', createRoutine);
router.get('/:id', getRoutineById);
router.put('/:id', updateRoutine);
router.delete('/:id', deleteRoutine);

export default router;

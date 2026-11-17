jest.mock('../repositories/routine.repository', () => ({
  routineRepository: {
    findByIdAndUser: jest.fn(),
    listDaily: jest.fn(),
    petBelongsToUser: jest.fn(),
    upsertLog: jest.fn(),
  },
}));

import { routineRepository } from '../repositories/routine.repository';
import { routineService } from '../services/routine.service';
import { DailyRoutineRecord, RoutineLogRecord, RoutineRecord } from '../types';

const repositoryMock = jest.mocked(routineRepository);

const dailyRoutine: DailyRoutineRecord = {
  id: 1,
  pet_id: 3,
  name: 'Alimentar',
  category: 'ALIMENTACION',
  description: null,
  scheduled_time: '11:00:00',
  frequency: 'DIARIA',
  days_of_week: null,
  selected_days: null,
  start_date: '2026-11-01',
  end_date: null,
  is_active: true,
  pet_name: 'Luna',
  pet_species: 'GATO',
  log_id: null,
  log_status: null,
  completed_at: null,
  notes: null,
};

const completedLog: RoutineLogRecord = {
  id: 10,
  routine_id: dailyRoutine.id,
  scheduled_date: '2026-11-17',
  status: 'NO_FINALIZADA',
  completed_at: null,
  notes: null,
};

describe('routineService daily status', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    jest.useFakeTimers().setSystemTime(new Date('2026-11-17T17:00:00.000Z'));
  });

  afterEach(() => {
    jest.useRealTimers();
    jest.restoreAllMocks();
  });

  it('records an overdue occurrence as NO_FINALIZADA in routine_logs', async () => {
    repositoryMock.listDaily.mockResolvedValue([dailyRoutine]);
    repositoryMock.upsertLog.mockResolvedValue(completedLog);

    const result = await routineService.getDaily(1, '2026-11-17');

    expect(repositoryMock.upsertLog).toHaveBeenCalledWith(expect.objectContaining({
      routine_id: dailyRoutine.id,
      scheduled_date: '2026-11-17',
      status: 'NO_FINALIZADA',
    }));
    expect(result[0].status).toBe('NO_FINALIZADA');
    expect(result[0].log_id).toBe(completedLog.id);
  });

  it('does not list a weekly routine on a different weekday', async () => {
    const weeklyRoutine: DailyRoutineRecord = {
      ...dailyRoutine,
      id: 2,
      frequency: 'SEMANAL',
      start_date: '2026-11-16',
    };
    repositoryMock.listDaily.mockResolvedValue([weeklyRoutine]);
    repositoryMock.upsertLog.mockResolvedValue(completedLog);

    const result = await routineService.getDaily(1, '2026-11-17');

    expect(result).toHaveLength(0);
    expect(repositoryMock.upsertLog).not.toHaveBeenCalled();
  });

  it('lists only the configured weekdays for a DIAS_ESPECIFICOS routine', async () => {
    const tuesdayRoutine: DailyRoutineRecord = {
      ...dailyRoutine,
      id: 3,
      scheduled_time: '13:00:00',
      frequency: 'DIAS_ESPECIFICOS',
      days_of_week: '[2,4]',
    };
    const mondayRoutine: DailyRoutineRecord = {
      ...tuesdayRoutine,
      id: 4,
      days_of_week: '[1]',
    };
    repositoryMock.listDaily.mockResolvedValue([tuesdayRoutine, mondayRoutine]);
    repositoryMock.upsertLog.mockResolvedValue(completedLog);

    const result = await routineService.getDaily(1, '2026-11-17');

    expect(result.map((routine) => routine.id)).toEqual([tuesdayRoutine.id]);
    expect(result[0].status).toBe('PENDIENTE');
  });

  it('marks a scheduled occurrence complete when requested for today', async () => {
    const routine: RoutineRecord = dailyRoutine;
    repositoryMock.findByIdAndUser.mockResolvedValue(routine);
    repositoryMock.upsertLog.mockResolvedValue({
      ...completedLog,
      status: 'COMPLETADA',
    });

    await routineService.setLogStatus(1, {
      routine_id: routine.id,
      scheduled_date: '2026-11-17',
      status: 'COMPLETADA',
    });

    expect(repositoryMock.upsertLog).toHaveBeenCalledWith(expect.objectContaining({
      routine_id: routine.id,
      scheduled_date: '2026-11-17',
      status: 'COMPLETADA',
    }));
  });
});

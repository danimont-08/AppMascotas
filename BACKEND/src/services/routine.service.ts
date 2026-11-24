import { routineRepository } from '../repositories/routine.repository';
import { DailyRoutineRecord, RoutineLogPayload, RoutinePayload, RoutinePriority, RoutineRecord, RoutineLogRecord } from '../types';
import { ApiError } from '../utils/errors';
import { normalizeRoutineCategory, validateRoutineInput, validateRoutineLogInput } from '../utils/validators';

const allowedFrequencies = new Set(['DIARIA', 'SEMANAL', 'DIAS_ESPECIFICOS', 'UNICA']);
const priorityRank: Record<string, number> = { ALTA: 0, MEDIA: 1, BAJA: 2 };
const weekDays: Record<string, number> = {
  DOMINGO: 0, SUNDAY: 0, DOM: 0, SUN: 0,
  LUNES: 1, MONDAY: 1, LUN: 1, MON: 1,
  MARTES: 2, TUESDAY: 2, MAR: 2, TUE: 2,
  MIERCOLES: 3, MIÉRCOLES: 3, WEDNESDAY: 3, MIE: 3, WED: 3,
  JUEVES: 4, THURSDAY: 4, JUE: 4, THU: 4,
  VIERNES: 5, FRIDAY: 5, VIE: 5, FRI: 5,
  SABADO: 6, SÁBADO: 6, SATURDAY: 6, SAB: 6, SAT: 6,
};

export const routineService = {
  async listByUser(userId: number, petId?: number): Promise<RoutineRecord[]> {
    return routineRepository.listByUser(userId, petId);
  },

  async create(userId: number, payload: RoutinePayload): Promise<RoutineRecord> {
    const validation = validateRoutineInput(payload);
    if (!validation.valid) {
      throw new ApiError(400, validation.message ?? 'La rutina enviada no es válida');
    }

    const category = normalizeRoutineCategory(payload.category);
    if (!category) {
      throw new ApiError(400, 'La categoría de la rutina es inválida');
    }

    if (!allowedFrequencies.has(payload.frequency.trim().toUpperCase())) {
      throw new ApiError(400, 'La frecuencia de la rutina es inválida');
    }

    if (!(await routineRepository.petBelongsToUser(payload.pet_id, userId))) {
      throw new ApiError(404, 'Mascota no encontrada');
    }

    const daysOfWeek = normalizeDaysOfWeek(payload.days_of_week);
    if (payload.frequency.trim().toUpperCase() === 'DIAS_ESPECIFICOS' && !daysOfWeek.length) {
      throw new ApiError(400, 'Debes indicar los días de la semana de la rutina');
    }
    const routine = await routineRepository.create({
      ...payload,
      category,
      frequency: payload.frequency.trim().toUpperCase(),
      days_of_week: daysOfWeek.length ? daysOfWeek : null,
      description: payload.description ?? null,
      priority: normalizePriority(payload.priority) ?? 'MEDIA',
      is_active: payload.is_active ?? true,
    });

    const created = await routineRepository.findByIdAndUser(routine, userId);
    if (!created) {
      throw new ApiError(404, 'La rutina no se pudo crear');
    }

    return created;
  },

  async getById(userId: number, routineId: number): Promise<RoutineRecord> {
    const routine = await routineRepository.findByIdAndUser(routineId, userId);
    if (!routine) {
      throw new ApiError(404, 'Rutina no encontrada');
    }

    return routine;
  },

  async update(userId: number, routineId: number, payload: Partial<RoutinePayload>): Promise<RoutineRecord> {
    if (payload.category !== undefined) {
      const category = normalizeRoutineCategory(payload.category);
      if (!category) {
        throw new ApiError(400, 'La categoría de la rutina es inválida');
      }
      payload.category = category;
    }

    if (payload.priority !== undefined) {
      const priority = normalizePriority(payload.priority);
      if (!priority) {
        throw new ApiError(400, 'La prioridad de la rutina debe ser ALTA, MEDIA o BAJA');
      }
      payload.priority = priority;
    }

    if (payload.frequency !== undefined) {
      const frequency = payload.frequency.trim().toUpperCase();
      if (!allowedFrequencies.has(frequency)) {
        throw new ApiError(400, 'La frecuencia de la rutina es inválida');
      }
      payload.frequency = frequency;
      if (frequency === 'DIAS_ESPECIFICOS' && payload.days_of_week === undefined) {
        throw new ApiError(400, 'Debes indicar los días de la semana de la rutina');
      }
    }
    if (payload.days_of_week !== undefined) {
      const daysOfWeek = normalizeDaysOfWeek(payload.days_of_week);
      if (payload.frequency === 'DIAS_ESPECIFICOS' && !daysOfWeek.length) {
        throw new ApiError(400, 'Debes indicar los días de la semana de la rutina');
      }
      payload.days_of_week = daysOfWeek.length ? daysOfWeek : null;
    }

    const routine = await routineRepository.update(routineId, userId, payload);
    if (!routine) {
      throw new ApiError(404, 'Rutina no encontrada');
    }

    return routine;
  },

  async remove(userId: number, routineId: number): Promise<void> {
    const removed = await routineRepository.remove(routineId, userId);
    if (!removed) {
      throw new ApiError(404, 'Rutina no encontrada');
    }
  },

  async getDaily(userId: number, date: string, petId?: number): Promise<DailyRoutineRecord[]> {
    const routines = (await routineRepository.listDaily(userId, date, petId))
      .filter((routine) => isRoutineScheduledOnDate(routine, date))
      .sort(compareByPriorityThenTime);
    const now = new Date();
    const nowInBogota = getBogotaDateTime(now);

    return Promise.all(routines.map(async (routine) => {
      const { log_status: loggedStatus, ...routineRecord } = routine;
      const overdue = date < nowInBogota.date
        || (date === nowInBogota.date && routine.scheduled_time <= nowInBogota.time);
      let status = loggedStatus ?? (overdue ? 'NO_FINALIZADA' : 'PENDIENTE');
      let logId = routine.log_id;
      let completedAt = routine.completed_at;

      if (overdue && (!loggedStatus || loggedStatus === 'PENDIENTE')) {
        const log = await routineRepository.upsertLog({
          routine_id: routine.id,
          scheduled_date: date,
          status: 'NO_FINALIZADA',
          completed_at: null,
          notes: routine.notes,
        });
        status = log.status;
        logId = log.id;
        completedAt = log.completed_at;
      }

      return {
        ...routineRecord,
        status,
        log_id: logId,
        completed_at: completedAt,
      };
    }));
  },

  async getHistory(userId: number, from: string, to: string, petId?: number): Promise<RoutineLogRecord[]> {
    return routineRepository.listHistory(userId, from, to, petId);
  },

  async setLogStatus(userId: number, payload: RoutineLogPayload): Promise<RoutineLogRecord> {
    const validation = validateRoutineLogInput(payload);
    if (!validation.valid) {
      throw new ApiError(400, validation.message ?? 'El registro enviado no es válido');
    }

    const routine = await routineRepository.findByIdAndUser(payload.routine_id, userId);
    if (!routine) {
      throw new ApiError(404, 'La rutina no existe o no pertenece al usuario autenticado');
    }

    if (!routine.is_active) {
      throw new ApiError(400, 'No se puede registrar cumplimiento de una rutina inactiva');
    }

    payload.status = payload.status.trim().toUpperCase() as RoutineLogPayload['status'];
    if (!isRoutineScheduledOnDate(routine, payload.scheduled_date)) {
      throw new ApiError(400, 'La rutina no está programada para la fecha seleccionada');
    }
    const todayInBogota = getBogotaDateTime(new Date()).date;
    if (payload.status === 'COMPLETADA' && payload.scheduled_date > todayInBogota) {
      throw new ApiError(400, 'No se puede completar una rutina antes de la fecha programada');
    }

    if (payload.status === 'PENDIENTE') {
      const updated = await routineRepository.upsertLog({ ...payload, completed_at: null, notes: payload.notes ?? null });
      return updated;
    }

    const nowInBogota = getBogotaDateTime(new Date());
    const completedAt = payload.completed_at ?? `${nowInBogota.date} ${nowInBogota.time}`;
    const updated = await routineRepository.upsertLog({ ...payload, completed_at: completedAt });

    return updated;
  },
};

function normalizePriority(value: unknown): RoutinePriority | null {
  if (typeof value !== 'string') return null;
  const priority = value.trim().toUpperCase();
  return priority in priorityRank ? (priority as RoutinePriority) : null;
}

// Más prioridad primero; a igual prioridad, la hora más temprana primero.
function compareByPriorityThenTime(a: RoutineRecord, b: RoutineRecord): number {
  const rankA = priorityRank[a.priority] ?? priorityRank.MEDIA;
  const rankB = priorityRank[b.priority] ?? priorityRank.MEDIA;
  if (rankA !== rankB) return rankA - rankB;
  return a.scheduled_time.localeCompare(b.scheduled_time);
}

function isRoutineScheduledOnDate(routine: RoutineRecord, date: string): boolean {
  if (date < routine.start_date || (routine.end_date && date > routine.end_date)) return false;

  const dateWeekday = new Date(`${date}T00:00:00Z`).getUTCDay();
  switch (routine.frequency) {
    case 'DIARIA':
      return true;
    case 'UNICA':
      return date === routine.start_date;
    case 'SEMANAL':
      return dateWeekday === new Date(`${routine.start_date}T00:00:00Z`).getUTCDay();
    case 'DIAS_ESPECIFICOS':
      return normalizeDaysOfWeek(routine.days_of_week).includes(dateWeekday);
    default:
      return false;
  }
}

function normalizeDaysOfWeek(value: unknown): number[] {
  if (value === undefined || value === null || value === '') return [];

  let values: unknown[] = [];
  if (Array.isArray(value)) {
    values = value;
  } else if (typeof value === 'string') {
    try {
      const parsed: unknown = JSON.parse(value);
      values = Array.isArray(parsed) ? parsed : [parsed];
    } catch {
      values = value.split(/[,;|]/).map((day) => day.trim()).filter(Boolean);
    }
  } else {
    throw new ApiError(400, 'Los días de la semana deben ser una lista válida');
  }

  return [...new Set(values.map((day) => {
    if (typeof day === 'number' && Number.isInteger(day) && day >= 0 && day <= 6) return day;
    const normalized = String(day).trim().toUpperCase().normalize('NFD').replace(/[\u0300-\u036f]/g, '');
    if (/^[0-6]$/.test(normalized)) return Number(normalized);
    if (weekDays[normalized] !== undefined) return weekDays[normalized];
    throw new ApiError(400, `Día de la semana inválido: ${String(day)}`);
  }))];
}

function getBogotaDateTime(date: Date): { date: string; time: string } {
  const parts = new Intl.DateTimeFormat('en-CA', {
    timeZone: 'America/Bogota',
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hourCycle: 'h23',
  }).formatToParts(date);
  const values = Object.fromEntries(parts.map(({ type, value }) => [type, value]));
  return {
    date: `${values.year}-${values.month}-${values.day}`,
    time: `${values.hour}:${values.minute}:${values.second}`,
  };
}

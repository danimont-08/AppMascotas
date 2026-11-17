import { query, queryOne } from '../config/db';
import { DailyRoutineRecord, RoutineLogPayload, RoutineLogRecord, RoutinePayload, RoutineRecord } from '../types';

const routineSelect = `SELECT r.*, r.days_of_week AS selected_days FROM routines r JOIN pets p ON p.id = r.pet_id`;

export const routineRepository = {
  async petBelongsToUser(petId: number, userId: number): Promise<boolean> {
    const pet = await queryOne<{ id: number }>('SELECT id FROM pets WHERE id = ? AND user_id = ?', [petId, userId]);
    return pet !== null;
  },

  async listByUser(userId: number, petId?: number): Promise<RoutineRecord[]> {
    const params: unknown[] = [userId];
    let sql = `${routineSelect} WHERE p.user_id = ?`;

    if (petId) {
      sql += ' AND r.pet_id = ?';
      params.push(petId);
    }

    sql += ' ORDER BY r.start_date DESC, r.scheduled_time ASC';
    return query<RoutineRecord>(sql, params);
  },

  async findByIdAndUser(id: number, userId: number): Promise<RoutineRecord | null> {
    return queryOne<RoutineRecord>(`${routineSelect} WHERE r.id = ? AND p.user_id = ?`, [id, userId]);
  },

  async create(payload: RoutinePayload): Promise<number> {
    const result = await query<any>(
      `INSERT INTO routines (pet_id, name, category, description, scheduled_time, frequency, days_of_week, start_date, end_date, is_active)
       VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      [
        payload.pet_id,
        payload.name,
        payload.category,
        payload.description ?? null,
        payload.scheduled_time,
        payload.frequency,
        payload.days_of_week ? JSON.stringify(payload.days_of_week) : null,
        payload.start_date,
        payload.end_date ?? null,
        payload.is_active ?? true,
      ],
    );

    const insertId = (result as any)?.insertId ?? (result as any)?.[0]?.insertId ?? null;
    if (!insertId) {
      throw new Error('MariaDB no devolvió el identificador de la rutina creada');
    }

    return Number(insertId);
  },

  async update(id: number, userId: number, payload: Partial<RoutinePayload>): Promise<RoutineRecord | null> {
    const existing = await this.findByIdAndUser(id, userId);
    if (!existing) return null;

    const updates = [
      payload.name !== undefined ? 'name = ?' : null,
      payload.category !== undefined ? 'category = ?' : null,
      payload.description !== undefined ? 'description = ?' : null,
      payload.scheduled_time !== undefined ? 'scheduled_time = ?' : null,
      payload.frequency !== undefined ? 'frequency = ?' : null,
      payload.days_of_week !== undefined ? 'days_of_week = ?' : null,
      payload.start_date !== undefined ? 'start_date = ?' : null,
      payload.end_date !== undefined ? 'end_date = ?' : null,
      payload.is_active !== undefined ? 'is_active = ?' : null,
    ].filter(Boolean) as string[];

    if (!updates.length) {
      return existing;
    }

    const values: unknown[] = [];
    if (payload.name !== undefined) values.push(payload.name);
    if (payload.category !== undefined) values.push(payload.category);
    if (payload.description !== undefined) values.push(payload.description ?? null);
    if (payload.scheduled_time !== undefined) values.push(payload.scheduled_time);
    if (payload.frequency !== undefined) values.push(payload.frequency);
    if (payload.days_of_week !== undefined) values.push(payload.days_of_week ? JSON.stringify(payload.days_of_week) : null);
    if (payload.start_date !== undefined) values.push(payload.start_date);
    if (payload.end_date !== undefined) values.push(payload.end_date ?? null);
    if (payload.is_active !== undefined) values.push(payload.is_active ? 1 : 0);
    values.push(id, userId);

    await query(`UPDATE routines r JOIN pets p ON p.id = r.pet_id SET ${updates.join(', ')} WHERE r.id = ? AND p.user_id = ?`, values);

    return this.findByIdAndUser(id, userId);
  },

  async remove(id: number, userId: number): Promise<boolean> {
    const result = await query<any>(
      'DELETE r FROM routines r JOIN pets p ON p.id = r.pet_id WHERE r.id = ? AND p.user_id = ?',
      [id, userId],
    );

    const affectedRows = (result as any)?.affectedRows ?? (result as any)?.[0]?.affectedRows ?? 0;
    return Number(affectedRows) > 0;
  },

  async listDaily(userId: number, date: string, petId?: number): Promise<DailyRoutineRecord[]> {
    const params: unknown[] = [date, userId, date, date];
    let sql = `SELECT r.*, r.days_of_week AS selected_days, p.name AS pet_name, p.species AS pet_species,
                      rl.id AS log_id, rl.status AS log_status, rl.completed_at, rl.notes
               FROM routines r
               JOIN pets p ON p.id = r.pet_id
               LEFT JOIN routine_logs rl ON rl.routine_id = r.id AND rl.scheduled_date = ?
               WHERE p.user_id = ? AND r.is_active = TRUE AND r.start_date <= ? AND (r.end_date IS NULL OR r.end_date >= ?)`;

    if (petId) {
      sql += ' AND r.pet_id = ?';
      params.push(petId);
    }

    sql += ' ORDER BY r.pet_id, r.scheduled_time ASC';
    return query<DailyRoutineRecord>(sql, params);
  },

  async listHistory(userId: number, from: string, to: string, petId?: number): Promise<RoutineLogRecord[]> {
    const params: unknown[] = [userId, from, to];
    let sql = `SELECT rl.* FROM routine_logs rl JOIN routines r ON r.id = rl.routine_id JOIN pets p ON p.id = r.pet_id WHERE p.user_id = ? AND rl.scheduled_date >= ? AND rl.scheduled_date <= ?`;

    if (petId) {
      sql += ' AND r.pet_id = ?';
      params.push(petId);
    }

    sql += ' ORDER BY rl.scheduled_date DESC';
    return query<RoutineLogRecord>(sql, params);
  },

  async ensureLog(routineId: number): Promise<RoutineLogRecord | null> {
    return queryOne<RoutineLogRecord>('SELECT * FROM routine_logs WHERE routine_id = ? ORDER BY id DESC LIMIT 1', [routineId]);
  },

  async upsertLog(payload: RoutineLogPayload): Promise<RoutineLogRecord> {
    await query(
      `INSERT INTO routine_logs (routine_id, scheduled_date, status, completed_at, notes)
       VALUES (?, ?, ?, ?, ?)
       ON DUPLICATE KEY UPDATE
         status = IF(status = 'COMPLETADA' AND VALUES(status) = 'NO_FINALIZADA', status, VALUES(status)),
         completed_at = IF(status = 'COMPLETADA' AND VALUES(status) = 'NO_FINALIZADA', completed_at, VALUES(completed_at)),
         notes = IF(status = 'COMPLETADA' AND VALUES(status) = 'NO_FINALIZADA', notes, VALUES(notes))`,
      [payload.routine_id, payload.scheduled_date, payload.status, payload.completed_at ?? null, payload.notes ?? null],
    );

    const log = await queryOne<RoutineLogRecord>(
      'SELECT * FROM routine_logs WHERE routine_id = ? AND scheduled_date = ?',
      [payload.routine_id, payload.scheduled_date],
    );
    if (!log) {
      throw new Error('No se pudo obtener el registro de rutina actualizado');
    }

    return log;
  },

  async findLogByIdAndUser(logId: number, userId: number): Promise<RoutineLogRecord | null> {
    return queryOne<RoutineLogRecord>(
      `SELECT rl.* FROM routine_logs rl JOIN routines r ON r.id = rl.routine_id JOIN pets p ON p.id = r.pet_id WHERE rl.id = ? AND p.user_id = ?`,
      [logId, userId],
    );
  },
};

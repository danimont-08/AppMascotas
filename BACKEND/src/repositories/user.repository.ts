// Ejecuta consultas parametrizadas de usuarios mediante el pool de MariaDB.
import { query, queryOne } from '../config/db';
import { UserRecord } from '../types';

export const userRepository = {
  async create({ name, email, passwordHash }: { name: string; email: string; passwordHash: string }) {
    const result = await query<any>(
      'INSERT INTO users (name, email, password_hash) VALUES (?, ?, ?)',
      [name, email, passwordHash],
    );

    // En el driver de MariaDB para Node.js, result es un OkPacket con la propiedad insertId
    const insertId = (result as any)?.insertId ?? (result as any)?.[0]?.insertId ?? null;

    return insertId ? Number(insertId) : null;
  },

  async findByEmail(email: string): Promise<UserRecord | null> {
    const rows = await query<UserRecord>('SELECT * FROM users WHERE email = ?', [email]);
    return rows[0] ?? null;
  },

  async findById(id: number): Promise<UserRecord | null> {
    const row = await queryOne<UserRecord>('SELECT * FROM users WHERE id = ?', [id]);
    return row;
  },

  async findProfileById(id: number): Promise<Pick<UserRecord, 'id' | 'name' | 'email'> | null> {
    return queryOne<Pick<UserRecord, 'id' | 'name' | 'email'>>(
      'SELECT id, name, email FROM users WHERE id = ?',
      [id],
    );
  },

  async update(id: number, { name, email }: { name: string; email: string }): Promise<boolean> {
    await query(
      'UPDATE users SET name = ?, email = ? WHERE id = ?',
      [name, email, id],
    );
    return true;
  },
};

// Mantiene el pool de MariaDB y ofrece consultas parametrizadas para los repositorios.
import mariadb, { Pool } from 'mariadb';
import { env } from './env';

export const pool: Pool = mariadb.createPool({
  host: env.dbHost,
  port: env.dbPort,
  user: env.dbUser,
  password: env.dbPassword,
  database: env.dbName,
  connectionLimit: 10,
  acquireTimeout: 30000,
  connectTimeout: 30000,
  timezone: 'Z',
  // Devuelve DATE como 'AAAA-MM-DD' (y DATETIME como texto) en lugar de objetos Date con hora UTC.
  dateStrings: true,
  multipleStatements: false,
});

export async function query<T = Record<string, unknown>>(sql: string, params: unknown[] = []): Promise<T[]> {
  const connection = await pool.getConnection();

  try {
    return (await connection.query(sql, params)) as T[];
  } finally {
    connection.release();
  }
}

export async function queryOne<T = Record<string, unknown>>(sql: string, params: unknown[] = []): Promise<T | null> {
  const rows = await query<T>(sql, params);
  return rows[0] ?? null;
}

export async function testDatabaseConnection(): Promise<{ status: 'ok' | 'error'; message: string }> {
  let connection: Awaited<ReturnType<typeof pool.getConnection>> | undefined;

  try {
    connection = await pool.getConnection();
    await connection.ping();
    return {
      status: 'ok',
      message: 'MariaDB conectada correctamente.',
    };
  } catch (error) {
    const message = error instanceof Error ? error.message : 'No se pudo conectar a MariaDB.';
    return {
      status: 'error',
      message,
    };
  } finally {
    connection?.release();
  }
}

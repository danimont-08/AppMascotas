// Carga y normaliza la configuración de entorno usada por el servidor y los servicios.
import dotenv from 'dotenv';

dotenv.config();

const getNumber = (value: string | undefined, fallback: number): number => {
  const parsed = Number(value ?? fallback);
  return Number.isFinite(parsed) ? parsed : fallback;
};

export const env = {
  port: getNumber(process.env.PORT, 3000),
  dbHost: process.env.DB_HOST ?? 'localhost',
  dbPort: getNumber(process.env.DB_PORT, 3306),
  dbName: process.env.DB_NAME ?? 'huellario_db',
  dbUser: process.env.DB_USER ?? 'huellario_app',
  dbPassword: process.env.DB_PASSWORD ?? '',
  jwtSecret: process.env.JWT_SECRET ?? 'development-secret-change-me',
  jwtExpiresIn: process.env.JWT_EXPIRES_IN ?? '1h',
  uploadDir: process.env.UPLOAD_DIR ?? 'uploads',
  appBaseUrl: process.env.APP_BASE_URL ?? 'http://localhost:3000',
};

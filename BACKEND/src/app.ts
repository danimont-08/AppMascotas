// Configura Express, las rutas de la API, Swagger y el manejo centralizado de errores.
import express from 'express';
import cors from 'cors';
import swaggerUi from 'swagger-ui-express';
import path from 'node:path';
import authRoutes from './routes/auth.routes';
import petRoutes from './routes/pets.routes';
import routineRoutes from './routes/routine.routes';
import userRoutes from './routes/user.routes';
import { testDatabaseConnection } from './config/db';
import { swaggerSpec } from './docs/swagger';
import { ApiError } from './utils/errors';
import { uploadDirectory } from './config/upload';

const app = express();

// CORS abierto para permitir conexiones desde celulares físicos (hotspot / Wi-Fi local)
app.use(cors({ origin: true, credentials: true }));
app.use(express.json());
app.use('/uploads', express.static(path.resolve(uploadDirectory)));

/**
 * @openapi
 * /api/health:
 *   get:
 *     tags: [Health]
 *     summary: Comprueba el estado del servidor y la base de datos
 *     responses:
 *       200:
 *         description: Servidor y base de datos disponibles
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/HealthResponse'
 *       503:
 *         description: Servidor disponible, pero base de datos no disponible
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/HealthResponse'
 */
app.get('/api/health', async (req, res) => {
  const result = await testDatabaseConnection();

  if (result.status === 'ok') {
    return res.status(200).json({
      status: 'ok',
      message: 'Servidor funcionando correctamente.',
      database: 'ok',
    });
  }

  return res.status(503).json({
    status: 'error',
    message: 'Servidor disponible, pero la base de datos no responde.',
    database: 'error',
    details: result.message,
  });
});

app.use('/api/auth', authRoutes);
app.use('/api/pets', petRoutes);
app.use('/api/routines', routineRoutes);
app.use('/api/users', userRoutes);
app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(swaggerSpec));

app.use((err: unknown, req: express.Request, res: express.Response, next: express.NextFunction) => {
  if (err instanceof ApiError) {
    return res.status(err.statusCode).json({ message: err.message });
  }

  if (err instanceof Error) {
    return res.status(500).json({ message: 'Error interno del servidor' });
  }

  return res.status(500).json({ message: 'Error interno del servidor' });
});

export default app;

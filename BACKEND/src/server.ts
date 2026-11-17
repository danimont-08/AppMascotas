// Punto de entrada del backend: inicia Express y gestiona el cierre del servidor.
import app from './app';
import { env } from './config/env';

const port = env.port;

const server = app.listen(port, '0.0.0.0', () => {
  console.log(`Servidor Huellario disponible en http://localhost:${port} y en la red local`);
});

const shutdown = () => {
  server.close(() => {
    console.log('Servidor cerrado correctamente.');
    process.exit(0);
  });
};

process.on('SIGINT', shutdown);
process.on('SIGTERM', shutdown);

export default server;

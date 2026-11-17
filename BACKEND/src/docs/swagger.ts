// Define OpenAPI para documentar las rutas y contratos HTTP de Huellario.
import swaggerJSDoc from 'swagger-jsdoc';
import path from 'path';

const options: swaggerJSDoc.Options = {
  definition: {
    openapi: '3.0.0',
    info: {
      title: 'Huellario API',
      version: '1.0.0',
      description: 'API REST de Huellario para auth y gestión de mascotas.',
    },
    servers: [
      {
        url: 'http://localhost:3000',
        description: 'Servidor local de desarrollo',
      },
    ],
    components: {
      securitySchemes: {
        bearerAuth: {
          type: 'http',
          scheme: 'bearer',
          bearerFormat: 'JWT',
        },
      },
      schemas: {
        HealthResponse: {
          type: 'object',
          properties: {
            status: { type: 'string', enum: ['ok', 'error'] },
            message: { type: 'string' },
            database: { type: 'string', enum: ['ok', 'error'] },
            details: { type: 'string' },
          },
          required: ['status', 'message', 'database'],
        },
        ErrorResponse: {
          type: 'object',
          properties: {
            message: { type: 'string', example: 'Los datos enviados no son válidos' },
          },
          required: ['message'],
        },
        AuthResponse: {
          type: 'object',
          properties: {
            token: { type: 'string', example: 'jwt_token' },
            message: { type: 'string', example: 'Operación realizada correctamente' },
            user: {
              $ref: '#/components/schemas/SafeUser',
            },
          },
        },
        SafeUser: {
          type: 'object',
          properties: {
            id: { type: 'integer', example: 1 },
            fullName: { type: 'string', example: 'Nombre del propietario' },
            email: { type: 'string', format: 'email', example: 'usuario@ejemplo.com' },
          },
          required: ['id', 'fullName', 'email'],
        },
        UserProfileResponse: {
          type: 'object',
          properties: {
            user: { $ref: '#/components/schemas/SafeUser' },
          },
          required: ['user'],
        },
        UpdateProfileRequest: {
          type: 'object',
          required: ['fullName', 'email'],
          properties: {
            fullName: { type: 'string', minLength: 2, maxLength: 100, example: 'Nombre del propietario' },
            email: { type: 'string', format: 'email', maxLength: 150, example: 'usuario@ejemplo.com' },
          },
        },
        UpdateProfileResponse: {
          type: 'object',
          properties: {
            message: { type: 'string', example: 'Perfil actualizado correctamente' },
            user: { $ref: '#/components/schemas/SafeUser' },
          },
          required: ['message', 'user'],
        },
        RegisterRequest: {
          type: 'object',
          required: ['fullName', 'email', 'password'],
          properties: {
            fullName: { type: 'string', minLength: 2, maxLength: 100, example: 'Nombre del propietario' },
            email: { type: 'string', format: 'email', example: 'usuario@ejemplo.com' },
            password: { type: 'string', minLength: 6, example: 'contraseña123' },
          },
        },
        LoginRequest: {
          type: 'object',
          required: ['email', 'password'],
          properties: {
            email: { type: 'string', format: 'email', example: 'usuario@ejemplo.com' },
            password: { type: 'string', minLength: 6, example: 'contraseña123' },
          },
        },
        PetRequest: {
          type: 'object',
          required: ['name', 'species'],
          properties: {
            name: { type: 'string', minLength: 2, maxLength: 80, example: 'Luna' },
            species: { type: 'string', enum: ['PERRO', 'GATO', 'AVE', 'HAMSTER', 'PEZ', 'OTRO'], example: 'PERRO' },
            species_detail: { type: 'string', nullable: true, maxLength: 80, example: 'Golden Retriever' },
            breed: { type: 'string', nullable: true, example: 'Labrador' },
            birth_date: { type: 'string', nullable: true, example: '2023-05-10' },
            photo_url: { type: 'string', nullable: true, example: 'https://ejemplo.com/luna.jpg' },
          },
        },
        Pet: {
          type: 'object',
          properties: {
            id: { type: 'integer', example: 1 },
            user_id: { type: 'integer', example: 1 },
            name: { type: 'string', example: 'Luna' },
            species: { type: 'string', enum: ['PERRO', 'GATO', 'AVE', 'HAMSTER', 'PEZ', 'OTRO'], example: 'PERRO' },
            species_detail: { type: 'string', nullable: true, example: 'Golden Retriever' },
            breed: { type: 'string', nullable: true, example: 'Labrador' },
            birth_date: { type: 'string', nullable: true, example: '2023-05-10' },
            photo_url: { type: 'string', nullable: true, example: null },
            created_at: { type: 'string', example: '2026-10-26T12:00:00.000Z' },
          },
        },
      },
    },
  },
  apis: [
    path.join(__dirname, '../app.ts').replace(/\\/g, '/'),
    path.join(__dirname, '../app.js').replace(/\\/g, '/'),
    path.join(__dirname, '../routes/*.ts').replace(/\\/g, '/'),
    path.join(__dirname, '../routes/*.js').replace(/\\/g, '/'),
  ],
};

export const swaggerSpec = swaggerJSDoc(options);

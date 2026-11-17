// Verifica que OpenAPI documente todas las operaciones reales y su seguridad.
import { swaggerSpec } from '../docs/swagger';

type OpenApiOperation = {
  security?: Array<Record<string, unknown>>;
  parameters?: Array<Record<string, unknown>>;
};

type OpenApiDocument = {
  paths?: Record<string, Partial<Record<'get' | 'post' | 'put' | 'delete', OpenApiOperation>>>;
};

const document = swaggerSpec as OpenApiDocument;

describe('OpenAPI specification', () => {
  it('documents exactly the implemented API operations', () => {
    expect(Object.keys(document.paths ?? {}).sort()).toEqual([
      '/api/auth/login',
      '/api/auth/register',
      '/api/health',
      '/api/pets',
      '/api/pets/{id}',
      '/api/users/me',
    ]);

    expect(Object.keys(document.paths?.['/api/auth/register'] ?? {})).toContain('post');
    expect(Object.keys(document.paths?.['/api/auth/login'] ?? {})).toContain('post');
    expect(Object.keys(document.paths?.['/api/health'] ?? {})).toContain('get');
    expect(Object.keys(document.paths?.['/api/pets'] ?? {}).sort()).toEqual(['get', 'post']);
    expect(Object.keys(document.paths?.['/api/pets/{id}'] ?? {}).sort()).toEqual([
      'delete',
      'get',
      'put',
    ]);
    expect(Object.keys(document.paths?.['/api/users/me'] ?? {}).sort()).toEqual(['get', 'put']);
  });

  it('documents Bearer JWT security and pet id parameters', () => {
    const securedOperations = [
      document.paths?.['/api/pets']?.get,
      document.paths?.['/api/pets']?.post,
      document.paths?.['/api/pets/{id}']?.get,
      document.paths?.['/api/pets/{id}']?.put,
      document.paths?.['/api/pets/{id}']?.delete,
      document.paths?.['/api/users/me']?.get,
      document.paths?.['/api/users/me']?.put,
    ];

    for (const operation of securedOperations) {
      expect(operation?.security).toEqual([{ bearerAuth: [] }]);
    }

    for (const operation of [
      document.paths?.['/api/pets/{id}']?.get,
      document.paths?.['/api/pets/{id}']?.put,
      document.paths?.['/api/pets/{id}']?.delete,
    ]) {
      expect(operation?.parameters).toEqual(
        expect.arrayContaining([
          expect.objectContaining({ in: 'path', name: 'id', required: true }),
        ]),
      );
    }
  });
});
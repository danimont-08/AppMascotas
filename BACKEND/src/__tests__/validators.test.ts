// Comprueba validaciones básicas de autenticación y datos de mascotas.
import {
  validateLoginInput,
  validatePetInput,
  validateRegisterInput,
  validateRoutineInput,
  validateUpdateProfileInput,
} from '../utils/validators';

describe('validators', () => {
  it('accepts a valid registration payload', () => {
    const result = validateRegisterInput({
      fullName: 'María López',
      email: 'maria@ejemplo.com',
      password: 'secreto123',
    });

    expect(result.valid).toBe(true);
  });

  it('rejects invalid email format', () => {
    const result = validateLoginInput({
      email: 'correo-no-valido',
      password: 'secreto123',
    });

    expect(result.valid).toBe(false);
    expect(result.message).toMatch('correo');
  });

  it('rejects invalid pet species', () => {
    const result = validatePetInput({
      name: 'Luna',
      species: 'INVALIDA',
    });

    expect(result.valid).toBe(false);
    expect(result.message).toMatch('PERRO');
  });

  it.each(['HAMSTER', 'PEZ'])('accepts the %s species', (species) => {
    expect(validatePetInput({ name: 'Luna', species }).valid).toBe(true);
  });

  it('accepts routine input using the Android selected_days field', () => {
    expect(validateRoutineInput({
      pet_id: 1,
      name: 'Paseo',
      category: 'EJERCICIO',
      scheduled_time: '13:00',
      frequency: 'DIAS_ESPECIFICOS',
      selected_days: ['LUNES', 'MIERCOLES'],
      start_date: '2026-11-16',
    }).valid).toBe(true);
  });

  it('accepts a valid profile update', () => {
    expect(validateUpdateProfileInput({
      fullName: 'María López',
      email: 'maria@ejemplo.com',
    }).valid).toBe(true);
  });

  it.each([
    { fullName: 'A', email: 'maria@ejemplo.com' },
    { fullName: 'María López', email: 'correo-inválido' },
    { fullName: 'x'.repeat(101), email: 'maria@ejemplo.com' },
    { fullName: 'María López', email: `${'x'.repeat(141)}@example.com` },
  ])('rejects invalid profile data', (input) => {
    expect(validateUpdateProfileInput(input).valid).toBe(false);
  });
});

// Valida y normaliza los datos de entrada antes de ejecutar operaciones de negocio.
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

export const isValidEmail = (value: unknown): value is string =>
  typeof value === 'string' && emailRegex.test(value.trim());

export const validateRegisterInput = (input: unknown): { valid: boolean; message?: string } => {
  if (!input || typeof input !== 'object') {
    return { valid: false, message: 'Los datos enviados no son válidos' };
  }

  const body = input as Record<string, unknown>;
  const fullName = typeof body.fullName === 'string' ? body.fullName.trim() : '';
  const email = typeof body.email === 'string' ? body.email.trim() : '';
  const password = typeof body.password === 'string' ? body.password : '';

  if (!fullName || fullName.length < 2 || fullName.length > 100) {
    return { valid: false, message: 'El nombre completo debe tener entre 2 y 100 caracteres' };
  }

  if (!isValidEmail(email)) {
    return { valid: false, message: 'El correo electrónico no es válido' };
  }

  if (!password || password.length < 6) {
    return { valid: false, message: 'La contraseña debe tener al menos 6 caracteres' };
  }

  return { valid: true };
};

export const validateLoginInput = (input: unknown): { valid: boolean; message?: string } => {
  if (!input || typeof input !== 'object') {
    return { valid: false, message: 'Los datos enviados no son válidos' };
  }

  const body = input as Record<string, unknown>;
  const email = typeof body.email === 'string' ? body.email.trim() : '';
  const password = typeof body.password === 'string' ? body.password : '';

  if (!isValidEmail(email)) {
    return { valid: false, message: 'El correo electrónico no es válido' };
  }

  if (!password || password.length < 6) {
    return { valid: false, message: 'La contraseña debe tener al menos 6 caracteres' };
  }

  return { valid: true };
};

export const validateUpdateProfileInput = (input: unknown): { valid: boolean; message?: string } => {
  if (!input || typeof input !== 'object') {
    return { valid: false, message: 'Los datos enviados no son válidos' };
  }

  const body = input as Record<string, unknown>;
  const fullName = typeof body.fullName === 'string' ? body.fullName.trim() : '';
  const email = typeof body.email === 'string' ? body.email.trim() : '';

  if (fullName.length < 2 || fullName.length > 100) {
    return { valid: false, message: 'El nombre completo debe tener entre 2 y 100 caracteres' };
  }

  if (!isValidEmail(email) || email.length > 150) {
    return { valid: false, message: 'El correo electrónico no es válido' };
  }

  return { valid: true };
};

export const normalizeSpecies = (species: unknown): 'PERRO' | 'GATO' | 'AVE' | 'HAMSTER' | 'PEZ' | 'OTRO' | null => {
  const value = typeof species === 'string' ? species.trim().toUpperCase() : '';

  if (value.startsWith('PERRO')) return 'PERRO';
  if (value.startsWith('GATO')) return 'GATO';
  if (value.startsWith('AVE')) return 'AVE';
  if (value.startsWith('HAMSTER')) return 'HAMSTER';
  if (value.startsWith('PEZ')) return 'PEZ';
  if (value.startsWith('OTRO')) return 'OTRO';

  return null;
};

export const validatePetInput = (input: unknown): { valid: boolean; message?: string } => {
  if (!input || typeof input !== 'object') {
    return { valid: false, message: 'Los datos enviados no son válidos' };
  }

  const body = input as Record<string, unknown>;
  const name = typeof body.name === 'string' ? body.name.trim() : '';
  const species = normalizeSpecies(body.species);

  if (!name || name.length < 2 || name.length > 80) {
    return { valid: false, message: 'El nombre de la mascota debe tener entre 2 y 80 caracteres' };
  }

  if (!species) {
    return { valid: false, message: 'La especie debe ser PERRO, GATO, AVE, HAMSTER, PEZ u OTRO' };
  }

  if (body.species_detail !== undefined && body.species_detail !== null) {
    if (typeof body.species_detail !== 'string') {
      return { valid: false, message: 'El detalle de especie debe ser texto o nulo' };
    }

    if (body.species_detail.trim().length > 80) {
      return { valid: false, message: 'El detalle de especie debe tener máximo 80 caracteres' };
    }
  }

  if (body.breed !== undefined && body.breed !== null && typeof body.breed !== 'string') {
    return { valid: false, message: 'La raza debe ser texto o nula' };
  }

  if (body.breed !== undefined && body.breed !== null && body.breed.trim().length > 80) {
    return { valid: false, message: 'La raza debe tener máximo 80 caracteres' };
  }

  if (body.birth_date !== undefined && body.birth_date !== null && body.birth_date !== '') {
    if (typeof body.birth_date !== 'string') {
      return { valid: false, message: 'La fecha de nacimiento debe ser texto o nula' };
    }
    const cleanDate = body.birth_date.trim().replace(/\s+/g, '-').replace(/\//g, '-');
    if (!/^\d{4}-\d{2}-\d{2}$/.test(cleanDate)) {
      return { valid: false, message: 'La fecha de nacimiento debe tener el formato AAAA-MM-DD (ejemplo: 2025-06-13)' };
    }
  }

  if (body.photo_url !== undefined && body.photo_url !== null && typeof body.photo_url !== 'string') {
    return { valid: false, message: 'La URL de la foto debe ser texto o nula' };
  }

  return { valid: true };
};

export const validateRoutineInput = (input: unknown): { valid: boolean; message?: string } => {
  if (!input || typeof input !== 'object') {
    return { valid: false, message: 'Los datos enviados no son válidos' };
  }

  const body = input as Record<string, unknown>;

  const petId = Number(body.pet_id);
  const name = typeof body.name === 'string' ? body.name.trim() : '';
  const category = typeof body.category === 'string' ? body.category.trim().toUpperCase() : '';
  const frequency = typeof body.frequency === 'string' ? body.frequency.trim().toUpperCase() : '';
  const scheduledTime = typeof body.scheduled_time === 'string' ? body.scheduled_time.trim() : '';
  const startDate = typeof body.start_date === 'string' ? body.start_date.trim() : '';
  const daysOfWeek = body.days_of_week ?? body.selected_days;

  if (!Number.isInteger(petId) || petId <= 0) {
    return { valid: false, message: 'El identificador de la mascota es inválido' };
  }

  if (!name || name.length < 2 || name.length > 100) {
    return { valid: false, message: 'El nombre de la rutina debe tener entre 2 y 100 caracteres' };
  }

  if (!['ALIMENTACION', 'AGUA', 'HIGIENE', 'EJERCICIO', 'MEDICACION', 'LIMPIEZA', 'SOCIALIZACION', 'OTRO'].includes(category)) {
    return { valid: false, message: 'La categoría de la rutina es inválida' };
  }

  if (!['DIARIA', 'SEMANAL', 'DIAS_ESPECIFICOS', 'UNICA'].includes(frequency)) {
    return { valid: false, message: 'La frecuencia de la rutina es inválida' };
  }

  if (!/^(?:[01]\d|2[0-3]):[0-5]\d(?::[0-5]\d)?$/.test(scheduledTime)) {
    return { valid: false, message: 'La hora programada debe tener formato HH:MM' };
  }

  if (!isValidCalendarDate(startDate)) {
    return { valid: false, message: 'La fecha de inicio debe tener formato YYYY-MM-DD' };
  }

  if (body.end_date !== undefined && body.end_date !== null && typeof body.end_date === 'string' && body.end_date.trim().length > 0) {
    const endDate = body.end_date.trim();
    if (!isValidCalendarDate(endDate) || endDate < startDate) {
      return { valid: false, message: 'La fecha fin debe ser válida y no anterior a la fecha de inicio' };
    }
  }

  if (frequency === 'DIAS_ESPECIFICOS' && (daysOfWeek === undefined || daysOfWeek === null || daysOfWeek === '')) {
    return { valid: false, message: 'Debes indicar los días de la semana de la rutina' };
  }

  return { valid: true };
};

export const validateRoutineLogInput = (input: unknown): { valid: boolean; message?: string } => {
  if (!input || typeof input !== 'object') {
    return { valid: false, message: 'Los datos enviados no son válidos' };
  }

  const body = input as Record<string, unknown>;
  const routineId = Number(body.routine_id);
  const scheduledDate = typeof body.scheduled_date === 'string' ? body.scheduled_date.trim() : '';
  const status = typeof body.status === 'string' ? body.status.trim().toUpperCase() : '';

  if (!Number.isInteger(routineId) || routineId <= 0) {
    return { valid: false, message: 'El identificador de la rutina es inválido' };
  }

  if (!/^\d{4}-\d{2}-\d{2}$/.test(scheduledDate)) {
    return { valid: false, message: 'La fecha programada debe tener formato YYYY-MM-DD' };
  }

  if (!['PENDIENTE', 'COMPLETADA', 'OMITIDA'].includes(status)) {
    return { valid: false, message: 'El estado puede ser PENDIENTE, COMPLETADA u OMITIDA; NO_FINALIZADA se asigna automáticamente' };
  }

  return { valid: true };
};

const isValidCalendarDate = (value: string): boolean => {
  if (!/^\d{4}-\d{2}-\d{2}$/.test(value)) return false;
  const date = new Date(`${value}T00:00:00Z`);
  return !Number.isNaN(date.getTime()) && date.toISOString().slice(0, 10) === value;
};

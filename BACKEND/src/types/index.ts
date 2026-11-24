// Comparte los tipos de dominio, payloads y solicitudes autenticadas del backend.
import { Request } from 'express';

export interface SafeUser {
  id: number;
  fullName: string;
  email: string;
}

export interface UserRecord {
  id: number;
  name: string;
  email: string;
  password_hash: string;
  created_at?: string;
}

export type PetSpecies = 'PERRO' | 'GATO' | 'AVE' | 'HAMSTER' | 'PEZ' | 'OTRO';

export interface PetRecord {
  id: number;
  user_id: number;
  name: string;
  species: PetSpecies;
  species_detail: string | null;
  breed: string | null;
  birth_date: string | null;
  photo_url: string | null;
  created_at?: string;
}

export interface RegisterPayload {
  fullName: string;
  email: string;
  password: string;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface PetPayload {
  name: string;
  species: string;
  species_detail?: string | null;
  breed?: string | null;
  birth_date?: string | null;
  photo_url?: string | null;
}

export type RoutineFrequency = 'DIARIA' | 'SEMANAL' | 'DIAS_ESPECIFICOS' | 'UNICA';
export type RoutineCategory =
  | 'ALIMENTACION'
  | 'AGUA'
  | 'HIGIENE'
  | 'EJERCICIO'
  | 'MEDICACION'
  | 'LIMPIEZA'
  | 'SOCIALIZACION'
  | 'OTRO';
export type RoutinePriority = 'ALTA' | 'MEDIA' | 'BAJA';
export type RoutineStatus = 'PENDIENTE' | 'COMPLETADA' | 'NO_FINALIZADA' | 'OMITIDA';

export interface RoutineRecord {
  id: number;
  pet_id: number;
  name: string;
  category: RoutineCategory;
  description: string | null;
  priority: RoutinePriority;
  scheduled_time: string;
  frequency: RoutineFrequency;
  days_of_week: string | null;
  selected_days?: string[] | string | null;
  start_date: string;
  end_date: string | null;
  is_active: boolean;
  created_at?: string;
  updated_at?: string;
}

export interface DailyRoutineRecord extends RoutineRecord {
  pet_name: string;
  pet_species: PetSpecies;
  status?: RoutineStatus;
  log_status?: RoutineStatus | null;
  log_id: number | null;
  completed_at: string | null;
  notes: string | null;
}

export interface RoutinePayload {
  pet_id: number;
  name: string;
  category: string;
  description?: string | null;
  priority?: string;
  scheduled_time: string;
  frequency: string;
  days_of_week?: unknown;
  start_date: string;
  end_date?: string | null;
  is_active?: boolean;
}

export interface RoutineLogRecord {
  id: number;
  routine_id: number;
  scheduled_date: string;
  status: RoutineStatus;
  completed_at: string | null;
  notes: string | null;
  created_at?: string;
}

export interface RoutineLogPayload {
  routine_id: number;
  scheduled_date: string;
  status: RoutineStatus;
  notes?: string | null;
  completed_at?: string | null;
}

export interface AuthenticatedRequest extends Request {
  user?: {
    id: number;
    email?: string;
  };
  file?: Express.Multer.File;
  files?: Express.Multer.File[];
}

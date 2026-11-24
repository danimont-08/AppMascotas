// Aplica las reglas de negocio y autorización de propietario para las mascotas.
import { petRepository } from '../repositories/pet.repository';
import { PetPayload, PetRecord } from '../types';
import { ApiError } from '../utils/errors';
import { normalizeSpecies, parsePetWeight } from '../utils/validators';

export const petService = {
  async listByUser(userId: number): Promise<PetRecord[]> {
    return petRepository.findByUser(userId);
  },

  async create(userId: number, payload: PetPayload): Promise<PetRecord> {
    const species = normalizeSpecies(payload.species);
    if (!species) {
      throw new ApiError(400, 'La especie debe ser PERRO, GATO, AVE, HAMSTER, PEZ u OTRO');
    }

    let cleanBirthDate: string | null = null;
    if (payload.birth_date && typeof payload.birth_date === 'string' && payload.birth_date.trim().length > 0) {
      cleanBirthDate = payload.birth_date.trim().replace(/\s+/g, '-').replace(/\//g, '-');
    }

    const createdId = await petRepository.create(userId, {
      ...payload,
      species,
      birth_date: cleanBirthDate,
      species_detail: payload.species_detail ?? null,
      weight_kg: parsePetWeight(payload.weight_kg),
    });

    const pet = await petRepository.findByIdAndUser(createdId, userId);
    if (!pet) {
      throw new ApiError(500, 'No se pudo obtener la mascota creada');
    }

    return pet;
  },

  async getById(userId: number, petId: number): Promise<PetRecord> {
    const pet = await petRepository.findByIdAndUser(petId, userId);
    if (!pet) {
      throw new ApiError(404, 'Mascota no encontrada');
    }
    return pet;
  },

  async update(userId: number, petId: number, payload: Partial<PetPayload>): Promise<PetRecord> {
    if (payload.species !== undefined) {
      const species = normalizeSpecies(payload.species);
      if (!species) {
        throw new ApiError(400, 'La especie debe ser PERRO, GATO, AVE, HAMSTER, PEZ u OTRO');
      }
      payload.species = species;
    }

    if (payload.birth_date !== undefined) {
      if (payload.birth_date && typeof payload.birth_date === 'string' && payload.birth_date.trim().length > 0) {
        payload.birth_date = payload.birth_date.trim().replace(/\s+/g, '-').replace(/\//g, '-');
      } else {
        payload.birth_date = null;
      }
    }

    if (payload.weight_kg !== undefined) {
      payload.weight_kg = parsePetWeight(payload.weight_kg);
    }

    if (payload.species_detail !== undefined) {
      payload.species_detail = typeof payload.species_detail === 'string' ? payload.species_detail.trim() : null;
    }

    const updatedPet = await petRepository.update(petId, userId, payload);
    if (!updatedPet) {
      throw new ApiError(404, 'Mascota no encontrada');
    }

    return updatedPet;
  },

  async remove(userId: number, petId: number): Promise<void> {
    const removed = await petRepository.remove(petId, userId);
    if (!removed) {
      throw new ApiError(404, 'Mascota no encontrada');
    }
  },
};

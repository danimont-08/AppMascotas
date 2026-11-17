// Traduce las solicitudes HTTP de mascotas a operaciones del servicio autenticado.
import { NextFunction, Request, Response } from 'express';
import { AuthenticatedRequest } from '../types';
import { petService } from '../services/pet.service';
import { ApiError } from '../utils/errors';
import { validatePetInput } from '../utils/validators';
import { imageUrlFor } from '../config/upload';

export const listPets = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const authReq = req as AuthenticatedRequest;
    const pets = await petService.listByUser(Number(authReq.user?.id));
    res.status(200).json(pets);
  } catch (error) {
    next(error);
  }
};

export const createPet = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const authReq = req as AuthenticatedRequest;
    const validation = validatePetInput(req.body);
    if (!validation.valid) {
      throw new ApiError(400, validation.message ?? 'Los datos enviados no son válidos');
    }

    const pet = await petService.create(Number(authReq.user?.id), req.body);
    res.status(201).json(pet);
  } catch (error) {
    next(error);
  }
};

export const getPetById = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const authReq = req as AuthenticatedRequest;
    const pet = await petService.getById(Number(authReq.user?.id), Number(req.params.id));
    res.status(200).json(pet);
  } catch (error) {
    next(error);
  }
};

export const updatePet = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const authReq = req as AuthenticatedRequest;
    const validation = validatePetInput(req.body);
    if (!validation.valid) {
      throw new ApiError(400, validation.message ?? 'Los datos enviados no son válidos');
    }

    const pet = await petService.update(Number(authReq.user?.id), Number(req.params.id), req.body);
    res.status(200).json(pet);
  } catch (error) {
    next(error);
  }
};

export const deletePet = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const authReq = req as AuthenticatedRequest;
    await petService.remove(Number(authReq.user?.id), Number(req.params.id));
    res.status(204).send();
  } catch (error) {
    next(error);
  }
};

export const uploadPetPhoto = async (req: Request, res: Response, next: NextFunction) => {
  try {
    const authReq = req as AuthenticatedRequest;
    const petId = Number(req.params.id);
    const file = authReq.file;

    if (!file) {
      throw new ApiError(400, 'Debe adjuntar una imagen válida');
    }

    const photoUrl = imageUrlFor(file.filename);
    const pet = await petService.update(Number(authReq.user?.id), petId, { photo_url: photoUrl });

    res.status(200).json({
      message: 'Foto actualizada correctamente',
      pet,
    });
  } catch (error) {
    next(error);
  }
};

import multer, { FileFilterCallback } from 'multer';
import { Request } from 'express';
import path from 'node:path';
import { uploadDirectory, isAllowedImageMimeType, makeSafeFileName } from '../config/upload';

const storage = multer.diskStorage({
  destination: (_req, _file, callback) => {
    callback(null, uploadDirectory);
  },
  filename: (_req, file, callback) => {
    const safeName = makeSafeFileName(file.originalname);
    callback(null, safeName);
  },
});

const fileFilter = (_req: Request, file: Express.Multer.File, callback: FileFilterCallback) => {
  const acceptable = isAllowedImageMimeType(file.mimetype) && /^image\//.test(file.mimetype);

  if (!acceptable) {
    callback(new Error('Solo se permiten imágenes JPEG, PNG o WebP'));
    return;
  }

  const extension = path.extname(file.originalname).toLowerCase();
  if (!['.jpg', '.jpeg', '.png', '.webp'].includes(extension)) {
    callback(new Error('La extensión del archivo no es válida para la imagen'));
    return;
  }

  callback(null, true);
};

export const uploadPetPhoto = multer({
  storage,
  limits: {
    fileSize: 5 * 1024 * 1024,
  },
  fileFilter,
});

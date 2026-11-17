import fs from 'node:fs';
import path from 'node:path';
import { env } from './env';

const uploadRoot = path.resolve(process.cwd(), env.uploadDir);

export const ensureUploadDirectory = () => {
  fs.mkdirSync(uploadRoot, { recursive: true });
  return uploadRoot;
};

export const uploadDirectory = ensureUploadDirectory();

export const allowedMimeTypes = new Set(['image/jpeg', 'image/png', 'image/webp']);

export const isAllowedImageMimeType = (mimeType: string): boolean => allowedMimeTypes.has(mimeType.toLowerCase());

export const makeSafeFileName = (originalName: string): string => {
  const sanitized = path.basename(originalName).replace(/[^a-zA-Z0-9._-]/g, '-');
  const timestamp = Date.now();
  const random = Math.random().toString(36).slice(2, 10);
  const extension = path.extname(sanitized).toLowerCase();
  const base = path.basename(sanitized, extension).slice(0, 50) || 'pet';

  return `${base}-${timestamp}-${random}${extension}`;
};

export const imageUrlFor = (relativePath: string): string => {
  const normalized = relativePath.replace(/\\/g, '/');
  return `${env.appBaseUrl.replace(/\/$/, '')}/uploads/${normalized.replace(/^\//, '')}`;
};

import { PdfMetadataDto } from '../types/api';

const BASE_URL = '/api/v1/pdf/core';

async function downloadBlobResponse(response: Response, defaultFilename: string) {
  if (!response.ok) {
    const errorJson = await response.json().catch(() => null);
    throw new Error(errorJson?.message || `Operation failed (${response.status})`);
  }
  const blob = await response.blob();
  const contentDisposition = response.headers.get('Content-Disposition');
  let filename = defaultFilename;
  if (contentDisposition) {
    const match = contentDisposition.match(/filename="?([^"]+)"?/);
    if (match && match[1]) {
      filename = match[1];
    }
  }

  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  window.URL.revokeObjectURL(url);
  document.body.removeChild(a);
}

export const pdfCoreApi = {
  mergePdfs: async (files: File[]) => {
    const formData = new FormData();
    files.forEach((file) => formData.append('files', file));
    const response = await fetch(`${BASE_URL}/merge`, {
      method: 'POST',
      body: formData,
    });
    await downloadBlobResponse(response, 'paperforge_merged.pdf');
  },

  splitPdf: async (file: File, splitFrequency: number) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('splitFrequency', splitFrequency.toString());
    const response = await fetch(`${BASE_URL}/split`, {
      method: 'POST',
      body: formData,
    });
    await downloadBlobResponse(response, 'paperforge_split.zip');
  },

  rotatePdf: async (file: File, angle: number, pageNumbers?: number[]) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append(
      'request',
      new Blob([JSON.stringify({ angle, pageNumbers: pageNumbers || [] })], {
        type: 'application/json',
      })
    );
    const response = await fetch(`${BASE_URL}/rotate`, {
      method: 'POST',
      body: formData,
    });
    await downloadBlobResponse(response, 'paperforge_rotated.pdf');
  },

  cropPdf: async (file: File, x: number, y: number, width: number, height: number) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append(
      'request',
      new Blob([JSON.stringify({ x, y, width, height })], {
        type: 'application/json',
      })
    );
    const response = await fetch(`${BASE_URL}/crop`, {
      method: 'POST',
      body: formData,
    });
    await downloadBlobResponse(response, 'paperforge_cropped.pdf');
  },

  getMetadata: async (file: File): Promise<PdfMetadataDto> => {
    const formData = new FormData();
    formData.append('file', file);
    const response = await fetch(`${BASE_URL}/metadata/get`, {
      method: 'POST',
      body: formData,
    });
    if (!response.ok) {
      const errorJson = await response.json().catch(() => null);
      throw new Error(errorJson?.message || 'Failed to fetch PDF metadata');
    }
    return response.json();
  },

  setMetadata: async (file: File, metadata: PdfMetadataDto) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append(
      'metadata',
      new Blob([JSON.stringify(metadata)], {
        type: 'application/json',
      })
    );
    const response = await fetch(`${BASE_URL}/metadata/set`, {
      method: 'POST',
      body: formData,
    });
    await downloadBlobResponse(response, 'paperforge_metadata_updated.pdf');
  },
};

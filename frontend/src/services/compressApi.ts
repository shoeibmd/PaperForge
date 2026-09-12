export interface CompressionParams {
  level?: string;
  targetDpi?: number;
  imageQuality?: number;
  stripMetadata?: boolean;
  linearize?: boolean;
}

export interface CompressionResponse {
  blob: Blob;
  originalSize: number;
  compressedSize: number;
  savedBytes: number;
  ratio: string;
}

export const compressApi = {
  compressPdf: async (file: File, params: CompressionParams): Promise<CompressionResponse> => {
    const formData = new FormData();
    formData.append('file', file);

    const requestBlob = new Blob([JSON.stringify(params)], { type: 'application/json' });
    formData.append('request', requestBlob);

    const response = await fetch('/api/v1/pdf/compress', {
      method: 'POST',
      body: formData,
    });

    if (!response.ok) {
      const text = await response.text();
      let message = 'PDF compression failed';
      try {
        const json = JSON.parse(text);
        if (json.message) message = json.message;
      } catch (e) {
        if (text) message = text;
      }
      throw new Error(message);
    }

    const originalSize = parseInt(response.headers.get('X-PaperForge-Original-Size') || '0', 10);
    const compressedSize = parseInt(response.headers.get('X-PaperForge-Compressed-Size') || '0', 10);
    const savedBytes = parseInt(response.headers.get('X-PaperForge-Saved-Bytes') || '0', 10);
    const ratio = response.headers.get('X-PaperForge-Compression-Ratio') || '0%';

    const blob = await response.blob();
    return { blob, originalSize, compressedSize, savedBytes, ratio };
  },
};

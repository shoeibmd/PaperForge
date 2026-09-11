export interface OcrParams {
  languages?: string[];
  pageNumbers?: number[];
  deskew?: boolean;
  dpi?: number;
}

export const ocrApi = {
  processOcr: async (file: File, params: OcrParams): Promise<Blob> => {
    const formData = new FormData();
    formData.append('file', file);

    const requestBlob = new Blob([JSON.stringify(params)], { type: 'application/json' });
    formData.append('request', requestBlob);

    const response = await fetch('/api/v1/pdf/ocr', {
      method: 'POST',
      body: formData,
    });

    if (!response.ok) {
      const text = await response.text();
      let message = 'OCR recognition failed';
      try {
        const json = JSON.parse(text);
        if (json.message) message = json.message;
      } catch (e) {
        if (text) message = text;
      }
      throw new Error(message);
    }

    return response.blob();
  },
};

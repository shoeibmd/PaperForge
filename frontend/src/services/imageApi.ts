export interface ImageToPdfParams {
  pageSize?: string;
  orientation?: string;
  fitOption?: string;
  margin?: number;
}

export interface PdfToImageParams {
  format?: string;
  dpi?: number;
  quality?: number;
  pageNumbers?: number[];
}

export const imageApi = {
  imagesToPdf: async (files: File[], params: ImageToPdfParams): Promise<Blob> => {
    const formData = new FormData();
    files.forEach((file) => formData.append('files', file));

    const requestBlob = new Blob([JSON.stringify(params)], { type: 'application/json' });
    formData.append('request', requestBlob);

    const response = await fetch('/api/v1/pdf/image/to-pdf', {
      method: 'POST',
      body: formData,
    });

    if (!response.ok) {
      const text = await response.text();
      let message = 'Image conversion failed';
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

  pdfToImages: async (file: File, params: PdfToImageParams): Promise<{ blob: Blob; filename: string }> => {
    const formData = new FormData();
    formData.append('file', file);

    const requestBlob = new Blob([JSON.stringify(params)], { type: 'application/json' });
    formData.append('request', requestBlob);

    const response = await fetch('/api/v1/pdf/image/from-pdf', {
      method: 'POST',
      body: formData,
    });

    if (!response.ok) {
      const text = await response.text();
      let message = 'PDF to Image extraction failed';
      try {
        const json = JSON.parse(text);
        if (json.message) message = json.message;
      } catch (e) {
        if (text) message = text;
      }
      throw new Error(message);
    }

    const contentDisposition = response.headers.get('content-disposition');
    let filename = 'extracted_images';
    if (contentDisposition) {
      const match = contentDisposition.match(/filename="?([^"]+)"?/);
      if (match && match[1]) filename = match[1];
    } else {
      const contentType = response.headers.get('content-type');
      filename = contentType === 'application/zip' ? 'paperforge_pages_images.zip' : `page_1.${params.format || 'png'}`;
    }

    const blob = await response.blob();
    return { blob, filename };
  },
};

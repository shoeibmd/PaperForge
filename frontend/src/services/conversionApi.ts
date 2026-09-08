const BASE_URL = '/api/v1/pdf/convert';

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

export const conversionApi = {
  convertToPdf: async (file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    const response = await fetch(`${BASE_URL}/to-pdf`, {
      method: 'POST',
      body: formData,
    });
    await downloadBlobResponse(response, 'paperforge_converted.pdf');
  },

  convertFromPdf: async (file: File, targetFormat: string) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('targetFormat', targetFormat);
    const response = await fetch(`${BASE_URL}/from-pdf`, {
      method: 'POST',
      body: formData,
    });
    await downloadBlobResponse(response, `paperforge_converted.${targetFormat}`);
  },
};

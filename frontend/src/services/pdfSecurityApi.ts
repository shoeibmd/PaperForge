const BASE_URL = '/api/v1/pdf/security';

export interface EncryptRequest {
  userPassword?: string;
  ownerPassword?: string;
  keyLength?: number;
  allowPrinting?: boolean;
  allowModification?: boolean;
  allowCopy?: boolean;
  allowFormFilling?: boolean;
}

export interface DecryptRequest {
  password?: string;
}

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

export const pdfSecurityApi = {
  encryptPdf: async (file: File, request: EncryptRequest) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append(
      'request',
      new Blob([JSON.stringify(request)], {
        type: 'application/json',
      })
    );
    const response = await fetch(`${BASE_URL}/encrypt`, {
      method: 'POST',
      body: formData,
    });
    await downloadBlobResponse(response, 'paperforge_protected.pdf');
  },

  decryptPdf: async (file: File, request: DecryptRequest) => {
    const formData = new FormData();
    formData.append('file', file);
    formData.append(
      'request',
      new Blob([JSON.stringify(request)], {
        type: 'application/json',
      })
    );
    const response = await fetch(`${BASE_URL}/decrypt`, {
      method: 'POST',
      body: formData,
    });
    await downloadBlobResponse(response, 'paperforge_unprotected.pdf');
  },
};

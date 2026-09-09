import React, { useState } from 'react';
import { Modal, FileInput, Button, Select, TextInput, Group, Stack, Alert } from '@mantine/core';
import { IconUpload, IconAlertCircle } from '@tabler/icons-react';
import { imageApi, PdfToImageParams } from '../../services/imageApi';

interface PdfToImageToolProps {
  opened: boolean;
  onClose: () => void;
}

export const PdfToImageTool: React.FC<PdfToImageToolProps> = ({ opened, onClose }) => {
  const [file, setFile] = useState<File | null>(null);
  const [format, setFormat] = useState<string>('png');
  const [dpi, setDpi] = useState<number>(150);
  const [pagesInput, setPagesInput] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleConvert = async () => {
    if (!file) return;
    setLoading(true);
    setError(null);

    try {
      let pageNumbers: number[] | undefined = undefined;
      if (pagesInput.trim()) {
        pageNumbers = pagesInput
          .split(',')
          .map((s) => parseInt(s.trim(), 10))
          .filter((n) => !isNaN(n) && n > 0);
      }

      const params: PdfToImageParams = {
        format,
        dpi,
        pageNumbers,
      };

      const result = await imageApi.pdfToImages(file, params);
      const url = window.URL.createObjectURL(result.blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = result.filename;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Failed to convert PDF to images');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal opened={opened} onClose={onClose} title="PDF to Images" size="md">
      <Stack gap="md">
        {error && (
          <Alert icon={<IconAlertCircle size={16} />} title="Error" color="red">
            {error}
          </Alert>
        )}

        <FileInput
          label="Select PDF File"
          placeholder="Choose a PDF file"
          leftSection={<IconUpload size={16} />}
          accept="application/pdf"
          value={file}
          onChange={setFile}
        />

        <Group grow>
          <Select
            label="Target Image Format"
            value={format}
            onChange={(v) => setFormat(v || 'png')}
            data={[
              { value: 'png', label: 'PNG' },
              { value: 'jpeg', label: 'JPEG' },
              { value: 'webp', label: 'WEBP' },
              { value: 'tiff', label: 'TIFF' },
            ]}
          />

          <Select
            label="Resolution (DPI)"
            value={dpi.toString()}
            onChange={(v) => setDpi(v ? parseInt(v, 10) : 150)}
            data={[
              { value: '72', label: '72 DPI (Draft / Screen)' },
              { value: '150', label: '150 DPI (Standard)' },
              { value: '300', label: '300 DPI (High Print Quality)' },
            ]}
          />
        </Group>

        <TextInput
          label="Specific Pages (Optional)"
          placeholder="e.g. 1, 3, 5 (Leave blank for all pages)"
          value={pagesInput}
          onChange={(e) => setPagesInput(e.currentTarget.value)}
        />

        <Group justify="flex-end" mt="md">
          <Button variant="outline" onClick={onClose}>
            Cancel
          </Button>
          <Button onClick={handleConvert} loading={loading} disabled={!file}>
            Extract Images
          </Button>
        </Group>
      </Stack>
    </Modal>
  );
};

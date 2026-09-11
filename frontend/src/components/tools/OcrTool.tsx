import React, { useState } from 'react';
import { Modal, FileInput, Button, MultiSelect, Checkbox, TextInput, Group, Stack, Alert, Select } from '@mantine/core';
import { IconUpload, IconAlertCircle } from '@tabler/icons-react';
import { ocrApi, OcrParams } from '../../services/ocrApi';

interface OcrToolProps {
  opened: boolean;
  onClose: () => void;
}

const LANGUAGE_OPTIONS = [
  { value: 'eng', label: 'English (eng)' },
  { value: 'spa', label: 'Spanish (spa)' },
  { value: 'fra', label: 'French (fra)' },
  { value: 'deu', label: 'German (deu)' },
  { value: 'ita', label: 'Italian (ita)' },
  { value: 'por', label: 'Portuguese (por)' },
  { value: 'nld', label: 'Dutch (nld)' },
  { value: 'rus', label: 'Russian (rus)' },
  { value: 'chi_sim', label: 'Chinese Simplified (chi_sim)' },
  { value: 'chi_tra', label: 'Chinese Traditional (chi_tra)' },
  { value: 'jpn', label: 'Japanese (jpn)' },
  { value: 'kor', label: 'Korean (kor)' },
  { value: 'ara', label: 'Arabic (ara)' },
  { value: 'hin', label: 'Hindi (hin)' },
];

export const OcrTool: React.FC<OcrToolProps> = ({ opened, onClose }) => {
  const [file, setFile] = useState<File | null>(null);
  const [languages, setLanguages] = useState<string[]>(['eng']);
  const [deskew, setDeskew] = useState<boolean>(true);
  const [dpi, setDpi] = useState<number>(300);
  const [pagesInput, setPagesInput] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleProcessOcr = async () => {
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

      const params: OcrParams = {
        languages: languages.length > 0 ? languages : ['eng'],
        deskew,
        dpi,
        pageNumbers,
      };

      const pdfBlob = await ocrApi.processOcr(file, params);
      const url = window.URL.createObjectURL(pdfBlob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'paperforge_ocr_searchable.pdf';
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Failed to perform OCR processing');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal opened={opened} onClose={onClose} title="OCR Text Recognition" size="md">
      <Stack gap="md">
        {error && (
          <Alert icon={<IconAlertCircle size={16} />} title="Error" color="red">
            {error}
          </Alert>
        )}

        <FileInput
          label="Select Scanned PDF"
          placeholder="Choose a PDF file to make searchable"
          leftSection={<IconUpload size={16} />}
          accept="application/pdf"
          value={file}
          onChange={setFile}
        />

        <MultiSelect
          label="Recognition Languages"
          placeholder="Select one or more languages"
          data={LANGUAGE_OPTIONS}
          value={languages}
          onChange={setLanguages}
          searchable
          clearable
        />

        <Group grow>
          <Select
            label="Resolution (DPI)"
            value={dpi.toString()}
            onChange={(v) => setDpi(v ? parseInt(v, 10) : 300)}
            data={[
              { value: '150', label: '150 DPI (Fast / Draft)' },
              { value: '300', label: '300 DPI (Recommended / Standard)' },
            ]}
          />

          <TextInput
            label="Specific Pages (Optional)"
            placeholder="e.g. 1, 3, 5"
            value={pagesInput}
            onChange={(e) => setPagesInput(e.currentTarget.value)}
          />
        </Group>

        <Checkbox
          label="Automatic deskew and orientation correction"
          checked={deskew}
          onChange={(e) => setDeskew(e.currentTarget.checked)}
        />

        <Group justify="flex-end" mt="md">
          <Button variant="outline" onClick={onClose}>
            Cancel
          </Button>
          <Button onClick={handleProcessOcr} loading={loading} disabled={!file}>
            Run OCR Process
          </Button>
        </Group>
      </Stack>
    </Modal>
  );
};

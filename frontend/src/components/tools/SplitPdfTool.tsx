import { useState } from 'react';
import { Button, FileInput, NumberInput, Stack, Alert, Text } from '@mantine/core';
import { IconAlertCircle, IconFileTypePdf, IconScissors } from '@tabler/icons-react';
import { pdfCoreApi } from '../../services/pdfCoreApi';

export function SplitPdfTool() {
  const [file, setFile] = useState<File | null>(null);
  const [splitFrequency, setSplitFrequency] = useState<number>(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSplit = async () => {
    if (!file) {
      setError('Please select a PDF file.');
      return;
    }
    setError(null);
    setLoading(true);
    try {
      await pdfCoreApi.splitPdf(file, splitFrequency);
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('An unexpected error occurred during split');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Stack gap="md">
      <Text size="sm" c="dimmed">
        Split a PDF document into multiple single-page or chunked files in a ZIP archive.
      </Text>

      <FileInput
        label="Select PDF File"
        placeholder="Choose PDF file..."
        leftSection={<IconFileTypePdf size={18} />}
        accept="application/pdf"
        value={file}
        onChange={setFile}
      />

      <NumberInput
        label="Split Frequency (pages per file)"
        min={1}
        value={splitFrequency}
        onChange={(val) => setSplitFrequency(Number(val) || 1)}
      />

      {error && (
        <Alert icon={<IconAlertCircle size={16} />} title="Error" color="red">
          {error}
        </Alert>
      )}

      <Button
        onClick={handleSplit}
        loading={loading}
        disabled={!file}
        leftSection={<IconScissors size={18} />}
      >
        Split PDF
      </Button>
    </Stack>
  );
}

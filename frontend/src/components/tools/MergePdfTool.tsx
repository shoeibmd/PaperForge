import { useState } from 'react';
import { Button, FileInput, Stack, Alert, Text } from '@mantine/core';
import { IconAlertCircle, IconFileTypePdf, IconUpload } from '@tabler/icons-react';
import { pdfCoreApi } from '../../services/pdfCoreApi';

export function MergePdfTool() {
  const [files, setFiles] = useState<File[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleMerge = async () => {
    if (files.length < 2) {
      setError('Please select at least 2 PDF files to merge.');
      return;
    }
    setError(null);
    setLoading(true);
    try {
      await pdfCoreApi.mergePdfs(files);
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('An unexpected error occurred during merge');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Stack gap="md">
      <Text size="sm" c="dimmed">
        Select two or more PDF files to combine into a single document.
      </Text>

      <FileInput
        label="Select PDF Files"
        placeholder="Choose PDF files..."
        leftSection={<IconFileTypePdf size={18} />}
        accept="application/pdf"
        multiple
        value={files}
        onChange={setFiles}
      />

      {error && (
        <Alert icon={<IconAlertCircle size={16} />} title="Error" color="red">
          {error}
        </Alert>
      )}

      <Button
        onClick={handleMerge}
        loading={loading}
        disabled={files.length < 2}
        leftSection={<IconUpload size={18} />}
      >
        Merge PDFs
      </Button>
    </Stack>
  );
}

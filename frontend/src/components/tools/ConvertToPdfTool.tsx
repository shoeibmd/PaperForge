import { useState } from 'react';
import { Button, FileInput, Stack, Alert, Text } from '@mantine/core';
import { IconAlertCircle, IconFileText, IconTransform } from '@tabler/icons-react';
import { conversionApi } from '../../services/conversionApi';

export function ConvertToPdfTool() {
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleConvert = async () => {
    if (!file) {
      setError('Please select a document file.');
      return;
    }
    setError(null);
    setLoading(true);
    try {
      await conversionApi.convertToPdf(file);
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('An unexpected error occurred during conversion');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Stack gap="md">
      <Text size="sm" c="dimmed">
        Convert Word (DOCX/DOC), Excel (XLSX/XLS), PowerPoint (PPTX/PPT), Text (TXT/CSV), or HTML files into PDF.
      </Text>

      <FileInput
        label="Select Document File"
        placeholder="Choose Word, Excel, PowerPoint, Text, or HTML file..."
        leftSection={<IconFileText size={18} />}
        accept=".docx,.doc,.odt,.xlsx,.xls,.pptx,.ppt,.html,.txt,.csv"
        value={file}
        onChange={setFile}
      />

      {error && (
        <Alert icon={<IconAlertCircle size={16} />} title="Error" color="red">
          {error}
        </Alert>
      )}

      <Button
        onClick={handleConvert}
        loading={loading}
        disabled={!file}
        leftSection={<IconTransform size={18} />}
      >
        Convert to PDF
      </Button>
    </Stack>
  );
}

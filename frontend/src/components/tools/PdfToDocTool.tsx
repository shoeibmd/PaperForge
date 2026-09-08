import { useState } from 'react';
import { Button, FileInput, Select, Stack, Alert, Text } from '@mantine/core';
import { IconAlertCircle, IconFileTypePdf, IconFileExport } from '@tabler/icons-react';
import { conversionApi } from '../../services/conversionApi';

export function PdfToDocTool() {
  const [file, setFile] = useState<File | null>(null);
  const [targetFormat, setTargetFormat] = useState('docx');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleExport = async () => {
    if (!file) {
      setError('Please select a PDF file.');
      return;
    }
    setError(null);
    setLoading(true);
    try {
      await conversionApi.convertFromPdf(file, targetFormat);
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('An unexpected error occurred during export');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Stack gap="md">
      <Text size="sm" c="dimmed">
        Export a PDF document into Word (DOCX), Excel (XLSX), PowerPoint (PPTX), or Text (TXT).
      </Text>

      <FileInput
        label="Select PDF File"
        placeholder="Choose PDF file..."
        leftSection={<IconFileTypePdf size={18} />}
        accept="application/pdf"
        value={file}
        onChange={setFile}
      />

      <Select
        label="Target Format"
        data={[
          { value: 'docx', label: 'Word Document (.docx)' },
          { value: 'xlsx', label: 'Excel Spreadsheet (.xlsx)' },
          { value: 'pptx', label: 'PowerPoint Presentation (.pptx)' },
          { value: 'txt', label: 'Plain Text (.txt)' },
        ]}
        value={targetFormat}
        onChange={(val) => setTargetFormat(val || 'docx')}
      />

      {error && (
        <Alert icon={<IconAlertCircle size={16} />} title="Error" color="red">
          {error}
        </Alert>
      )}

      <Button
        onClick={handleExport}
        loading={loading}
        disabled={!file}
        leftSection={<IconFileExport size={18} />}
      >
        Export Document
      </Button>
    </Stack>
  );
}

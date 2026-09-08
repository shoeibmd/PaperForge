import { useState } from 'react';
import { Button, FileInput, Select, Stack, Alert, Text, TextInput } from '@mantine/core';
import { IconAlertCircle, IconFileTypePdf, IconRotateClockwise } from '@tabler/icons-react';
import { pdfCoreApi } from '../../services/pdfCoreApi';

export function RotatePdfTool() {
  const [file, setFile] = useState<File | null>(null);
  const [angle, setAngle] = useState<string>('90');
  const [pagesStr, setPagesStr] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleRotate = async () => {
    if (!file) {
      setError('Please select a PDF file.');
      return;
    }
    setError(null);
    setLoading(true);
    try {
      const pageNumbers = pagesStr
        .split(',')
        .map((p) => p.trim())
        .filter((p) => p.length > 0)
        .map((p) => parseInt(p, 10))
        .filter((n) => !isNaN(n));

      await pdfCoreApi.rotatePdf(file, parseInt(angle, 10), pageNumbers);
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('An unexpected error occurred during rotation');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Stack gap="md">
      <Text size="sm" c="dimmed">
        Rotate PDF document pages by 90, 180, or 270 degrees.
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
        label="Rotation Angle"
        data={[
          { value: '90', label: '90° Clockwise' },
          { value: '180', label: '180° Flip' },
          { value: '270', label: '270° Counter-Clockwise' },
        ]}
        value={angle}
        onChange={(val) => setAngle(val || '90')}
      />

      <TextInput
        label="Pages to Rotate (optional)"
        placeholder="e.g. 1, 3, 5 (leave blank to rotate all pages)"
        value={pagesStr}
        onChange={(e) => setPagesStr(e.currentTarget.value)}
      />

      {error && (
        <Alert icon={<IconAlertCircle size={16} />} title="Error" color="red">
          {error}
        </Alert>
      )}

      <Button
        onClick={handleRotate}
        loading={loading}
        disabled={!file}
        leftSection={<IconRotateClockwise size={18} />}
      >
        Rotate PDF
      </Button>
    </Stack>
  );
}

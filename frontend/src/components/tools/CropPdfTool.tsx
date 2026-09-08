import { useState } from 'react';
import { Button, FileInput, NumberInput, SimpleGrid, Stack, Alert, Text } from '@mantine/core';
import { IconAlertCircle, IconCrop, IconFileTypePdf } from '@tabler/icons-react';
import { pdfCoreApi } from '../../services/pdfCoreApi';

export function CropPdfTool() {
  const [file, setFile] = useState<File | null>(null);
  const [x, setX] = useState<number>(10);
  const [y, setY] = useState<number>(10);
  const [width, setWidth] = useState<number>(400);
  const [height, setHeight] = useState<number>(500);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleCrop = async () => {
    if (!file) {
      setError('Please select a PDF file.');
      return;
    }
    setError(null);
    setLoading(true);
    try {
      await pdfCoreApi.cropPdf(file, x, y, width, height);
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('An unexpected error occurred during crop');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Stack gap="md">
      <Text size="sm" c="dimmed">
        Crop document margins by setting the crop box coordinates (X, Y, Width, Height).
      </Text>

      <FileInput
        label="Select PDF File"
        placeholder="Choose PDF file..."
        leftSection={<IconFileTypePdf size={18} />}
        accept="application/pdf"
        value={file}
        onChange={setFile}
      />

      <SimpleGrid cols={2}>
        <NumberInput label="X Position" value={x} onChange={(v) => setX(Number(v) || 0)} />
        <NumberInput label="Y Position" value={y} onChange={(v) => setY(Number(v) || 0)} />
        <NumberInput label="Width" value={width} onChange={(v) => setWidth(Number(v) || 100)} />
        <NumberInput label="Height" value={height} onChange={(v) => setHeight(Number(v) || 100)} />
      </SimpleGrid>

      {error && (
        <Alert icon={<IconAlertCircle size={16} />} title="Error" color="red">
          {error}
        </Alert>
      )}

      <Button
        onClick={handleCrop}
        loading={loading}
        disabled={!file}
        leftSection={<IconCrop size={18} />}
      >
        Crop PDF
      </Button>
    </Stack>
  );
}

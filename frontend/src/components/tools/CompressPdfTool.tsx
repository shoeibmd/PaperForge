import React, { useState } from 'react';
import { Modal, FileInput, Button, Select, Checkbox, Group, Stack, Alert, Text, Card, SimpleGrid } from '@mantine/core';
import { IconUpload, IconAlertCircle, IconCheck } from '@tabler/icons-react';
import { compressApi, CompressionParams } from '../../services/compressApi';

interface CompressPdfToolProps {
  opened: boolean;
  onClose: () => void;
}

export const CompressPdfTool: React.FC<CompressPdfToolProps> = ({ opened, onClose }) => {
  const [file, setFile] = useState<File | null>(null);
  const [level, setLevel] = useState<string>('BALANCED');
  const [stripMetadata, setStripMetadata] = useState<boolean>(true);
  const [linearize, setLinearize] = useState<boolean>(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [metrics, setMetrics] = useState<{ originalSize: number; compressedSize: number; savedBytes: number; ratio: string } | null>(null);

  const handleCompress = async () => {
    if (!file) return;
    setLoading(true);
    setError(null);
    setMetrics(null);

    try {
      const params: CompressionParams = {
        level,
        stripMetadata,
        linearize,
      };

      const result = await compressApi.compressPdf(file, params);
      setMetrics({
        originalSize: result.originalSize,
        compressedSize: result.compressedSize,
        savedBytes: result.savedBytes,
        ratio: result.ratio,
      });

      const url = window.URL.createObjectURL(result.blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'paperforge_compressed.pdf';
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Failed to compress PDF document');
    } finally {
      setLoading(false);
    }
  };

  const formatKB = (bytes: number) => (bytes / 1024).toFixed(1) + ' KB';

  return (
    <Modal opened={opened} onClose={onClose} title="Compress PDF Document" size="md">
      <Stack gap="md">
        {error && (
          <Alert icon={<IconAlertCircle size={16} />} title="Error" color="red">
            {error}
          </Alert>
        )}

        <FileInput
          label="Select PDF File"
          placeholder="Choose a PDF file to reduce size"
          leftSection={<IconUpload size={16} />}
          accept="application/pdf"
          value={file}
          onChange={(f) => {
            setFile(f);
            setMetrics(null);
          }}
        />

        <Select
          label="Compression Level"
          value={level}
          onChange={(v) => setLevel(v || 'BALANCED')}
          data={[
            { value: 'LOW', label: 'Low Compression (Minimal quality reduction)' },
            { value: 'BALANCED', label: 'Balanced (Recommended - Optimal size/quality ratio)' },
            { value: 'HIGH', label: 'High Compression (Maximum file size reduction)' },
          ]}
        />

        <Group grow>
          <Checkbox
            label="Strip document metadata"
            checked={stripMetadata}
            onChange={(e) => setStripMetadata(e.currentTarget.checked)}
          />
          <Checkbox
            label="Linearize for fast web view"
            checked={linearize}
            onChange={(e) => setLinearize(e.currentTarget.checked)}
          />
        </Group>

        {metrics && (
          <Card withBorder padding="sm" radius="md" style={{ backgroundColor: '#f8f9fa' }}>
            <Group gap="xs" mb="xs">
              <IconCheck size={18} color="green" />
              <Text fw={600} size="sm" c="green">
                Compression Complete!
              </Text>
            </Group>
            <SimpleGrid cols={2} spacing="xs">
              <div>
                <Text size="xs" c="dimmed">Original Size:</Text>
                <Text size="sm" fw={500}>{formatKB(metrics.originalSize)}</Text>
              </div>
              <div>
                <Text size="xs" c="dimmed">Compressed Size:</Text>
                <Text size="sm" fw={500}>{formatKB(metrics.compressedSize)}</Text>
              </div>
              <div>
                <Text size="xs" c="dimmed">Saved Bytes:</Text>
                <Text size="sm" fw={500}>{formatKB(metrics.savedBytes)}</Text>
              </div>
              <div>
                <Text size="xs" c="dimmed">Reduction Ratio:</Text>
                <Text size="sm" fw={700} c="blue">{metrics.ratio}</Text>
              </div>
            </SimpleGrid>
          </Card>
        )}

        <Group justify="flex-end" mt="md">
          <Button variant="outline" onClick={onClose}>
            Close
          </Button>
          <Button onClick={handleCompress} loading={loading} disabled={!file}>
            Compress PDF
          </Button>
        </Group>
      </Stack>
    </Modal>
  );
};

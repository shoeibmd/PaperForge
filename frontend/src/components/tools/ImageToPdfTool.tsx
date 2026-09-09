import React, { useState } from 'react';
import { Modal, FileInput, Button, Select, NumberInput, Group, Stack, Alert, ActionIcon, Text, Table } from '@mantine/core';
import { IconUpload, IconAlertCircle, IconArrowUp, IconArrowDown, IconTrash } from '@tabler/icons-react';
import { imageApi, ImageToPdfParams } from '../../services/imageApi';

interface ImageToPdfToolProps {
  opened: boolean;
  onClose: () => void;
}

export const ImageToPdfTool: React.FC<ImageToPdfToolProps> = ({ opened, onClose }) => {
  const [files, setFiles] = useState<File[]>([]);
  const [pageSize, setPageSize] = useState<string>('A4');
  const [orientation, setOrientation] = useState<string>('PORTRAIT');
  const [fitOption, setFitOption] = useState<string>('FIT_PAGE');
  const [margin, setMargin] = useState<number>(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleFileSelect = (newFiles: File[]) => {
    if (newFiles && newFiles.length > 0) {
      setFiles((prev) => [...prev, ...newFiles]);
    }
  };

  const moveFile = (index: number, direction: 'up' | 'down') => {
    const newFiles = [...files];
    const targetIndex = direction === 'up' ? index - 1 : index + 1;
    if (targetIndex >= 0 && targetIndex < newFiles.length) {
      const temp = newFiles[index];
      newFiles[index] = newFiles[targetIndex];
      newFiles[targetIndex] = temp;
      setFiles(newFiles);
    }
  };

  const removeFile = (index: number) => {
    setFiles(files.filter((_, i) => i !== index));
  };

  const handleConvert = async () => {
    if (files.length === 0) return;
    setLoading(true);
    setError(null);

    try {
      const params: ImageToPdfParams = {
        pageSize,
        orientation,
        fitOption,
        margin: margin || 0,
      };

      const resultBlob = await imageApi.imagesToPdf(files, params);
      const url = window.URL.createObjectURL(resultBlob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'paperforge_images_merged.pdf';
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
      onClose();
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Failed to convert images to PDF');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal opened={opened} onClose={onClose} title="Images to PDF" size="lg">
      <Stack gap="md">
        {error && (
          <Alert icon={<IconAlertCircle size={16} />} title="Error" color="red">
            {error}
          </Alert>
        )}

        <FileInput
          label="Select Image Files"
          placeholder="Choose JPEG, PNG, WEBP, or TIFF images"
          leftSection={<IconUpload size={16} />}
          multiple
          accept="image/jpeg,image/png,image/webp,image/tiff"
          onChange={handleFileSelect}
        />

        {files.length > 0 && (
          <Table striped highlightOnHover>
            <Table.Thead>
              <Table.Tr>
                <Table.Th>File Name</Table.Th>
                <Table.Th>Size</Table.Th>
                <Table.Th style={{ textAlign: 'right' }}>Actions</Table.Th>
              </Table.Tr>
            </Table.Thead>
            <Table.Tbody>
              {files.map((file, idx) => (
                <Table.Tr key={`${file.name}-${idx}`}>
                  <Table.Td><Text size="sm">{file.name}</Text></Table.Td>
                  <Table.Td><Text size="xs" color="dimmed">{(file.size / 1024).toFixed(1)} KB</Text></Table.Td>
                  <Table.Td style={{ textAlign: 'right' }}>
                    <Group gap={4} justify="flex-end">
                      <ActionIcon size="sm" variant="subtle" disabled={idx === 0} onClick={() => moveFile(idx, 'up')}>
                        <IconArrowUp size={14} />
                      </ActionIcon>
                      <ActionIcon size="sm" variant="subtle" disabled={idx === files.length - 1} onClick={() => moveFile(idx, 'down')}>
                        <IconArrowDown size={14} />
                      </ActionIcon>
                      <ActionIcon size="sm" variant="subtle" color="red" onClick={() => removeFile(idx)}>
                        <IconTrash size={14} />
                      </ActionIcon>
                    </Group>
                  </Table.Td>
                </Table.Tr>
              ))}
            </Table.Tbody>
          </Table>
        )}

        <Group grow>
          <Select
            label="Page Size"
            value={pageSize}
            onChange={(v) => setPageSize(v || 'A4')}
            data={[
              { value: 'A4', label: 'A4' },
              { value: 'LETTER', label: 'US Letter' },
              { value: 'LEGAL', label: 'US Legal' },
              { value: 'EXECUTIVE', label: 'Executive' },
              { value: 'AUTO', label: 'Fit to Image Size' },
            ]}
          />

          <Select
            label="Orientation"
            value={orientation}
            onChange={(v) => setOrientation(v || 'PORTRAIT')}
            data={[
              { value: 'PORTRAIT', label: 'Portrait' },
              { value: 'LANDSCAPE', label: 'Landscape' },
            ]}
            disabled={pageSize === 'AUTO'}
          />
        </Group>

        <Group grow>
          <Select
            label="Image Alignment / Scaling"
            value={fitOption}
            onChange={(v) => setFitOption(v || 'FIT_PAGE')}
            data={[
              { value: 'FIT_PAGE', label: 'Fit to Page (Maintain Aspect Ratio)' },
              { value: 'STRETCH', label: 'Fill Entire Page (Stretch)' },
              { value: 'ORIGINAL', label: 'Original Size (Center)' },
            ]}
          />

          <NumberInput
            label="Margin (points)"
            value={margin}
            onChange={(v) => setMargin(typeof v === 'number' ? v : 0)}
            min={0}
            max={100}
          />
        </Group>

        <Group justify="flex-end" mt="md">
          <Button variant="outline" onClick={onClose}>
            Cancel
          </Button>
          <Button onClick={handleConvert} loading={loading} disabled={files.length === 0}>
            Convert to PDF
          </Button>
        </Group>
      </Stack>
    </Modal>
  );
};

import { useState } from 'react';
import { Button, FileInput, TextInput, Stack, Alert, Text, Group } from '@mantine/core';
import { IconAlertCircle, IconFileTypePdf, IconInfoCircle, IconDeviceFloppy } from '@tabler/icons-react';
import { pdfCoreApi } from '../../services/pdfCoreApi';
import { PdfMetadataDto } from '../../types/api';

export function MetadataPdfTool() {
  const [file, setFile] = useState<File | null>(null);
  const [title, setTitle] = useState('');
  const [author, setAuthor] = useState('');
  const [subject, setSubject] = useState('');
  const [keywords, setKeywords] = useState('');
  const [creator, setCreator] = useState('');
  const [producer, setProducer] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleFileChange = async (selectedFile: File | null) => {
    setFile(selectedFile);
    if (!selectedFile) return;

    setError(null);
    setLoading(true);
    try {
      const meta = await pdfCoreApi.getMetadata(selectedFile);
      setTitle(meta.title || '');
      setAuthor(meta.author || '');
      setSubject(meta.subject || '');
      setKeywords(meta.keywords || '');
      setCreator(meta.creator || '');
      setProducer(meta.producer || '');
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Failed to fetch document metadata');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async () => {
    if (!file) return;
    setError(null);
    setLoading(true);
    try {
      const metadataDto: PdfMetadataDto = { title, author, subject, keywords, creator, producer };
      await pdfCoreApi.setMetadata(file, metadataDto);
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('An unexpected error occurred while saving metadata');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Stack gap="md">
      <Text size="sm" c="dimmed">
        Inspect and update metadata properties of a PDF document.
      </Text>

      <FileInput
        label="Select PDF File"
        placeholder="Choose PDF file..."
        leftSection={<IconFileTypePdf size={18} />}
        accept="application/pdf"
        value={file}
        onChange={handleFileChange}
      />

      {file && (
        <>
          <TextInput label="Document Title" value={title} onChange={(e) => setTitle(e.currentTarget.value)} />
          <TextInput label="Author" value={author} onChange={(e) => setAuthor(e.currentTarget.value)} />
          <TextInput label="Subject" value={subject} onChange={(e) => setSubject(e.currentTarget.value)} />
          <TextInput label="Keywords" value={keywords} onChange={(e) => setKeywords(e.currentTarget.value)} />
          <Group grow>
            <TextInput label="Creator" value={creator} onChange={(e) => setCreator(e.currentTarget.value)} />
            <TextInput label="Producer" value={producer} onChange={(e) => setProducer(e.currentTarget.value)} />
          </Group>
        </>
      )}

      {error && (
        <Alert icon={<IconAlertCircle size={16} />} title="Error" color="red">
          {error}
        </Alert>
      )}

      <Button
        onClick={handleSave}
        loading={loading}
        disabled={!file}
        leftSection={file ? <IconDeviceFloppy size={18} /> : <IconInfoCircle size={18} />}
      >
        Save Updated Metadata
      </Button>
    </Stack>
  );
}

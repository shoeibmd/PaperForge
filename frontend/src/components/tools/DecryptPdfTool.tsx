import { useState } from 'react';
import { Button, FileInput, PasswordInput, Stack, Alert, Text } from '@mantine/core';
import { IconAlertCircle, IconFileTypePdf, IconLockOpen } from '@tabler/icons-react';
import { pdfSecurityApi } from '../../services/pdfSecurityApi';

export function DecryptPdfTool() {
  const [file, setFile] = useState<File | null>(null);
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleDecrypt = async () => {
    if (!file) {
      setError('Please select an encrypted PDF file.');
      return;
    }
    setError(null);
    setLoading(true);
    try {
      await pdfSecurityApi.decryptPdf(file, { password });
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('An unexpected error occurred during decryption');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Stack gap="md">
      <Text size="sm" c="dimmed">
        Remove password protection and security restrictions from an encrypted PDF document.
      </Text>

      <FileInput
        label="Select Protected PDF File"
        placeholder="Choose encrypted PDF..."
        leftSection={<IconFileTypePdf size={18} />}
        accept="application/pdf"
        value={file}
        onChange={setFile}
      />

      <PasswordInput
        label="Password"
        placeholder="Enter password to unlock PDF"
        value={password}
        onChange={(e) => setPassword(e.currentTarget.value)}
      />

      {error && (
        <Alert icon={<IconAlertCircle size={16} />} title="Error" color="red">
          {error}
        </Alert>
      )}

      <Button
        onClick={handleDecrypt}
        loading={loading}
        disabled={!file}
        leftSection={<IconLockOpen size={18} />}
      >
        Remove Password
      </Button>
    </Stack>
  );
}

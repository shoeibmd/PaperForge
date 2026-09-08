import { useState } from 'react';
import { Button, FileInput, PasswordInput, Select, Checkbox, Stack, Alert, Text, Group } from '@mantine/core';
import { IconAlertCircle, IconFileTypePdf, IconLock } from '@tabler/icons-react';
import { pdfSecurityApi } from '../../services/pdfSecurityApi';

export function ProtectPdfTool() {
  const [file, setFile] = useState<File | null>(null);
  const [userPassword, setUserPassword] = useState('');
  const [ownerPassword, setOwnerPassword] = useState('');
  const [keyLength, setKeyLength] = useState('128');
  const [allowPrinting, setAllowPrinting] = useState(true);
  const [allowModification, setAllowModification] = useState(false);
  const [allowCopy, setAllowCopy] = useState(true);
  const [allowFormFilling, setAllowFormFilling] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleProtect = async () => {
    if (!file) {
      setError('Please select a PDF file.');
      return;
    }
    if (!userPassword && !ownerPassword) {
      setError('Please provide at least a User or Owner password.');
      return;
    }
    setError(null);
    setLoading(true);
    try {
      await pdfSecurityApi.encryptPdf(file, {
        userPassword,
        ownerPassword,
        keyLength: parseInt(keyLength, 10),
        allowPrinting,
        allowModification,
        allowCopy,
        allowFormFilling,
      });
    } catch (err: unknown) {
      if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('An unexpected error occurred during encryption');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Stack gap="md">
      <Text size="sm" c="dimmed">
        Encrypt PDF document with password protection and configure permission restrictions.
      </Text>

      <FileInput
        label="Select PDF File"
        placeholder="Choose PDF file..."
        leftSection={<IconFileTypePdf size={18} />}
        accept="application/pdf"
        value={file}
        onChange={setFile}
      />

      <Group grow>
        <PasswordInput
          label="User Password (Open)"
          placeholder="Password required to open"
          value={userPassword}
          onChange={(e) => setUserPassword(e.currentTarget.value)}
        />
        <PasswordInput
          label="Owner Password (Permissions)"
          placeholder="Password required to modify permissions"
          value={ownerPassword}
          onChange={(e) => setOwnerPassword(e.currentTarget.value)}
        />
      </Group>

      <Select
        label="Encryption Strength"
        data={[
          { value: '128', label: '128-bit AES' },
          { value: '256', label: '256-bit AES' },
        ]}
        value={keyLength}
        onChange={(val) => setKeyLength(val || '128')}
      />

      <Stack gap="xs">
        <Text size="xs" fw={500}>Document Permissions</Text>
        <Checkbox label="Allow Printing" checked={allowPrinting} onChange={(e) => setAllowPrinting(e.currentTarget.checked)} />
        <Checkbox label="Allow Modifications" checked={allowModification} onChange={(e) => setAllowModification(e.currentTarget.checked)} />
        <Checkbox label="Allow Copying Content" checked={allowCopy} onChange={(e) => setAllowCopy(e.currentTarget.checked)} />
        <Checkbox label="Allow Form Filling" checked={allowFormFilling} onChange={(e) => setAllowFormFilling(e.currentTarget.checked)} />
      </Stack>

      {error && (
        <Alert icon={<IconAlertCircle size={16} />} title="Error" color="red">
          {error}
        </Alert>
      )}

      <Button
        onClick={handleProtect}
        loading={loading}
        disabled={!file}
        leftSection={<IconLock size={18} />}
      >
        Protect PDF
      </Button>
    </Stack>
  );
}

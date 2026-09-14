import { useState, useEffect } from 'react';
import {
  Title,
  Text,
  Button,
  TextInput,
  MultiSelect,
  Card,
  Group,
  Badge,
  Table,
  ActionIcon,
  Modal,
  Alert,
  Code,
  CopyButton,
  Tooltip,
  Stack
} from '@mantine/core';
import { IconKey, IconTrash, IconCheck, IconCopy, IconAlertCircle } from '@tabler/icons-react';
import { apiKeyApi, ApiKeyDto } from '../services/apiKeyApi';
import { useAuth } from '../context/AuthContext';

const AVAILABLE_SCOPES = [
  { value: 'pdf:read', label: 'pdf:read (Read PDF content/metadata)' },
  { value: 'pdf:write', label: 'pdf:write (Modify/forge PDF files)' },
  { value: 'ocr', label: 'ocr (Execute Tesseract OCR)' },
  { value: 'conversion', label: 'conversion (Convert office docs to PDF)' },
  { value: 'admin', label: 'admin (Administrative operations)' },
];

export function ApiKeyPage() {
  const [keys, setKeys] = useState<ApiKeyDto[]>([]);
  const [keyName, setKeyName] = useState('');
  const [selectedScopes, setSelectedScopes] = useState<string[]>([
    'pdf:read',
    'pdf:write',
    'ocr',
    'conversion',
  ]);
  const [error, setError] = useState<string | null>(null);
  const [rawSecretModal, setRawSecretModal] = useState<string | null>(null);
  const { token } = useAuth();

  const loadKeys = async () => {
    if (!token) return;
    try {
      const data = await apiKeyApi.getUserApiKeys(token);
      setKeys(data);
    } catch (err: any) {
      setError(err.message || 'Failed to load API keys');
    }
  };

  useEffect(() => {
    loadKeys();
  }, [token]);

  const handleCreateKey = async () => {
    if (!token || !keyName.trim()) return;
    try {
      const result = await apiKeyApi.createApiKey(
        {
          name: keyName.trim(),
          scopes: selectedScopes,
        },
        token
      );
      setKeyName('');
      setRawSecretModal(result.rawSecret);
      loadKeys();
    } catch (err: any) {
      setError(err.message || 'Failed to create API key');
    }
  };

  const handleRevokeKey = async (id: number) => {
    if (!token) return;
    try {
      await apiKeyApi.revokeApiKey(id, token);
      loadKeys();
    } catch (err: any) {
      setError(err.message || 'Failed to revoke API key');
    }
  };

  return (
    <div>
      <Title order={2} mb="xs">
        Programmatic API Keys
      </Title>
      <Text c="dimmed" mb="lg">
        Manage secure API keys to integrate PaperForge features into external pipelines and microservices.
      </Text>

      {error && (
        <Alert
          icon={<IconAlertCircle size={16} />}
          color="red"
          mb="md"
          onClose={() => setError(null)}
          withCloseButton
        >
          {error}
        </Alert>
      )}

      <Card withBorder padding="md" radius="md" mb="xl">
        <Title order={4} mb="md">
          Generate New API Key
        </Title>
        <Stack gap="sm">
          <Group grow align="flex-start">
            <TextInput
              label="Key Name / Description"
              placeholder="e.g. CI/CD Automated Converter"
              value={keyName}
              onChange={(e) => setKeyName(e.currentTarget.value)}
              required
            />
            <MultiSelect
              label="Scopes / Permissions"
              data={AVAILABLE_SCOPES}
              value={selectedScopes}
              onChange={setSelectedScopes}
              placeholder="Select allowed scopes"
            />
          </Group>
          <Group justify="flex-end" mt="sm">
            <Button
              leftSection={<IconKey size={16} />}
              onClick={handleCreateKey}
              disabled={!keyName.trim() || selectedScopes.length === 0}
            >
              Generate Key
            </Button>
          </Group>
        </Stack>
      </Card>

      <Title order={3} mb="md">
        Your Active & Historical API Keys
      </Title>
      <Table striped highlightOnHover>
        <Table.Thead>
          <Table.Tr>
            <Table.Th>Name</Table.Th>
            <Table.Th>Prefix</Table.Th>
            <Table.Th>Scopes</Table.Th>
            <Table.Th>Usage</Table.Th>
            <Table.Th>Status</Table.Th>
            <Table.Th>Created</Table.Th>
            <Table.Th style={{ textAlign: 'right' }}>Actions</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {keys.length === 0 ? (
            <Table.Tr>
              <Table.Td colSpan={7} style={{ textAlign: 'center' }}>
                No API keys generated yet.
              </Table.Td>
            </Table.Tr>
          ) : (
            keys.map((k) => (
              <Table.Tr key={k.id}>
                <Table.Td fw={600}>{k.name}</Table.Td>
                <Table.Td>
                  <Code>{k.keyPrefix}...</Code>
                </Table.Td>
                <Table.Td>
                  <Group gap={4}>
                    {k.scopes.map((s) => (
                      <Badge key={s} size="xs" variant="light">
                        {s}
                      </Badge>
                    ))}
                  </Group>
                </Table.Td>
                <Table.Td>{k.usageCount} requests</Table.Td>
                <Table.Td>
                  {k.revoked ? (
                    <Badge color="red">Revoked</Badge>
                  ) : (
                    <Badge color="green">Active</Badge>
                  )}
                </Table.Td>
                <Table.Td>{new Date(k.createdAt).toLocaleDateString()}</Table.Td>
                <Table.Td style={{ textAlign: 'right' }}>
                  {!k.revoked && (
                    <ActionIcon color="red" variant="subtle" onClick={() => handleRevokeKey(k.id)}>
                      <IconTrash size={16} />
                    </ActionIcon>
                  )}
                </Table.Td>
              </Table.Tr>
            ))
          )}
        </Table.Tbody>
      </Table>

      <Modal
        opened={!!rawSecretModal}
        onClose={() => setRawSecretModal(null)}
        title="API Key Created Successfully"
        centered
        size="lg"
      >
        <Alert color="yellow" icon={<IconAlertCircle size={20} />} mb="md">
          Copy this secret key immediately! For security reasons, it will <strong>NEVER</strong> be shown again.
        </Alert>
        <Text size="sm" mb="xs">
          Secret API Key:
        </Text>
        <Group mb="lg">
          <Code style={{ fontSize: '14px', padding: '8px 12px', flexGrow: 1 }} color="teal">
            {rawSecretModal}
          </Code>
          {rawSecretModal && (
            <CopyButton value={rawSecretModal}>
              {({ copied, copy }) => (
                <Tooltip label={copied ? 'Copied' : 'Copy Key'} withArrow position="right">
                  <ActionIcon color={copied ? 'teal' : 'gray'} variant="subtle" onClick={copy}>
                    {copied ? <IconCheck size={16} /> : <IconCopy size={16} />}
                  </ActionIcon>
                </Tooltip>
              )}
            </CopyButton>
          )}
        </Group>
        <Group justify="flex-end">
          <Button color="blue" onClick={() => setRawSecretModal(null)}>
            I Have Saved My Key
          </Button>
        </Group>
      </Modal>
    </div>
  );
}

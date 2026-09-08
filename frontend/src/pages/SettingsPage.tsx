import { useEffect, useState } from 'react';
import { Title, Text, Card, Code, Table, Loader, Alert } from '@mantine/core';
import { IconAlertCircle } from '@tabler/icons-react';
import { apiService } from '../services/api';
import { ConfigResponse } from '../types/api';

export function SettingsPage() {
  const [config, setConfig] = useState<ConfigResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    apiService
      .getConfig()
      .then((data) => {
        setConfig(data);
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message || 'Failed to load configuration');
        setLoading(false);
      });
  }, []);

  return (
    <div>
      <Title order={2} mb="xs">
        Settings
      </Title>
      <Text c="dimmed" mb="lg">
        System configuration and client settings.
      </Text>

      {loading && <Loader size="md" />}

      {error && (
        <Alert icon={<IconAlertCircle size={16} />} title="Error" color="red" mb="md">
          {error}
        </Alert>
      )}

      {config && (
        <Card shadow="sm" padding="lg" radius="md" withBorder>
          <Title order={4} mb="md">
            System Configuration
          </Title>
          <Table>
            <Table.Tbody>
              <Table.Tr>
                <Table.Td fw={500}>Max Upload Size</Table.Td>
                <Table.Td>
                  {(config.maxUploadSizeBytes / (1024 * 1024)).toFixed(0)} MB ({config.maxUploadSizeBytes} bytes)
                </Table.Td>
              </Table.Tr>
              <Table.Tr>
                <Table.Td fw={500}>Default Theme</Table.Td>
                <Table.Td>{config.defaultTheme}</Table.Td>
              </Table.Tr>
              <Table.Tr>
                <Table.Td fw={500}>Support Contact</Table.Td>
                <Table.Td>{config.supportEmail}</Table.Td>
              </Table.Tr>
              <Table.Tr>
                <Table.Td fw={500}>Enabled Features</Table.Td>
                <Table.Td>
                  <Code block>{JSON.stringify(config.featureFlags, null, 2)}</Code>
                </Table.Td>
              </Table.Tr>
            </Table.Tbody>
          </Table>
        </Card>
      )}
    </div>
  );
}

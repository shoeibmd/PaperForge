import { useEffect, useState } from 'react';
import { Title, Text, Card, Group, Badge, Loader, Alert, Stack } from '@mantine/core';
import { IconAlertCircle, IconFileTypePdf } from '@tabler/icons-react';
import { apiService } from '../services/api';
import { InfoResponse } from '../types/api';

export function AboutPage() {
  const [info, setInfo] = useState<InfoResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    apiService
      .getInfo()
      .then((data) => {
        setInfo(data);
        setLoading(false);
      })
      .catch((err) => {
        setError(err.message || 'Failed to fetch application information');
        setLoading(false);
      });
  }, []);

  return (
    <div>
      <Title order={2} mb="xs">
        About PaperForge
      </Title>
      <Text c="dimmed" mb="lg">
        Forge your documents.
      </Text>

      {loading && <Loader size="md" />}

      {error && (
        <Alert icon={<IconAlertCircle size={16} />} title="Error" color="red" mb="md">
          {error}
        </Alert>
      )}

      {info && (
        <Card shadow="sm" padding="xl" radius="md" withBorder>
          <Group gap="md" mb="md">
            <IconFileTypePdf size={48} color="#228be6" />
            <Stack gap={0}>
              <Title order={3}>{info.name}</Title>
              <Text c="dimmed">{info.tagline}</Text>
            </Stack>
          </Group>

          <Group gap="xs" mb="lg">
            <Badge color="blue">Version: {info.version}</Badge>
            <Badge color="teal">Environment: {info.environment}</Badge>
            <Badge color="gray">License: MIT</Badge>
          </Group>

          <Text size="sm" c="dimmed">
            PaperForge is an independent, self-hosted document processing platform designed for privacy, performance, and modularity.
          </Text>
        </Card>
      )}
    </div>
  );
}

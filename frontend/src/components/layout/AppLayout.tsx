import { useEffect, useState } from 'react';
import { Outlet } from 'react-router-dom';
import { AppShell, Text, Group } from '@mantine/core';
import { Header } from './Header';
import { Sidebar } from './Sidebar';
import { apiService } from '../../services/api';

export function AppLayout() {
  const [healthStatus, setHealthStatus] = useState<string>('UNKNOWN');

  useEffect(() => {
    apiService
      .getHealth()
      .then((res) => setHealthStatus(res.status))
      .catch(() => setHealthStatus('DOWN'));
  }, []);

  return (
    <AppShell
      header={{ height: 60 }}
      navbar={{ width: 240, breakpoint: 'sm' }}
      padding="md"
    >
      <AppShell.Header>
        <Header healthStatus={healthStatus} />
      </AppShell.Header>

      <AppShell.Navbar>
        <Sidebar />
      </AppShell.Navbar>

      <AppShell.Main>
        <Outlet />
      </AppShell.Main>

      <AppShell.Footer p="xs">
        <Group justify="space-between" px="md">
          <Text size="xs" c="dimmed">
            PaperForge Document Studio &copy; {new Date().getFullYear()} — Forge your documents.
          </Text>
          <Text size="xs" c="dimmed">
            v0.0.1-SNAPSHOT
          </Text>
        </Group>
      </AppShell.Footer>
    </AppShell>
  );
}

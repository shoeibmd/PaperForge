import { useState } from 'react';
import { Title, Text, Tabs } from '@mantine/core';
import { IconUsers, IconActivity, IconDatabase, IconShieldCheck } from '@tabler/icons-react';
import { UserManagementTable } from '../components/admin/UserManagementTable';

export function AdminDashboardPage() {
  const [activeTab, setActiveTab] = useState<string | null>('users');

  return (
    <div>
      <Title order={2} mb="xs">
        PaperForge Administration Studio
      </Title>
      <Text c="dimmed" mb="lg">
        Manage users, roles, active sessions, storage quotas, and security audit logs.
      </Text>

      <Tabs value={activeTab} onChange={setActiveTab}>
        <Tabs.List mb="md">
          <Tabs.Tab value="users" leftSection={<IconUsers size={16} />}>
            Users & Roles
          </Tabs.Tab>
          <Tabs.Tab value="storage" leftSection={<IconDatabase size={16} />}>
            Storage Quotas
          </Tabs.Tab>
          <Tabs.Tab value="jobs" leftSection={<IconActivity size={16} />}>
            Processing Jobs
          </Tabs.Tab>
          <Tabs.Tab value="audit" leftSection={<IconShieldCheck size={16} />}>
            Security Audit Logs
          </Tabs.Tab>
        </Tabs.List>

        <Tabs.Panel value="users">
          <UserManagementTable />
        </Tabs.Panel>

        <Tabs.Panel value="storage">
          <Text c="dimmed" size="sm">Global storage monitoring and per-user quota allocation overview.</Text>
        </Tabs.Panel>

        <Tabs.Panel value="jobs">
          <Text c="dimmed" size="sm">Active document conversion and OCR background processing job queues.</Text>
        </Tabs.Panel>

        <Tabs.Panel value="audit">
          <Text c="dimmed" size="sm">Security event audit stream displaying security requests, logins, and administrative actions.</Text>
        </Tabs.Panel>
      </Tabs>
    </div>
  );
}

import { NavLink } from 'react-router-dom';
import { NavLink as MantineNavLink, Stack } from '@mantine/core';
import {
  IconLayoutDashboard,
  IconTools,
  IconEdit,
  IconGitFork,
  IconKey,
  IconUserShield,
  IconSettings,
  IconHelp,
  IconInfoCircle,
} from '@tabler/icons-react';

const navItems = [
  { path: '/', label: 'Dashboard', icon: IconLayoutDashboard },
  { path: '/tools', label: 'Tools', icon: IconTools },
  { path: '/editor', label: 'PDF Editor', icon: IconEdit },
  { path: '/pipelines', label: 'Pipeline Builder', icon: IconGitFork },
  { path: '/api-keys', label: 'API Keys', icon: IconKey },
  { path: '/admin', label: 'Admin Studio', icon: IconUserShield },
  { path: '/admin/audit-logs', label: 'Audit Logs', icon: IconHelp },
  { path: '/settings', label: 'Settings', icon: IconSettings },
  { path: '/help', label: 'Help & Docs', icon: IconHelp },
  { path: '/about', label: 'About', icon: IconInfoCircle },
];

export function Sidebar() {
  return (
    <Stack gap="xs" p="xs">
      {navItems.map((item) => (
        <NavLink
          key={item.path}
          to={item.path}
          style={{ textDecoration: 'none' }}
        >
          {({ isActive }) => (
            <MantineNavLink
              label={item.label}
              leftSection={<item.icon size={20} stroke={1.5} />}
              active={isActive}
              variant="filled"
            />
          )}
        </NavLink>
      ))}
    </Stack>
  );
}

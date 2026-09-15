import { NavLink } from 'react-router-dom';
import { NavLink as MantineNavLink, Stack } from '@mantine/core';
import { useTranslation } from 'react-i18next';
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

export function Sidebar() {
  const { t } = useTranslation();

  const navItems = [
    { path: '/', label: t('nav.dashboard'), icon: IconLayoutDashboard },
    { path: '/tools', label: t('nav.tools'), icon: IconTools },
    { path: '/editor', label: t('nav.editor'), icon: IconEdit },
    { path: '/pipelines', label: t('nav.pipelines'), icon: IconGitFork },
    { path: '/api-keys', label: t('nav.apiKeys'), icon: IconKey },
    { path: '/admin', label: t('nav.admin'), icon: IconUserShield },
    { path: '/admin/audit-logs', label: t('nav.auditLogs'), icon: IconHelp },
    { path: '/settings', label: t('nav.settings'), icon: IconSettings },
    { path: '/help', label: t('nav.help'), icon: IconHelp },
    { path: '/about', label: t('nav.about'), icon: IconInfoCircle },
  ];

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

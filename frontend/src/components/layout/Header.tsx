import { Group, Title, ActionIcon, useMantineColorScheme, Badge, Button, Menu, Avatar, Text } from '@mantine/core';
import { IconSun, IconMoon, IconFileTypePdf, IconUser, IconLogout, IconLogin } from '@tabler/icons-react';
import { useNavigate, Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuth } from '../../context/AuthContext';
import { LanguageSwitcher } from './LanguageSwitcher';

interface HeaderProps {
  healthStatus: string;
}

export function Header({ healthStatus }: HeaderProps) {
  const { colorScheme, toggleColorScheme } = useMantineColorScheme();
  const dark = colorScheme === 'dark';
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const { t } = useTranslation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <Group justify="space-between" h="100%" px="md">
      <Group gap="xs">
        <IconFileTypePdf size={28} color="#228be6" />
        <Title order={3} style={{ fontFamily: 'sans-serif' }}>
          PaperForge <TextSpan text={t('app.title')} />
        </Title>
      </Group>

      <Group gap="sm">
        <LanguageSwitcher />

        <Badge
          color={healthStatus === 'UP' ? 'green' : 'red'}
          variant="light"
          size="sm"
        >
          API: {healthStatus === 'UP' ? t('common.systemOnline') : t('common.systemOffline')}
        </Badge>

        <ActionIcon
          variant="default"
          onClick={() => toggleColorScheme()}
          size="lg"
          aria-label="Toggle color scheme"
        >
          {dark ? <IconSun size={18} /> : <IconMoon size={18} />}
        </ActionIcon>

        {isAuthenticated && user ? (
          <Menu shadow="md" width={200} position="bottom-end">
            <Menu.Target>
              <Button variant="subtle" leftSection={<Avatar color="blue" radius="xl" size="sm"><IconUser size={14} /></Avatar>}>
                <Text size="sm" fw={500}>{user.username}</Text>
              </Button>
            </Menu.Target>

            <Menu.Dropdown>
              <Menu.Label>Signed in as {user.username}</Menu.Label>
              <Menu.Item leftSection={<IconLogout size={14} />} color="red" onClick={handleLogout}>
                Logout
              </Menu.Item>
            </Menu.Dropdown>
          </Menu>
        ) : (
          <Button component={Link} to="/login" variant="light" size="xs" leftSection={<IconLogin size={14} />}>
            Sign In
          </Button>
        )}
      </Group>
    </Group>
  );
}

function TextSpan({ text }: { text: string }) {
  return (
    <span style={{ fontWeight: 400, opacity: 0.7, fontSize: '0.9em', marginLeft: '6px' }}>
      {text}
    </span>
  );
}

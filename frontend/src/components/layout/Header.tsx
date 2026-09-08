import { Group, Title, ActionIcon, useMantineColorScheme, Badge } from '@mantine/core';
import { IconSun, IconMoon, IconFileTypePdf } from '@tabler/icons-react';

interface HeaderProps {
  healthStatus: string;
}

export function Header({ healthStatus }: HeaderProps) {
  const { colorScheme, toggleColorScheme } = useMantineColorScheme();
  const dark = colorScheme === 'dark';

  return (
    <Group justify="space-between" h="100%" px="md">
      <Group gap="xs">
        <IconFileTypePdf size={28} color="#228be6" />
        <Title order={3} style={{ fontFamily: 'sans-serif' }}>
          PaperForge <TextSpan text="Document Studio" />
        </Title>
      </Group>

      <Group gap="md">
        <Badge
          color={healthStatus === 'UP' ? 'green' : 'red'}
          variant="light"
          size="sm"
        >
          API: {healthStatus}
        </Badge>

        <ActionIcon
          variant="default"
          onClick={() => toggleColorScheme()}
          size="lg"
          aria-label="Toggle color scheme"
        >
          {dark ? <IconSun size={18} /> : <IconMoon size={18} />}
        </ActionIcon>
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

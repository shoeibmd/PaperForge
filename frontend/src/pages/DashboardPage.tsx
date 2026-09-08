import { Title, Text, SimpleGrid, Card, Group, ThemeIcon, Button } from '@mantine/core';
import { IconFiles, IconTransform, IconShieldCheck, IconCpu } from '@tabler/icons-react';
import { Link } from 'react-router-dom';

export function DashboardPage() {
  const stats = [
    { title: 'Core PDF Tools', icon: IconFiles, color: 'blue', desc: 'Merge, split, rotate, crop & compress' },
    { title: 'Conversion Engine', icon: IconTransform, color: 'teal', desc: 'Office, image, HTML & text conversions' },
    { title: 'Security & Privacy', icon: IconShieldCheck, color: 'violet', desc: 'Redact, watermark & password protect' },
    { title: 'OCR Processing', icon: IconCpu, color: 'orange', desc: 'Optical character recognition engine' },
  ];

  return (
    <div>
      <Title order={2} mb="xs">
        Dashboard
      </Title>
      <Text c="dimmed" mb="lg">
        Welcome to PaperForge. Select a tool module below or navigate via the sidebar.
      </Text>

      <SimpleGrid cols={{ base: 1, sm: 2, lg: 4 }} spacing="md" mb="xl">
        {stats.map((stat) => (
          <Card key={stat.title} shadow="sm" padding="lg" radius="md" withBorder>
            <Group justify="space-between" mb="xs">
              <ThemeIcon color={stat.color} variant="light" size="lg">
                <stat.icon size={22} />
              </ThemeIcon>
            </Group>
            <Text fw={600} size="md" mt="sm">
              {stat.title}
            </Text>
            <Text size="xs" c="dimmed" mt="xs">
              {stat.desc}
            </Text>
          </Card>
        ))}
      </SimpleGrid>

      <Card shadow="sm" padding="lg" radius="md" withBorder>
        <Title order={4} mb="xs">
          Quick Start
        </Title>
        <Text size="sm" c="dimmed" mb="md">
          Explore all available document operations in the PaperForge Document Studio.
        </Text>
        <Button component={Link} to="/tools" variant="light" color="blue">
          Browse All Tools
        </Button>
      </Card>
    </div>
  );
}

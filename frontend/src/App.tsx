import { MantineProvider, Container, Title, Text, Card, Group } from '@mantine/core';
import '@mantine/core/styles.css';

export function App() {
  return (
    <MantineProvider>
      <Container size="md" py="xl">
        <Card shadow="sm" padding="lg" radius="md" withBorder>
          <Group justify="space-between" mt="md" mb="xs">
            <Title order={1}>PaperForge</Title>
          </Group>
          <Text size="lg" c="dimmed">
            Forge your documents.
          </Text>
          <Text mt="sm" size="sm">
            Welcome to PaperForge! Project structure initialized successfully.
          </Text>
        </Card>
      </Container>
    </MantineProvider>
  );
}

export default App;

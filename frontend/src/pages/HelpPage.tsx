import { Title, Text, Accordion, Card, Group, Button } from '@mantine/core';

export function HelpPage() {
  return (
    <div>
      <Title order={2} mb="xs">
        Help & Documentation
      </Title>
      <Text c="dimmed" mb="lg">
        Frequently asked questions and guides for PaperForge.
      </Text>

      <Card shadow="sm" padding="md" radius="md" withBorder mb="lg">
        <Group justify="space-between">
          <div>
            <Text fw={600}>Interactive OpenAPI 3.0 Documentation</Text>
            <Text size="xs" c="dimmed">Explore and test all PaperForge REST API endpoints via Swagger UI.</Text>
          </div>
          <Button component="a" href="http://localhost:8080/swagger-ui/index.html" target="_blank" variant="light" size="xs">
            Open Swagger UI
          </Button>
        </Group>
      </Card>

      <Card shadow="sm" padding="lg" radius="md" withBorder>
        <Accordion variant="separated">
          <Accordion.Item value="what-is-paperforge">
            <Accordion.Control>What is PaperForge?</Accordion.Control>
            <Accordion.Panel>
              PaperForge is an open-source, self-hosted document platform for manipulating, converting, OCRing, and securing PDF files.
            </Accordion.Panel>
          </Accordion.Item>

          <Accordion.Item value="privacy">
            <Accordion.Control>Is my data private?</Accordion.Control>
            <Accordion.Panel>
              Yes. PaperForge runs completely self-hosted with zero telemetry or third-party file uploads. Your documents never leave your server.
            </Accordion.Panel>
          </Accordion.Item>

          <Accordion.Item value="file-limits">
            <Accordion.Control>Are there file size or user limits?</Accordion.Control>
            <Accordion.Panel>
              No artificial user or file limits exist in PaperForge. Operations are limited only by your host infrastructure resources (CPU, RAM, storage).
            </Accordion.Panel>
          </Accordion.Item>
        </Accordion>
      </Card>
    </div>
  );
}

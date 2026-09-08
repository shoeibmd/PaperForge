import { Title, Text, SimpleGrid, Card, Badge, Group } from '@mantine/core';

interface ToolItem {
  id: string;
  name: string;
  category: string;
  description: string;
  phase: string;
}

const toolsList: ToolItem[] = [
  { id: 'merge', name: 'Merge PDFs', category: 'PDF Core', description: 'Combine multiple PDF documents into a single file.', phase: 'Phase 2' },
  { id: 'split', name: 'Split PDF', category: 'PDF Core', description: 'Extract pages or split PDFs into separate documents.', phase: 'Phase 2' },
  { id: 'compress', name: 'Compress PDF', category: 'PDF Core', description: 'Reduce document file size with customizable quality settings.', phase: 'Phase 7' },
  { id: 'convert-office', name: 'Office to PDF', category: 'Conversion', description: 'Convert Word, Excel, and PowerPoint files to PDF.', phase: 'Phase 4' },
  { id: 'ocr', name: 'OCR Text Recognition', category: 'OCR', description: 'Extract searchable text from scanned PDFs and images.', phase: 'Phase 6' },
  { id: 'protect', name: 'Password Protect', category: 'Security', description: 'Encrypt and secure PDF documents with passwords.', phase: 'Phase 3' },
];

export function ToolsPage() {
  return (
    <div>
      <Title order={2} mb="xs">
        Document Tools
      </Title>
      <Text c="dimmed" mb="lg">
        All document forge operations available in PaperForge.
      </Text>

      <SimpleGrid cols={{ base: 1, sm: 2, md: 3 }} spacing="md">
        {toolsList.map((tool) => (
          <Card key={tool.id} shadow="sm" padding="lg" radius="md" withBorder>
            <Group justify="space-between" mb="xs">
              <Badge color="blue" variant="light">
                {tool.category}
              </Badge>
              <Badge color="gray" variant="outline" size="xs">
                {tool.phase}
              </Badge>
            </Group>
            <Text fw={600} size="lg" mt="xs">
              {tool.name}
            </Text>
            <Text size="sm" c="dimmed" mt="xs">
              {tool.description}
            </Text>
          </Card>
        ))}
      </SimpleGrid>
    </div>
  );
}

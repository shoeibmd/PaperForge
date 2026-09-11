import { useState } from 'react';
import { Title, Text, SimpleGrid, Card, Badge, Group, Modal, UnstyledButton } from '@mantine/core';
import { MergePdfTool } from '../components/tools/MergePdfTool';
import { SplitPdfTool } from '../components/tools/SplitPdfTool';
import { RotatePdfTool } from '../components/tools/RotatePdfTool';
import { CropPdfTool } from '../components/tools/CropPdfTool';
import { MetadataPdfTool } from '../components/tools/MetadataPdfTool';
import { ProtectPdfTool } from '../components/tools/ProtectPdfTool';
import { DecryptPdfTool } from '../components/tools/DecryptPdfTool';
import { ConvertToPdfTool } from '../components/tools/ConvertToPdfTool';
import { PdfToDocTool } from '../components/tools/PdfToDocTool';
import { ImageToPdfTool } from '../components/tools/ImageToPdfTool';
import { PdfToImageTool } from '../components/tools/PdfToImageTool';
import { OcrTool } from '../components/tools/OcrTool';

interface ToolItem {
  id: string;
  name: string;
  category: string;
  description: string;
  phase: string;
  active: boolean;
}

const toolsList: ToolItem[] = [
  { id: 'merge', name: 'Merge PDFs', category: 'PDF Core', description: 'Combine multiple PDF documents into a single file.', phase: 'Phase 2', active: true },
  { id: 'split', name: 'Split PDF', category: 'PDF Core', description: 'Extract pages or split PDFs into separate documents.', phase: 'Phase 2', active: true },
  { id: 'rotate', name: 'Rotate PDF', category: 'PDF Core', description: 'Rotate pages by 90, 180, or 270 degrees.', phase: 'Phase 2', active: true },
  { id: 'crop', name: 'Crop PDF', category: 'PDF Core', description: 'Adjust document crop margins and page bounding boxes.', phase: 'Phase 2', active: true },
  { id: 'metadata', name: 'Edit Metadata', category: 'PDF Core', description: 'View and update PDF title, author, subject, and keywords.', phase: 'Phase 2', active: true },
  { id: 'protect', name: 'Password Protect', category: 'Security', description: 'Encrypt PDFs and set printing/copying permission controls.', phase: 'Phase 3', active: true },
  { id: 'decrypt', name: 'Remove Password', category: 'Security', description: 'Unlock encrypted PDFs by removing password protection.', phase: 'Phase 3', active: true },
  { id: 'convert-to-pdf', name: 'Convert to PDF', category: 'Conversion', description: 'Convert Word, Excel, PowerPoint, Text, and HTML to PDF.', phase: 'Phase 4', active: true },
  { id: 'pdf-to-doc', name: 'PDF to Office/Text', category: 'Conversion', description: 'Export PDF documents to Word, Excel, PowerPoint, or Text.', phase: 'Phase 4', active: true },
  { id: 'img-to-pdf', name: 'Images to PDF', category: 'Image Processing', description: 'Convert JPEG, PNG, WEBP, and TIFF images to PDF.', phase: 'Phase 5', active: true },
  { id: 'pdf-to-img', name: 'PDF to Images', category: 'Image Processing', description: 'Render PDF pages into PNG, JPEG, WEBP, or TIFF images.', phase: 'Phase 5', active: true },
  { id: 'ocr', name: 'OCR Text Recognition', category: 'OCR', description: 'Extract searchable text from scanned PDFs and images.', phase: 'Phase 6', active: true },
  { id: 'compress', name: 'Compress PDF', category: 'PDF Core', description: 'Reduce document file size with customizable quality settings.', phase: 'Phase 7', active: false },
];

export function ToolsPage() {
  const [activeToolId, setActiveToolId] = useState<string | null>(null);

  const activeTool = toolsList.find((t) => t.id === activeToolId);

  const renderToolComponent = () => {
    switch (activeToolId) {
      case 'merge':
        return <MergePdfTool />;
      case 'split':
        return <SplitPdfTool />;
      case 'rotate':
        return <RotatePdfTool />;
      case 'crop':
        return <CropPdfTool />;
      case 'metadata':
        return <MetadataPdfTool />;
      case 'protect':
        return <ProtectPdfTool />;
      case 'decrypt':
        return <DecryptPdfTool />;
      case 'convert-to-pdf':
        return <ConvertToPdfTool />;
      case 'pdf-to-doc':
        return <PdfToDocTool />;
      case 'img-to-pdf':
        return <ImageToPdfTool opened={true} onClose={() => setActiveToolId(null)} />;
      case 'pdf-to-img':
        return <PdfToImageTool opened={true} onClose={() => setActiveToolId(null)} />;
      case 'ocr':
        return <OcrTool opened={true} onClose={() => setActiveToolId(null)} />;
      default:
        return null;
    }
  };

  return (
    <div>
      <Title order={2} mb="xs">
        Document Tools
      </Title>
      <Text c="dimmed" mb="lg">
        All document forge operations available in PaperForge. Click any active tool to launch it.
      </Text>

      <SimpleGrid cols={{ base: 1, sm: 2, md: 3 }} spacing="md">
        {toolsList.map((tool) => (
          <UnstyledButton
            key={tool.id}
            onClick={() => tool.active && setActiveToolId(tool.id)}
            style={{ cursor: tool.active ? 'pointer' : 'not-allowed' }}
          >
            <Card
              shadow="sm"
              padding="lg"
              radius="md"
              withBorder
              style={{
                height: '100%',
                opacity: tool.active ? 1 : 0.6,
                borderColor: tool.active ? '#228be6' : undefined,
              }}
            >
              <Group justify="space-between" mb="xs">
                <Badge color={tool.active ? 'blue' : 'gray'} variant="light">
                  {tool.category}
                </Badge>
                <Badge color={tool.active ? 'green' : 'gray'} variant="outline" size="xs">
                  {tool.active ? 'Ready' : tool.phase}
                </Badge>
              </Group>
              <Text fw={600} size="lg" mt="xs">
                {tool.name}
              </Text>
              <Text size="sm" c="dimmed" mt="xs">
                {tool.description}
              </Text>
            </Card>
          </UnstyledButton>
        ))}
      </SimpleGrid>

      <Modal
        opened={!!activeToolId}
        onClose={() => setActiveToolId(null)}
        title={<Text fw={700}>{activeTool?.name}</Text>}
        size="lg"
        centered
      >
        {renderToolComponent()}
      </Modal>
    </div>
  );
}

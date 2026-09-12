import React from 'react';
import { Group, Button, ActionIcon, Select, Tooltip, FileInput, Text } from '@mantine/core';
import {
  IconUpload,
  IconZoomIn,
  IconZoomOut,
  IconHighlight,
  IconPencil,
  IconTextPlus,
  IconDownload,
  IconTrash,
  IconRotateClockwise,
  IconCopy,
} from '@tabler/icons-react';

export type EditorMode = 'view' | 'select' | 'highlight' | 'draw' | 'text';

interface EditorToolbarProps {
  onFileSelect: (file: File) => void;
  zoom: number;
  onZoomChange: (newZoom: number) => void;
  mode: EditorMode;
  onModeChange: (newMode: EditorMode) => void;
  onSave: () => void;
  onRotatePage: () => void;
  onDuplicatePage: () => void;
  onDeletePage: () => void;
  hasDocument: boolean;
  activePageIndex: number;
  totalPages: number;
}

export const EditorToolbar: React.FC<EditorToolbarProps> = ({
  onFileSelect,
  zoom,
  onZoomChange,
  mode,
  onModeChange,
  onSave,
  onRotatePage,
  onDuplicatePage,
  onDeletePage,
  hasDocument,
  activePageIndex,
  totalPages,
}) => {
  return (
    <Group justify="space-between" p="xs" style={{ borderBottom: '1px solid #dee2e6', backgroundColor: '#fff' }}>
      <Group gap="xs">
        <FileInput
          placeholder="Open PDF"
          accept="application/pdf"
          leftSection={<IconUpload size={16} />}
          onChange={(f) => f && onFileSelect(f)}
          size="xs"
        />

        {hasDocument && (
          <>
            <Tooltip label="Highlight Text">
              <ActionIcon
                variant={mode === 'highlight' ? 'filled' : 'light'}
                color="yellow"
                onClick={() => onModeChange(mode === 'highlight' ? 'view' : 'highlight')}
              >
                <IconHighlight size={18} />
              </ActionIcon>
            </Tooltip>

            <Tooltip label="Draw Freehand">
              <ActionIcon
                variant={mode === 'draw' ? 'filled' : 'light'}
                color="blue"
                onClick={() => onModeChange(mode === 'draw' ? 'view' : 'draw')}
              >
                <IconPencil size={18} />
              </ActionIcon>
            </Tooltip>

            <Tooltip label="Add Text Annotation">
              <ActionIcon
                variant={mode === 'text' ? 'filled' : 'light'}
                color="teal"
                onClick={() => onModeChange(mode === 'text' ? 'view' : 'text')}
              >
                <IconTextPlus size={18} />
              </ActionIcon>
            </Tooltip>
          </>
        )}
      </Group>

      {hasDocument && (
        <Group gap="xs">
          <Text size="xs" c="dimmed">
            Page {activePageIndex + 1} of {totalPages}
          </Text>

          <Tooltip label="Rotate Active Page">
            <ActionIcon variant="light" onClick={onRotatePage}>
              <IconRotateClockwise size={18} />
            </ActionIcon>
          </Tooltip>

          <Tooltip label="Duplicate Active Page">
            <ActionIcon variant="light" onClick={onDuplicatePage}>
              <IconCopy size={18} />
            </ActionIcon>
          </Tooltip>

          <Tooltip label="Delete Active Page">
            <ActionIcon variant="light" color="red" onClick={onDeletePage}>
              <IconTrash size={18} />
            </ActionIcon>
          </Tooltip>

          <Group gap={4}>
            <ActionIcon
              variant="subtle"
              size="sm"
              disabled={zoom <= 0.5}
              onClick={() => onZoomChange(Math.max(0.5, zoom - 0.25))}
            >
              <IconZoomOut size={16} />
            </ActionIcon>

            <Select
              size="xs"
              style={{ width: 90 }}
              value={Math.round(zoom * 100).toString()}
              onChange={(v) => v && onZoomChange(parseInt(v, 10) / 100)}
              data={[
                { value: '50', label: '50%' },
                { value: '75', label: '75%' },
                { value: '100', label: '100%' },
                { value: '125', label: '125%' },
                { value: '150', label: '150%' },
                { value: '200', label: '200%' },
              ]}
            />

            <ActionIcon
              variant="subtle"
              size="sm"
              disabled={zoom >= 2.5}
              onClick={() => onZoomChange(Math.min(2.5, zoom + 0.25))}
            >
              <IconZoomIn size={16} />
            </ActionIcon>
          </Group>
        </Group>
      )}

      {hasDocument && (
        <Button size="xs" leftSection={<IconDownload size={16} />} onClick={onSave}>
          Export PDF
        </Button>
      )}
    </Group>
  );
};

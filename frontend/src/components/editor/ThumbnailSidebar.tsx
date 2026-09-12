import React from 'react';
import { Stack, Card, Image, Text, ActionIcon, Group, Badge } from '@mantine/core';
import { IconArrowUp, IconArrowDown, IconTrash, IconRotateClockwise } from '@tabler/icons-react';

interface ThumbnailSidebarProps {
  thumbnails: { pageIndex: number; dataUrl: string }[];
  activePageIndex: number;
  onPageSelect: (pageIndex: number) => void;
  onMovePage: (fromIndex: number, toIndex: number) => void;
  onRotatePage: (pageIndex: number) => void;
  onDeletePage: (pageIndex: number) => void;
}

export const ThumbnailSidebar: React.FC<ThumbnailSidebarProps> = ({
  thumbnails,
  activePageIndex,
  onPageSelect,
  onMovePage,
  onRotatePage,
  onDeletePage,
}) => {
  return (
    <Stack
      gap="xs"
      p="xs"
      style={{
        width: 220,
        borderRight: '1px solid #dee2e6',
        backgroundColor: '#f8f9fa',
        overflowY: 'auto',
        maxHeight: 'calc(100vh - 120px)',
      }}
    >
      <Text size="xs" fw={700} c="dimmed" tt="uppercase">
        Page Thumbnails ({thumbnails.length})
      </Text>

      {thumbnails.map((thumb, idx) => {
        const isActive = idx === activePageIndex;

        return (
          <Card
            key={`page-${idx}`}
            padding="xs"
            radius="sm"
            withBorder
            style={{
              cursor: 'pointer',
              borderColor: isActive ? '#228be6' : '#dee2e6',
              borderWidth: isActive ? 2 : 1,
              backgroundColor: isActive ? '#e7f5ff' : '#fff',
            }}
            onClick={() => onPageSelect(idx)}
          >
            <Group justify="space-between" mb="xs">
              <Badge size="xs" variant={isActive ? 'filled' : 'light'} color={isActive ? 'blue' : 'gray'}>
                Page {idx + 1}
              </Badge>
              <Group gap={2} onClick={(e) => e.stopPropagation()}>
                <ActionIcon
                  size="xs"
                  variant="subtle"
                  disabled={idx === 0}
                  onClick={() => onMovePage(idx, idx - 1)}
                >
                  <IconArrowUp size={12} />
                </ActionIcon>
                <ActionIcon
                  size="xs"
                  variant="subtle"
                  disabled={idx === thumbnails.length - 1}
                  onClick={() => onMovePage(idx, idx + 1)}
                >
                  <IconArrowDown size={12} />
                </ActionIcon>
                <ActionIcon size="xs" variant="subtle" onClick={() => onRotatePage(idx)}>
                  <IconRotateClockwise size={12} />
                </ActionIcon>
                <ActionIcon
                  size="xs"
                  variant="subtle"
                  color="red"
                  disabled={thumbnails.length <= 1}
                  onClick={() => onDeletePage(idx)}
                >
                  <IconTrash size={12} />
                </ActionIcon>
              </Group>
            </Group>

            {thumb.dataUrl ? (
              <Image src={thumb.dataUrl} alt={`Thumbnail page ${idx + 1}`} radius="xs" fit="contain" h={140} />
            ) : (
              <div style={{ height: 140, display: 'flex', alignItems: 'center', justifyContent: 'center', backgroundColor: '#e9ecef' }}>
                <Text size="xs" c="dimmed">
                  Rendering...
                </Text>
              </div>
            )}
          </Card>
        );
      })}
    </Stack>
  );
};

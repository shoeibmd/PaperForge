import { useState, useEffect, useRef } from 'react';
import {
  Title,
  Text,
  Button,
  TextInput,
  Select,
  Card,
  Group,
  Badge,
  Table,
  Pagination,
  Alert,
  Code,
  Modal,
  Stack
} from '@mantine/core';
import { useVirtualizer } from '@tanstack/react-virtual';
import { IconDownload, IconSearch, IconAlertCircle, IconFilter } from '@tabler/icons-react';
import { auditLogApi, AuditLogDto } from '../services/auditLogApi';
import { useAuth } from '../context/AuthContext';

const EVENT_TYPES = [
  'LOGIN',
  'LOGOUT',
  'LOGIN_FAILED',
  'USER_CREATED',
  'USER_DELETED',
  'USER_DISABLED',
  'ROLE_CHANGED',
  'API_KEY_CREATED',
  'API_KEY_REVOKED',
  'API_KEY_AUTH_SUCCESS',
  'API_KEY_AUTH_FAILED',
  'FILE_UPLOADED',
  'FILE_PROCESSED',
  'FILE_DELETED',
  'JOB_SUBMITTED',
  'JOB_COMPLETED',
  'JOB_FAILED',
  'ADMIN_ACTION',
  'RATE_LIMIT_EXCEEDED',
  'PATH_TRAVERSAL_ATTEMPT',
];

export function AuditLogPage() {
  const [logs, setLogs] = useState<AuditLogDto[]>([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);
  const [eventType, setEventType] = useState<string | null>(null);
  const [username, setUsername] = useState('');
  const [query, setQuery] = useState('');
  const [selectedLog, setSelectedLog] = useState<AuditLogDto | null>(null);
  const [error, setError] = useState<string | null>(null);
  const { token } = useAuth();

  const tableContainerRef = useRef<HTMLDivElement>(null);

  const fetchLogs = async (targetPage: number) => {
    if (!token) return;
    try {
      const data = await auditLogApi.getAuditLogs(
        {
          eventType: eventType || undefined,
          username: username.trim() || undefined,
          query: query.trim() || undefined,
          page: targetPage - 1,
          size: 20,
        },
        token
      );
      setLogs(data.content);
      setTotalPages(data.totalPages || 1);
      setTotalElements(data.totalElements || 0);
    } catch (err: any) {
      setError(err.message || 'Failed to load audit logs');
    }
  };

  useEffect(() => {
    fetchLogs(page);
  }, [page, token]);

  const rowVirtualizer = useVirtualizer({
    count: logs.length,
    getScrollElement: () => tableContainerRef.current,
    estimateSize: () => 48,
    overscan: 5,
  });

  const handleApplyFilter = () => {
    setPage(1);
    fetchLogs(1);
  };

  const handleExportCsv = async () => {
    if (!token) return;
    try {
      await auditLogApi.downloadCsvExport(
        {
          eventType: eventType || undefined,
          username: username.trim() || undefined,
          query: query.trim() || undefined,
        },
        token
      );
    } catch (err: any) {
      setError(err.message || 'Failed to export CSV');
    }
  };

  return (
    <div>
      <Group justify="space-between" align="center" mb="md">
        <div>
          <Title order={2}>Security Audit Logs</Title>
          <Text c="dimmed">
            Administrator governance and tamper-evident event stream monitoring ({totalElements} events logged)
          </Text>
        </div>
        <Button leftSection={<IconDownload size={16} />} color="teal" onClick={handleExportCsv}>
          Export CSV Report
        </Button>
      </Group>

      {error && (
        <Alert icon={<IconAlertCircle size={16} />} color="red" mb="md" onClose={() => setError(null)} withCloseButton>
          {error}
        </Alert>
      )}

      <Card withBorder padding="md" radius="md" mb="xl">
        <Group align="flex-end">
          <Select
            label="Event Type"
            placeholder="All Event Types"
            data={EVENT_TYPES.map((t) => ({ value: t, label: t }))}
            value={eventType}
            onChange={setEventType}
            clearable
            style={{ flexGrow: 1 }}
          />
          <TextInput
            label="Username Filter"
            placeholder="Filter by username"
            value={username}
            onChange={(e) => setUsername(e.currentTarget.value)}
            style={{ flexGrow: 1 }}
          />
          <TextInput
            label="Keyword Search"
            placeholder="Search details or resource ID"
            leftSection={<IconSearch size={16} />}
            value={query}
            onChange={(e) => setQuery(e.currentTarget.value)}
            style={{ flexGrow: 1 }}
          />
          <Button leftSection={<IconFilter size={16} />} onClick={handleApplyFilter}>
            Apply Filters
          </Button>
        </Group>
      </Card>

      <div ref={tableContainerRef} style={{ maxHeight: '600px', overflowY: 'auto' }}>
        <Table striped highlightOnHover>
          <Table.Thead style={{ position: 'sticky', top: 0, backgroundColor: 'var(--mantine-color-body)', zIndex: 1 }}>
            <Table.Tr>
              <Table.Th>Timestamp</Table.Th>
              <Table.Th>Event Type</Table.Th>
              <Table.Th>User</Table.Th>
              <Table.Th>Client IP</Table.Th>
              <Table.Th>Resource ID</Table.Th>
              <Table.Th>Status</Table.Th>
              <Table.Th style={{ textAlign: 'right' }}>Details</Table.Th>
            </Table.Tr>
          </Table.Thead>
          <Table.Tbody>
            {logs.length === 0 ? (
              <Table.Tr>
                <Table.Td colSpan={7} style={{ textAlign: 'center' }}>
                  No audit log entries found.
                </Table.Td>
              </Table.Tr>
            ) : (
              rowVirtualizer.getVirtualItems().map((virtualRow) => {
                const log = logs[virtualRow.index];
                return (
                  <Table.Tr key={log.id} style={{ height: `${virtualRow.size}px` }}>
                    <Table.Td>{new Date(log.timestamp).toLocaleString()}</Table.Td>
                    <Table.Td>
                      <Badge color={log.eventType.includes('FAIL') || log.eventType.includes('REVOKED') ? 'red' : 'blue'} variant="light">
                        {log.eventType}
                      </Badge>
                    </Table.Td>
                    <Table.Td fw={600}>{log.username || 'ANONYMOUS'}</Table.Td>
                    <Table.Td>{log.clientIp}</Table.Td>
                    <Table.Td>{log.resourceId || '—'}</Table.Td>
                    <Table.Td>
                      <Badge color={log.success ? 'green' : 'red'}>
                        {log.success ? 'SUCCESS' : 'FAILED'}
                      </Badge>
                    </Table.Td>
                    <Table.Td style={{ textAlign: 'right' }}>
                      <Button variant="subtle" size="xs" onClick={() => setSelectedLog(log)}>
                        View JSON
                      </Button>
                    </Table.Td>
                  </Table.Tr>
                );
              })
            )}
          </Table.Tbody>
        </Table>
      </div>

      {totalPages > 1 && (
        <Group justify="center" mt="xl">
          <Pagination value={page} onChange={setPage} total={totalPages} />
        </Group>
      )}

      <Modal opened={!!selectedLog} onClose={() => setSelectedLog(null)} title="Audit Event Payload JSON" centered size="lg">
        {selectedLog && (
          <Stack gap="xs">
            <Text size="sm">
              <strong>Event ID:</strong> {selectedLog.id} | <strong>Timestamp:</strong> {new Date(selectedLog.timestamp).toLocaleString()}
            </Text>
            <Text size="sm">
              <strong>User Agent:</strong> {selectedLog.userAgent || 'Not Specified'}
            </Text>
            <Text size="sm" fw={600} mt="xs">
              Details JSON (Sanitized & Redacted):
            </Text>
            <Code block style={{ maxHeight: '300px', overflowY: 'auto' }}>
              {selectedLog.detailsJson}
            </Code>
          </Stack>
        )}
      </Modal>
    </div>
  );
}

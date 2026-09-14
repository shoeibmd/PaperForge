import React, { useEffect, useState } from 'react';
import { Table, Badge, Progress, Button, Group, ActionIcon, Alert } from '@mantine/core';
import { IconX, IconRefresh, IconDownload } from '@tabler/icons-react';
import { jobApi, JobResponse } from '../../services/jobApi';
import { useAuth } from '../../context/AuthContext';

export const JobsOverview: React.FC = () => {
  const [jobs, setJobs] = useState<JobResponse[]>([]);
  const [error, setError] = useState<string | null>(null);
  const { token } = useAuth();

  const fetchJobs = async () => {
    if (!token) return;
    try {
      const data = await jobApi.getUserJobs(token);
      setJobs(data);
    } catch (err: any) {
      setError(err.message || 'Failed to load background processing jobs');
    }
  };

  useEffect(() => {
    fetchJobs();
    const interval = setInterval(fetchJobs, 5000);
    return () => clearInterval(interval);
  }, [token]);

  const handleCancelJob = async (id: string) => {
    if (!token) return;
    try {
      await jobApi.cancelJob(id, token);
      fetchJobs();
    } catch (err: any) {
      setError(err.message);
    }
  };

  const getStatusBadgeColor = (status: JobResponse['status']) => {
    switch (status) {
      case 'COMPLETED': return 'green';
      case 'PROCESSING': return 'blue';
      case 'QUEUED': return 'yellow';
      case 'FAILED': return 'red';
      case 'CANCELLED': return 'gray';
      case 'EXPIRED': return 'orange';
      default: return 'gray';
    }
  };

  return (
    <div>
      {error && <Alert color="red" mb="md" onClose={() => setError(null)} withCloseButton>{error}</Alert>}

      <Group justify="space-between" mb="md">
        <Button size="xs" leftSection={<IconRefresh size={14} />} onClick={fetchJobs}>
          Refresh Queue
        </Button>
      </Group>

      <Table striped highlightOnHover>
        <Table.Thead>
          <Table.Tr>
            <Table.Th>Job ID</Table.Th>
            <Table.Th>Type</Table.Th>
            <Table.Th>Input File</Table.Th>
            <Table.Th>Status</Table.Th>
            <Table.Th style={{ width: 150 }}>Progress</Table.Th>
            <Table.Th style={{ textAlign: 'right' }}>Actions</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {jobs.length === 0 ? (
            <Table.Tr>
              <Table.Td colSpan={6} style={{ textAlign: 'center' }}>
                No active or historical background jobs found.
              </Table.Td>
            </Table.Tr>
          ) : (
            jobs.map((j) => (
              <Table.Tr key={j.id}>
                <Table.Td style={{ fontFamily: 'monospace' }}>{j.id.substring(0, 8)}...</Table.Td>
                <Table.Td fw={500}>{j.jobType}</Table.Td>
                <Table.Td>{j.inputFilename}</Table.Td>
                <Table.Td>
                  <Badge color={getStatusBadgeColor(j.status)} size="xs">
                    {j.status}
                  </Badge>
                </Table.Td>
                <Table.Td>
                  <Progress value={j.progressPercent} size="sm" color={j.status === 'COMPLETED' ? 'green' : 'blue'} />
                </Table.Td>
                <Table.Td style={{ textAlign: 'right' }}>
                  <Group gap={4} justify="flex-end">
                    {j.status === 'COMPLETED' && (
                      <ActionIcon component="a" href={`/api/v1/jobs/${j.id}/download`} size="sm" color="blue" variant="subtle">
                        <IconDownload size={14} />
                      </ActionIcon>
                    )}
                    {(j.status === 'QUEUED' || j.status === 'PROCESSING') && (
                      <ActionIcon size="sm" color="red" variant="subtle" onClick={() => handleCancelJob(j.id)}>
                        <IconX size={14} />
                      </ActionIcon>
                    )}
                  </Group>
                </Table.Td>
              </Table.Tr>
            ))
          )}
        </Table.Tbody>
      </Table>
    </div>
  );
};

import { useState, useEffect } from 'react';
import { Title, Text, Button, TextInput, Textarea, Card, Group, Stack, Badge, Select, ActionIcon, Alert, Table } from '@mantine/core';
import { IconTrash, IconAlertCircle, IconArrowRight } from '@tabler/icons-react';
import { pipelineApi, ToolMetadata, Pipeline } from '../services/pipelineApi';
import { useAuth } from '../context/AuthContext';

export function PipelinesPage() {
  const [tools, setTools] = useState<ToolMetadata[]>([]);
  const [pipelines, setPipelines] = useState<Pipeline[]>([]);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [selectedSteps, setSelectedSteps] = useState<string[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [validationMsg, setValidationMsg] = useState<string | null>(null);
  const { token } = useAuth();

  const fetchToolsAndPipelines = async () => {
    if (!token) return;
    try {
      const [toolList, pipelineList] = await Promise.all([
        pipelineApi.getTools(token),
        pipelineApi.getPipelines(token),
      ]);
      setTools(toolList);
      setPipelines(pipelineList);
    } catch (err: any) {
      setError(err.message || 'Failed to load pipeline data');
    }
  };

  useEffect(() => {
    fetchToolsAndPipelines();
  }, [token]);

  const validateChain = (steps: string[]) => {
    if (steps.length === 0) {
      setValidationMsg(null);
      return true;
    }

    let currentProduce = '';
    for (let i = 0; i < steps.length; i++) {
      const tool = tools.find((t) => t.id === steps[i]);
      if (!tool) continue;

      if (i === 0) {
        currentProduce = tool.produces;
      } else {
        if (tool.accepts !== 'ANY' && currentProduce !== 'ANY' && tool.accepts !== currentProduce) {
          setValidationMsg(`Incompatible Step ${i + 1} (${tool.name}): Accepts ${tool.accepts}, but previous step produces ${currentProduce}`);
          return false;
        }
        currentProduce = tool.produces;
      }
    }

    setValidationMsg('Pipeline Chain Compatible & Validated');
    return true;
  };

  const handleAddStep = (toolId: string) => {
    const newSteps = [...selectedSteps, toolId];
    setSelectedSteps(newSteps);
    validateChain(newSteps);
  };

  const handleRemoveStep = (idx: number) => {
    const newSteps = selectedSteps.filter((_, i) => i !== idx);
    setSelectedSteps(newSteps);
    validateChain(newSteps);
  };

  const handleCreatePipeline = async () => {
    if (!token || !name || selectedSteps.length === 0) return;
    try {
      const stepsPayload = selectedSteps.map((toolId) => ({ toolId }));
      await pipelineApi.createPipeline(name, description, stepsPayload, token);
      setName('');
      setDescription('');
      setSelectedSteps([]);
      setValidationMsg(null);
      fetchToolsAndPipelines();
    } catch (err: any) {
      setError(err.message || 'Failed to create pipeline');
    }
  };

  const handleDeletePipeline = async (id: number) => {
    if (!token) return;
    try {
      await pipelineApi.deletePipeline(id, token);
      fetchToolsAndPipelines();
    } catch (err: any) {
      setError(err.message);
    }
  };

  return (
    <div>
      <Title order={2} mb="xs">Visual Pipeline Builder</Title>
      <Text c="dimmed" mb="lg">Chain multiple document forge tools into automated multi-step pipelines.</Text>

      {error && <Alert icon={<IconAlertCircle size={16} />} color="red" mb="md" onClose={() => setError(null)} withCloseButton>{error}</Alert>}

      <Card withBorder padding="md" radius="md" mb="xl">
        <Title order={4} mb="md">Create Automated Pipeline</Title>
        <Stack gap="sm">
          <Group grow>
            <TextInput label="Pipeline Name" placeholder="e.g. Scan & Optimize Workflow" value={name} onChange={(e) => setName(e.currentTarget.value)} required />
            <Select label="Add Tool Step" placeholder="Choose a tool step" data={tools.map((t) => ({ value: t.id, label: `${t.name} (${t.accepts} -> ${t.produces})` }))} onChange={(v) => v && handleAddStep(v)} />
          </Group>

          <Textarea label="Description" placeholder="Optional pipeline description" value={description} onChange={(e) => setDescription(e.currentTarget.value)} rows={2} />

          {selectedSteps.length > 0 && (
            <div>
              <Text size="sm" fw={600} mb="xs">Pipeline Step Sequence:</Text>
              <Group gap="xs" wrap="wrap">
                {selectedSteps.map((stepId, idx) => {
                  const tool = tools.find((t) => t.id === stepId);
                  return (
                    <Group key={`step-${idx}`} gap={4}>
                      <Badge variant="filled" color="blue" size="lg" style={{ textTransform: 'none' }}>
                        {idx + 1}. {tool?.name} ({tool?.accepts} ➔ {tool?.produces})
                        <ActionIcon size="xs" color="white" variant="subtle" ml={4} onClick={() => handleRemoveStep(idx)}>
                          <IconTrash size={10} />
                        </ActionIcon>
                      </Badge>
                      {idx < selectedSteps.length - 1 && <IconArrowRight size={16} color="gray" />}
                    </Group>
                  );
                })}
              </Group>
            </div>
          )}

          {validationMsg && (
            <Alert color={validationMsg.includes('Compatible') ? 'green' : 'red'}>
              {validationMsg}
            </Alert>
          )}

          <Group justify="flex-end" mt="sm">
            <Button onClick={handleCreatePipeline} disabled={!name || selectedSteps.length === 0 || (validationMsg !== null && !validationMsg.includes('Compatible'))}>
              Save Pipeline
            </Button>
          </Group>
        </Stack>
      </Card>

      <Title order={3} mb="md">Saved User Pipelines</Title>
      <Table striped highlightOnHover>
        <Table.Thead>
          <Table.Tr>
            <Table.Th>Name</Table.Th>
            <Table.Th>Description</Table.Th>
            <Table.Th>Steps Count</Table.Th>
            <Table.Th style={{ textAlign: 'right' }}>Actions</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {pipelines.length === 0 ? (
            <Table.Tr>
              <Table.Td colSpan={4} style={{ textAlign: 'center' }}>No saved pipelines found.</Table.Td>
            </Table.Tr>
          ) : (
            pipelines.map((p) => (
              <Table.Tr key={p.id}>
                <Table.Td fw={600}>{p.name}</Table.Td>
                <Table.Td>{p.description || '—'}</Table.Td>
                <Table.Td><Badge color="teal">{p.steps.length} Steps</Badge></Table.Td>
                <Table.Td style={{ textAlign: 'right' }}>
                  <ActionIcon color="red" variant="subtle" onClick={() => handleDeletePipeline(p.id)}>
                    <IconTrash size={16} />
                  </ActionIcon>
                </Table.Td>
              </Table.Tr>
            ))
          )}
        </Table.Tbody>
      </Table>
    </div>
  );
}

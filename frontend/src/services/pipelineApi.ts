export interface ToolMetadata {
  id: string;
  name: string;
  description: string;
  accepts: 'PDF' | 'DOCUMENT' | 'IMAGE' | 'TEXT' | 'ANY';
  produces: 'PDF' | 'DOCUMENT' | 'IMAGE' | 'TEXT' | 'ANY';
}

export interface PipelineStep {
  id?: number;
  stepOrder: number;
  toolId: string;
  stepParamsJson?: string;
}

export interface Pipeline {
  id: number;
  name: string;
  description: string;
  createdAt: string;
  steps: PipelineStep[];
}

export const pipelineApi = {
  getTools: async (token: string): Promise<ToolMetadata[]> => {
    const response = await fetch('/api/v1/pipelines/tools', {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) throw new Error('Failed to fetch available tool metadata');
    return response.json();
  },

  getPipelines: async (token: string): Promise<Pipeline[]> => {
    const response = await fetch('/api/v1/pipelines', {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) throw new Error('Failed to fetch user pipelines');
    return response.json();
  },

  createPipeline: async (
    name: string,
    description: string,
    steps: { toolId: string; stepParamsJson?: string }[],
    token: string
  ): Promise<Pipeline> => {
    const response = await fetch('/api/v1/pipelines', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ name, description, steps }),
    });

    if (!response.ok) {
      const text = await response.text();
      let message = 'Failed to create pipeline';
      try {
        const json = JSON.parse(text);
        if (json.message) message = json.message;
      } catch (e) {
        if (text) message = text;
      }
      throw new Error(message);
    }

    return response.json();
  },

  deletePipeline: async (id: number, token: string): Promise<void> => {
    const response = await fetch(`/api/v1/pipelines/${id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) throw new Error('Failed to delete pipeline');
  },

  executePipeline: async (id: number, file: File, token: string): Promise<any> => {
    const formData = new FormData();
    formData.append('file', file);

    const response = await fetch(`/api/v1/pipelines/${id}/execute`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
      body: formData,
    });

    if (!response.ok) {
      const text = await response.text();
      throw new Error(text || 'Failed to execute pipeline');
    }

    return response.json();
  },
};

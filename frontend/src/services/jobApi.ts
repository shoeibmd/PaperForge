export interface JobResponse {
  id: string;
  userId: number;
  jobType: string;
  status: 'QUEUED' | 'PROCESSING' | 'COMPLETED' | 'FAILED' | 'CANCELLED' | 'EXPIRED';
  progressPercent: number;
  errorMessage?: string;
  inputFilename: string;
  outputFilename?: string;
  createdAt: string;
  completedAt?: string;
  expiresAt: string;
}

export const jobApi = {
  getJobStatus: async (id: string, token: string): Promise<JobResponse> => {
    const response = await fetch(`/api/v1/jobs/${id}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) throw new Error('Failed to fetch job status');
    return response.json();
  },

  getUserJobs: async (token: string): Promise<JobResponse[]> => {
    const response = await fetch('/api/v1/jobs', {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) throw new Error('Failed to fetch jobs list');
    return response.json();
  },

  cancelJob: async (id: string, token: string): Promise<void> => {
    const response = await fetch(`/api/v1/jobs/${id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) throw new Error('Failed to cancel job');
  },
};

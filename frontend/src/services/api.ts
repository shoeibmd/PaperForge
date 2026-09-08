import { HealthResponse, InfoResponse, ConfigResponse } from '../types/api';

const API_BASE = '/api/v1';

async function fetchJson<T>(endpoint: string): Promise<T> {
  const response = await fetch(`${API_BASE}${endpoint}`);
  if (!response.ok) {
    throw new Error(`API error (${response.status}): ${response.statusText}`);
  }
  return response.json();
}

export const apiService = {
  getHealth: () => fetchJson<HealthResponse>('/health'),
  getInfo: () => fetchJson<InfoResponse>('/info'),
  getConfig: () => fetchJson<ConfigResponse>('/config'),
};

export interface ApiKeyDto {
  id: number;
  name: string;
  keyPrefix: string;
  username: string;
  revoked: boolean;
  scopes: string[];
  createdAt: string;
  expiresAt: string | null;
  lastUsedAt: string | null;
  usageCount: number;
}

export interface ApiKeyCreatedDto {
  apiKey: ApiKeyDto;
  rawSecret: string;
}

export interface ApiKeyCreateRequest {
  name: string;
  scopes?: string[];
  expiresAt?: string | null;
}

export const apiKeyApi = {
  getUserApiKeys: async (token: string): Promise<ApiKeyDto[]> => {
    const res = await fetch('/api/v1/api-keys', {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error('Failed to fetch API keys');
    return res.json();
  },

  createApiKey: async (request: ApiKeyCreateRequest, token: string): Promise<ApiKeyCreatedDto> => {
    const res = await fetch('/api/v1/api-keys', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(request),
    });
    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      throw new Error(err.message || 'Failed to create API key');
    }
    return res.json();
  },

  revokeApiKey: async (id: number, token: string): Promise<void> => {
    const res = await fetch(`/api/v1/api-keys/${id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error('Failed to revoke API key');
  },

  getAllApiKeysAdmin: async (token: string): Promise<ApiKeyDto[]> => {
    const res = await fetch('/api/v1/admin/api-keys', {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error('Failed to fetch admin API keys');
    return res.json();
  },

  revokeApiKeyAdmin: async (id: number, token: string): Promise<void> => {
    const res = await fetch(`/api/v1/admin/api-keys/${id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error('Failed to admin revoke API key');
  },
};

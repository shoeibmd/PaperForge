import { AuthRequestDto, AuthResponseDto, RegisterRequestDto } from '../types/auth';

const API_BASE = '/api/v1/auth';

export const authApi = {
  login: async (credentials: AuthRequestDto): Promise<AuthResponseDto> => {
    const response = await fetch(`${API_BASE}/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credentials),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || 'Invalid credentials');
    }

    return response.json();
  },

  register: async (data: RegisterRequestDto): Promise<AuthResponseDto> => {
    const response = await fetch(`${API_BASE}/register`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(data),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || 'Registration failed');
    }

    return response.json();
  },

  getCurrentUser: async (token: string): Promise<any> => {
    const response = await fetch(`${API_BASE}/me`, {
      headers: { Authorization: `Bearer ${token}` },
    });

    if (!response.ok) {
      throw new Error('Session expired');
    }

    return response.json();
  },
};

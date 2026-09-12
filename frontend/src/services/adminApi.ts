export interface UserManagement {
  id: number;
  username: string;
  email: string;
  enabled: boolean;
  storageQuotaBytes: number;
  createdAt: string;
  roles: string[];
}

export const adminApi = {
  getUsers: async (token: string): Promise<UserManagement[]> => {
    const response = await fetch('/api/v1/admin/users', {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) throw new Error('Failed to fetch users');
    return response.json();
  },

  toggleEnableUser: async (id: number, enable: boolean, token: string): Promise<UserManagement> => {
    const response = await fetch(`/api/v1/admin/users/${id}/${enable ? 'enable' : 'disable'}`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) throw new Error('Failed to update user status');
    return response.json();
  },

  toggleRole: async (id: number, token: string): Promise<UserManagement> => {
    const response = await fetch(`/api/v1/admin/users/${id}/toggle-role`, {
      method: 'PUT',
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) throw new Error('Failed to update user role');
    return response.json();
  },

  deleteUser: async (id: number, token: string): Promise<void> => {
    const response = await fetch(`/api/v1/admin/users/${id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) throw new Error('Failed to delete user');
  },

  forceLogout: async (id: number, token: string): Promise<void> => {
    const response = await fetch(`/api/v1/admin/users/${id}/force-logout`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) throw new Error('Failed to force logout user');
  },
};

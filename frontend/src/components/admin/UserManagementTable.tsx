import React, { useState, useEffect } from 'react';
import { Table, Button, Badge, Group, ActionIcon, Tooltip, Alert, TextInput } from '@mantine/core';
import { IconCheck, IconX, IconTrash, IconLock, IconShieldAlert, IconSearch } from '@tabler/icons-react';
import { adminApi, UserManagement } from '../../services/adminApi';
import { useAuth } from '../../context/AuthContext';

export const UserManagementTable: React.FC = () => {
  const [users, setUsers] = useState<UserManagement[]>([]);
  const [search, setSearch] = useState('');
  const [error, setError] = useState<string | null>(null);
  const { token } = useAuth();

  const fetchUsers = async () => {
    if (!token) return;
    try {
      const data = await adminApi.getUsers(token);
      setUsers(data);
    } catch (err: any) {
      setError(err.message || 'Failed to load users');
    }
  };

  useEffect(() => {
    fetchUsers();
  }, [token]);

  const handleToggleEnable = async (user: UserManagement) => {
    if (!token) return;
    try {
      await adminApi.toggleEnableUser(user.id, !user.enabled, token);
      fetchUsers();
    } catch (err: any) {
      setError(err.message);
    }
  };

  const handleToggleRole = async (user: UserManagement) => {
    if (!token) return;
    try {
      await adminApi.toggleRole(user.id, token);
      fetchUsers();
    } catch (err: any) {
      setError(err.message);
    }
  };

  const handleDeleteUser = async (user: UserManagement) => {
    if (!token || !window.confirm(`Delete user ${user.username}?`)) return;
    try {
      await adminApi.deleteUser(user.id, token);
      fetchUsers();
    } catch (err: any) {
      setError(err.message);
    }
  };

  const handleForceLogout = async (user: UserManagement) => {
    if (!token) return;
    try {
      await adminApi.forceLogout(user.id, token);
      alert(`Forced logout applied for ${user.username}`);
    } catch (err: any) {
      setError(err.message);
    }
  };

  const filteredUsers = users.filter(
    (u) =>
      u.username.toLowerCase().includes(search.toLowerCase()) ||
      u.email.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div>
      {error && <Alert color="red" mb="md" onClose={() => setError(null)} withCloseButton>{error}</Alert>}

      <Group justify="space-between" mb="md">
        <TextInput
          placeholder="Search users..."
          leftSection={<IconSearch size={16} />}
          value={search}
          onChange={(e) => setSearch(e.currentTarget.value)}
          style={{ width: 300 }}
        />
        <Button onClick={fetchUsers}>Refresh Users</Button>
      </Group>

      <Table striped highlightOnHover>
        <Table.Thead>
          <Table.Tr>
            <Table.Th>Username</Table.Th>
            <Table.Th>Email</Table.Th>
            <Table.Th>Roles</Table.Th>
            <Table.Th>Status</Table.Th>
            <Table.Th style={{ textAlign: 'right' }}>Actions</Table.Th>
          </Table.Tr>
        </Table.Thead>
        <Table.Tbody>
          {filteredUsers.map((u) => (
            <Table.Tr key={u.id}>
              <Table.Td fw={500}>{u.username}</Table.Td>
              <Table.Td>{u.email}</Table.Td>
              <Table.Td>
                <Group gap={4}>
                  {u.roles.map((r) => (
                    <Badge key={r} color={r === 'ROLE_ADMIN' ? 'red' : 'blue'} size="xs">
                      {r.replace('ROLE_', '')}
                    </Badge>
                  ))}
                </Group>
              </Table.Td>
              <Table.Td>
                <Badge color={u.enabled ? 'green' : 'gray'}>
                  {u.enabled ? 'Active' : 'Disabled'}
                </Badge>
              </Table.Td>
              <Table.Td style={{ textAlign: 'right' }}>
                <Group gap={4} justify="flex-end">
                  <Tooltip label={u.enabled ? 'Disable Account' : 'Enable Account'}>
                    <ActionIcon
                      variant="subtle"
                      color={u.enabled ? 'orange' : 'green'}
                      onClick={() => handleToggleEnable(u)}
                    >
                      {u.enabled ? <IconX size={16} /> : <IconCheck size={16} />}
                    </ActionIcon>
                  </Tooltip>

                  <Tooltip label="Toggle Admin Role">
                    <ActionIcon variant="subtle" color="blue" onClick={() => handleToggleRole(u)}>
                      <IconShieldAlert size={16} />
                    </ActionIcon>
                  </Tooltip>

                  <Tooltip label="Force Logout">
                    <ActionIcon variant="subtle" color="yellow" onClick={() => handleForceLogout(u)}>
                      <IconLock size={16} />
                    </ActionIcon>
                  </Tooltip>

                  <Tooltip label="Delete User">
                    <ActionIcon variant="subtle" color="red" onClick={() => handleDeleteUser(u)}>
                      <IconTrash size={16} />
                    </ActionIcon>
                  </Tooltip>
                </Group>
              </Table.Td>
            </Table.Tr>
          ))}
        </Table.Tbody>
      </Table>
    </div>
  );
};

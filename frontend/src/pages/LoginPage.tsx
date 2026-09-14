import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Paper, Title, TextInput, PasswordInput, Button, Alert, Stack, Text, Anchor } from '@mantine/core';
import { IconAlertCircle } from '@tabler/icons-react';
import { authApi } from '../services/authApi';
import { useAuth } from '../context/AuthContext';

export function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);

    try {
      const resp = await authApi.login({ username, password });
      login(resp);
      navigate('/');
    } catch (err: any) {
      setError(err.message || 'Invalid credentials');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 420, margin: '60px auto' }}>
      <Paper radius="md" p="xl" withBorder>
        <Title order={2} ta="center" mb="md">
          Welcome to PaperForge
        </Title>
        <Text c="dimmed" size="sm" ta="center" mb="lg">
          Sign in to access document studio and tools
        </Text>

        {error && (
          <Alert icon={<IconAlertCircle size={16} />} title="Authentication Error" color="red" mb="md">
            {error}
          </Alert>
        )}

        <form onSubmit={handleSubmit}>
          <Stack gap="md">
            <TextInput
              required
              label="Username"
              placeholder="Your username"
              value={username}
              onChange={(e) => setUsername(e.currentTarget.value)}
            />

            <PasswordInput
              required
              label="Password"
              placeholder="Your password"
              value={password}
              onChange={(e) => setPassword(e.currentTarget.value)}
            />

            <Button type="submit" fullWidth loading={loading} mt="xs">
              Sign In
            </Button>
          </Stack>
        </form>

        <Text ta="center" size="sm" mt="md">
          Don't have an account?{' '}
          <Anchor component={Link} to="/register" fw={600}>
            Register
          </Anchor>
        </Text>
      </Paper>
    </div>
  );
}

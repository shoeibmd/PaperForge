import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Paper, Title, TextInput, PasswordInput, Button, Alert, Stack, Text, Anchor } from '@mantine/core';
import { IconAlertCircle } from '@tabler/icons-react';
import { authApi } from '../services/authApi';
import { useAuth } from '../context/AuthContext';

export function RegisterPage() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
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
      const resp = await authApi.register({ username, email, password });
      login(resp);
      navigate('/dashboard');
    } catch (err: any) {
      setError(err.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 420, margin: '60px auto' }}>
      <Paper radius="md" p="xl" withBorder>
        <Title order={2} ta="center" mb="md">
          Create Account
        </Title>
        <Text c="dimmed" size="sm" ta="center" mb="lg">
          Join PaperForge Document Studio
        </Text>

        {error && (
          <Alert icon={<IconAlertCircle size={16} />} title="Registration Error" color="red" mb="md">
            {error}
          </Alert>
        )}

        <form onSubmit={handleSubmit}>
          <Stack gap="md">
            <TextInput
              required
              label="Username"
              placeholder="Choose username"
              value={username}
              onChange={(e) => setUsername(e.currentTarget.value)}
            />

            <TextInput
              required
              type="email"
              label="Email Address"
              placeholder="user@example.com"
              value={email}
              onChange={(e) => setEmail(e.currentTarget.value)}
            />

            <PasswordInput
              required
              label="Password"
              placeholder="Choose strong password"
              value={password}
              onChange={(e) => setPassword(e.currentTarget.value)}
            />

            <Button type="submit" fullWidth loading={loading} mt="xs">
              Create Account
            </Button>
          </Stack>
        </form>

        <Text ta="center" size="sm" mt="md">
          Already have an account?{' '}
          <Anchor component={Link} to="/login" fw={600}>
            Sign In
          </Anchor>
        </Text>
      </Paper>
    </div>
  );
}

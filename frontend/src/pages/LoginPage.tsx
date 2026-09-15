import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Paper, Title, TextInput, PasswordInput, Button, Alert, Stack, Text, Anchor } from '@mantine/core';
import { IconAlertCircle } from '@tabler/icons-react';
import { useTranslation } from 'react-i18next';
import { authApi } from '../services/authApi';
import { useAuth } from '../context/AuthContext';

export function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();
  const { login } = useAuth();
  const { t } = useTranslation();

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
          {t('auth.loginTitle')}
        </Title>

        {error && (
          <Alert icon={<IconAlertCircle size={16} />} title="Error" color="red" mb="md">
            {error}
          </Alert>
        )}

        <form onSubmit={handleSubmit}>
          <Stack gap="md">
            <TextInput
              required
              label={t('auth.username')}
              placeholder={t('auth.username')}
              value={username}
              onChange={(e) => setUsername(e.currentTarget.value)}
            />

            <PasswordInput
              required
              label={t('auth.password')}
              placeholder={t('auth.password')}
              value={password}
              onChange={(e) => setPassword(e.currentTarget.value)}
            />

            <Button type="submit" fullWidth loading={loading} mt="xs">
              {t('auth.loginButton')}
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

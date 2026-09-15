import { render, screen } from '@testing-library/react';
import { MantineProvider } from '@mantine/core';
import { MemoryRouter } from 'react-router-dom';
import { LoginPage } from '../LoginPage';
import { AuthProvider } from '../../context/AuthContext';
import { describe, it, expect } from 'vitest';

const renderLoginPage = () =>
  render(
    <MantineProvider>
      <AuthProvider>
        <MemoryRouter>
          <LoginPage />
        </MemoryRouter>
      </AuthProvider>
    </MantineProvider>
  );

describe('LoginPage Component', () => {
  it('renders login form with username and password inputs', () => {
    renderLoginPage();
    expect(screen.getByLabelText(/Username/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/Password/i)).toBeInTheDocument();
  });
});

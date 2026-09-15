import { render, screen } from '@testing-library/react';
import { MantineProvider } from '@mantine/core';
import { MemoryRouter } from 'react-router-dom';
import { Header } from '../Header';
import { AuthProvider } from '../../../context/AuthContext';
import { describe, it, expect } from 'vitest';

const renderHeader = (healthStatus = 'UP') =>
  render(
    <MantineProvider>
      <AuthProvider>
        <MemoryRouter>
          <Header healthStatus={healthStatus} />
        </MemoryRouter>
      </AuthProvider>
    </MantineProvider>
  );

describe('Header Component', () => {
  it('renders PaperForge logo and online status badge', () => {
    renderHeader('UP');
    expect(screen.getAllByText(/PaperForge/i).length).toBeGreaterThan(0);
    expect(screen.getByText(/ONLINE/i)).toBeInTheDocument();
  });

  it('renders offline status badge when API is DOWN', () => {
    renderHeader('DOWN');
    expect(screen.getByText(/OFFLINE/i)).toBeInTheDocument();
  });
});

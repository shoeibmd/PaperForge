import { render, screen } from '@testing-library/react';
import { MantineProvider } from '@mantine/core';
import { MemoryRouter } from 'react-router-dom';
import { DashboardPage } from '../DashboardPage';
import { describe, it, expect } from 'vitest';

const renderDashboard = () =>
  render(
    <MantineProvider>
      <MemoryRouter>
        <DashboardPage />
      </MemoryRouter>
    </MantineProvider>
  );

describe('DashboardPage Component', () => {
  it('renders dashboard title and quick start section', () => {
    renderDashboard();
    expect(screen.getByText(/Document Studio Dashboard/i)).toBeInTheDocument();
    expect(screen.getByText(/Quick Start/i)).toBeInTheDocument();
  });
});

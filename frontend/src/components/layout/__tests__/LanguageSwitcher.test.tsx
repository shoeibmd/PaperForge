import { render } from '@testing-library/react';
import { MantineProvider } from '@mantine/core';
import { LanguageSwitcher } from '../LanguageSwitcher';
import i18n from '../../../i18n';
import { describe, it, expect, beforeEach } from 'vitest';

const renderComponent = () =>
  render(
    <MantineProvider>
      <LanguageSwitcher />
    </MantineProvider>
  );

describe('LanguageSwitcher Component', () => {
  beforeEach(() => {
    i18n.changeLanguage('en');
  });

  it('renders correctly and defaults to English', () => {
    renderComponent();
    expect(i18n.language).toBe('en');
    expect(document.documentElement.dir).toBe('ltr');
  });

  it('switches language to Arabic and updates document direction to RTL', async () => {
    renderComponent();
    await i18n.changeLanguage('ar');

    expect(i18n.language).toBe('ar');
    expect(document.documentElement.dir).toBe('rtl');
  });

  it('switches language to French and updates document direction to LTR', async () => {
    renderComponent();
    await i18n.changeLanguage('fr');

    expect(i18n.language).toBe('fr');
    expect(document.documentElement.dir).toBe('ltr');
  });
});

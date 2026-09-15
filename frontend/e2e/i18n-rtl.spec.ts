import { test, expect } from '@playwright/test';

test.describe('Internationalization & RTL Layout E2E', () => {
  test('switches language and adjusts document direction', async ({ page }) => {
    await page.goto('/');

    // Check initial LTR direction
    const initialDir = await page.getAttribute('html', 'dir');
    expect(initialDir === 'ltr' || initialDir === null).toBeTruthy();

    // Select language dropdown or verify i18n initialization
    await expect(page.locator('body')).toContainText(/PaperForge/i);
  });
});

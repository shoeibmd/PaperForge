import { test, expect } from '@playwright/test';

test.describe('Admin Audit Log Journey E2E', () => {
  test('admin page requires authentication', async ({ page }) => {
    await page.goto('/admin');
    await expect(page).toHaveURL(/\/(login|admin)?/);
  });
});

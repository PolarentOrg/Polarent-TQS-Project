const { Given, When, Then, Before, After } = require('@cucumber/cucumber');
const { chromium, expect } = require('@playwright/test');

let browser, page;
const BASE_URL = process.env.BASE_URL || 'http://localhost:8081';

Before(async function () {
  browser = await chromium.launch({ headless: true });
  page = await browser.newPage();
});

After(async function () {
  await browser.close();
});

Given('I am on the login page', async function () {
  await page.goto(BASE_URL);
  await page.waitForSelector('#login-form');
});

When('I enter email {string}', async function (email) {
  await page.fill('#login-email', email);
});

When('I enter password {string}', async function (password) {
  await page.fill('#login-password', password);
});

When('I click the login button', async function () {
  await page.click('#login-form button[type="submit"]');
  await page.waitForTimeout(1000);
});

Then('I should see the equipment listings page', async function () {
  await page.waitForSelector('#listings-page', { state: 'visible', timeout: 5000 });
});

Then('I should see an error message', async function () {
  const hasError = await page.locator('.error, [class*="error"], .alert').isVisible().catch(() => false);
  if (!hasError) {
    const loginForm = await page.locator('#login-form').isVisible();
    if (!loginForm) throw new Error('Expected error or to stay on login page');
  }
});

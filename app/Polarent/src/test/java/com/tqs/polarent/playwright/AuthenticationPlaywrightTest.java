package com.tqs.polarent.playwright;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.junit.UsePlaywright;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@UsePlaywright
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
public class AuthenticationPlaywrightTest {

    private static final String BASE_URL = System.getProperty("e2e.baseUrl", "http://localhost:8081");
    private static final String TEST_EMAIL = "playwright_" + System.currentTimeMillis() + "@test.com";
    private static final String TEST_PASSWORD = "password123";

    @Test
    @Order(1)
    void testRegisterNewUser(Page page) {
        page.navigate(BASE_URL);
        
        // Click register tab
        page.click(".auth-tab[data-tab='register']");
        
        // Fill registration form
        page.fill("#register-firstname", "Playwright");
        page.fill("#register-lastname", "Test");
        page.fill("#register-email", TEST_EMAIL);
        page.fill("#register-password", TEST_PASSWORD);
        
        // Submit
        page.click("#register-form button[type='submit']");
        
        // Should see listings page after successful registration
        page.waitForSelector("#listings-page.active", new Page.WaitForSelectorOptions().setTimeout(10000));
        assertThat(page.locator("#listings-page")).isVisible();
    }

    @Test
    @Order(2)
    void testLoginPageElements(Page page) {
        page.navigate(BASE_URL);
        
        // Verify login form elements exist
        assertThat(page.locator("#login-form")).isVisible();
        assertThat(page.locator("#login-email")).isVisible();
        assertThat(page.locator("#login-password")).isVisible();
        assertThat(page.locator(".auth-tab[data-tab='register']")).isVisible();
    }

    @Test
    @Order(3)
    void testInvalidLoginShowsError(Page page) {
        page.navigate(BASE_URL);
        
        // Try to login with invalid credentials
        page.fill("#login-email", "invalid@test.com");
        page.fill("#login-password", "wrongpassword");
        page.click("#login-form button[type='submit']");
        
        // Should remain on login page
        page.waitForTimeout(1500);
        assertThat(page.locator("#login-form")).isVisible();
    }
}

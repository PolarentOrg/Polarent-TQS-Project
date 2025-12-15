package com.tqs.polarent.cucumber;

import com.microsoft.playwright.*;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class E2EAuthenticationSteps {

    private Playwright playwright;
    private Browser browser;
    private Page page;
    
    private static final String BASE_URL = System.getProperty("e2e.baseUrl", "http://localhost:8081");

    @Before("@e2e")
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        page = browser.newPage();
    }

    @After("@e2e")
    public void tearDown() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @Given("I open the Polarent homepage")
    public void iOpenThePolarentHomepage() {
        page.navigate(BASE_URL);
        page.waitForSelector("#login-form");
    }

    @When("I click on the register tab")
    public void iClickOnTheRegisterTab() {
        page.click(".auth-tab[data-tab='register']");
        page.waitForSelector("#register-form", new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
    }

    @When("I enter first name {string}")
    public void iEnterFirstName(String firstName) {
        page.fill("#register-firstname", firstName);
    }

    @When("I enter last name {string}")
    public void iEnterLastName(String lastName) {
        page.fill("#register-lastname", lastName);
    }

    @When("I enter registration email {string}")
    public void iEnterRegistrationEmail(String email) {
        String uniqueEmail = "e2e_" + System.currentTimeMillis() + "@test.com";
        page.fill("#register-email", uniqueEmail);
    }

    @When("I enter registration password {string}")
    public void iEnterRegistrationPassword(String password) {
        page.fill("#register-password", password);
    }

    @When("I click the register button")
    public void iClickTheRegisterButton() {
        page.click("#register-form button[type='submit']");
    }

    @When("I enter email {string} in the login form")
    public void iEnterEmailInTheLoginForm(String email) {
        page.fill("#login-email", email);
    }

    @When("I enter password {string} in the login form")
    public void iEnterPasswordInTheLoginForm(String password) {
        page.fill("#login-password", password);
    }

    @When("I click the login button")
    public void iClickTheLoginButton() {
        page.click("#login-form button[type='submit']");
        page.waitForTimeout(1000);
    }

    @Then("I should see the equipment listings")
    public void iShouldSeeTheEquipmentListings() {
        page.waitForSelector("#listings-page.active", new Page.WaitForSelectorOptions().setTimeout(10000));
        assertThat(page.isVisible("#listings-page")).isTrue();
    }

    @Then("I should remain on the login page")
    public void iShouldRemainOnTheLoginPage() {
        page.waitForTimeout(1500);
        assertThat(page.isVisible("#auth-page.active") || page.isVisible("#login-form")).isTrue();
    }
}

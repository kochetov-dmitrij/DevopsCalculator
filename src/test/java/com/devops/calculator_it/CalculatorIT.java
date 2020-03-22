/*
   Copyright 2010 David Green

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package com.devops.calculator_it;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import static org.junit.Assert.assertEquals;

/**
 * Integration test for calculator.
 *
 * @author GreenD
 */
public class CalculatorIT {

    private static WebDriver webDriver;
    private static String testServerPort;

    @BeforeClass
    public static void openWebDriver() {
        testServerPort = System.getProperty("test.server.port", "8080");
//        System.setProperty("webdriver.gecko.driver", "/webdriver/geckodriver");
        FirefoxBinary firefoxBinary = new FirefoxBinary();
        firefoxBinary.addCommandLineOptions("--headless");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        firefoxOptions.setBinary(firefoxBinary);
        webDriver = new FirefoxDriver(firefoxOptions);
    }

    @AfterClass
    public static void closeWebDriver() {
        webDriver.close();
    }

    private CalculatorPage getHomePage() {
        webDriver.get("http://localhost:" + testServerPort + "/");
        return new CalculatorPage(webDriver);
    }

    @Test
    public void testEnterDigits() {
        CalculatorPage page = getHomePage();
        page.pressButton('C');

        page.pressButton('1');
        page.pressButton('2');
        page.pressButton('3');

        assertEquals("123", page.getDisplayAmount());
    }

    @Test
    public void testAddTwoNumbers() {
        CalculatorPage page = getHomePage();
        page.pressButton('C');

        page.pressButton('1');
        page.pressButton('+');
        page.pressButton('2');
        page.pressButton('=');

        assertEquals("3", page.getDisplayAmount());
    }

    @Test
    public void testSubtractTwoNumbers() {
        CalculatorPage page = getHomePage();
        page.pressButton('C');

        page.pressButton('3');
        page.pressButton('-');
        page.pressButton('2');
        page.pressButton('=');

        assertEquals("1", page.getDisplayAmount());
    }

    @Test
    public void testMultiplyTwoNumbers() {
        CalculatorPage page = getHomePage();
        page.pressButton('C');

        page.pressButton('3');
        page.pressButton('*');
        page.pressButton('2');
        page.pressButton('=');

        assertEquals("6", page.getDisplayAmount());
    }

    @Test
    public void testDivideTwoNumbers() {
        CalculatorPage page = getHomePage();
        page.pressButton('C');

        page.pressButton('6');
        page.pressButton('/');
        page.pressButton('2');
        page.pressButton('=');

        assertEquals("3", page.getDisplayAmount());
    }

    @Test
    public void testClear() {
        CalculatorPage page = getHomePage();
        page.pressButton('C');

        page.pressButton('3');
        page.pressButton('*');
        page.pressButton('C');

        assertEquals("0", page.getDisplayAmount());
    }

}

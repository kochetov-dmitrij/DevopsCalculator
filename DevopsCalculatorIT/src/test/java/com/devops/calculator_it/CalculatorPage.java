/*
 * Copyright 2010 David Green
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.devops.calculator_it;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Encapsulates the calculator page.
 *
 * @author GreenD
 */
public class CalculatorPage {

    private WebDriver webDriver;

    CalculatorPage(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public void pressButton(char button) {
        webDriver.findElement(By.id("btn-" + button)).click();
    }

    public String getDisplayAmount() {
        return webDriver.findElement(By.id("display")).getAttribute("value");
    }
}

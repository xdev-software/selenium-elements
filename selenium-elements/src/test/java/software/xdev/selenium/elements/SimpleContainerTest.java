/*
 * Copyright © 2025 XDEV Software (https://xdev.software)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package software.xdev.selenium.elements;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import software.xdev.selenium.elements.annotation.FindBySelector;
import software.xdev.selenium.elements.remote.CustomizableRemoteWebElementInstaller;
import software.xdev.selenium.elements.remote.ImprovedRemoteWebElement;
import software.xdev.testcontainers.selenium.containers.browser.CapabilitiesBrowserWebDriverContainer;


class SimpleContainerTest implements CanFindElements
{
	RemoteWebDriver remoteWebDriver;
	
	@MethodSource
	@ParameterizedTest
	void simpleCheck(final Capabilities capabilities)
	{
		try(final CapabilitiesBrowserWebDriverContainer<?> browserContainer =
			new CapabilitiesBrowserWebDriverContainer<>(capabilities))
		{
			browserContainer.start();
			this.remoteWebDriver =
				new RemoteWebDriver(browserContainer.getSeleniumAddressURI().toURL(), capabilities, false);
			
			CustomizableRemoteWebElementInstaller.install(
				this.remoteWebDriver,
				() -> new ImprovedRemoteWebElement("return document.readyState == 'complete';"));
			
			this.remoteWebDriver.manage().window().maximize();
			this.remoteWebDriver.get(capabilities instanceof FirefoxOptions ? "about:support" : "chrome://version");
			
			final BodyElement bodyElement = this.waitForFirst(BodyElement.class);
			final String text = bodyElement.getText();
			Assertions.assertFalse(text.isEmpty());
			
			this.remoteWebDriver.quit();
		}
		catch(final IOException ioe)
		{
			throw new UncheckedIOException(ioe);
		}
	}
	
	static Stream<Arguments> simpleCheck()
	{
		return Stream.of(
			Arguments.of(new FirefoxOptions()),
			Arguments.of(new ChromeOptions())
		);
	}
	
	@AfterEach
	void afterEach()
	{
		this.remoteWebDriver = null;
	}
	
	@Override
	public WebDriver getWebDriver()
	{
		return this.remoteWebDriver;
	}
	
	@FindBySelector(tagName = "body")
	abstract static class BodyElement implements ImprovedWebElement
	{
	}
}

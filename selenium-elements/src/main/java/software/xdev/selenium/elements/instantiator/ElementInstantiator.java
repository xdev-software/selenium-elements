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
package software.xdev.selenium.elements.instantiator;

import java.util.List;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import software.xdev.selenium.elements.annotation.FindBySelector;


/**
 * Creates Element proxies that utilize {@link FindBySelector}
 */
public interface ElementInstantiator
{
	default int priority()
	{
		return 0;
	}
	
	default <T extends WebElement> T find(final SearchContext searchContext, final Class<T> clazz)
	{
		return this.find(searchContext::findElement, clazz);
	}
	
	default <T extends WebElement> List<T> findAll(final SearchContext searchContext, final Class<T> clazz)
	{
		return this.findAll(searchContext::findElements, clazz);
	}
	
	<T extends WebElement> T find(
		final Function<By, WebElement> search,
		final Class<T> clazz);
	
	<T extends WebElement> List<T> findAll(
		final Function<By, List<WebElement>> search,
		final Class<T> clazz);
	
	<T extends WebElement> By buildSelector(final Class<T> clazz);
	
	<T extends WebElement> T proxyWebElement(final Class<T> clazz, final WebElement webElement);
}

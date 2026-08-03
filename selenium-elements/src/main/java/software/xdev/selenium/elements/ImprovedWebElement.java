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

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;

import software.xdev.selenium.elements.remote.ImprovedRemoteWebElement;


/**
 * Improved version of {@link WebElement}:
 * <ul>
 *     <li>Contains pre-defined methods for getting and setting properties</li>
 *     <li>Can use a custom search context</li>
 * </ul>
 *
 * @apiNote Requires a underlying {@link ImprovedRemoteWebElement}
 */
public interface ImprovedWebElement extends WebElement, CanFindElementsSelfSearchContext, WrapsElement
{
	default void performJsClick()
	{
		this.getWrappedRemoteElement().performJsClick();
	}
	
	default void nativeClick()
	{
		this.getWrappedRemoteElement().nativeClick();
	}
	
	@Override
	default WebDriver getWebDriver()
	{
		return this.getWrappedRemoteElement().getWrappedDriver();
	}
	
	default ImprovedRemoteWebElement getWrappedRemoteElement()
	{
		return this.getWrappedElement() instanceof final ImprovedRemoteWebElement improvedRemoteWebElement
			? improvedRemoteWebElement
			: null;
	}
	
	default Object getProperty(final String... propertyNames)
	{
		this.prepareForOperation();
		
		return this.executeScript(
			"var value = arguments[0];"
				+ IntStream.range(0, propertyNames.length)
				.mapToObj(i -> "if (typeof value != 'undefined') value = value[arguments[" + (i + 1) + "]];")
				.collect(Collectors.joining())
				+ "return value;",
			Stream.concat(Stream.of(this), Stream.of(propertyNames)).toArray());
	}
	
	default String getStringProperty(final String... propertyNames)
	{
		return this.getProperty(propertyNames) instanceof final String c ? c : null;
	}
	
	default Integer getIntProperty(final String... propertyNames)
	{
		return this.getProperty(propertyNames) instanceof final Number c ? c.intValue() : null;
	}
	
	default Boolean getBoolProperty(final String... propertyNames)
	{
		return this.getProperty(propertyNames) instanceof final Boolean c ? c : null;
	}
	
	default void setProperty(final String name, final Object value)
	{
		this.executeScript("arguments[0][arguments[1]]=arguments[2]", this, name, value);
	}
	
	default void dispatchCustomEvent(
		final String type,
		final Map<String, Object> options)
	{
		this.executeScript(
			"arguments[0].dispatchEvent(new CustomEvent(arguments[1], arguments[2]));",
			this,
			type,
			options);
	}
	
	default boolean hasAttribute(final String attribute)
	{
		this.prepareForOperation();
		return (boolean)this.callFunction("hasAttribute", attribute);
	}
	
	default void prepareForOperation()
	{
		this.getWrappedRemoteElement().prepareForOperation();
	}
}

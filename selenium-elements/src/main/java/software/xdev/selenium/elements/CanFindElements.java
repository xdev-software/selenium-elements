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

import java.time.Duration;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;
import org.openqa.selenium.support.ui.WebDriverWait;

import software.xdev.selenium.elements.instantiator.ElementInstantiator;
import software.xdev.selenium.elements.instantiator.ElementInstantiatorInstance;


/**
 * Shorthand interface for common element lookup and interaction strategies/methods.
 */
public interface CanFindElements
{
	Duration DEFAULT_WAIT_UNTIL_DURATION = Duration.ofSeconds(10);
	
	default By byAttribute(final String attribute, final String value)
	{
		return By.cssSelector("[" + attribute + "='" + value + "']");
	}
	
	default By byClassNamePart(final String className)
	{
		return By.cssSelector("." + className);
	}
	
	default WebElement waitForFirstChained(final By... chainedBys)
	{
		return this.waitForFirst(new ByChained(chainedBys));
	}
	
	default WebElement waitForFirstAnd(final By... andBys)
	{
		return this.waitForFirst(new ByAnd(andBys));
	}
	
	default WebElement waitForFirst(final By by)
	{
		return this.waitUntil(wd -> this.determineSearchContext(wd).findElement(by));
	}
	
	default <T extends WebElement> T waitForFirst(final Class<T> clazz)
	{
		return this.waitForFirst(clazz, null);
	}
	
	default <T extends WebElement> T waitForFirst(
		final Class<T> clazz,
		final By additionalAndBy)
	{
		return this.waitForFirst(clazz, additionalAndBy, DEFAULT_WAIT_UNTIL_DURATION);
	}
	
	default <T extends WebElement> T waitForFirst(
		final Class<T> clazz,
		final By additionalAndBy,
		final Duration duration)
	{
		return this.elementProxyCreator().find(
			by -> this.waitUntil(
				wd -> this.determineSearchContext(wd)
					.findElement(additionalAndBy != null ? new ByAnd(by, additionalAndBy) : by),
				duration),
			clazz);
	}
	
	default ElementInstantiator elementProxyCreator()
	{
		return ElementInstantiatorInstance.instance();
	}
	
	default <V> V waitUntil(final Function<WebDriver, V> isTrue)
	{
		return this.waitUntil(isTrue, DEFAULT_WAIT_UNTIL_DURATION);
	}
	
	default <V> V waitUntil(final Function<WebDriver, V> isTrue, final Duration duration)
	{
		return new WebDriverWait(this.getWebDriver(), duration).until(isTrue);
	}
	
	default <T extends WebElement> T waitForFirstByClassName(final Class<T> clazz, final String className)
	{
		return this.waitForFirst(clazz, By.cssSelector("." + className));
	}
	
	default SearchContext determineSearchContext(final WebDriver webDriver)
	{
		return webDriver;
	}
	
	WebDriver getWebDriver();
	
	default Object executeScript(final String script, final Object... args)
	{
		if(this.getWebDriver() instanceof final JavascriptExecutor jsExecutor)
		{
			return jsExecutor.executeScript(script, args);
		}
		
		throw new UnsupportedOperationException("WebDriver can't execute JS");
	}
	
	default Object callFunction(final String methodName, final Object... args)
	{
		final String paramPlaceholders = IntStream.range(0, args.length)
			.mapToObj(i -> "arguments[" + (i + 1) + "]") // Offset by 1!
			.collect(Collectors.joining(","));
		
		return this.executeScript(
			"return arguments[0]." + methodName + "(" + paramPlaceholders + ")",
			Stream.concat(Stream.of(this), Stream.of(args)).toArray(Object[]::new));
	}
}

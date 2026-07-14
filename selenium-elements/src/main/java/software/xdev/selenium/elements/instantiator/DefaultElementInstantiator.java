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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.interactions.Locatable;

import javassist.util.proxy.ProxyFactory;
import software.xdev.selenium.elements.annotation.AnnotationSelectorBuilder;
import software.xdev.selenium.elements.annotation.FindBySelector;


public class DefaultElementInstantiator implements ElementInstantiator
{
	@Override
	public <T extends WebElement> T find(final SearchContext searchContext, final Class<T> clazz)
	{
		return this.find(searchContext::findElement, clazz);
	}
	
	@Override
	public <T extends WebElement> List<T> findAll(final SearchContext searchContext, final Class<T> clazz)
	{
		return this.findAll(searchContext::findElements, clazz);
	}
	
	@Override
	public <T extends WebElement> T find(
		final Function<By, WebElement> search,
		final Class<T> clazz)
	{
		return this.proxyWebElement(clazz, search.apply(this.buildSelector(clazz)));
	}
	
	@Override
	public <T extends WebElement> List<T> findAll(
		final Function<By, List<WebElement>> search,
		final Class<T> clazz)
	{
		final List<WebElement> elements = search.apply(this.buildSelector(clazz));
		
		return elements.stream()
			.map(webElement -> this.proxyWebElement(clazz, webElement))
			.toList();
	}
	
	@Override
	public <T extends WebElement> By buildSelector(final Class<T> clazz)
	{
		final FindBySelector selector = clazz.getAnnotation(FindBySelector.class);
		if(selector == null)
		{
			return null;
		}
		final Class<? extends AnnotationSelectorBuilder> builder = selector.builder();
		if(builder == null)
		{
			return null;
		}
		
		final AnnotationSelectorBuilder annotationSelectorBuilder;
		try
		{
			annotationSelectorBuilder = builder.getConstructor().newInstance();
		}
		catch(final InstantiationException
					| IllegalAccessException
					| InvocationTargetException
					| NoSuchMethodException e)
		{
			throw new IllegalStateException("Failed to create selector builder", e);
		}
		
		return annotationSelectorBuilder.build(selector);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends WebElement> T proxyWebElement(final Class<T> clazz, final WebElement webElement)
	{
		final ProxyFactory factory = new ProxyFactory();
		factory.setSuperclass(clazz);
		factory.setInterfaces(new Class[]{WrapsElement.class, Locatable.class});
		try
		{
			final Method mGetWrappedElement = WrapsElement.class.getMethod("getWrappedElement");
			return (T)factory.create(
				new Class[0],
				new Object[0],
				(self, thisMethod, proceed, args) -> {
					if(mGetWrappedElement.equals(thisMethod))
					{
						return webElement;
					}
					if(proceed != null)
					{
						return this.invokeMethodWithProperExceptionHandling(proceed, self, args);
					}
					return this.invokeMethodWithProperExceptionHandling(thisMethod, webElement, args);
				});
		}
		catch(final NoSuchMethodException
					| InstantiationException
					| IllegalAccessException
					| InvocationTargetException e)
		{
			throw new IllegalStateException("Failed to create proxy", e);
		}
	}
	
	@SuppressWarnings("PMD.PreserveStackTrace")
	protected Object invokeMethodWithProperExceptionHandling(
		final Method method,
		final Object obj,
		final Object... args)
	{
		try
		{
			return method.invoke(obj, args);
		}
		catch(final InvocationTargetException ex)
		{
			final String msg = "Failed to invoke method '" + method.getName() + "' on " + obj.getClass();
			
			// Directly throw if possible so that Selenium correctly detects it's exceptions
			if(ex.getCause() instanceof final RuntimeException rex)
			{
				rex.addSuppressed(new IllegalStateException(msg));
				throw rex;
			}
			
			throw new IllegalStateException(msg, ex.getCause());
		}
		catch(final IllegalAccessException ex)
		{
			throw new IllegalStateException(
				"Failed to access method '" + method.getName() + "' on " + obj.getClass(), ex);
		}
	}
}

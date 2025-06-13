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

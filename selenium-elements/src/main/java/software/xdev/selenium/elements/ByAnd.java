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

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;


/**
 * Chains multiple {@link By Bys} together.
 *
 * @apiNote You should consider using {@link org.openqa.selenium.support.pagefactory.ByChained} as it's more performant
 */
@SuppressWarnings("java:S2160") // Not needed
public class ByAnd extends By
{
	private final By[] bys;
	
	public ByAnd(final By... bys)
	{
		this.bys = bys;
	}
	
	@Override
	public WebElement findElement(final SearchContext context)
	{
		final List<WebElement> elements = this.findElements(context);
		if(elements.isEmpty())
		{
			throw new NoSuchElementException("Cannot locate an element using " + this);
		}
		return elements.get(0);
	}
	
	@Override
	public List<WebElement> findElements(final SearchContext context)
	{
		if(this.bys.length == 0)
		{
			return List.of();
		}
		
		final LinkedHashSet<WebElement> elements = new LinkedHashSet<>(context.findElements(this.bys[0]));
		for(int i = 1; i < this.bys.length; i++)
		{
			if(elements.isEmpty()) // No match!
			{
				return List.of();
			}
			
			elements.retainAll(context.findElements(this.bys[i]));
		}
		
		return elements.stream().toList();
	}
	
	@Override
	public String toString()
	{
		return "By.and({"
			+ Arrays.stream(this.bys)
			.map(By::toString)
			.collect(Collectors.joining(","))
			+ "}";
	}
}

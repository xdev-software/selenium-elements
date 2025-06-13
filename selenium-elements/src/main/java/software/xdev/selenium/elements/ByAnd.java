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

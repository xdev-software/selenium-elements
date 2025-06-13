package software.xdev.selenium.elements.remote;

import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.slf4j.LoggerFactory;

import software.xdev.selenium.elements.CanFindElements;


/**
 * An improved {@link RemoteWebElement} that
 * <ul>
 *     <li>Waits for the page to be loaded properly</li>
 *     <li>Automatically scrolls elements into the visible area if needed</li>
 *     <li>Uses the fallback JavaScript click function if the element couldn't be clicked</li>
 * </ul>
 */
@SuppressWarnings("java:S2160")
public class ImprovedRemoteWebElement extends RemoteWebElement implements CanFindElements
{
	protected String waitForServerLoadToFinishFunction;
	
	public ImprovedRemoteWebElement(final String waitForServerLoadToFinishFunction)
	{
		this.waitForServerLoadToFinishFunction = waitForServerLoadToFinishFunction;
	}
	
	@Override
	public WebDriver getWebDriver()
	{
		return this.getWrappedDriver();
	}
	
	@Override
	public void click()
	{
		this.prepareForOperation();
		try
		{
			super.click();
		}
		catch(final ElementNotInteractableException ex)
		{
			LoggerFactory.getLogger(this.getClass())
				.warn(
					"Element can't be clicked via UI - executing JS click. "
						+ "Please manually check if the element is accessible. "
						+ "If the element is accessible consider calling performJsClick directly.", ex);
			this.performJsClick();
		}
	}
	
	public void performJsClick()
	{
		this.callFunction("click");
	}
	
	@Override
	public String getText()
	{
		this.prepareForOperation();
		return super.getText();
	}
	
	@Override
	public void sendKeys(final CharSequence... keysToSend)
	{
		this.prepareForOperation();
		super.sendKeys(keysToSend);
	}
	
	// Shortcut so that not all invoked methods need to be written each time
	public void prepareForOperation()
	{
		this.waitForServerLoadToFinish();
		this.scrollIntoViewIfRequired();
	}
	
	public void scrollIntoViewIfRequired()
	{
		if(!this.isDisplayed())
		{
			this.executeScript("arguments[0].scrollIntoView(true);", this);
		}
	}
	
	public void waitForServerLoadToFinish()
	{
		if(this.waitForServerLoadToFinishFunction == null)
		{
			return;
		}
		
		final long timeoutTime = System.currentTimeMillis() + DEFAULT_WAIT_UNTIL_DURATION.toMillis();
		
		boolean finished = false;
		while(System.currentTimeMillis() < timeoutTime && !finished)
		{
			final Boolean retVal = (Boolean)this.executeScript(this.waitForServerLoadToFinishFunction);
			if(retVal == null)
			{
				LoggerFactory.getLogger(this.getClass())
					.warn("waitForLoadToFinishFunction returned null! It should either return true or false");
			}
			finished = Boolean.TRUE.equals(retVal);
		}
	}
}

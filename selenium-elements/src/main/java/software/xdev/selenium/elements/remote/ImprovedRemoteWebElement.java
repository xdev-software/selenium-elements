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
package software.xdev.selenium.elements.remote;

import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebElement;
import org.slf4j.Logger;
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
	protected Logger logger;
	protected final String waitForServerLoadToFinishFunction;
	protected boolean autoScrollIntoView = true;
	
	public ImprovedRemoteWebElement(final String waitForServerLoadToFinishFunction)
	{
		this.waitForServerLoadToFinishFunction = waitForServerLoadToFinishFunction;
	}
	
	public ImprovedRemoteWebElement withAutoScrollIntoView(final boolean autoScrollIntoView)
	{
		this.autoScrollIntoView = autoScrollIntoView;
		return this;
	}
	
	protected Logger logger()
	{
		if(this.logger == null)
		{
			this.logger = LoggerFactory.getLogger(this.getClass());
		}
		return this.logger;
	}
	
	@Override
	public WebDriver getWebDriver()
	{
		return this.getWrappedDriver();
	}
	
	@Override
	public SearchContext determineSearchContext(final WebDriver webDriver)
	{
		return this;
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
			this.logger().warn(
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
	
	/**
	 * Calls the original/upstream click
	 */
	public void nativeClick()
	{
		super.click();
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
		try
		{
			if(this.autoScrollIntoView && !this.isDisplayed())
			{
				this.executeScript("arguments[0].scrollIntoView(true);", this);
			}
		}
		catch(final ElementNotInteractableException ex)
		{
			this.logger().warn("Element can't be scrolled into view", ex);
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
				this.logger().warn("waitForLoadToFinishFunction returned null! It should either return true or false");
			}
			finished = Boolean.TRUE.equals(retVal);
		}
	}
}

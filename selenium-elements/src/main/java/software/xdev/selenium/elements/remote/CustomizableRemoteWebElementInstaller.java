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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

import org.openqa.selenium.remote.JsonToWebElementConverter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.RemoteWebElement;


/**
 * Allows to install a custom {@link RemoteWebElement} supplier into {@link RemoteWebDriver}.
 */
@SuppressWarnings("java:S3011") // Force accessibility
public final class CustomizableRemoteWebElementInstaller
{
	public static void install(
		final RemoteWebDriver driver,
		final Supplier<RemoteWebElement> remoteWebElementSupplier)
	{
		try
		{
			final Method mSetElementConverter =
				RemoteWebDriver.class.getDeclaredMethod("setElementConverter", JsonToWebElementConverter.class);
			mSetElementConverter.setAccessible(true);
			mSetElementConverter.invoke(
				driver,
				new CustomizableJsonToWebElementConverter(driver, remoteWebElementSupplier));
		}
		catch(final NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
		{
			throw new IllegalStateException("Failed to access setElementConverter", e);
		}
	}
	
	public static class CustomizableJsonToWebElementConverter extends JsonToWebElementConverter
	{
		// Can be removed once https://github.com/SeleniumHQ/selenium/issues/15884 is fixed
		private final Method mSetOwner;
		private final Supplier<RemoteWebElement> remoteWebElementSupplier;
		
		public CustomizableJsonToWebElementConverter(
			final RemoteWebDriver driver,
			final Supplier<RemoteWebElement> remoteWebElementSupplier)
		{
			super(driver);
			
			try
			{
				this.mSetOwner = JsonToWebElementConverter.class.getDeclaredMethod("setOwner", RemoteWebElement.class);
				this.mSetOwner.setAccessible(true);
			}
			catch(final NoSuchMethodException ex)
			{
				throw new IllegalStateException("Failed to find setOwner", ex);
			}
			this.remoteWebElementSupplier = remoteWebElementSupplier;
		}
		
		@Override
		protected RemoteWebElement newRemoteWebElement()
		{
			try
			{
				return (RemoteWebElement)this.mSetOwner.invoke(this, this.remoteWebElementSupplier.get());
			}
			catch(final IllegalAccessException | InvocationTargetException e)
			{
				throw new IllegalStateException("Failed to call setOwner", e);
			}
		}
	}
	
	private CustomizableRemoteWebElementInstaller()
	{
	}
}

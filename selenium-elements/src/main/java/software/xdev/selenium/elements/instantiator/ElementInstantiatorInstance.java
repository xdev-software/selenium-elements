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

import java.util.Comparator;
import java.util.ServiceLoader;


public final class ElementInstantiatorInstance
{
	private static boolean initialized;
	private static ElementInstantiator instance;
	
	public static ElementInstantiator instance()
	{
		if(!initialized)
		{
			init();
		}
		return instance;
	}
	
	private static synchronized void init()
	{
		if(initialized)
		{
			return;
		}
		
		setInstance(ServiceLoader.load(ElementInstantiator.class)
			.stream()
			.map(ServiceLoader.Provider::get)
			.max(Comparator.comparing(ElementInstantiator::priority))
			.orElse(null));
	}
	
	public static void setInstance(final ElementInstantiator instance)
	{
		initialized = true;
		ElementInstantiatorInstance.instance = instance;
	}
	
	private ElementInstantiatorInstance()
	{
	}
}

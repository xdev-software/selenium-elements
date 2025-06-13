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

package software.xdev.selenium.elements.annotation;

import org.openqa.selenium.By;


public interface AnnotationSelectorBuilder
{
	By build(Object annotation);
}

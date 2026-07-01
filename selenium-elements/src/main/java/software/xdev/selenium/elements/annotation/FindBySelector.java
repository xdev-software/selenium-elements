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
package software.xdev.selenium.elements.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;

import software.xdev.selenium.elements.ByAnd;


/**
 * Used to mark elements. Similar to {@link org.openqa.selenium.support.FindBy} with a few enhancements.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface FindBySelector
{
	String id() default "";
	
	String name() default "";
	
	String classNamePart() default "";
	
	String classNameExact() default "";
	
	String css() default "";
	
	String tagName() default "";
	
	String linkText() default "";
	
	String partialLinkText() default "";
	
	String xpath() default "";
	
	Class<? extends AnnotationSelectorBuilder> builder() default Builder.class;
	
	class Builder implements AnnotationSelectorBuilder
	{
		@SuppressWarnings("PMD.NPathComplexity") // FP
		@Override
		public By build(final Object annotation)
		{
			final FindBySelector selector = (FindBySelector)annotation;
			
			final List<By> bys = new ArrayList<>();
			
			// Most specific first!
			// Less work for ByAnd!
			if(!"".equals(selector.id()))
			{
				bys.add(By.id(selector.id()));
			}
			
			if(!"".equals(selector.classNamePart()))
			{
				bys.add(By.cssSelector("." + selector.classNamePart()));
			}
			
			if(!"".equals(selector.css()))
			{
				bys.add(By.cssSelector(selector.css()));
			}
			
			if(!"".equals(selector.classNameExact()))
			{
				bys.add(By.className(selector.classNameExact()));
			}
			
			if(!"".equals(selector.xpath()))
			{
				bys.add(By.xpath(selector.xpath()));
			}
			
			if(!"".equals(selector.linkText()))
			{
				bys.add(By.linkText(selector.linkText()));
			}
			
			if(!"".equals(selector.name()))
			{
				bys.add(By.name(selector.name()));
			}
			
			if(!"".equals(selector.partialLinkText()))
			{
				bys.add(By.partialLinkText(selector.partialLinkText()));
			}
			
			if(!"".equals(selector.tagName()))
			{
				bys.add(By.tagName(selector.tagName()));
			}
			
			return !bys.isEmpty()
				? new ByAnd(bys.toArray(By[]::new))
				: null;
		}
	}
}

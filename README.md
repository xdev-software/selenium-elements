[![Latest version](https://img.shields.io/maven-central/v/software.xdev/selenium-elements?logo=apache%20maven)](https://mvnrepository.com/artifact/software.xdev/selenium-elements)
[![Build](https://img.shields.io/github/actions/workflow/status/xdev-software/selenium-elements/check-build.yml?branch=develop)](https://github.com/xdev-software/selenium-elements/actions/workflows/check-build.yml?query=branch%3Adevelop)

# <img src="https://raw.githubusercontent.com/SeleniumHQ/seleniumhq.github.io/690acbad7b4bf4656f116274809765db64e6ccf7/website_and_docs/static/images/logos/webdriver.svg" width=24 /> Elements for Selenium

Define Selenium HTML elements as Java classes, similar to Selenium's [``@FindBy`` annotation](https://www.selenium.dev/selenium/docs/api/java/org/openqa/selenium/support/FindBy.html).

Also contains a ton of other predefined utility, for example:
* Automatically scrolls elements that perform operations into the view
* Safe click: When an element is detected as stale a JavaScript click is executed instead
* Option to globally wait until the page finished loading
* Waiting for some time until the element is present (``waitUntil``)

Overall this should result in:
* Less [flaky](https://www.browserstack.com/test-reporting-and-analytics/features/test-reporting/what-is-flaky-test) tests
* Abstraction of elements in an object oriented way

## Usage

1. Define the elements you want to access
    ```java
    @FindBySelector(tagName = "body")
    abstract class BodyElement implements ImprovedWebElement {
        public MyElement myElement() {
            return waitForFirst(MyElement.class);
        }
    }

    @FindBySelector(id = "abc")
    abstract class MyElement implements ImprovedWebElement {
    }
    ```
2. Utilize the predefined methods and classes to get/access the elements in a test
    ```java
    class MyWebDriverTest implements CanFindElements {
        WebDriver webDriver;

        @BeforeEach
        void beforeEach() {
            webDriver = createWebDriver();
            CustomizableRemoteWebElementInstaller.install(
                webDriver,
                () -> new ImprovedRemoteWebElement(
                    "return document.readyState == 'complete';")
            );
        }

        @Test
        void test() {
            MyElement myElement = waitForFirst(BodyElement.class).myElement();
            assertEquals("Hello world", myElement.getText());

            // Or alternatively
            ElementInstantiatorInstance.instance().find(webDriver, BodyElement.class);
        }

        @AfterEach
        void afterEach() {
            // Stop webDriver here...
            webDriver = null;
        }

        @Override
        public WebDriver getWebDriver() {
            return webDriver;
        }
    }
    ```

You can also checkout the [integrated tests](./selenium-elements/src/test/java/) to see this in action.

## Installation
[Installation guide for the latest release](https://github.com/xdev-software/selenium-elements/releases/latest#Installation)

## Support
If you need support as soon as possible and you can't wait for any pull request, feel free to use [our support](https://xdev.software/en/services/support).

## Contributing
See the [contributing guide](./CONTRIBUTING.md) for detailed instructions on how to get started with our project.

## Dependencies and Licenses
View the [license of the current project](LICENSE) or the [summary including all dependencies](https://xdev-software.github.io/selenium-elements/dependencies)

<sub>Disclaimer: This is not an official Testcontainers/Selenium product and not associated</sub>

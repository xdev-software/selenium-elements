# 1.0.5
* Add new shortcut method ``CanFindElements#waitForFirstByClassName``
* Updated dependencies

# 1.0.4
* ``ImprovedWebElement#hasAttribute`` now waits for loading operations to complete (now calls ``prepareForOperation`` - like ``getProperty`` already does)

# 1.0.3
* Removed reflection calls in ``CustomizableJsonToWebElementConverter`` as https://github.com/SeleniumHQ/selenium/issues/15884 was fixed #11
  * Selenium version 4.34+ is required
* Updated dependencies

# 1.0.2
* ``ImprovedRemoteWebElement``
  * Make it possible to disable auto scroll
  * Don't throw an exception when scrolling into view is somehow not working
  * Improve logger initialization

# 1.0.1
* Correctly declare ``software.xdev:testcontainers-selenium`` as scope ``test``

# 1.0.0
_Initial release_

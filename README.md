# MirrorDisplay
MirroDisplay is yet another Android App for use in [smart mirrors](https://www.postscapes.com/diy-smart-mirrors/) and work from Android 4.0.3 (API 15). The project offers basic functions and is easy to extend. 
The following features are implemented so far:
  - weather forecast (OpenWeather)
  - agenda (Google calendar, events of all activated calendars are shown)
  - rss feed
  - date and time display
  - customizable settings for rss feed url, number of rss feed entries, OpenWeather API key and weather location

Update (July 2020):
Implemented the use of OpenWeather API due to the closing of DarkSky. A free api key can be acquired at https://openweathermap.org/price. Further more uv index information and 
"feels like" temperature was added to weather info. Older devices from Android "Jelly Bean" 4.3+ are still supported.

The displayed data is update every 15 minutes but can be changed in each service class easily.
  
  ![Main screen](https://raw.githubusercontent.com/open-sourced/mirrordisplay/master/pub/Screenshot_Main.png)
  
  ![Preferences screen](https://raw.githubusercontent.com/open-sourced/mirrordisplay/master/pub/Screenshot_Preferences.png)
  
## Not implemented:
  - Multilanguage
  - Landscape mode
  - Support for smaller screens
  
## License
This project is licensed under the terms of the MIT license.

# bluetoothmonitorplugin
This is a plug in  developed in Cordova frame work for Android to monitor Bluetooth connections.

This is a Cordova plug in developed for Android application. The plug in will moitor and notify the connection status so that it is possible to moitor 'Bluetooth' connections. This is an experimental plug in and currently it has the following functionalities:

  -- Can get Bonded bluetooth devices
  
  -- Bluetooth connection events
  
  -- Bluetooth disconnect event
  
  -- Finding near by bluetooth devices
  
  -- scannning devices
  
  How to use it
  =============
  
  The project is a complete mirror of the development folder. In order to add it as a plug-in in any Android project, you need to issue the following command in your CLI:
  
  <b>cordova plugin add bluetoothmonitorplugin</b>
  
  The other alternate method is , just open the project in Android studio. I think this is the easiest way of doing it. All the client  side code ie, javascript, css and html files are included in the folder. Please take a look at it. You can open the project as if you are opening an Android project in studio. You will get the project information if you go through 'platform' folder in the project.
  
  
For Development
==============
Please go through index.js file to know more of its usage. You can see two methods in that file. One is an event which is used to listen the 'Bluetooth events'.

The second method will fetch all the 'bonded' devices.

Note: I hadve added 'alert' in all places. This is necessary to understand the bluetooth events. Later, I will remove these noisy alerts.

If there are any issues, please open issue tracker so that I can look into it.

Testing
=======
I have tested this plug in with Android 5. I do not have other android devices to test this plug in at the moment.

Ionic Users
===========


Scaffold project with ionic command and add this plug in by using 'ionic cordova plugin add bluetoothmonitor'



  
  
  

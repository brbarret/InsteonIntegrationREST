# InsteonIntegrationREST

29 September 2017 - Version 1.2 for General Release
SmartThings device handlers and SmartApp to facilitate integration between SmartThings and Insteon devices running on Indigo.

These tools allow a user to create devices in SmartThings corresponding to your Insteon lighting devices 
(other things may work, but aren't yet supported) in Indigo.  These devices may be controlled by Indigo or by 
SmartThings and the device handlers will help keept the device state in sync on both platforms.

Indigo ultimately manages the devices, but SmartThings can control the devices and get their status. 

## Requirements

* Apple Mac computer running Indigo Domotics Server 7.0+, with REST services enabled
* Insteon devices (designed for lighting, should work with anything switched
* SmartThings Hub (duh)
* Knowledge of using triggers in Indigo 7.0+ when device state changes
 
## Architecture

The device handlers allow the user to create devices in SmartThings corresponding to the lighting (or switched) 
Insteon devices.  The intention is that the device handler could control the device and the REST server on Indigo
would return an HTTP Response containing the current state of the device.  A refresh() command was intended to allow the user to ask Indigo for status without initiating control of the device.

But as usual, things didn't quite work that way.  The device handlers were unable to properly capture the HTTP response despite many hours of searching and trying stuff.

Enter the SmartApp:  It establishes an endpoint that allows Indigo to send device status to SmartThings, pretty much acting like a callback.  It is called by triggers in Indigo that sense when an applicable device has changed and then 
initiates an HTTP GET to your ST hub to update the state of the corresponding device in HT by calling the devices refresh() command.

## Device Handlers

* dimmer-insteon-rest : Intended for devices that support dimming
* switch-insteon-rest : Intended for devices that only support on/off functionality

## SmartApp

* insteon-status-grabber : Sets up the endpoint on your ST hub and allows calls that update your corresponding
devices in SmartThings.

### TODO 28 Sep 2017:

* Would like to initiate refresh from the devices on the SmartThings side
* Need to allow users to specify their own device maps in the SmartApp (or dispense with the map altogether) **DONE 29 Sep**
* Iron out and document the OAuth process for any other users

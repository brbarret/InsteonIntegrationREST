#Applescript Snippet for Use In Your Triggers in Indigo

##Use this for dimmers

set deviceName to "Front Porch"
tell application "IndigoServer"
status request deviceName
delay 2 -- the status request is asynchronous, so you need to wait a second or two for the reply
set deviceStatus to (brightness of device deviceName as string)
end tell

set stDeviceName to "Front%20Porch" -- This is the actual name of the device in SmartThings Hub, URLEncoded

do shell script "curl -H \"Authorization: Bearer <your-auth-token>\" -X GET \"<your-endpoint-path>/statusreport/" & stDeviceName & "/" & deviceStatus & "\""


##Use this for on/off switches

set deviceName to "Office Torchiere"
tell application "IndigoServer"
status request deviceName
delay 2 -- the status request is asynchronous, so you need to wait a second or two for the reply
set bDeviceStatus to (on state of device deviceName as string)
end tell

if bDeviceStatus is "true" then
set deviceStatus to "On"
else
set deviceStatus to "Off"
end if

set stDeviceName to "Study" -- This is the actual name of the device in SmartThings Hub, URLEncoded

do shell script "curl -H \"Authorization: Bearer <your-auth-token>\" -X GET \"<your-endpoint-path>/statusreport/" & stDeviceName & "/" & deviceStatus & "\""

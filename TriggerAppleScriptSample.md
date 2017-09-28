#Applescript Snippet for Use In Your Triggers in Indigo

##Use this for dimmers

set deviceName to "Bedside Lamps"
tell application "IndigoServer"
status request deviceName
delay 2 -- the status request is asynchronous, so you need to wait a second or two for the reply
set deviceStatus to (brightness of device deviceName as string)
end tell

set deviceName to "Bedside%20Lamps"

do shell script "curl -H \"Authorization: Bearer <your-auth-token>\" -X GET \"<your-endpoint-path>/statusreport/" & deviceName & "/" & deviceStatus & "\""


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

set deviceName to "Office%20Torchiere"

do shell script "curl -H \"Authorization: Bearer <your-auth-token>\" -X GET \"<your-endpoint-path>/statusreport/" & deviceName & "/" & deviceStatus & "\""

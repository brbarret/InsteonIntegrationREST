/**
 *  Switch_Insteon_REST
 *
 *  Copyright 2017 Brandt Barretto
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 * VERSION HISTORY
 * 2017-09-19 : 1.0 BETA : Initial Version
 * 2017-09-27 : 1.1 BETA : First version published on GitHub
 * 2017-09-29 : 1.2 : Code cleanup
 * 
 */
 
metadata {
	definition (name: "Switch_Insteon_REST", namespace: "brbarret", author: "Brandt Barretto") {
		capability "Switch"
        capability "Refresh"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label: '${name}', action: "switch.off", icon:"st.Lighting.light4", backgroundColor: "#00a0dc"
				attributeState "off", label: '${name}', action: "switch.on", icon:"st.Lighting.light4", backgroundColor: "#ffffff"
			}

            
		}
        
		valueTile("power", "device.power", width: 2, height: 2) {
			state "power", label:'${currentValue}', defaultState: true
		}
		main "switch"
		details(["switch", "power"])
	}
    //"st.Lighting.light6"
    preferences {
    	input "ip", "string", title: "IP Address", description: "IP Address of Indigo Server", 
        	required: true, displayDuringSetup: true
            
        input "port", "string", title: "Port", description: "Port used by Indigo Server", 
        	required: true, displayDuringSetup: true
            
        input "theDevice", "string", title: "Path", description: "Device to control, please URLEncode the string and match the device name in Indigo (i.e. - My%20Light)", 
        	required: true, displayDuringSetup: true
            
    }
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'switch' attribute
	def msg = parseLanMessage(description)

    def headersAsString = msg.header // => headers as a string
    def headerMap = msg.headers      // => headers as a Map
    def body = msg.body              // => request body as a string
    def status = msg.status          // => http status code of the response
    def json = msg.json              // => any JSON included in response body, as a data structure of lists and maps
    def xml = msg.xml                // => any XML included in response body, as a document tree structure
    log.debug "Parsed ${msg}"
}

// handle commands
def on() {

	log.debug "Executing 'on'"

	//log.debug "On result is: ${result}"
    sendEvent(name: "power", value: "On")
    def result = sendEthernet("isOn=true", ip, port)
    //def result = sendInternet("isOn=true")
    log.debug "Result is: ${result}"

    return result

}

def off() {

	log.debug "Executing 'off'"
	// TODO: handle 'off' command

    sendEvent(name: "power", value: "Off")
    def result = sendEthernet("isOn=false", ip, port)
    //def result = sendInternet("isOn=false")
    log.debug "Result is: ${result}"

    return result

}

def refresh(thisState) {
	
	sendEvent(name: "power", value: "$thisState")
    thisState = thisState.toLowerCase()
    sendEvent(name: "switch", value: "$thisState")
	
}


private sendEthernet(whatToDo, ip, port) {

	def indigoPath = "/devices/"
	return  new physicalgraph.device.HubAction(
        method: "PUT",
        path: "${indigoPath}${theDevice}?${whatToDo}",
        headers: [
            HOST: convertIPtoHex(ip) + ":" + convertPortToHex(port),
            "Content-Length": "5000",
			Accept: 	"*/*",
            "Content-Type": "application/json"
        ]
    )
}

private String convertIPtoHex(ipAddress) { 
   String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02X', it.toInteger() ) }.join()
   //log.debug "IP address entered is $ipAddress and the converted hex code is $hex"
   return hex

}

private String convertPortToHex(port) {
   String hexport = port.toString().format( '%04X', port.toInteger() )
   //log.debug "Port ${port} is ${hexport} in hex"
   return hexport
}

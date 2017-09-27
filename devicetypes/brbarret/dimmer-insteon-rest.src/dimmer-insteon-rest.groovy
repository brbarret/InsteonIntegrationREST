/**
 *  Dimmer_Insteon_REST
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
 */
metadata {
	definition (name: "Dimmer_Insteon_REST", namespace: "brbarret", author: "Brandt Barretto") {
		capability "Refresh"
        capability "Switch"
		capability "Switch Level"
	}


	simulator {
		// TODO: define status and reply messages here
	}

		tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#00a0dc", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#00a0dc", nextState:"on"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"off"
			}
			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel"
			}
		}

		standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}

		valueTile("level", "device.level", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "level", label:'${currentValue} %', unit:"%", backgroundColor:"#ffffff"
		}

		main(["switch"])
		details(["switch", "level"])	
	}
    
    preferences {
    	input "ip", "string", title: "IP Address", description: "IP Address of Indigo Server", 
        	required: true, displayDuringSetup: true
            
        input "port", "string", title: "Port", description: "Port used by Indigo Server", 
        	required: true, displayDuringSetup: true
            
        input "theDevice", "string", title: "Path", description: "Device to control", 
        	required: true, displayDuringSetup: true
    }
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'level' attribute

}

// handle commands
def refresh(thisState) {
	log.debug "Executing refresh ${thisState}"
    def thisLevel = thisState.toInteger()
	sendEvent(name: "level", value: "$thisLevel", unit: "%", isStateChange: true)
    if (thisLevel > 0) {
		sendEvent(name: "switch", value: "on")
	} else {
		sendEvent(name: "switch", value: "off")
	}
}

def on() { setDimmerLevel(100) }

def off() { setDimmerLevel(0) } 

private setDimmerLevel(value) {
	
	log.debug "Executing 'setDimmerLevel' ${value}"
    if (value > 0) {
		sendEvent(name: "switch", value: "on")
	} else {
		sendEvent(name: "switch", value: "off")
	}
	sendEvent(name: "level", value: value, unit: "%")
    def result = sendEthernet("brightness=${value}", ip, port)
    //def result = sendInternet("isOn=true")
    log.debug "Result is: ${result}"

    return result
}

def setLevel(level) {
	
	log.debug "Executing 'setLevel' ${level}"
    if (level > 0) {
		sendEvent(name: "switch", value: "on")
	} else {
		sendEvent(name: "switch", value: "off")
	}
	sendEvent(name: "level", value: level, unit: "%")
    def result = sendEthernet("brightness=${level}", ip, port)
    //def result = sendInternet("isOn=true")
    log.debug "Result is: ${result}"

    return result
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
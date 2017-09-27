/**
 *  Insteon Status Grabber
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
definition(
    name: "Insteon Status Grabber",
    namespace: "brbarret",
    author: "Brandt Barretto",
    description: "Exposing REST endpoint so that an server running Indigo Domotics can send REST calls with device status to SmartThings.  This will keep the status of any devices managed in both places in sync",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")


preferences {
        section("Insteon Interface") {
            // TODO: put inputs here

            input "theSwitches", "capability.switch", title: "Switches", required: true, multiple: true 
            //input "theDimmer", "capability.switchLevel", title: "Dimmer Switches", required: true, multiple: true 
	}
}

mappings {
  path("/statusreport") {
    action: [
      GET: "showAllStates"
    ]
  }
  path("/statusreport/:device/:state") {
    action: [
      GET: "showState"
    ]
  }
  path("/insteonControl/:device/:state") {
  	action: [
    	GET: "controlDevice"
    ]
  }

}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	// TODO: subscribe to attributes, devices, locations, etc.
	//state.myDevices = [:]
    
}

// TODO: implement event handlers

def showAllStates () {
	def resp = []
    theSwitches.each {
      resp << [name: it.displayName, value: it]
    }
    return resp}

def showState() {
    
    //log.debug("Device parameter: ${params.device}, deviceMap -> ${deviceMap()}")
    // get the device from the list
    def thisDevice = theSwitches.find{it.name == deviceMap().get(params.device)} 
    def deviceState = params.state
    log.debug("Device info: ${thisDevice?.getDeviceNetworkId()}")
    log.debug("The device ${thisDevice} is ${deviceState}")
    
    thisDevice.refresh("${deviceState}")
}

// Mapping of names from the Indigo Server to the names that are (will be) used for the device in ST
def deviceMap() { return ['Office Torchiere':'Study', 'Loft Lamp':'Loft', 'Bird Cage':'Bird Cage', 'Bedside Lamps':'Bedside',
			'Front Porch':'Front Porch', 'Garage Lights':'Garage Outdoor Lights', 'Screen Porch':'Screen Porch', 'Mud Room': 'Mud Room', 'FR Table Lamp':'Family Room 1',
            'FR Floor Lamp': 'Family Room 2', 'Front Spot':'Front Spot', 'Game Room 1': 'Game Room 1', 'Game Room 2': 'Game Room 2',
            'MBR Stick Lamp': 'MBR Stick Lamp'] }

def controlDevice() {}

def manageCallback() { return true }
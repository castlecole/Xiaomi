/**
 *  Xiaomi Mijia Honeywell Gas Detector
 *  Version 0.51
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
 *  Contributions to code from alecm, alixjg, bspranger, gn0st1c, Inpier, foz333, jmagnuson, KennethEvers, rinkek, ronvandegraaf, snalee, tmleaf  
 *  Discussion board for this DH: https://community.smartthings.com/t/original-aqara-xiaomi-zigbee-sensors-contact-temp-motion-button-outlet-leak-etc/
 *
 *  Useful Links:
 *	Review in english photos dimensions etc... https://blog.tlpa.nl/2017/11/12/xiaomi-also-mijia-and-honeywell-smart-fire-detector/
 *	Device purchased here (€20.54)... https://www.gearbest.com/alarm-systems/pp_615081.html
 *	RaspBee packet sniffer... https://github.com/dresden-elektronik/deconz-rest-plugin/issues/152
 *	Instructions in English.. http://files.xiaomi-mi.com/files/MiJia_Honeywell/MiJia_Honeywell_Smoke_Detector_EN.pdf
 *	Fire Certification is CCCF... https://www.china-certification.com/en/ccc-certification-for-fire-safety-products-cccf/
 *	... in order to be covered by your insurance and for piece of mind, please also use correctly certified detectors if CCCF is not accepted in your country  
 *  
 *  Todo:
 *	Possible to force alarm test from application?
 *	... manual simulation mode activated by holding physical button for 3 seconds
 *	Possible to set installation site
 *	... apparently with MiApp you can choose from 3 sites to adjust sensitivity
 *	... Screenshot of app option here: http://www.cooltechbox.com/review-xiaomi-mijia-honeywell-smoke-detector/
 *
 *  Known issues:
 *	Xiaomi sensors do not seem to respond to refresh requests // workaround... push physical button 1 time for refresh
 *	Inconsistent rendering of user interface text/graphics between iOS and Android devices - This is due to SmartThings, not this device handler
 *	Pairing Xiaomi sensors can be difficult as they were not designed to use with a SmartThings hub, for this one, normally just tap main button 3 times
 *
 *  Fingerprint Endpoint data:
 *        01 - endpoint id
 *        0104 - profile id
 *        0402 - device id
 *        01 - ignored
 *        06 - number of in clusters
 *        0000 0003 0012 0500 000C 0001 - inClusters
 *        01 - number of out clusters
 *        0019 - outClusters
 *        manufacturer "LUMI" - must match manufacturer field in fingerprint
 *        model "lumi.sensor_smoke" - must match model in fingerprint
 *
 *
 *  Change Log:
 *	14.02.2018 - foz333 - Version 0.5 Released
 *	19.02.2018 - test state tile added, smoke replaces fire for SHM support
 *	23.02.2018 - new battery icon introdused, default volatge set to 3.25
 */

def version() {
	return "v3 (20170317)\nXiaomi Honeywell Gas Detector"
}


metadata {
	definition (name: "Xiaomi Honeywell Gas Detector V3", namespace: "castlecole", author: "bspranger") {
		capability "Configuration" //commands: configure()
		capability "Smoke Detector" //attributes: smoke ("detected","clear","tested")

		capability "Health Check"		
		capability "Sensor"

		command "resetClear"
		command "resetSmoke"
		command "enrollResponse"
		attribute "lastTested", "String"
		attribute "lastTestedDate", "Date"
		attribute "lastCheckinDate", "Date"		
		attribute "lastCheckin", "string"
		attribute "lastSmoke", "String"
		attribute "lastSmokeDate", "Date"		
	
		fingerprint endpointId: "01", profileID: "0104", deviceID: "0402", inClusters: "0000,0003,0012,0500,000C,0001", outClusters: "0019", manufacturer: "LUMI", model: "lumi.sensor_smoke", deviceJoinName: "Xiaomi Honeywell Smoke Detector"
	}       

    	// simulator metadata
	simulator {
    	}
	
	preferences {
		//Date & Time Config
		input description: "", type: "paragraph", element: "paragraph", title: "DATE & CLOCK"    
		input name: "dateformat", type: "enum", title: "Set Date Format\nUS (MDY) - UK (DMY) - Other (YMD)", description: "Date Format", options:["US","UK","Other"]
		input name: "clockformat", type: "bool", title: "Use 24 hour clock?"
		input description: "Version: ${version()}", type: "paragraph", element: "paragraph", title: ""
	}
	
	tiles(scale: 2) {
		multiAttributeTile(name:"smoke", type: "lighting", width: 6, height: 4) {
			tileAttribute ("device.smoke", key: "PRIMARY_CONTROL") {
           			attributeState( "clear", label:'CLEAR', icon:"https://raw.githubusercontent.com/castlecole/customdevices/master/alarm-clear0.png", backgroundColor:"#359148")
				attributeState( "tested", label:"TEST", icon:"https://raw.githubusercontent.com/castlecole/customdevices/master/alarm-notclear0.png", backgroundColor:"#e86d13")
				attributeState( "detected", label:'SMOKE', icon:"https://raw.githubusercontent.com/castlecole/customdevices/master/alarm-notclear0.png", backgroundColor:"#ed0000")   
 			}
			tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
				attributeState("default", label:'Last Checkin: ${currentValue}', icon: "st.Health & Wellness.health9")
			}
		}

		multiAttributeTile(name:"smoke2", type: "lighting", width: 6, height: 4) {
			tileAttribute ("device.smoke", key: "PRIMARY_CONTROL") {
   			attributeState( "clear", label:'CLEAR', icon:"https://raw.githubusercontent.com/castlecole/customdevices/master/House-Normal.png", backgroundColor:"#359148")
				attributeState( "tested", label:"TEST", icon:"https://raw.githubusercontent.com/castlecole/customdevices/master/House-Fire.png", backgroundColor:"#e86d13")
				attributeState( "detected", label:'SMOKE', icon:"https://raw.githubusercontent.com/castlecole/customdevices/master/House-Fire.png", backgroundColor:"#ed0000")   
 			}
		}
		valueTile("lastTested", "device.lastTested", inactiveLabel: false, decoration: "flat", width: 4, height: 2) {
        		state "default", label:'Last Tested:\n ${currentValue}'
		}
		standardTile("refresh", "device.refresh", inactiveLabel: False, decoration: "flat", width: 2, height: 2) {
		    	state "default", action:"refresh.refresh", icon:"https://raw.githubusercontent.com/castlecole/customdevices/master/refresh.png"
    		}
		
		main (["smoke2"])
		details(["smoke", "lastTested", "refresh"])
	}
}

// Parse incoming device messages to generate events
def parse(String description) {
	log.debug "${device.displayName}: Parsing description: ${description}"

	// Determine current time and date in the user-selected date format and clock style
	def now = formatDate()    
	def nowDate = new Date(now).getTime()

	// Any report - test, smoke, clear in a lastCheckin event and update to Last Checkin tile
	// However, only a non-parseable report results in lastCheckin being displayed in events log
	sendEvent(name: "lastCheckin", value: now, displayed: false)
	sendEvent(name: "lastCheckinDate", value: nowDate, displayed: false)

	// getEvent automatically retrieves temp and humidity in correct unit as integer
	Map map = zigbee.getEvent(description)

	if (description?.startsWith('zone status')) {
		map = parseZoneStatusMessage(description)
		if (map.value == "detected") {
			sendEvent(name: "lastSmoke", value: now, displayed: false)
			sendEvent(name: "lastSmokeDate", value: nowDate, displayed: false)
		} else if (map.value == "tested") {
			sendEvent(name: "lastTested", value: now, displayed: false)
			sendEvent(name: "lastTestedDate", value: nowDate, displayed: false)
		}	
	} else if (description?.startsWith('catchall:')) {
		map = parseCatchAllMessage(description)
	} else if (description?.startsWith('read attr - raw:')) {
		map = parseReadAttr(description)
	} else if (description?.startsWith('enroll request')) {
		List cmds = zigbee.enrollResponse()
		log.debug "enroll response: ${cmds}"
		result = cmds?.collect { new physicalgraph.device.HubAction(it) }
	} else {
		log.debug "${device.displayName}: was unable to parse ${description}"
		sendEvent(name: "lastCheckin", value: now) 
	}
	if (map) {
		log.debug "${device.displayName}: Parse returned ${map}"
		return createEvent(map)
	} else {
		return [:]
	}
}

// Parse the IAS messages
private Map parseZoneStatusMessage(String description) {
	def result = [
		name: 'smoke',
		value: value,
		descriptionText: 'smoke detected'
	]
	if (description?.startsWith('zone status')) {
		if (description?.startsWith('zone status 0x0002')) { // User Test
			result.value = "tested"
			result.descriptionText = "${device.displayName} has been tested"
		} else if (description?.startsWith('zone status 0x0001')) { // smoke detected
			result.value = "detected"
			result.descriptionText = "${device.displayName} has detected smoke"
		} else if (description?.startsWith('zone status 0x0000')) { // situation normal... no smoke
			result.value = "clear"
			result.descriptionText = "${device.displayName} is all clear"
		}
		return result
	}
	return [:]
}

// Check catchall for battery voltage data to pass to getBatteryResult for conversion to percentage report
private Map parseCatchAllMessage(String description) {
	Map resultMap = [:]
	def catchall = zigbee.parse(description)
	log.debug catchall

	return resultMap
}

// Parse raw data on reset button press
private Map parseReadAttr(String description) {
	Map resultMap = [:]

	def cluster = description.split(",").find {it.split(":")[0].trim() == "cluster"}?.split(":")[1].trim()
	def attrId = description.split(",").find {it.split(":")[0].trim() == "attrId"}?.split(":")[1].trim()
	def value = description.split(",").find {it.split(":")[0].trim() == "value"}?.split(":")[1].trim()

	log.debug "${device.displayName}: Parsing read attr: cluster: ${cluster}, attrId: ${attrId}, value: ${value}"

	if (cluster == "0000" && attrId == "0005") {
		def modelName = ""
		// Parsing the model name
		for (int i = 0; i < value.length(); i+=2) {
			def str = value.substring(i, i+2);
			def NextChar = (char)Integer.parseInt(str, 16);
			modelName = modelName + NextChar
		}
		log.debug "${device.displayName}: Reported model: ${modelName}"
	}
	return resultMap
}

def resetClear() {
	sendEvent(name:"smoke", value:"clear")
}

def resetSmoke() {
	sendEvent(name:"smoke", value:"smoke")
}

// configure() runs after installed() when a sensor is paired
def configure() {
	log.debug "${device.displayName}: configuring"
	return
}

// updated() will run twice every time user presses save in preference settings page
def updated() {
	checkIntervalEvent("updated")
}

// Device wakes up every 1 hours, this interval allows us to miss one wakeup notification before marking offline	
private checkIntervalEvent(text) {
	log.debug "${device.displayName}: Configured health checkInterval when ${text}()"
	sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
}

def formatDate(batteryReset) {
	def correctedTimezone = ""
	def timeString = clockformat ? "HH:mm:ss" : "h:mm:ss aa"

	// If user's hub timezone is not set, display error messages in log and events log, and set timezone to GMT to avoid errors
	if (!(location.timeZone)) {
		correctedTimezone = TimeZone.getTimeZone("GMT")
		log.error "${device.displayName}: Time Zone not set, so GMT was used. Please set up your location in the SmartThings mobile app."
		sendEvent(name: "error", value: "", descriptionText: "ERROR: Time Zone not set, so GMT was used. Please set up your location in the SmartThings mobile app.")
	} else {
		correctedTimezone = location.timeZone
	}
	if (dateformat == "US" || dateformat == "" || dateformat == null) {
			return new Date().format("EEE MMM dd yyyy ${timeString}", correctedTimezone)
	} else if (dateformat == "UK") {
			return new Date().format("EEE dd MMM yyyy ${timeString}", correctedTimezone)
	} else {
			return new Date().format("EEE yyyy MMM dd ${timeString}", correctedTimezone)
	}
}
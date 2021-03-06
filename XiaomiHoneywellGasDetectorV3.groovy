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
 *
 *  zbjoin: {"dni":"6C10","d":"00158D000103901E","capabilities":"8E","endpoints":[
 *				{"simple":"01 0104 0101 02 07 0000 0004 0003 0001 0002 000A 0500 02 0019 000A","application":"08","manufacturer":"LUMI","model":"lumi.sensor_natgas"}]
 *				,"parent":"0000","joinType":1}
 *
 *  Fingerprint Endpoint data:
 *        01 - endpoint id
 *        0104 - profile id
 *        0101 - device id
 *        02 - ignored (Version)
 *        07 - number of in clusters
 *        0000 0004 0003 0001 0002 000A 0500 - inClusters
 *        02 - number of out clusters
 *        0019 000A - outClusters
 *        manufacturer "LUMI" - must match manufacturer field in fingerprint
 *        model "lumi.sensor_natgas" - must match model in fingerprint
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
		capability "Battery" //attributes: battery
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
		attribute "lastDescription", "String"
	
		fingerprint endpointId: "01", profileID: "0104", deviceID: "0101", inClusters: "0000,0004,0003,0001,0002,000A,0500", outClusters: "0019,000A", manufacturer: "LUMI", model: "lumi.sensor_natgas", deviceJoinName: "Xiaomi Honeywell Gas Detector"
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
           			attributeState( "clear", label:'CLEAR', icon:"https://raw.githubusercontent.com/castlecole/customdevices/master/alarm-clear0.png", backgroundColor:"#00a0dc")
				attributeState( "tested", label:"TESTED!", icon:"https://raw.githubusercontent.com/castlecole/customdevices/master/alarm-notclear0.png", backgroundColor:"#e86d13")
				attributeState( "detected", label:'GAS DETECTED!', icon:"https://raw.githubusercontent.com/castlecole/customdevices/master/alarm-notclear0.png", backgroundColor:"#ed0000")   
 			}
			tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
				attributeState("default", label:'Last Checkin: ${currentValue}', icon: "st.Health & Wellness.health9")
			}
		}

		multiAttributeTile(name:"smoke2", type: "lighting", width: 6, height: 4) {
			tileAttribute ("device.smoke", key: "PRIMARY_CONTROL") {
   				attributeState( "clear", label:'CLEAR', icon:"https://raw.githubusercontent.com/castlecole/customdevices/master/House-GAS-Normal.png", backgroundColor:"#00a0dc")
				attributeState( "tested", label:"TESTED!", icon:"https://raw.githubusercontent.com/castlecole/customdevices/master/House-GAS-Event.png", backgroundColor:"#e86d13")
				attributeState( "detected", label:'GAS DETECTED!', icon:"https://raw.githubusercontent.com/castlecole/customdevices/master/House-GAS-Event.png", backgroundColor:"#ed0000")   
 			}
		}
        	valueTile("battery", "device.battery", decoration: "flat", inactiveLabel: false, width: 2, height: 2) {
            		state "battery", label:'99%'+"\n", unit:"%", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Battery.png",
				backgroundColors:[
					[value: 0, color: "#ff1800"],
					[value: 10, color: "#fb854a"],
					[value: 25, color: "#ceec24"],
					[value: 50, color: "#71f044"],
					[value: 75, color: "#33d800"]
				]
		}
		valueTile("lastCheckin", "device.lastCheckin",  inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'', icon: "st.Health & Wellness.health9", backgroundColor: "#ceec24"
		}
		valueTile("lastSmoke", "device.lastSmoke", inactiveLabel: false, decoration: "flat", width: 4, height: 1) {
        		state "default", label:'Last GAS Detected:\n ${currentValue}'
		}
		standardTile("refresh", "device.refresh", inactiveLabel: False, decoration: "flat", width: 2, height: 2) {
		    	state "default", action:"refresh.refresh", icon:"https://raw.githubusercontent.com/castlecole/customdevices/master/refresh.png"
    		}
		valueTile("lastTested", "device.lastTested", inactiveLabel: false, decoration: "flat", width: 4, height: 1) {
        		state "default", label:'Last Tested:\n ${currentValue}'
		}
		valueTile("lastDescription", "device.lastDescription", inactiveLabel: false, decoration: "flat", width: 4, height: 1) {
        		state "default", label:'${currentValue}'
		}
		
		main (["smoke2"])
		details(["smoke", "lastSmoke", "lastTested", "refresh"])
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
	sendEvent(name: "lastCheckin", value: now, displayed: true)
	sendEvent(name: "lastCheckinDate", value: nowDate, displayed: false)
	sendEvent(name: "lastDescription", value: description, displayed: false)

	// getEvent automatically retrieves temp and humidity in correct unit as integer
	Map map = zigbee.getEvent(description)

	if (description?.startsWith('zone status')) {
		map = parseZoneStatusMessage(description)
		if (map.value == "detected") {
			sendEvent(name: "lastSmoke", value: now, displayed: true)
			sendEvent(name: "lastSmokeDate", value: nowDate, displayed: false)
			sendEvent(name: "lastDescription", value: map.descriptionText, displayed: false)
		} else if (map.value == "tested") {
			sendEvent(name: "lastTested", value: now, displayed: true)
			sendEvent(name: "lastTestedDate", value: nowDate, displayed: false)
			sendEvent(name: "lastDescription", value: map.descriptionText, displayed: false)
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
		sendEvent(name: "lastCheckin", value: now, displayed: true) 
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
		descriptionText: 'Gas detected',
		displayed: true
	]
	if (description?.startsWith('zone status')) {
		if (description?.startsWith('zone status 0x0002')) { // User Test
			result.value = "tested"
			result.descriptionText = "${device.displayName} has been tested"
		} else if (description?.startsWith('zone status 0x0001')) { // gas detected
			result.value = "detected"
			result.descriptionText = "${device.displayName} has detected gas!!!"
		} else if (description?.startsWith('zone status 0x0000')) { // situation normal... no gas
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

	if (catchall.clusterId == 0x0000) {
		def MsgLength = catchall.data.size()
		// Original Xiaomi CatchAll does not have identifiers, first UINT16 is Battery
		if ((catchall.data.get(0) == 0x01 || catchall.data.get(0) == 0x02) && (catchall.data.get(1) == 0xFF)) {
			for (int i = 4; i < (MsgLength-3); i++) {
				if (catchall.data.get(i) == 0x21) { // check the data ID and data type
					// next two bytes are the battery voltage
					resultMap = getBatteryResult((catchall.data.get(i+2)<<8) + catchall.data.get(i+1))
					break
				}
			}
		}
	}
	
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

// Convert raw 4 digit integer voltage value into percentage based on minVolts/maxVolts range

private Map getBatteryResult(rawValue) {

	// raw voltage is normally supplied as a 4 digit integer that needs to be divided by 1000
	// but in the case the final zero is dropped then divide by 100 to get actual voltage value 
/*	def rawVolts = rawValue / 1000
	def minVolts
	def maxVolts

	if (voltsmin == null || voltsmin == ""){
		minVolts = 2.5
	} else {
		minVolts = voltsmin
	}

	if (voltsmax == null || voltsmax == "") {
		maxVolts = 3.0
	} else {
		maxVolts = voltsmax
	}

	def pct = (rawVolts - minVolts) / (maxVolts - minVolts)
	def roundedPct = Math.min(100, Math.round(pct * 100))
*/
	def result = [
		name: 'battery',
		value: 100,
		unit: "%",
		isStateChange: true,	
		descriptionText : "${device.displayName} Battery at 100% - Mains power"
	]

	return result

}
def resetClear() {
    sendEvent(name:"smoke", value:"clear", display: false)
}

def resetSmoke() {
    sendEvent(name:"smoke", value:"smoke", display: false)
}

// configure() runs after installed() when a sensor is paired
def configure() {
    log.debug "${device.displayName}: configuring"
    state.battery = 0

    sendEvent(name: "lastSmoke", value: "--", displayed: true)
    sendEvent(name: "lastTested", value: "--", displayed: true)

    return zigbee.configureReporting(0x0006, 0x0000, 0x10, 1, 7200, null)
    // cluster 0x0006, attr 0x0000, datatype 0x10 (boolean), min 1 sec, max 7200 sec, reportableChange = null (because boolean)
    //zigbee.readAttribute(0x0006, 0x0000) 
    // Read cluster 0x0006 (on/off status)
    // return
}

def refresh() {

    log.debug "${device.displayName}: refreshing"
    zigbee.configureReporting(0x0006, 0x0000, 0x10, 1, 7200, null)
    sendEvent(name: "lastSmoke", value: "--", displayed: true)
    sendEvent(name: "lastTested", value: "--", displayed: true)
    sendEvent(name: "smoke", value: "clear", displayed: true)
    checkIntervalEvent("refreshed")

    //return zigbee.readAttribute(0x0006, 0x0000) +
    // Read cluster 0x0006 (on/off status)
    //zigbee.configureReporting(0x0006, 0x0000, 0x10, 1, 7200, null)
    // cluster 0x0006, attr 0x0000, datatype 0x10 (boolean), min 1 sec, max 7200 sec, reportableChange = null (because boolean)
}

def installed() {
    state.battery = 100
    sendEvent(name: "lastSmoke", value: "--", displayed: true)
    sendEvent(name: "lastTested", value: "--", displayed: true)
    checkIntervalEvent("installed")
}

// updated() will run twice every time user presses save in preference settings page
def updated() {
    checkIntervalEvent("updated")
}

def ping() {
    return zigbee.readAttribute(zigbee.POWER_CONFIGURATION_CLUSTER, 0x0020) // Read the Battery Level
}

// Device wakes up every 6 to 10 hours, a 12 hour interval allows us to miss one wakeup notification before marking offline	
private checkIntervalEvent(text) {
    log.debug "${device.displayName}: Configured health checkInterval when ${text}()"
    zigbee.configureReporting(0x0006, 0x0000, 0x10, 1, 7200, null)
    sendEvent(name: "checkInterval", value: 12 * 60 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
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

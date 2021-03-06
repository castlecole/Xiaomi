/**
 *  Xiaomi Aqara Leak Sensor
 *
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
 *  Based on original DH by Eric Maycock 2015 and Rave from Lazcad
 *
 *  Change log:
 *  Added DH Colours
 *  Added 100% battery max
 *  Fixed battery parsing problem
 *  Added lastcheckin attribute and tile
 *  Added extra tile to show when last opened
 *  Colours to confirm to new smartthings standards
 *  Added ability to force override current state to Open or Closed.
 *  Added experimental health check as worked out by rolled54.Why
 *  bspranger - Adding Aqara Support
 *  Rinkelk - added date-attribute support for Webcore
 *  Rinkelk - Changed battery percentage with code from cancrusher
 *  Rinkelk - Changed battery icon according to Mobile785
 *  sulee - Added endpointId copied from GvnCampbell's DH - Detects sensor when adding
 *  sulee - Track battery as average of min and max over time
 *  sulee - Clean up some of the code
 *  bspranger - renamed to bspranger to remove confusion of a4refillpad
 *  veeceeoh - added battery parse on button press
 *  veeceeoh - added wet/dry override capability
 */

def version() {
	return "v2 (20170324)\nXiaomi Aqara Leak Sensor - Zigbee"
}

metadata {
    definition (name: "Xiaomi Aqara Leak Sensor", namespace: "castlecole", author: "bspranger") {
        capability "Configuration"
        capability "Sensor"
        capability "Water Sensor"
        capability "Refresh"
        capability "Battery"
        capability "Health Check"

	attribute "lastCheckin", "String"
        attribute "lastWet", "String"
        attribute "batteryRuntime", "String"

        fingerprint endpointId: "01", profileId: "0104", deviceId: "0402", inClusters: "0000,0003,0001", outClusters: "0019", manufacturer: "LUMI", model: "lumi.sensor_wleak.aq1", deviceJoinName: "Xiaomi Leak Sensor"

        command "resetDry"
        command "resetWet"
        command "resetBatteryRuntime"
    }

    simulator {
        status "dry": "on/off: 0"
        status "wet": "on/off: 1"
    }

    preferences {
	input description: "Version: ${version()}", type: "paragraph", element: "paragraph", title: ""
    }

    tiles(scale: 2) {
        multiAttributeTile(name:"water", type: "generic", width: 6, height: 4){
            tileAttribute ("device.water", key: "PRIMARY_CONTROL") {
                attributeState "dry", label:"", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Water_NoLeak.png", backgroundColor:"#00a0dc"
                attributeState "wet", label:"", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Water_Leak.png", backgroundColor:"#e86d13"
            }
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
                attributeState("default", label:'Last Checkin: ${currentValue}',icon: "st.Health & Wellness.health9")
            }
        }

        multiAttributeTile(name:"water2", type: "generic", width: 6, height: 4){
            tileAttribute ("device.water", key: "PRIMARY_CONTROL") {
                attributeState "dry", label:"DRY", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Water_NoLeak.png", backgroundColor:"#00a0dc"
                attributeState "wet", label:"WET / LEAK", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Water_Leak.png", backgroundColor:"#e86d13"
	    }
	}
	valueTile("battery", "device.battery", decoration: "flat", inactiveLabel: false, width: 2, height: 2) {
           state "default", label:'${currentValue}%'+"\n", unit:"", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Battery.png",
		backgroundColors:[
			[value: 0, color: "#ff1800"],
			[value: 10, color: "#fb854a"],
			[value: 25, color: "#ceec24"],
			[value: 50, color: "#71f044"],
			[value: 75, color: "#33d800"]
		]
        }
        valueTile("lastWet", "device.lastWet", decoration: "flat", inactiveLabel: false, width: 4, height: 1) {
            state "default", label:'Last Wet: \n${currentValue}'
        }
        valueTile("lastcheckin", "device.lastCheckin", decoration: "flat", inactiveLabel: false, width: 4, height: 1) {
            state "default", label:'Last Checkin:\n${currentValue}', backgroundColor:"#00a0dc"
        }
        standardTile("resetWet", "device.resetWet", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", action:"resetWet", label: "Override Wet", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Water_Leak2.png"
        }
        standardTile("resetDry", "device.resetDry", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", action:"resetDry", label: "Override Dry", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Water_NoLeak2.png"
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
	    state "default", action:"refresh.refresh", icon:"https://raw.githubusercontent.com/castlecole/customdevices/master/refresh.png"
        }
        valueTile("batteryRuntime", "device.batteryRuntime", inactiveLabel: false, decoration: "flat", width: 4, height: 1) {
            state "batteryRuntime", label:'Battery Changed (tap to reset):\n ${currentValue}', unit:"", action:"resetBatteryRuntime"
        }

        main (["water2"])
        details(["water", "battery", "resetDry", "resetWet", "lastWet", "batteryRuntime", "refresh"])
    }
}

def parse(String description) {
    log.debug "${device.displayName} Description:${description}"

    // send event for heartbeat
    def now = new Date().format("EEE MMM dd yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)

    Map map = [:]

    if (description?.startsWith('zone status')) {
        map = parseZoneStatusMessage(description)
        if (map.value == "wet") {
            sendEvent(name: "lastWet", value: now, displayed: false)
        }
    } else if (description?.startsWith('catchall:')) {
        map = parseCatchAllMessage(description)
    } else if (description?.startsWith('read attr - raw:')) {
        map = parseReadAttr(description)
    }

    log.debug "${device.displayName}: Parse returned ${map}"
    def results = map ? createEvent(map) : null
    return results
}

private Map parseZoneStatusMessage(String description) {
    def result = [
        name: 'water',
        value: value,
        descriptionText: 'water contact'
    ]
    if (description?.startsWith('zone status')) {
        if (description?.startsWith('zone status 0x0001')) { // detected water
            result.value = "wet"
            result.descriptionText = "${device.displayName} has detected water"
        } else if (description?.startsWith('zone status 0x0000')) { // did not detect water
            result.value = "dry"
            result.descriptionText = "${device.displayName} is dry"
        }
        return result
    }

    return [:]
}

private Map getBatteryResult(rawValue) {
    def rawVolts = rawValue / 1000

    def minVolts = 2.7
    def maxVolts = 3.3
    def pct = (rawVolts - minVolts) / (maxVolts - minVolts)
    def roundedPct = Math.min(100, Math.round(pct * 100))

    def result = [
        name: 'battery',
        value: roundedPct,
        unit: "%",
        isStateChange: true,
	descriptionText: "${device.displayName} Battery is ${roundedPct}%\n(${rawVolts} Volts)"
    ]

    log.debug "${device.displayName}: ${result}"
    if (state.battery != result.value) {
        state.battery = result.value
        resetBatteryRuntime()
    }
    return result
}

private Map parseCatchAllMessage(String description) {
    Map resultMap = [:]
    def i
    def cluster = zigbee.parse(description)
    log.debug "${device.displayName}: Parsing CatchAll: '${cluster}'"

    if (cluster) {
        switch(cluster.clusterId) {
            case 0x0000:
                def MsgLength = cluster.data.size();
                for (i = 0; i < (MsgLength-3); i++) {
                    if ((cluster.data.get(i) == 0x01) && (cluster.data.get(i+1) == 0x21))  // check the data ID and data type
                    {
                        // next two bytes are the battery voltage.
                        resultMap = getBatteryResult((cluster.data.get(i+3)<<8) + cluster.data.get(i+2))
                    }
                }
            break
        }
    }
    return resultMap
}

// Parse raw data on reset button press to retrieve reported battery voltage
private Map parseReadAttr(String description) {
    def buttonRaw = (description - "read attr - raw:")
    Map resultMap = [:]

    def cluster = description.split(",").find {it.split(":")[0].trim() == "cluster"}?.split(":")[1].trim()
    def attrId = description.split(",").find {it.split(":")[0].trim() == "attrId"}?.split(":")[1].trim()
    def value = description.split(",").find {it.split(":")[0].trim() == "value"}?.split(":")[1].trim()
    def model = value.split("01FF")[0]
    def data = value.split("01FF")[1]
    //log.debug "cluster: ${cluster}, attrId: ${attrId}, value: ${value}, model:${model}, data:${data}"

    if (data[4..7] == "0121") {
        def MaxBatteryVoltage = (Integer.parseInt((data[10..11] + data[8..9]),16))/1000
        state.maxBatteryVoltage = MaxBatteryVoltage
    }

    if (cluster == "0000" && attrId == "0005")  {
        resultMap.name = 'Model'
        resultMap.value = ""
        resultMap.descriptionText = "device model"
        // Parsing the model
        for (int i = 0; i < model.length(); i+=2) {
            def str = model.substring(i, i+2);
            def NextChar = (char)Integer.parseInt(str, 16);
            resultMap.value = resultMap.value + NextChar
        }
        return resultMap
    }

    return [:]
}

def configure() {
    state.battery = 0
    log.debug "${device.displayName}: configuring"
    return zigbee.readAttribute(0x0001, 0x0020) + zigbee.configureReporting(0x0001, 0x0020, 0x21, 600, 21600, 0x01)
}

def refresh() {
    log.debug "${device.displayName}: refreshing"
    return zigbee.readAttribute(0x0001, 0x0020) + zigbee.configureReporting(0x0001, 0x0020, 0x21, 600, 21600, 0x01)
}

def resetDry() {
    def now = new Date().format("EEE MMM dd yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "water", value:"dry")
    sendEvent(name: "lastCheckin", value: now)
}

def resetWet() {
    def now = new Date().format("EEE MMM dd yyyy h:mm:ss a", location.timeZone)
    sendEvent(name:"water", value:"wet")
    sendEvent(name: "lastWet", value: now)
}

def resetBatteryRuntime() {
    def now = new Date().format("MMM dd yyyy", location.timeZone)
    sendEvent(name: "batteryRuntime", value: now)
}

def installed() {
    checkIntervalEvent("installed");
}

def updated() {
    checkIntervalEvent("updated");
}

private checkIntervalEvent(text) {
    // Device wakes up every 1 hours, this interval allows us to miss one wakeup notification before marking offline
    log.debug "${device.displayName}: Configured health checkInterval when ${text}()"
    sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
}

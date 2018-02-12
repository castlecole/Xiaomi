/**
 *  Broadlink A1 Temperature Sensor
 *
 *  Copyright 2016 BeckyR
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
 *  HTTP Result of call:
 *       {"temperature":2.7,"light":0.0,"status":"ok","noisy":0.0,"timestamp":"1518381427205","humidity":66.2,"deviceMac":"b4430dfbf413","air":0.0,"uri":"/a1_status"}
 */
 
 
// 09/01/2016 - itsamti - Added new switch definition below 
metadata {
	
    definition (name: "BroadLink LAN A1 Temperature Sensor", namespace: "castlecole", author: "BeckyR") {
        capability "Temperature Measurement"
        capability "Relative Humidity Measurement"
        capability "Illuminance Measurement"
        capability "Sensor"
        capability "Refresh"
        capability "Health Check"

        attribute "lastCheckin", "String"
    }

    preferences {
	section {
	    input("server", "text", title: "Server", description: "Your BroadLink RM Bridge HTTP Server IP")
    	    input("port", "text", title: "Port", description: "Your BroadLink RM Bridge HTTP Server Port")
    	    input("userId", "text", title: "User Id", description: "Your BroadLink RM Bridge User Id")
    	    input("userPass", "text", title: "Password", description: "Your BroadLink RM Bridge Password")
    	    input("macAddress", "text", title: "Mac Address [aa:bb:cc:dd:ee:ff]", description: "Mac Address for the device (e.g. 34:ea:34:e4:84:7e)")
	}
	section {
            input title: "Temperature Offset", description: "This feature allows you to correct any temperature variations by selecting an offset. Ex: If your sensor consistently reports a temp that's 5 degrees too warm, you'd enter '-5'. If 3 degrees too cold, enter '+3'. Please note, any changes will take effect only on the NEXT temperature change.", displayDuringSetup: true, type: "paragraph", element: "paragraph"
            input "tempOffset", "number", title: "Degrees", description: "Adjust temperature by this many degrees", range: "*..*", displayDuringSetup: true, defaultValue: 0, required: true
        }
        section {
            input name: "PressureUnits", type: "enum", title: "Pressure Units", options: ["mbar", "kPa", "inHg", "mmHg"], description: "Sets the unit in which pressure will be reported", defaultValue: "mbar", displayDuringSetup: true, required: true
        }
        section {
            input title: "Pressure Offset", description: "This feature allows you to correct any pressure variations by selecting an offset. Ex: If your sensor consistently reports a pressure that's 5 too high, you'd enter '-5'. If 3 too low, enter '+3'. Please note, any changes will take effect only on the NEXT pressure change.", displayDuringSetup: true, type: "paragraph", element: "paragraph"
            input "pressOffset", "number", title: "Pressure", description: "Adjust pressure by this many units", range: "*..*", displayDuringSetup: true, defaultValue: 0, required: true
        }
    }

    simulator {
        for (int i = 0; i <= 100; i += 10) {
            status "${i}F": "temperature: $i F"
        }

        for (int i = 0; i <= 100; i += 10) {
            status "${i}%": "humidity: ${i}%"
        }
    }

    tiles(scale: 2) {
        multiAttributeTile(name:"temperature", type:"generic", width:6, height:4) {
            tileAttribute("device.temperature", key:"PRIMARY_CONTROL") {
                attributeState("temperature", label:'${currentValue}°',
                    backgroundColors:[
                        [value: 0, color: "#153591"],
                        [value: 5, color: "#1e9cbb"],
                        [value: 10, color: "#90d2a7"],
                        [value: 15, color: "#44b621"],
                        [value: 20, color: "#f1d801"],
                        [value: 25, color: "#d04e00"],
                        [value: 30, color: "#bc2323"],
                        [value: 44, color: "#1e9cbb"],
                        [value: 59, color: "#90d2a7"],
                        [value: 74, color: "#44b621"],
                        [value: 84, color: "#f1d801"],
                        [value: 95, color: "#d04e00"],
                        [value: 96, color: "#bc2323"]
                    ]
                )
            }
            tileAttribute("device.temperature", key:"SECONDARY_CONTROL") {
                attributeState("default", label:'', icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/temperature2.png")
            }
        }
        valueTile("humidity", "device.humidity", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:'${currentValue}%', icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/humidity.png"
        }
        valueTile("air", "device.air", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "air",
			labels:[
				[value: 0, label: "Perfect"],
				[value: 1, label: "Normal"],
				[value: 2, label: "Poor"]
			],
			backgroundColors:[
                    		[value: 0, color: "#C5C08B"],
                    		[value: 1, color: "#8E8400"],
                    		[value: 2, color: "#000000"]
			]
        }
        valueTile("light", "device.light", decoration: "flat", inactiveLabel: false, width: 2, height: 2) {
		state "light",
			labels:[
				[value: 0, label: "Dark"],
				[value: 1, label: "Normal"],
				[value: 2, label: "Bright"]
			],
			backgroundColors:[
                    		[value: 0, color: "#000000"],
                    		[value: 1, color: "#8E8400"],
                    		[value: 2, color: "#C5C08B"]
			]
        }
        valueTile("noisy", "device.noisy", decoration: "flat", inactiveLabel: false, width: 2, height: 2) {
            	state "noisy",
			labels:[
				[value: 0, label: "Silent"],
				[value: 1, label: "Normal"],
				[value: 2, label: "Noisy"]
			],
			backgroundColors:[
                    		[value: 0, color: "#000000"],
                    		[value: 1, color: "#8E8400"],
                    		[value: 2, color: "#C5C08B"]
			]
        }
        valueTile("temperature2", "device.temperature", decoration: "flat", inactiveLabel: false) {
            state "temperature", label:'${currentValue}°', icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/room-env-icon.png",
                backgroundColors:[
                    [value: 0, color: "#153591"],
                    [value: 5, color: "#1e9cbb"],
                    [value: 10, color: "#90d2a7"],
                    [value: 15, color: "#44b621"],
                    [value: 20, color: "#f1d801"],
                    [value: 25, color: "#d04e00"],
                    [value: 30, color: "#bc2323"],
                    [value: 44, color: "#1e9cbb"],
                    [value: 59, color: "#90d2a7"],
                    [value: 74, color: "#44b621"],
                    [value: 84, color: "#f1d801"],
                    [value: 95, color: "#d04e00"],
                    [value: 96, color: "#bc2323"]
                ]
        }
        valueTile("lastcheckin", "device.lastCheckin", decoration: "flat", inactiveLabel: false, width: 4, height: 2) {
            state "default", label:'Last Checkin:\n ${currentValue}'
        }
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
		state "default", action:"refresh.refresh", icon:"https://raw.githubusercontent.com/castlecole/customdevices/master/refresh.png"
        }

        main(["temperature2"])
        details(["temperature", "humidity", "light", "air", "noisy", "lastcheckin", "refresh"])
    
    }
}

/*
def parse(String description) {
	  def pair = description.split(":")
	  createEvent(name: pair[0].trim(), value: pair[1].trim())
}
*/

def installed() {
// Device wakes up every 1 hour, this interval allows us to miss one wakeup notification before marking offline
    log.debug "Configured health checkInterval when installed()"
    sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
    put()
}

def updated() {
// Device wakes up every 1 hours, this interval allows us to miss one wakeup notification before marking offline
    log.debug "Configured health checkInterval when updated()"
    sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 2 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
    put()
}

// Parse incoming device messages to generate events
def parse(String description) {
    log.debug "${device.displayName}: Parsing description: ${description}"
    // send event for heartbeat
    def now = new Date().format("EEE MMM dd yyyy h:mm:ss a", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)
    sendEvent(name: "lastCheckinDate", value: nowDate, displayed: false)

    Map map = [:]

    if (description?.startsWith("temperature: ")) {
        map = parseTemperature(description)
    } else if (description?.startsWith("humidity: ")) {
        map = parseHumidity(description)
    } else if (description?.startsWith('catchall:')) {
        map = parseCatchAllMessage(description)
    } else if (description?.startsWith('read attr - raw:')) {
        map = parseReadAttr(description)
    }
    def results = null
    if (map)
    {
    	log.debug "${device.displayName}: Parse returned ${map}"
    	results =createEvent(map)
    }
    else
    {
    	log.debug "${device.displayName}: was unable to parse ${description}"
    }
    return results
}


private Map parseTemperature(String description){
    def temp = ((description - "temperature: ").trim()) as Float
    if (tempOffset == null || tempOffset == "" ) tempOffset = 0
    if (temp > 100) {
      temp = 100.0 - temp
    }
    if (getTemperatureScale() == "C") {
        if (tempOffset) {
            temp = (Math.round(temp * 10))/ 10 + tempOffset as Float
        } else {
            temp = (Math.round(temp * 10))/ 10 as Float
        }
    } else {
        if (tempOffset) {
            temp =  (Math.round((temp * 90.0)/5.0))/10.0 + 32.0 + tempOffset as Float
        } else {
            temp = (Math.round((temp * 90.0)/5.0))/10.0 + 32.0 as Float
        }
    }
    def units = getTemperatureScale()

    def result = [
        name: 'temperature',
        value: temp,
        unit: units,
        isStateChange:true,
        descriptionText : "${device.displayName} temperature is ${temp}${units}"
    ]
    return result
}


private Map parseHumidity(String description){
    def pct = (description - "humidity: " - "%").trim()

    if (pct.isNumber()) {
        pct =  Math.round(new BigDecimal(pct))

        def result = [
            name: 'humidity',
            value: pct,
            unit: "%",
            isStateChange:true,
            descriptionText : "${device.displayName} Humidity is ${pct}%"
        ]
        return result
    }

    return [:]
}


private Map parseCatchAllMessage(String description) {
    def i
    Map resultMap = [:]
    def cluster = zigbee.parse(description)
    log.debug cluster
    if (cluster) {
        switch(cluster.clusterId) 
        {
            case 0x0000:
                def MsgLength = cluster.data.size();

                // Original Xiaomi CatchAll does not have identifiers, first UINT16 is Battery
                if ((cluster.data.get(0) == 0x02) && (cluster.data.get(1) == 0xFF)) {
                    for (i = 0; i < (MsgLength-3); i++)
                    {
                        if (cluster.data.get(i) == 0x21) // check the data ID and data type
                        {
                            // next two bytes are the battery voltage.
                            resultMap = getBatteryResult((cluster.data.get(i+2)<<8) + cluster.data.get(i+1))
                            break
                        }
                    }
                } else if ((cluster.data.get(0) == 0x01) && (cluster.data.get(1) == 0xFF)) {
                    for (i = 0; i < (MsgLength-3); i++)
                    {
                        if ((cluster.data.get(i) == 0x01) && (cluster.data.get(i+1) == 0x21))  // check the data ID and data type
                        {
                            // next two bytes are the battery voltage.
                            resultMap = getBatteryResult((cluster.data.get(i+3)<<8) + cluster.data.get(i+2))
                            break
                        }
                    }
                }
            break
        }
    }
    return resultMap
}


// Parse raw data on reset button press to retrieve reported battery voltage
private Map parseReadAttr(String description) {
    Map resultMap = [:]

    def cluster = description.split(",").find {it.split(":")[0].trim() == "cluster"}?.split(":")[1].trim()
    def attrId = description.split(",").find {it.split(":")[0].trim() == "attrId"}?.split(":")[1].trim()
    def value = description.split(",").find {it.split(":")[0].trim() == "value"}?.split(":")[1].trim()

    log.debug "${device.displayName} parseReadAttr: cluster: ${cluster}, attrId: ${attrId}, value: ${value}"

    if ((cluster == "0403") && (attrId == "0000")) {
        def result = value[0..3]
        float pressureval = Integer.parseInt(result, 16)

        log.debug "${device.displayName}: Converting ${pressureval} to ${PressureUnits}"

        switch (PressureUnits) {
            case "mbar":
                pressureval = (pressureval/10) as Float
                pressureval = pressureval.round(1);
                break;

            case "kPa":
                pressureval = (pressureval/100) as Float
                pressureval = pressureval.round(2);
                break;

            case "inHg":
                pressureval = (((pressureval/10) as Float) * 0.0295300)
                pressureval = pressureval.round(2);
                break;

            case "mmHg":
                pressureval = (((pressureval/10) as Float) * 0.750062)
                pressureval = pressureval.round(2);
                break;
        }

        log.debug "${device.displayName}: ${pressureval} ${PressureUnits} before applying the pressure offset."

	if (pressOffset == null || pressOffset == "" ) pressOffset = 0    
	if (pressOffset) {
            pressureval = (pressureval + pressOffset)
            pressureval = pressureval.round(2);
        }
        
        resultMap = [
            name: 'pressure',
            value: pressureval,
            unit: "${PressureUnits}",
            isStateChange:true,
            descriptionText : "${device.displayName} Pressure is ${pressureval}${PressureUnits}"
        ]
    }

    return resultMap
}


def refresh(){
    log.debug "${device.displayName}: refreshing"
//    return zigbee.readAttribute(0x0000, 0x0001) + zigbee.configureReporting(0x0000, 0x0001, 0x21, 600, 21600, 0x01) + zigbee.configureReporting(0x0402, 0x0000, 0x29, 30, 3600, 0x0064)
	
    put()
}

def configure() {
    // Device-Watch allows 2 check-in misses from device + ping (plus 1 min lag time)
    // enrolls with default periodic reporting until newer 5 min interval is confirmed
//    sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 1 * 60, displayed: false, data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
    put()
    // temperature minReportTime 30 seconds, maxReportTime 5 min. Reporting interval if no activity
    // battery minReport 30 seconds, maxReportTime 6 hrs by default
//    return zigbee.readAttribute(0x0000, 0x0001) + zigbee.configureReporting(0x0000, 0x0001, 0x21, 600, 21600, 0x01) + zigbee.configureReporting(0x0402, 0x0000, 0x29, 30, 3600, 0x0064)
}

	
private put() {
    def url1 = "${settings.server}:${settings.port}"
    def userpassascii="${userId.trim()}:${userPass.trim()}"
    def userpass = "Basic " + userpassascii.encodeAsBase64().toString()
    def uri = ""

    def ad = "${settings.macAddress.replace(':','')}
    uri = "/send?deviceMac=$ad"

    log.debug "URL1 is: ${url1}"
    log.debug "URI is : ${uri}"
    log.debug "User is : ${userpassascii}"

    def hubaction = new physicalgraph.device.HubAction(method: "GET",
	path: "$uri",
	headers: [HOST: "$url1", AUTHORIZATION: "$userpass"]
    )

    return hubaction
	
	
//sendHubCommand(new physicalgraph.device.HubAction("""GET /xml/device_description.xml HTTP/1.1\r\nHOST: $ip\r\n\r\n""", physicalgraph.device.Protocol.LAN, myMAC, [callback: calledBackHandler]))

// the below calledBackHandler() is triggered when the device responds to the sendHubCommand() with "device_description.xml" resource
/*
void calledBackHandler(physicalgraph.device.HubResponse hubResponse) {
    log.debug "Entered calledBackHandler()..."
    def body = hubResponse.xml
    def devices = getDevices()
    def device = devices.find { it?.key?.contains(body?.device?.UDN?.text()) }
    if (device) {
        device.value << [name: body?.device?.roomName?.text(), model: body?.device?.modelName?.text(), serialNumber: body?.device?.serialNum?.text(), verified: true]
    }
    log.debug "device in calledBackHandler() is: ${device}"
    log.debug "body in calledBackHandler() is: ${body}"
}
*/	

}

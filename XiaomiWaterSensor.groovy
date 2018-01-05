/**
 *  Copyright 2015 SmartThings
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
 
import physicalgraph.zigbee.clusters.iaszone.ZoneStatus

metadata {
    definition (name: "Xiaomi Aqara Water Sensor", namespace: "castlecole", author: "MaverickASC") {
        capability "Actuator"
        capability "Configuration"
        capability "Refresh"
        capability "Health Check"
        capability "Battery"
		capability "Water Sensor"
        
        command "enrollResponse"

        fingerprint inClusters: "0000 0003 0001", outClusters: "0019", manufacturer: "LUMI", model: "lumi.sensor_wleak.aq1", deviceJoinName: "Aqara Water Leak Sensor"
    }

	simulator {
	}

	preferences {
		section {
			image(name: 'educationalcontent', multiple: true, images: [
					"http://cdn.device-gse.smartthings.com/Moisture/Moisture1.png",
					"http://cdn.device-gse.smartthings.com/Moisture/Moisture2.png",
					"http://cdn.device-gse.smartthings.com/Moisture/Moisture3.png"
			])
		}
	}

    tiles(scale: 2) {
        multiAttributeTile(name: "water", type: "generic", width: 6, height: 4) {
			tileAttribute("device.water", key: "PRIMARY_CONTROL") {
				attributeState "dry", label: "Dry", icon: "st.alarm.water.dry", backgroundColor: "#ffffff"
				attributeState "wet", label: "Wet", icon: "st.alarm.water.wet", backgroundColor: "#00A0DC"
			}
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
				attributeState("default", label:'Last Update: ${currentValue}',icon: "st.Health & Wellness.health9")
			}
		}
        standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        
        valueTile("battery", "device.battery", decoration: "flat", inactiveLabel: false, width: 2, height: 2) {
			state "default", label:'${currentValue}% battery', unit:"",
            backgroundColors: [
							[value: 10, color: "#bc2323"],
							[value: 25, color: "#f1d801"],
							[value: 50, color: "#44b621"]
					]
		}
        
        standardTile("configure", "device.configure", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "configure", label:'', action:"configuration.configure", icon:"st.secondary.configure"
		}
        
        main "water"
        details(["water", "battery", "refresh","configure"])
    }
}

// Parse incoming device messages to generate events
def parse(String description) {

	log.debug "AQUARA-WS RAW: $description"
    Map result = [:]
    Map map = [:]

	if ((description?.startsWith('enroll request')) || description?.startsWith('zbjoin:') || description?.startsWith('non-TV event zbjoin:')) {
		List cmds = enrollResponse()
		log.debug "AQUARA-WS ENROLL RESPONSE: ${cmds}"
		result = cmds?.collect { new physicalgraph.device.HubAction(it) }
	} else {

		if (description?.startsWith('catchall: ')) {
		   map = parseCatchAllMessage(description)
		}
		else if (description?.startsWith('zone status')) {
		   map = parseIasMessage(description)
		}
		else if (description?.startsWith('read attr -')) {
			map = parseReportAttributeMessage(description)
		}
		else {
			log.debug "AQUARA-WS UNKNOWN: $description"			// TODO: Never seen this type of event. Please report when this kind of event occurs
		}

		log.debug "AQUARA-WS RESULT: $map"
		result = map ? createEvent(map) : [:]
    }

    def now = new Date().format("yyyy MMM dd EEE h:mm:ss a", location.timeZone)
    sendEvent(name: "lastCheckin", value: now)

	return result
}

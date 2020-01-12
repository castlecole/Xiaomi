/**
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
 *  Huge thanks, to the original by Lazcad / RaveTam
 *  Mods for Hui 3 Gang Switch by Netsheriff
 */

def version() {
	return "v2.0 (20190408)\nZigBee 2-Gang Switch"
}

metadata {
    definition (name: "ZigBee 2-Gang Switch", namespace: "castlecole", author: "smartthings") {
        capability "Actuator"
        capability "Configuration"
        capability "Refresh"
        capability "Switch"
        capability "Health Check"
  
        fingerprint profileId: "9C09", inClusters: "0000, 0003, 0006", outClusters: "0019", manufacturer: "TUYATEC-p9HCE8pZ", model: "TS0011", deviceJoinName: "ZigBee 2-Gang Switch"

  // 01 0104 0100 00 03 0000 0003 0006 01 0019   application: 46 endpointId: 01 manufacturer: TUYATEC-p9HCE8pZ model: TS0011 zigbeeNodeType: SLEEPY_END_DEVICE 
  // zbjoin: {"dni":"9C09","d":"EC1BBDFFFE5B768F","capabilities":"80","endpoints":[{"simple":"01 0104 0100 00 03 0000 0003 0006 01 0019","application":"46","manufacturer":"TUYATEC-O6SNCwd6","model":"TS0012"},{"simple":"02 0104 0100 00 03 0000 0003 0006 01 0019","application":"46","manufacturer":"TUYATEC-O6SNCwd6","model":"TS0012"}],"parent":"0000","joinType":1,"joinDurationMs":14543,"joinAttempts":1}
	    
  // zbjoin: {"dni":"8D26","d":"00124B001A441DE2","capabilities":"8E","endpoints":[
	//  {"simple":"01 C05E 0000 02 05 0000 0003 0004 0005 0006 00","application":"01","manufacturer":"ShenZhen_Homa","model":"HOMA1005"},
	//  {"simple":"02 C05E 0000 02 05 0000 0003 0004 0005 0006 00","application":"01","manufacturer":"ShenZhen_Homa","model":"HOMA1005"},
	//  {"simple":"03 C05E 0000 02 05 0000 0003 0004 0005 0006 00","application":"01","manufacturer":"ShenZhen_Homa","model":"HOMA1005"},
	//  {"simple":"0D C05E E15E 02 01 1000 01 1000","application":"","manufacturer":"","model":""}],"parent":"FFFF","joinType":15}
       
        attribute "lastCheckin", "string"
        attribute "switch", "string"
        attribute "switch1", "string"
        attribute "switch2", "string"
        // attribute "switch3", "string"

	command "on0"
    	command "off0"
    	command "on1"
    	command "off1"
        command "on2"
	command "off2"
	// command "on3"
	// command "off3"

        attribute "switch1","ENUM",["on","off"]
        attribute "switch2","ENUM",["on","off"]
        // attribute "switch3","ENUM",["on","off"]    
        // attribute "switchstate","ENUM",["on","off"] 
    
    }

    // simulator metadata
    simulator {
        
        // status messages
        status "on": "on/off: 1"
        status "off": "on/off: 0"
        
        
        status "switch1 on": "on/off: 1"
	status "switch1 off": "on/off: 0"
        status "switch2 on": "on/off: 1"
	status "switch2 off": "on/off: 0"
	// status "switch3 on": "on/off: 1"
	// status "switch3 off": "on/off: 0"

        // reply messages
        reply "zcl on-off on": "on/off: 1"
        reply "zcl on-off off": "on/off: 0"
    
    }

    preferences {
	      section {
	          input description: "Version: ${version()}", type: "paragraph", element: "paragraph", title: ""
	      }
    }

    tiles(scale: 2) {
     	multiAttributeTile(name:"switch", type: "device.switch", width: 6, height: 4, canChangeIcon: false){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") { 
                attributeState "on", label:'${name}', action:"off0", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Switch212On.png", backgroundColor:"#359148", nextState:"turningOff"
                attributeState "on1", label:'${name}', action:"off0", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Switch21Off.png", backgroundColor:"#359148", nextState:"turningOff"
                attributeState "on2", label:'${name}', action:"off0", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Switch22Off.png", backgroundColor:"#359148", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"on0", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Switch20Off.png", backgroundColor:"#00a0dc", nextState:"turningOn"
                attributeState "turningOn", label:'${name}', action:"off0", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Switch212On.png", backgroundColor:"#359148", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"on0", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Switch20Off.png", backgroundColor:"#00a0dc", nextState:"turningOn"
            }
            tileAttribute("device.lastCheckin", key: "SECONDARY_CONTROL") {
    		attributeState("default", label:'Last Update: ${currentValue}',icon: "st.Health & Wellness.health9")
	    }
        }
	    
	standardTile("switch1", "device.switch1", inactiveLabel: false, decoration: "flat", width: 2, height: 2, canChangeIcon: false){
                state("on", label:'SW1 On', action:"off1", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Switch1On.png", backgroundColor:"#359148", nextState:"turningOff")
                state("off", label:'SW1 Off', action:"on1", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Switch1Off.png", backgroundColor:"#00a0dc", nextState:"turningOn")
                state("turningOn", label:'Turning On', action:"off1", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Switch1On.png", backgroundColor:"#359148", nextState:"turningOff")
                state("turningOff", label:'Turning Off', action:"on1", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Switch1Off.png", backgroundColor:"#00a0dc", nextState:"turningOn")
        }

        standardTile("switch2", "device.switch2", inactiveLabel: false, decoration: "flat", width: 2, height: 2, canChangeIcon: false){
                state("on", label:'SW2 On', action:"off2", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Switch1On.png", backgroundColor:"#359148", nextState:"turningOff")
                state("off", label:'SW2 Off', action:"on2", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Switch1Off.png", backgroundColor:"#00a0dc", nextState:"turningOn")
                state("turningOn", label:'Turning On', action:"off2", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Switch1On.png", backgroundColor:"#359148", nextState:"turningOff")
                state("turningOff", label:'Turning Off', action:"on2", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/Switch1Off.png", backgroundColor:"#00a0dc", nextState:"turningOn")
        }

        standardTile("blank", "device.refresh", inactiveLabel: true, decoration: "flat", width: 4, height: 2) {
  	    state "default", label:"", action:""
	}

	standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh.refresh", icon:"https://raw.githubusercontent.com/castlecole/customdevices/master/refresh.png"
        }
        
	main(["switch"])
        details(["switch", "switch1", "switch2", "refresh"])
    }
}

// Parse incoming device messages to generate events

def parse(String description) {

    log.debug "Parsing message: '${description}'"
   
    def value = zigbee.parse(description)?.text
    log.debug "Parse message: $value"
    Map map = [:]
   
    if (description?.startsWith('catchall:')) {
        log.debug "Parse Catchall..."
	map = parseCatchAllMessage(description)
    }
    else if (description?.startsWith('read attr -')) {
        log.debug "Parse Read Attribute..."
	map = parseReportAttributeMessage(description)
    }
    else if (description?.startsWith('on/off: ')) {
        log.debug "Parse On/Off message..."
        // def refreshCmds = zigbee.readAttribute(0x0006, 0x0000, [destEndpoint: 0x10]) +
    	//		  zigbee.readAttribute(0x0006, 0x0000, [destEndpoint: 0x11]) +
 	//		  zigbee.readAttribute(0x0006, 0x0000, [destEndpoint: 0x12]) +             
	//		  zigbee.readAttribute(0x0006, 0x0000, [destEndpoint: 0xFF])
		
   	// return refreshCmds.collect { new physicalgraph.device.HubAction(it) }     
    	def resultMap = zigbee.getKnownDescription(description)
   	log.debug "Parse resultMap: ${resultMap}"
        
        map = parseCustomMessage(description) 
    }

    log.debug "Parse returned $map"
    //  send event for heartbeat    
    def now = new Date()
   
    sendEvent(name: "lastCheckin", value: now)
    
    def results = map ? createEvent(map) : null
    return results;

}

private Map parseCatchAllMessage(String description) {

    Map resultMap = [:]
    def cluster = zigbee.parse(description)
    log.debug cluster
    
    if (cluster.clusterId == 0x0006 && cluster.command == 0x01){
    	if (cluster.sourceEndpoint == 0x01) {
        	log.debug "Its Switch one"
    		def onoff = cluster.data[-1]
        	if (onoff == 1)
        	    resultMap = createEvent(name: "switch1", value: "on")
        	else if (onoff == 0)
        	    resultMap = createEvent(name: "switch1", value: "off")
        }
        else if (cluster.sourceEndpoint == 0x02) {
            	log.debug "Its Switch two"
    		def onoff = cluster.data[-1]
        	if (onoff == 1)
        	    resultMap = createEvent(name: "switch2", value: "on")
        	else if (onoff == 0)
            	    resultMap = createEvent(name: "switch2", value: "off")
	}
//	else if (cluster.sourceEndpoint == 0x03) {
//            	log.debug "Its Switch three"
//    		def onoff = cluster.data[-1]
//        	if (onoff == 1)
//        	    resultMap = createEvent(name: "switch3", value: "on")
//        	else if (onoff == 0)
//            	    resultMap = createEvent(name: "switch3", value: "off")
//            	}					
//    	}
    
	return resultMap
}

private Map parseReportAttributeMessage(String description) {
    
    Map descMap = (description - "read attr - ").split(",").inject([:]) { map, param ->
        def nameAndValue = param.split(":")
        map += [(nameAndValue[0].trim()):nameAndValue[1].trim()]
    }
    
    //log.debug "Desc Map: $descMap"
    Map resultMap = [:]

    //if (descMap.cluster == "0001" && descMap.attrId == "0020") {
	//resultMap = getBatteryResult(convertHexToInt(descMap.value / 2))
    //}
    
    if (descMap.cluster == "0002" && descMap.attrId == "0000") {
	resultMap = createEvent(name: "temperature", value: zigbee.parseHATemperatureValue("temperature: " + (convertHexToInt(descMap.value) / 2), "temperature: ", getTemperatureScale()), unit: getTemperatureScale())
	log.debug "Temperature Hex convert to ${resultMap.value}%"
    }
    else if (descMap.cluster == "0008" && descMap.attrId == "0000") {
resultMap = createEvent(name: "switch", value: "off")
    } 
    
    return resultMap
}

def off0() {
    	
	log.debug "ALL Off"
	sendEvent(name: "switch", value: "off")
	sendEvent(name: "switch1", value: "off", displayed: false)
	sendEvent(name: "switch2", value: "off", displayed: false)
//	sendEvent(name: "switch3", value: "off", displayed: false)
	
    	"st cmd 0x${device.deviceNetworkId} 0xFF 0x0006 0x0 {}" 
}

def on0() {
   	log.debug "ALL On"
	sendEvent(name: "switch", value: "on")
	sendEvent(name: "switch1", value: "on", displayed: false)
	sendEvent(name: "switch2", value: "on", displayed: false)
//	sendEvent(name: "switch3", value: "on", displayed: false)
	
    	"st cmd 0x${device.deviceNetworkId} 0xFF 0x0006 0x1 {}" 
}

def off1() {

    	log.debug "off1()"
	sendEvent(name: "switch1", value: "off")
	def currval2 = device.currentValue("switch2")
	
	log.debug "OFF_1: ${currval2} & ${currval3}"
	
	if(currval2=="off") {
	    sendEvent(name: "switch", value: "off", displayed: false)			 
	}
	else if (currval2=="on") {
	    sendEvent(name: "switch", value: "on2", displayed: false)			 
	}

	"st cmd 0x${device.deviceNetworkId} 0x01 0x0006 0x0 {}" 
}

def on1() {
   	log.debug "on1()"
	sendEvent(name: "switch1", value: "on")

	def currval2 = device.currentValue("switch2")
	
	log.debug "ON_1: ${currval2} & ${currval3}"
	
	if(currval2=="off") {
	    sendEvent(name: "switch", value: "on1", displayed: false)			 
	}
	else if (currval2=="on") {
	    sendEvent(name: "switch", value: "on", displayed: false)			 
	}

	"st cmd 0x${device.deviceNetworkId} 0x01 0x0006 0x1 {}" 
}

def off2() {
    	log.debug "off2()"
	sendEvent(name: "switch2", value: "off")

	def currval1 = device.currentValue("switch1")
	
	log.debug "OFF_2: ${currval1} & ${currval3}"

	if(currval1=="off") {
	    sendEvent(name: "switch", value: "off", displayed: false)			 
	}
	else if (currval1=="on") {
	    sendEvent(name: "switch", value: "on1", displayed: false)			 
	}

	"st cmd 0x${device.deviceNetworkId} 0x02 0x0006 0x0 {}" 
   }

def on2() {
   	log.debug "on2()"
	sendEvent(name: "switch2", value: "on")
	
	def currval1 = device.currentValue("switch1")
	
	log.debug "ON_2: ${currval1} & ${currval3}"

	if(currval1=="off") {
	    sendEvent(name: "switch", value: "on2", displayed: false)			 
	}
	else if (currval1=="on") {
	    sendEvent(name: "switch", value: "on", displayed: false)			 
	}

	"st cmd 0x${device.deviceNetworkId} 0x02 0x0006 0x1 {}"
}


def ping() {
	return refresh()
}

def refresh() {
	log.debug "Refreshing..."
	zigbee.onOffRefresh()
}

def configure() {
	log.debug "Config..."
	[
	"st rattr 0x${device.deviceNetworkId} 0x01 0x0006 0x0", "delay 1000",
	"st rattr 0x${device.deviceNetworkId} 0x02 0x0006 0x0", "delay 1000",
	// "st rattr 0x${device.deviceNetworkId} 0x03 0x0006 0x0", "delay 1000",
	"st rattr 0x${device.deviceNetworkId} 0xFF 0x0006 0x0", "delay 1000"
	]
	
}

private Map parseCustomMessage(String description) {
	def result
	if (description?.startsWith('on/off: ')) {
    		if (description == 'on/off: 0')
    			result = createEvent(name: "switch", value: "off")
    		else if (description == 'on/off: 1')
    			result = createEvent(name: "switch", value: "on")
	}
    
    	return result
}

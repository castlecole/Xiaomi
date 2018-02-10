/**
 *  Broadlink Light Switch
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
 *  1/7/17 - updated with better switch displays and use of device ID from user itsamti
 */
 
 
// 09/01/2016 - itsamti - Added new switch definition below 
metadata {
	
    definition (name: "BroadLink LAN Light Switch", namespace: "castlecole", author: "BeckyR") {
	capability "Switch"
        capability "Actuator"
        capability "Configuration"
        capability "Refresh"

	command "onPhysical"
	command "offPhysical"
    }

    preferences {
    	input("server", "text", title: "Server", description: "Your BroadLink RM Bridge HTTP Server IP")
    	input("port", "text", title: "Port", description: "Your BroadLink RM Bridge HTTP Server Port")
    	input("userId", "text", title: "User Id", description: "Your BroadLink RM Bridge User Id")
    	input("userPass", "text", title: "Password", description: "Your BroadLink RM Bridge Password")
    	input("macAddress", "text", title: "Mac Address [aa:bb:cc:dd:ee:ff]", description: "Mac Address for the device (e.g. 34:ea:34:e4:84:7e)")
	input("deviceIdOn", "text", title: "Device Code [xxx]", description: "Code ID for the ON server device command (i.e. switch ON the device)")
	input("deviceIdOff", "text", title: "Device Code [xxx]", description: "Code ID for the OFF server device command (i.e. switch OFF the device)")
    }

    simulator {
        // status messages
        status "on": "on/off: 1"
        status "off": "on/off: 0"

        // reply messages
        reply "zcl on-off on": "on/off: 1"
        reply "zcl on-off off": "on/off: 0"
    }

    tiles(scale: 2) {
        multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: false){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") { 
                attributeState "on", label:"", action:"switch.off", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/light_bulb_on.png", backgroundColor:"#359148", nextState:"turningOff"
                attributeState "off", label:"", action:"switch.on", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/light_bulb_off.png", backgroundColor:"#00a0dc", nextState:"turningOn"
                attributeState "turningOn", label:'\n\n Turning On', action:"switch.off", icon:"", backgroundColor:"#359148", nextState:"turningOff"
                attributeState "turningOff", label:'\n\n Turning Off', action:"switch.on", icon:"", backgroundColor:"#00a0dc", nextState:"turningOn"
            }
        }
        standardTile("blank", "device.refresh", inactiveLabel: true, decoration: "flat", width: 4, height: 2) {
            state "default", label:"", action:""
	}
	standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
  	    state "default", label:"", action:"refresh.refresh", icon:"https://raw.githubusercontent.com/castlecole/customdevices/master/refresh.png"
        }

        multiAttributeTile(name:"switch2", type: "lighting", width: 6, height: 4, canChangeIcon: false){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") { 
                attributeState "on", label:'${name}', action:"switch.off", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/light_bulb_on.png", backgroundColor:"#359148", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"switch.on", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/light_bulb_off.png", backgroundColor:"#00a0dc", nextState:"turningOn"
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/light_bulb_on.png", backgroundColor:"#359148", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"switch.on", icon:"https://raw.githubusercontent.com/castlecole/Xiaomi/master/light_bulb_off.png", backgroundColor:"#00a0dc", nextState:"turningOn"
            }
	}
	    
	main(["switch2"])
        details(["switch", "blank", "refresh"])
    }
}

def parse(String description) {
	  def pair = description.split(":")
	  createEvent(name: pair[0].trim(), value: pair[1].trim())
}

def on() {
	  sendEvent(name: "light", value: "on")
	  put('on')
}

def off() {
	  sendEvent(name: "light", value: "off")
	  put('off')
}

def onPhysical() {
	  sendEvent(name: "light", value: "on", type: "physical")
	  put('on')
}

def offPhysical() {
	  sendEvent(name: "light", value: "off", type: "physical")
	  put('off')
}

/*
private put(toggle) {
    def url1="192.168.1.21:7474"
    def userpassascii="root:Universe-02"
    def userpass = "Basic " + userpassascii.encodeAsBase64().toString()
    def toReplace = device.deviceNetworkId
    def replaced = toReplace.replaceAll(' ', '%20')
    def hubaction = new physicalgraph.device.HubAction(method: "GET",
               path: "/send?deviceMac=$replaced",
               headers: [HOST: "${url1}", AUTHORIZATION: "${userpass}"],
    )
    return hubaction
*/

	
private put(toggle) {
    def url1 = "${settings.server}:${settings.port}"
    def userpassascii="${userId.trim()}:${userPass.trim()}"
    def userpass = "Basic " + userpassascii.encodeAsBase64().toString()

    if ( toggle == "on" )
    {
	def ad = "${settings.macAddress.replace(':','')}&codeId=${settings.deviceIdOn.trim()}"
        def uri = "/send?deviceMac=$ad"
    }
    else if ( bulbRequest == "off" )
    {
	def ad = "${settings.macAddress.replace(':','')}&codeId=${settings.deviceIdOff.trim()}"
        def uri = "/send?deviceMac=$ad"
    }

    def hubaction = new physicalgraph.device.HubAction(method: "GET",
	path: "$uri",
        headers: [HOST: "${url1}", AUTHORIZATION: "${userpass}"],
    )

    return hubaction
	
	
//sendHubCommand(new physicalgraph.device.HubAction("""GET /xml/device_description.xml HTTP/1.1\r\nHOST: $ip\r\n\r\n""", physicalgraph.device.Protocol.LAN, myMAC, [callback: calledBackHandler]))

...

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

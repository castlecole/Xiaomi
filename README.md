# Xiaomi
Repository of SmartThings device handlers for Xiaomi Devices. Huge thanks to a4refillpad for creating these. I have made alterations to look and parameter access for my own use, but you are free to use if you so wish.

The devices are difficult to get paired initially. Plenty of information https://community.smartthings.com/t/release-xiaomi-sensors-and-button-beta/77576


I have now tested the following Xiaomi devices in the UK on my Smartthings Hub (All are Version 2 devices which are square with rounded corners, not the original round shaped devices):

1. Magic Cube - Works - But still evaluating device driver as only seems to recognize one motion.
2. Temperature Pressure & Humidity Sensor - Works great  - 4+ weeks and no issues at all.
3. Door Sensor - Works great - very responsive - 4+ weeks and no issues at all.
4. IR & Luminosity Sensor - Works great - very responsive - 4+ weeks and no issues at all.
5. Button - Works fine - although I have a Piston configured to watch for it and it has missed it on at least 2 occassions.
6. Power outlet - Works fine (See note below)

All these devices were purchased direct from China using AliExpress. They all took about 18 days to arrive, which is much better than it was 2 years ago with parcels from China, so full marks for the improved delivery. Most sellers ensured that there were no duties to pay, however, one ended up with the 20% VAT to be paid on collection from PO.

Now I know they all work, I will be investing some time in tidying up the Icons and layouts so that Readings are a consistent font size and colours are aligned to other devices on Smartthings.

<b>Note:</b> a4refillpad did not recommend the outlets, as they appear to make the system less stable. However, other people do not seem to report this. I am based in the Uk and have tested an outlet on 240v. It worked for me first time, with the usual initial pairing issues. I tested it for over a week, with power removed and applied to the outlet, as well as switching it on/off. The outlet never needed pairing again, it simply found itself as soon as power was applied and responded immediately to switching requests. As the pin configuration is not set for UK, and it comes appart very easily (one screw), I am now looking to use this inside exterior light switches, to trigger them whenever my cameras see something interesting...

# Xiaomi Original & Aqara Device Handlers for SmartThings

Maintained by bspranger
Forked from a4refillpad's Xiaomi repository. Contributions from veeceeoh, ronvandegraaf, tmleafs & gn0st1c.

Install manually or using GitHub integration with these settings:

---

## Pairing

These devices are not easy to pair initially. More information and help is available at <a href="https://community.smartthings.com/t/original-aqara-xiaomi-zigbee-sensors-contact-temp-motion-button-outlet-leak-etc/113253/1">this SmartThings Community Post</a>.


## Supported Xiaomi Devices

|||
|---|---|
|![Xiaomi Button](button.jpg)|![Xiaomi Aqara Button](aqarabutton.jpg)|
|**Xiaomi Button**|**Xiaomi Aqara Button**|
|![Xiaomi Door/Window Sensor](door.jpg)|![Xiaomi Aqara Door/Window Sensor](aqaradoor.jpg)|
|**Xiaomi Door/Window Sensor**|**Xiaomi Aqara Door/Window Sensor**|
|![Xiaomi Motion Sensor](motion.jpg)|![Xiaomi Aqara Motion Sensor](aqaramotion.jpg)|
|**Xiaomi Motion Sensor**|**Xiaomi Aqara Motion Sensor**|
|![Xiaomi Temperature Humidity Sensor](temp.jpg)|![Xiaomi Aqara Temperature Humidity Sensor](aqaratemp.jpg)|
|**Xiaomi Temperature Humidity Sensor**|**Xiaomi Aqara Temperature Humidity Sensor**|
|![Xiaomi mijia Honeywell Fire Alarm Detector](smoke.jpg)|![Xiaomi Aqara Leak Sensor](aqarawater.jpg)|
|**Xiaomi mijia Honeywell Fire Alarm Detector**|**Xiaomi Aqara Leak Sensor**|
|![Xiaomi Zigbee Outlet](outlet.jpg)||
|**Xiaomi Zigbee Outlet**||

**We do not recommend the Xiaomi Zigbee outlet as they may make SmartThings less stable.**

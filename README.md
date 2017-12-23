# Xiaomi
Repository of SmartThings device handlers for Xiaomi Devices. Huge thanks to a4refillpad for creating these. I have made alterations to look and parameter access for my own use, but you are free to use if you so wish.

The devices are difficult to get paired initially. Plenty of information https://community.smartthings.com/t/release-xiaomi-sensors-and-button-beta/77576


I have now tested the following Xiaomi devices in the UK on my Smartthings Hub (All are Version 2 devices which are square with rounded corners, not the original round shaped devices):

1. Magic Cube - Works - But still evaluating device driver as only seems to recognize one motion.
2. Temperature Pressure & Hunmidity Sensor - Works fine.
3. Door Sensor - Works fine.
4. IR & Luminosity Sensor - Works fine.
5. Button - Works fine.
6. Power outlet - Works fine (See note below)

<b>Note:</b> a4refillpad did not recommend the outlets, as they appear to make the system less stable. However, other people do not seem to report this. I am based in the Uk and have tested an outlet on 240v. It worked for me first time, with the usual initial pairing issues. I tested it for over a week, with power removed and applied to the outlet, as well as switching it on/off. The outlet never needed pairing again, it simply found itself as soon as power was applied and responded immediately to switching requests. As the pin configuration is not set for UK, and it comes appart very easily (one screw), I am now looking to use this inside exterior light switches, to trigger them whenever my cameras see something interesting...

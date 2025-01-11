# Intellifire Binding

This binding integrates the Intellifire IFT-ECM Electronic Control Module and IFT-ACM Auxilliary Control Module using the IFT-WFM Wi-Fi Module. 

This Wi-Fi module is used in Majestic, Heat & Glo, Heatilator, Quadra-Fire, Vermont Castings, Monessen and SimpliFire fireplaces. 
The IFT-WFM Wi-Fi module will work along side of the IFT-RFM and corresponding IFT-RC400 touchscreen remote control.

## Supported Things

The table below lists the Intellifire binding thing types:

| Things                       | Description                                                                     | Thing Type    |
|------------------------------|---------------------------------------------------------------------------------|---------------|
| Intellifire Account          | Connection to Intellifire's Server                                              | bridge        |
| Fireplace                    | Fireplace (IFT-ECM)                                                             | fireplace     |
| Remote                       | Remote (IFT-RC400)                                                              | remote        |

## Discovery

The binding will automatically discover the Intellifire fireplace things from the cloud server using your Intellifire credentials.
The cloud server will provide the local IP of each fireplace for local polling and further thing discovery.

## Thing Configuration

Intellifire Account Parameters:

| Name             | Type    | Description                           | Default | Required | Advanced |
|------------------|---------|---------------------------------------|---------|----------|----------|
| Username         | text    | Your Intellifire User Name            | N/A     | yes      | no       |
| Password         | text    | Your Intellifire Password             | N/A     | yes      | no       |
| Refresh Interval | integer | Interval the device is polled in sec. | 15      | yes      | yes      |

## Channels

### Fireplace Channels

| Channel Type ID   | Item Type          | Description                          | Read Write |
|-------------------|--------------------|--------------------------------------|:----------:|
| battery           | Switch             | Battery Status                       |      R     |
| coldClimatePilot  | Switch             | Cold Climate Pilot                   |     R/W    |
| ecmLatency        | Number             | ECM Latency                          |      R     |
| errors            | String             | Errors                               |      R     |
| fan               | Dimmer             | Fan (0-100, step 25) (If equipped)   |     R/W    |
| flameHeight       | Dimmer             | Flame Height (0-100, step 20)        |      R     |
| hot               | Switch             | Appliance is hot                     |      R     |
| light             | Dimmer             | Light (0-100, step 33) (If equipped) |     R/W    |
| power             | Switch             | Flame Power                          |     R/W    |
| prePurge          | Switch             | Power Vent Pre-purge                 |      R     |

### Remote Channels

| Channel Type ID  -------- | Item Type           | Description                  | Read Write |
|---------------------------|---------------------|------------------------------|:----------:|
| remoteConnectionQuality   | Number              | Remote Connection Quality    |      R     |
| remoteDowntime            | Number              | Remote Downtime              |      R     |
| remoteUptime              | Number              | Remote Uptime                |      R     |
| roomTemperature           | Number:Temperature  | Room Temperature             |      R     |
| thermostatEnable          | Switch              | Thermostat Enable            |     R/W    |
| thermostatSetpoint        | Number:Temperature  | Thermostat Setpoint          |     R/W    |
| timer*                    | Number:Time         | Timer                        |     R/W    |
| timerEnable               | Switch              | Timer Enable                 |     R/W    |

*timer channel should have unit metadata set to "min" (minutes)

## Full Example

Ensure the fireplace is fully functional using the Intellifire mobile application.
After installing the binding, you will need to manually add the Intellifire Account thing and enter your credentials.
All things can be automatically discovered by scanning the account bridge.
Goto the inbox and add the things.

The account thing will login to the cloud server to retreive a list of locations and fireplaces at each location.
It will then cloud poll each fireplace once.
It will then locally poll each fireplace continuously at the account refresh interval.
If the local polling fails a number of attempts, the polling will stop, the account bridge will reset and begin the cloud login process again.
Please note the local polling currently has a cloud dependancy in the IFT-WFM and will not function without cloud connectivity.

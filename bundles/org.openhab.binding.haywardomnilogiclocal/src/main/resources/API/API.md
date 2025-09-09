OmniLogic Home Automation Specification for Local Network

NGC-1342







Signature Page



____________________________________________

Qiwei Huang, Document Author



____________________________________________

David Blaine, Firmware Engineering Manager



____________________________________________

Greg Fournier, Sr. Product Manager, Controls

Table of Contents:

1Purpose of the Document9

2References9

3Home Directory9

4Terminology9

5Hayward OmniLogic APIs9

5.1Overview9

5.2Workflow10

5.3Files/Messages specification13

Appendix A: Complete API List14

1.RequestConfiguration14

2.RequestTelemetryData14

1.SetHeaterEnable16

2.SetUIHeaterCmd16

3.SetUIEquipmentCmd16

4.SetUISpilloverCmd17

5.SetUISuperCHLORCmd18

6.SetUISuperCHLORTimeoutCmd18

7.SetStandAloneLightShow19

Appendix B: MSP_LeadMessage19

Appendix C: MSP_BlockMessage20

Appendix D: Msp Configuration File20

1.Basic Configuration File Structure and Rules20

1.1Overview20

1.2Tags21

1.3Structure21

1.3.1High Level Structure21

1.3.2High Level Structure Part 222

1.3.3System Structure23

1.3.4Backyard Structure23

1.4More Backyard Details25

1.4.1Body of Water (BOW) Structure25

1.4.2Pool Equipment Structures26

1.4.3Other Backyard Structures26

1.5Public Naming Rules27

1.5.1Overview27

1.5.2Allowed Size27

1.5.3Specifying a Public Name27

1.5.4Uniqueness28

1.5.5Structures Requiring Names28

1.5.6Default Names28

1.6Configuration File Identifiers29

1.6.1System ID29

1.6.2Logical ID30

1.7Devices30

1.7.1Overview30

1.7.2Real Devices31

1.7.3Virtual Devices31

1.8Miscellaneous Topics32

1.8.1Configuration File Units33

1.9Sample Pool33

2System Structure34

2.1Overview34

2.2Rules34

2.3Parameters35

3Backyard Structure35

3.1Overview35

3.2Backyard Structure36

3.2.1Parameters36

3.2.2Pool Equipment Types38

3.3Sensor Structures39

3.3.1Sensor Types39

3.3.2Sensor Units39

3.3.3Water Temperature Sensor40

3.3.4Air Temperature Sensor41

3.3.5Flow Sensor42

3.3.6Solar Temperature Sensor43

3.4Body of Water44

3.4.1Overview44

3.4.2Shared Equipment Types45

3.4.3Shared Priority46

3.4.4Common Requirements47

3.4.5Independent Pool (Pool Only)47

3.4.6Independent Spa (Spa Only)48

3.4.7Shared Equipment (Pool and Spa SE)49

3.4.8Pool and Spa DE with Separate Heaters51

3.4.9Pool and Spa DE with Shared Heaters53

3.4.10Spillover55

3.5Filter Pumps56

3.5.1Allowed Types56

3.5.2Suction and Return Valves57

3.5.3Single Speed Pump57

3.5.4Dual Speed Pump60

3.5.5Variable Speed Pump61

3.6Heaters64

3.6.1Overview64

3.6.2Virtual Heater65

3.6.3Gas Heater67

3.6.4Heat Pump68

3.6.5Solar Heater69

3.7Chlorinators71

3.7.1Virtual Chlorinator71

3.7.2Main Panel Chlorinator76

3.7.3Expansion Panel Chlorinator77

3.7.4Aqua Rite Chlorinator77

3.8Chemistry Sense and Dispense (CSAD)78

3.8.1Chemistry Sense Module78

3.8.2Chemistry Dispense79

3.8.3Virtual CSAD (Chemistry Sense and Dispense) System80

3.9Pool and Backyard Accessories81

3.9.1Overview81

3.9.2Relays81

3.9.3Pumps83

3.9.4Combination of Relays and Pumps83

3.9.5Single Speed Boost Pump Driven84

3.10Backyard Lights / Features85

3.10.1Overview85

3.10.2HV Relay Driven85

4MspConfig.xml Sample86

Appendix E: Telemetry message enums101





- Purpose of the Document

- Every Hayward OmniLogic Controller has a configuration file that describes what equipment the pool pad has. During the system run time, status messages can be obtained by client programs from the MSP Local Home Automation Interface. 

- The goal of this document is to completely describe 1) the structure of both Status Messages and the API; 2) The major workflows how client applications can request the Hayward OmniLogic Home Automation Local Network API for Configuration File, and polling for Status.

- References

- The latest version of all documents applies.

- The system documentation index can be found in: https://glsvn.goldlinecontrols.com/svn/NextGen/trunk/Documentation/000 Overview

- NGC-000 (Next Gen System Design Specification).docx

- Home Directory

- The home directory for this document is: https://glsvn.goldlinecontrols.com/svn/NextGen/trunk/Documentation/xxx

- Terminology

- See NGC-050 (Next Gen System Terminology).

- Hayward OmniLogic APIs

- Overview

- MSP_ConfigurationUpdate.xml and MSP_TelemetryUpdate are the most important XML messages that client applications will be handling, 

- MSP_ConfigurationUpdate

This MSP_ConfigurationUpdate carries the MSP configuration file which describes what devices/features a Hayward OmniLogic site has, and their relationships.

- Msp_TelemetryUpdate.xml 

This message carries status information of all the devices in a site. 

- Workflow

- Overview

Communication protocol used here is UDP, MSP’s IP address is shown in its [config] -> [network] menu.

The port that MSP listens on is 10444. 

Client applications should be polling this port and calling the following API’s for MSP configuration and status change

. Configuration: RequestConfiguration

. Status: RequestTelemetryData

- Message Header

All messages, including messages sent to MSP and messages sent from MSP, have a 24-byte header and the XML content shown as below,



typedef struct

{

 U32 msgId;

 U64 timeStamp;

 U8  version[4];

 U32 opid;

 U8  clientType;

 U8  reserved[3];

}IGW_UDPHeader;

Note: 

msgId(4 bytes): Unique id for each message, usually we use next_id = current_id  + 1;

timestamp(8 bytes): This is a long Integer, its value indicating how many milliseconds between current UTC time and “1970-01-01 00:00:00.000”.

version(4 bytes): Always use “1.12” for now, It’s corresponding Hex is: 31 2E 38 20

msgType: Specifies the type of a message/command. i.e., MSP_LeadMessage(1998), MSP_BlockMessage(1999), MSP_ConfigurationUpdate(1003), MSP_TelemetryUpdate(1004), See Appendix B and C for more details.

clientType(1 byte): This one identifying the type of Client which talking with MSP. The Hex for it should be 01 for now.

Reserved(3 byte): The Hex for it should be 00 00 00 for now.



- ACK messages

. Table below details the message directions and whether an ACK message is required to send back to the sender

. MsgId of an ACK message should be the same as the MsgId of the message this ACK is for



- RequestConfiguration command

Client application sends this command to MSP, on Port 10444 via UDP, for requesting MSP to send the latest configuration.

On receiving this command, MSP will respond an ACK and then start the configuration file transferring based on the following 2 scenarios,

- If the configuration file is less than 1KB, MSP will send only one MSP_ConfigurationUpdate message which wraps the whole configuration file in it

- If the configuration file is larger than 1KB, MSP will firstly send a MSP_LeadMessage, similar to the following,

<Response>

<Name>LeadMessage</Name>

<Parameters>

<Parameter dataType="int" name="SourceOpId">1003</Parameter>

<Parameter dataType="int" name="MsgSize">180975</Parameter>

<Parameter dataType="int" name="MsgBlockCount">18</Parameter>

<Parameter dataType="int" name="Type">0</Parameter>

</Parameters>

</Response> 

Note: 

- SourceOpId will always be 1003 if this MSP_LeadMessage is for configuration file transferring

- MsgSize is the size of the complete configuration file, in bytes

- MsgBlockCount =18 means the configuration file is divided into 18 MSP_BlockMessage’s 

- MSP will NOT send the MSP_ConfigurationUpdate message if the configuration file is divided and sent in smaller MSP_BlockMessage

- Client application must send an ACK on receiving MSP_LeadMessage and MSP_BlockMessage messages.

- CheckSum of the configurations file is included in the config file. Client application can use this to figure out if the config file has changed.

- GetTelemetry command

Client application sends this command to MSP, on Port 10444 via UDP, for requesting MSP to send the latest status.

On receiving this command, MSP will send an ACK and then the MSP_TelemetryUpdate message, which includes status for ALL the features of the pool pad.

Please see the appendix for command/message format.

Note: 

- Client application must send an ACK on receiving a MSP_ TelemetryUpdate message.

- The content part of MSP_TelemetryUpdate messages is compressed with zlib standard compression

- Please see Appendix E for parameters that can appear in telemetry messages and their possible values

- Files/Messages specification

- Msp Configuration File

See Appendix B: Msp Configuration File





































Appendix A: Complete API List

## RequestConfiguration

Request:

<?xml version="1.0" encoding="utf-8"?>

<Request xmlns="http://nextgen.hayward.com/api">

  <Name>RequestConfiguration</Name>

</Request>



Response:

MSP should start configuration file transferring with a MSP_LeadMessage and followed by a list of MSP_BlockMessage 

## RequestTelemetryData

Request: 

<?xml version="1.0" encoding="utf-8"?>

<Request xmlns="http://nextgen.hayward.com/api">

  <Name>RequestTelemetryData</Name>

</Request>

Response:

MSP should send back a MSP_TelemetryUpdate similar to the following:

<?xml version="1.0" encoding="UTF-8"?>

-<STATUS version="1.8">

  <Backyard messageVersion="11.8" datetime="7/31/2016 3:50:57 PM" configUpdatedTime="8/1/2016 1:19:38 AM" state="1" status="1" airTemp="51" statusVersion="3" systemId="10107"/>

  <ColorLogic-Light systemId="151" currentShow="0" lightState="0" speed="4" brightness="4"/>

  <BodyOfWater systemId="1" waterTemp="-1" flow="1"/>

  <Filter systemId="2" fpOverride="0" whyFilterIsOn="0" filterState="0" filterSpeed="0" valvePosition="1"/>

  <VirtualHeater systemId="3" enable="yes" Current-Set-Point="69"/>

  <Heater systemId="4" enable="yes" maintainFor="2" priority="0" temp="255" heaterState="0"/>

  <Heater systemId="23" enable="yes" maintainFor="1" priority="2" temp="255" heaterState="0"/>

  <Heater systemId="24" enable="yes" maintainFor="1" priority="1" temp="51" heaterState="0"/>

  <Chlorinator status="8" systemId="8" instantSaltLevel="0" avgSaltLevel="2300" chlrAlert="0" chlrError="0" scMode="0" operatingState="1" Timed-Percent="10" operatingMode="1"/>

  <Relay systemId="20" relayState="0"/>

  <Relay systemId="21" relayState="0"/>

  <Pump systemId="10" pumpSpeed="0" pumpState="0"/>

  <Pump systemId="33" pumpSpeed="0" pumpState="0"/>

  <ColorLogic-Light systemId="53" currentShow="0" lightState="0" speed="4" brightness="4"/>

  <CSAD status="0" systemId="49" mode="0" orp="" ph=""/>

  <BodyOfWater systemId="11" waterTemp="-1" flow="1"/>

  <Filter systemId="12" fpOverride="0" whyFilterIsOn="0" filterState="0" filterSpeed="0" valvePosition="1"/>

  <VirtualHeater systemId="13" enable="yes" Current-Set-Point="65"/>

  <Heater systemId="14" enable="yes" maintainFor="2" priority="0" temp="255" heaterState="0"/>

  <Heater systemId="25" enable="yes" maintainFor="1" priority="2" temp="255" heaterState="0"/>

  <Heater systemId="26" enable="yes" maintainFor="1" priority="1" temp="51" heaterState="0"/>

  <Chlorinator status="0" systemId="17" instantSaltLevel="0" avgSaltLevel="0" chlrAlert="0" chlrError="0" scMode="0" operatingState="1" Timed-Percent="10" operatingMode="1"/>

  <Pump systemId="19" pumpSpeed="0" pumpState="0"/>

  <ColorLogic-Light systemId="54" currentShow="0" lightState="0" speed="4" brightness="4"/>

  <CSAD status="0" systemId="149" mode="0" orp="" ph=""/>

</STATUS>





Appendix B: Device Operation Command Samples

## SetHeaterEnable

Request:

<Request xmlns="http://nextgen.hayward.com/api">

  <Name>SetHeaterEnable</Name>

  -<Parameters>

    <Parameter name="PoolID" dataType="int">8801</Parameter>

    <Parameter name="HeaterID" dataType="int">8802</Parameter>

    <Parameter name="Enabled" dataType="bool">1</Parameter>

  </Parameters>

</Request>

Response:

ACK

## SetUIHeaterCmd

Request:

<?xml version="1.0" encoding="utf-8"?>

<Request>

  <Name>SetUIHeaterCmd</Name>

  <Parameters>

    <Parameter name="PoolID" dataType="int">1</Parameter>

    <Parameter name="HeaterID" dataType="int">3</Parameter>

    <Parameter name="Temp" dataType="int">98</Parameter>

  </Parameters>

</Request>

Response:

ACK

## SetUIEquipmentCmd

Request:

<?xml version="1.0" encoding="utf-8"?>

<Request>

  <Name>SetUIEquipmentCmd</Name>

  <Parameters>

    <Parameter name="PoolID" dataType="int">1</Parameter>

    <Parameter name="EquipmentID" dataType="int">2</Parameter>

    <Parameter name="IsOn" dataType="int">0</Parameter>

    <Parameter name="IsCountDownTimer" dataType="bool">false</Parameter>

    <Parameter name="StartTimeHours" dataType="int">0</Parameter>

    <Parameter name="StartTimeMinutes" dataType="int">0</Parameter>

    <Parameter name="EndTimeHours" dataType="int">0</Parameter>

    <Parameter name="EndTimeMinutes" dataType="int">0</Parameter>

    <Parameter name="DaysActive" dataType="int">0</Parameter>

    <Parameter name="Recurring" dataType="bool">false</Parameter>

         </Parameters>

</Request>



The IsOn Parameter needs to be assigned a speed if the equipment is mapped to a pump, otherwise 1 is ON and 0 is OFF.  For a single speed pumps IsOn requires the speed of 100 for turning the pump ON and 0 for turning the pump OFF.

IsOn:



Response:

ACK

## SetUISpilloverCmd

Request:

<?xml version="1.0" encoding="utf-8"?>

<Request>

  <Name>SetUISpilloverCmd</Name>

  <Parameters>

    <Parameter name="PoolID" dataType="int">1</Parameter>

    <Parameter name="Speed" dataType="int">50</Parameter>

    <Parameter name="IsCountDownTimer" dataType="bool">0</Parameter>

    <Parameter name="StartTimeHours" dataType="int"></Parameter>

    <Parameter name="StartTimeMinutes" dataType="int">0</Parameter>

    <Parameter name="EndTimeHours" dataType="int">0</Parameter>

    <Parameter name="EndTimeMinutes" dataType="int">0</Parameter>

    <Parameter name="DaysActive" dataType="int">0</Parameter>

    <Parameter name="Recurring" dataType="bool">0</Parameter>

  </Parameters>

</Request>

 Response:

ACK

## SetUISuperCHLORCmd

Request:

<?xml version="1.0" encoding="utf-8"?>

<Request>

  <Name>SetUISuperCHLORCmd</Name>

  <Parameters>

<Parameter name="PoolID" dataType="int">1</Parameter>

    <Parameter name="ChlorID" dataType="int">8</Parameter>

    <Parameter name="IsOn" dataType="int">0</Parameter>

  </Parameters>

</Request>

Response:

ACK

## SetUISuperCHLORTimeoutCmd

Request:

<?xml version="1.0" encoding="utf-8"?>

<Request>

  <Name>SetUISuperCHLORTimeoutCmd</Name>

  <Parameters>

<Parameter name="PoolID" dataType="int">1</Parameter>

    <Parameter name="ChlorID" dataType="int">8</Parameter>

    <Parameter name="Timeout" dataType="byte">6</Parameter>

  </Parameters>

</Request>

 Response:

ACK



## SetStandAloneLightShow

Request:

If light is NOT  Omni Direct type use the following parameters:

<?xml version="1.0" encoding="utf-8"?>

<Request>

  <Name>SetStandAloneLightShow</Name>

  <Parameters>

<Parameter name="PoolID" dataType="int">1</Parameter>

    <Parameter name="LightID" dataType="int">53</Parameter>

    <Parameter name="Show" dataType="int">4</Parameter>

    <Parameter name="IsCountDownTimer" dataType="bool">false</Parameter>

    <Parameter name="StartTimeHours" dataType="int">0</Parameter>

    <Parameter name="StartTimeMinutes" dataType="int">0</Parameter>

    <Parameter name="EndTimeHours" dataType="int">0</Parameter>

    <Parameter name="EndTimeMinutes" dataType="int">0</Parameter>

    <Parameter name="DaysActive" dataType="int">0</Parameter>

    <Parameter name="Recurring" dataType="bool">false</Parameter>

  </Parameters>

</Request>

If light is Omni Direct type, add Speed, Brightness, and Reserved parameters:

<?xml version="1.0" encoding="utf-8"?>

<Request>

  <Name>SetStandAloneLightShow</Name>

  <Parameters>

<Parameter name="PoolID" dataType="int">1</Parameter>

    <Parameter name="LightID" dataType="int">53</Parameter>

<Parameter name="Show" dataType="byte">5</Parameter>    <Parameter name="Speed" dataType="byte">4</Parameter>    <Parameter name="Brightness" dataType="byte">4</Parameter>    <Parameter name="Reserved" dataType="byte">0</Parameter>

<Parameter name="IsCountDownTimer" dataType="bool">false</Parameter>

    <Parameter name="StartTimeHours" dataType="int">0</Parameter>

    <Parameter name="StartTimeMinutes" dataType="int">0</Parameter>

    <Parameter name="EndTimeHours" dataType="int">0</Parameter>

    <Parameter name="EndTimeMinutes" dataType="int">0</Parameter>

    <Parameter name="DaysActive" dataType="int">0</Parameter>

    <Parameter name="Recurring" dataType="bool">false</Parameter>

  </Parameters>

</Request>

  Where 

         <Brightness>

                <Key name="0">CL_20_PERCENT</Key>

                <Key name="1">CL_40_PERCENT</Key>

                <Key name="2">CL_60_PERCENT</Key>

                <Key name="3">CL_80_PERCENT</Key>

                <Key name="4">CL_100_PERCENT</Key>

            </Brightness>

            <Speed>

                <Key name="0">CL_TIMES_ONE_SIXTEENTH</Key>

                <Key name="1">CL_TIMES_ONE_EIGHTH</Key>

                <Key name="2">CL_TIMES_ONE_QUARTER</Key>

                <Key name="3">CL_TIMES_ONE_HALF</Key>

                <Key name="4">CL_TIMES_ONE</Key>

                <Key name="5">CL_TIMES_TWO</Key>

                <Key name="6">CL_TIMES_FOUR</Key>

                <Key name="7">CL_TIMES_EIGHT</Key>

                <Key name="8">CL_TIMES_SIXTEEN</Key>

            </Speed>



See  Appendix E for enumeration of colors.

Response:

ACK

Appendix B: MSP_LeadMessage

Message Header: 

struct packet_header {

    int msgId;            // {unique id}

    byte timestamp[8];   // {now}

    int version;          // “1.8 ”

    int msgType;          // 1998

    int headerSize;       // 24

}

Note: 

- We will need to decompress after combining all the corresponding MSP_BlockMessage If the 23rd byte of the packet_header (packet_header[22]) is set to 1. 

- The MSP_LeadMessage itself is never compressed.

Message Content:

<Response>

<Name>LeadMessage</Name>

<Parameters>

<Parameter dataType="int" name="SourceOpId">1003</Parameter>

<Parameter dataType="int" name="MsgSize">180975</Parameter>

<Parameter dataType="int" name="MsgBlockCount">18</Parameter>

<Parameter dataType="int" name="Type">0</Parameter>

</Parameters>

</Response>



Appendix C: MSP_BlockMessage

Message Header: 

struct packet_header {

    int msgId;            // {unique id}

    byte timestamp[8];   // {now}

    int version;          // “1.8”

    int msgType;          // 1999

    int headerSize;       // 24

}

Message Content:

Part of the configuration file.

NOTE: The corresponding LeadMessage may say the config file is compressed. See the MSP_LeadMessage section for more details.



Appendix D: Msp Configuration File

- Basic Configuration File Structure and Rules

- Overview

- The configuration file is a hierarchically nested structure in XML format. This section will define the structure. Later sections will provide the configuration details.

- Tags

- XML format uses tags to define the structure. These tags are specific to the OmniLogic system.

- Tags always come in pairs: <abc> is an opening tag and </abc> is the corresponding closing tab.

- Configuration information is contained between the opening and the closing tag.

- Tag pairs can be nested within other tag pairs.

- Tag pairs nested within the same tag pair (at the same level of the hierarchy) must have unique names.

- All required XML tags are defined in this document

- Structure

- High Level Structure

- Every device added to the configuration file has a place within the structure.

- In the Configuration File returned by the GetMspConfigFile API, the highest level of the structure is the tag pair <Response> which contains the entire structure

- At the second highest level of the structure are the tag pairs <MSPConfig>. <MSPConfig> contains the entire Configuration structure; 

- Nested within <MSPConfig> are four other tags which must be present: <System>, <Backyard>. These other tag pairs form sub-structures within the overall structure. In all cases a tag pair defines a structure.

- There is a checksum on the entire structure, used to verify the integrity of the structure, which is contained within the <CHECKSUM> tag.

- The highest level of this structure is shown below; the contents of the sub-structures are not shown:



  <MSPConfig>

<System> 

…

</System>

<Backyard>

…

</Backyard>

<Schedules>

…

</Schedules>



</MSPConfig>

- High Level Structure Part 2

- The highest level MSPConfig structure contains several different nested structures:

- The System structure contains user choices for system level parameters like choice of units and choice of language.

- The Backyard structure contains all of the equipment that is automated / controlled by the OmniLogic system. Bodies of water, for example, are within the backyard.

- The checksum insures that there is no corruption within the MSPConfig structure. The checksum should always be verified before information is read from the structure.

- Each nested structure will be explained in far more detail in the following sections of this document.

- System Structure

- System level (system wide) parameters are defined here. Some examples of these are shown below:

- <Units> is either standard or metric and applies to several different parameters.

- <Msp-Vsp-Speed-Format> is the format that the user prefers to have variable speed pump speeds displayed in. The options are either percent (of maximum RPM) or direct RPM.

- <Msp-Time-Format> is either 12 hour (AM / PM) format or 24 hour (military time).

- <Msp-Language> is the language used for the user interface.

- All of the system parameters are detailed in a later section of this document.

- System parameters affect how data is presented to the user via the user interface. System parameters do NOT affect how data is entered within the configuration file.

- Backyard Structure

- Both parameters and operations are defined here.

- By definition, ALL of the equipment that is automated by the OmniLogic pool controller is contained within the Backyard structure. This includes:

- Landscape lighting

- Colorlogic Lights

- Above ground lighting such as spotlights, patio lights, porch lights, and cabana lights.

- The air temperature sensor.

- Every body of water is within the backyard. All pool equipment is within a particular body of water (or can be shared between two bodies of water).

- XML Example:

<Backyard>

MSP Configuration

<System-Id>0</System-Id>

<Name>Backyard</Name>

<Sensor>

<System-Id>1</System-Id>

<Name>Air Sensor</Name>

…

</Sensor>

<Body-of-water>

1

<System-Id>2</System-Id>

<Name>Pool</Name>

…

</Body-of-water>

<Body-of-water>

2

<System-Id>3</System-Id>

<Name>Spa</Name>

…

</Body-of-water>

<Relay>

<System-Id>4</System-Id>

<Name>Patio Light</Name>

…

</Relay>

<Relay>

<System-Id>5</System-Id>

<Name>Irrigation</Name>

…

</Relay>

</Backyard>

- The Backyard structure is detailed in many of the sections to follow. The bulk of this document is a description of the Backyard structure.

- More Backyard Details

- Body of Water (BOW) Structure

- Each body of water (BOW) has a structure that is contained within the Backyard structure.

- All of the pool equipment that affects a given BOW must be included in the configuration structure for this BOW.

- XML Example:

<Body-of-water>

1

<System-Id>2</System-Id>

<Name>Pool</Name>

<Type>BOW_POOL</Type>

<Shared-Type>BOW_SHARED_EQUIPMENT</Shared-Type>

<Shared-Equipment-System-ID>10

</Shared-Equipment-System-ID>

<Supports-Spillover>yes</Supports-Spillover>

<Use-Spillover-For-Filter-Operations>Yes

</Use-Spillover-For-Filter-Operations>

<Filter>

…

</Filter>

<Heater>

…

</Heater>

<Sensor>

…

</Sensor>

<Chlorinator>

…

</Chlorinator>

<Relay>

…

</Relay>

<Pump>

…

</Pump>

<ColorLogic-Light>

…

</ColorLogic-Light>

<CSAD>

…

</CSAD>

</Body-of-water>

- By definition, a BOW MUST contain a filter pump. Water that is not filtered is not a BOW.

- A BOW is defined based on the filter pump. If two separate pools are plumbed such that they cannot be separated, then they must be treated as a single BOW.

- Even in cases where there is shared equipment, it must be possible to filter / chlorinate / heat one BOW at a time.

- Pool Equipment Structures

- Within each BOW there are structures for Filter, Heater, Chlorinator, CSAD (chemistry sense and dispense), Sensors, Pumps, Relays, and ColorLogic Lights.

- These structures are covered in detail in later sections.

- Other Backyard Structures

- Relay structures, Sensor structures, and ColorLogic Light structures can occur at the Backyard level (and not within a body of water).

- Some examples would be:

- Backyard, porch, patio, and cabana lights can be relay controlled.

- Lawn sprinklers can be relay controlled.

- Misters and Foggers can be relay controlled.

- Public Naming Rules

- Overview 

- The OmniLogic system encourages the naming of most devices within the system because, if done correctly, this can greatly enhance usability.

- Naming is important, and there are associated rules.

- This section is only concerned with the use of the <name> tag. These are public names, exposed to the user and for the benefit of the user.

- There are other names within the configuration file that do NOT use the <name> tag, which are NOT public, and which are NOT bound by these rules.

- Allowed Size

- The maximum number of characters in any name is 12. The minimum number of characters in any name is 1.

- Any names within the configuration file that are longer than 12 characters will be truncated after the 12th character.

- Specifying a Public Name

- Names use the Name tag:

 <Name>Backyard</Name>

- Allowed characters are capital and small letters, spaces, numbers, dashes, and the apostrophe.

- All characters, including spaces, are counted.

- Uniqueness

- While there are no enforced restrictions, the configuration file should contain unique names where possible.

- Public Names are used on UI screens and confusion should be avoided.

- Structures Requiring Names

- While most things are named, there are exceptions. In the following sections as new structures are introduced, it will always be specified whether or not names are required.

- Names are not allowed; they are either required or forbidden. In the case of required names a list of defaults is required.

- In some cases the default name cannot be changed. Backyard, Sensors, PH Reduction (CSAD), and Chlorination have default names that cannot be changed.

- Default Names

- Default names are supplied for everything that requires a name. The following table lists the default names.

Table #1

- Configuration File Identifiers

- System ID

- System IDs use the System-Id tag:

<System-Id>16</System-Id>

- System IDs are assigned to everything in the Backyard section of the configuration file:

- Backyard itself

- Every BOW

- Every real device

- Every virtual device

- System IDs are signed integers that must be unique. They will typically be assigned in sequential order.

- The only fixed System ID is that of the Backyard: The Backyard structure always has System ID 0.

<Backyard>

MSP Configuration

<System-Id>0</System-Id>

<Name>Backyard</Name>

…

</Backyard>

- Logical ID

- Logical IDs do not have a tag. They occur when you can have multiple identical “things” at the same level of the structure.

- One example is a BOW; there can be up to 5 BOWs within the backyard. The Logical ID occurs within the Body-of-water tag (immediately after the opening Body-of-water tag):

<Body-of-water>

1<!-- this is a logical ID -->

<System-Id>1</System-Id>

<Name>Pool</Name>

…

</ Body-of-water >

…

<Body-of-water>

2<!-- this is a logical ID -->

<System-Id>18</System-Id>

<Name>Spa</Name>

…

</ Body-of-water >

- In the above example the 1 is the logical ID of the first BOW and 2 is the logical ID of the second BOW. Logical IDs are assigned sequentially starting at 1.

- Devices

- Overview

- The OmniLogic system allows for the definition of both real and virtual devices. There are specific instances where virtual devices are required (to group together multiple real devices that are working together).

- Real Devices

- As a simple example of how a real device is represented in the Body of Water section (within the Backyard section) of the configuration file, consider a water temperature sensor:

<Sensor>

<System-Id>11</System-Id>

<Name>Water Sensor</Name>

<Type>SENSOR_WATER_TEMP</Type>

<Units>UNITS_FAHRENHEIT</Units>

</Sensor>

- A device structure begins with parameters (the number and type of parameters depends on the device) and then concludes with Operations.

- The above device has 4 parameters:

- Everything in the Backyard is assigned a unique System-ID.

- Every BOW and physical device in the system is assigned a Public Name.

- Every device in the system has a Type (equipment type).

- Every sensor in the system returns data in a particular system of Units.

- Virtual Devices

- It is possible to have multiple instances of the same device, or similar devices, performing the same function and working together on a single BOW.

- When multiple devices are working together on the same BOW toward the same end they are treated as a combined virtual device.

- One example is a pool with multiple heaters. Assume that a pool has three physical heaters available (gas heater, heat pump, solar heater).

- There is a single temperature set point for each body of water. This temperature set point is implemented by a virtual heater that contains these three physical heaters.

- All of the physical heaters work together under the control of the virtual heater.

- Up to five physical heaters are supported per virtual heater.

- There can also be virtual chlorinators and virtual Chemistry Sense and Dispense devices that group together multiple instances of the same type of real device.

- Up to ten dispense relays are supported per virtual CSAD.

- Number of physical chlorinators supported per virtual chlorinator is TBD.

- For all devices that support virtual devices: There must be a virtual device present even if only 1 physical device is present.

- Miscellaneous Topics

- Configuration File Units

- There are required units within the configuration file, as shown in the following table (this is completely independent of the System Structure unit settings):

Table #2

- Sample Pool

- The following pool will be used as a sample for much of the configuration file explanations that are to follow. 

- This is Pool and Spa Single Equipment system (there is one filtration loop.

- There is spillover available.

- This can be done with 2 EcoStar Pumps, and a Chemistry Sense Module. This pool requires 8 HV Relays, 1 LV Relay, 5 Valve Actuator Relays, 3 Temperature sensors (including Air), 1 Flow sensor, and 1 Chlorinator.

- The complete configuration file for this pool is contained in Appendix A.

Figure #3

- System Structure

- Overview

- The system tags are used to specify user preferences such as language and units. They are contained in the <System> structure.

- Rules

- The <System> structure is at the same level as the <Backyard> structure and occurs just before the <Backyard> structure in the configuration file.

- The parameters relate to the user interface, not to the configuration file itself.

- Parameters

- The required parameters are shown below; each parameter has a unique tag:

- <Msp-Vsp-Speed-Format> is either Percent or RPM. The default is Percent.

- <Msp-Time-Format> is either 12 Hour Format or 24 Hour Format. The default is 12 Hour Format.

- <Units> is either Standard or Metric. The default is Standard.

- <Msp-Chlor-Display> is either Salt or Minerals. The default is Salt. Salt and Minerals are not units. Either Salt or Minerals can be displayed in ppm or grams / liter, which is controlled by a separate parameter.

- <Msp-Language> is either English or French or Spanish. The default is English.

- XML example:

<System>

<Msp-Vsp-Speed-Format>RPM</Msp-Vsp-Speed-Format>

<Msp-Time-Format>12 Hour Format</Msp-Time-Format>

<Units>Standard</Units>

<Msp-Chlor-Display>Salt</Msp-Chlor-Display>

<Msp-Language>English</Msp-Language>

 </System>

- Backyard Structure

- Overview

- This is where all of the equipment that is being automated / controlled is defined.

- The Backyard structure is the container for all devices and BOWs that are automated by the OmniLogic pool control system.

- There is one and only one Backyard structure allowed in the configuration file. There CANNOT be separate Backyard structures or nested Backyard structures.

- Backyard Structure

- Parameters

- The required parameters are shown below:

- The <System-Id> must be 0. Not configurable.

- The default <Name> is My Backyard. A custom name may be substituted.

- The following XML example shows the Backyard structure and the sub-structures within the Backyard structure. Note that the <System-Id> for the second BOW (Spa) is 100 + <System-Id> for the first BOW. While this is not a requirement, it is a good convention to follow because it keeps things simple.

<Backyard>

MSP Configuration

<System-Id>0</System-Id>

<Name>My Backyard</Name>

<!-- The air sensor always comes first -->

<Sensor>

<System-Id>7</System-Id>

<Name>Air Temp</Name>

<Type>SENSOR_AIR_TEMP</Type>

…

</Sensor>

<!-- Next comes equipment not associated with any BOW -->

<Relay>

<System-Id>16</System-Id>

<Name>Frontyard LT</Name>

…

</Relay>

<Relay>

<System-Id>17</System-Id>

<Name>Backyard LT</Name>

…

</Relay>

<Relay>

<System-Id>18</System-Id>

<Name>Fire Pit</Name>

…

</Relay>

<!-- Next comes the BOWs -->

<Body-of-water>

1

<System-Id>1</System-Id>

<Name>Main Pool</Name>

…

<!-- Next comes the water temperature sensor -->

<Sensor>

<System-Id>3</System-Id>

<Type>SENSOR_WATER_TEMP</Type>

<Name>Water Temp</Name>

…

</Sensor>

<!-- Next comes the solar sensor (only if there is solar equipment) -->

<Sensor>

<System-Id>XX</System-Id>

<Type>SENSOR_SOLAR_TEMP</Type>

<Name>Solar Temp</Name>

…

</Sensor>

<!-- Next comes the flow sensor -->

<Sensor>

<System-Id>8</System-Id>

<Type>SENSOR_FLOW</Type>

<Name>Flow Sensor</Name>

…

</Sensor>

<!-- Next comes the filter pump -->

<Filter> … </Filter>

<!-- Next comes the chlorinator -->

<Chlorinator> … </Chlorinator>

<!-- Next comes the heaters -->

<Heater> … </Heater>

…

</Body-of-water>

<Body-of-water>

2

<System-Id>101</System-Id>

<Name>Spa</Name>

…

<!-- Next comes the water temperature sensor -->

<Sensor>

<System-Id>103</System-Id>

<Type>SENSOR_WATER_TEMP</Type>

<Name>Water Temp</Name>

…

</Sensor>

<!-- Next comes the solar sensor (only if there is solar equipment) -->

<Sensor>

<System-Id>XX</System-Id>

<Type>SENSOR_SOLAR_TEMP</Type>

<Name>Solar Temp</Name>

…

</Sensor>

<!-- Next comes the flow sensor -->

<Sensor>

<System-Id>108</System-Id>

<Type>SENSOR_FLOW</Type>

<Name>Flow Sensor</Name>

…

</Sensor>

<!-- Next comes the filter pump -->

<Filter> … </Filter>

<!-- Next comes the chlorinator -->

<Chlorinator> … </Chlorinator>

<!-- Next comes the heaters -->

<Heater> … </Heater>

…

</Body-of-water>

</Backyard>

- Pool Equipment Types

- The following table lists all allowed pool equipment types:

Table #12

- Sensor Structures

- Sensor Types

- All allowed sensor types are defined below:

Table #13

- Sensor Units

- All allowed sensor units are defined below:

Table #14

- Water Temperature Sensor

- The Water Temperature sensor is always present and is structured to belong to a particular BOW. There MUST be a Water Temperature sensor for each BOW that will be heated and there should be a Water Temperature sensor for every BOW.

- The required parameters are shown below:

- The <System-Id> must be unique and should be assigned sequentially.

- The default <Name> is Water Temp. A custom name may be substituted.

- The <Type> must be SENSOR_WATER_TEMP. Not configurable.

- The <Units> tag tells what units that the Smart Component will return the data in and has nothing to do with how the data will be displayed by the UI. Units must be UNITS_FAHRENHEIT. Not configurable.

- The following XML example shows the full configuration required for a water temperature sensor:

<Sensor>

<System-Id>3</System-Id>

<Name>Water Temp</Name>

<Type>SENSOR_WATER_TEMP</Type>

<Units>UNITS_FAHRENHEIT</Units>



</Sensor>

- Air Temperature Sensor

- The Air Temperature sensor is always present and is structured by default at the Backyard level because it does not apply to a particular BOW. 

- For an outdoor pool the Air Temperature is the outdoor air temperature. Note that the primary purposes of the Air Temperature for the pool controller are to implement freeze protection and check the ambient temperature for a heat pump. The Air Temperature is also provided to the user.

- If you have an indoor pool the Air Temperature sensor measures the room temperature in which the pool is located. If this rule is ever unheated then the pool controller can use the Air Temp for the same reasons as an outdoor pool.

- For a pool having both indoor and outdoor bodies of water it must be possible to configure multiple Air Temp sensors. In this case it may be necessary to structure the Air Temp sensor to a particular body of water.

- The required parameters are shown below:

- The <System-Id> must be unique and should be assigned sequentially.

- The default <Name> is Air Temp. A custom name may be substituted.

- <Type> must be SENSOR_AIR_TEMP. Not configurable.

- The <Units> tag tells what units that the Smart Component will return the data in and has nothing to do with how the data will be displayed by the UI. Units must be UNITS_FAHRENHEIT. Not configurable.

- The following XML example completely specifies an Air Temperature sensor:

<Sensor>

<System-Id>7</System-Id>

<Name>Outdoor Temp</Name>

<Type>SENSOR_AIR_TEMP</Type>

<Units>UNITS_FAHRENHEIT</Units>



</Sensor>

- Flow Sensor

- This is a digital flow sensor that detects flow or no flow. It must be structured within the body of water that uses the flow sensor.

- The required parameters are shown below:

- The <System-Id> must be unique and should be assigned sequentially.

- The default <Name> is Flow Sensor. A custom name may be substituted.

- <Type> must be SENSOR_FLOW. Not configurable.

- This function returns yes or no; there really are no units. The Units field must be UNITS_NO_UNITS. Not configurable.

- The following XML example completely specifies a Flow Sensor:

<Sensor>

<System-Id>8</System-Id>

<Name>Flow Sensor</Name>

<Type>SENSOR_FLOW</Type>

<Units>UNITS_NO_UNITS</Units>



</Sensor>

- Solar Temperature Sensor

- The Solar Temperature sensor is present whenever there are solar panels used to heat pool water. It should be structured within any BOW that uses the solar heat.

- The required parameters are shown below:

- The <System-Id> must be unique and should be assigned sequentially.

- The default <Name> is Solar Temp. A custom name may be substituted.

- <Type> must be SENSOR_SOLAR_TEMP. Not configurable.

- The <Units> tag tells what units that the Smart Component will return the data in and has nothing to do with how the data will be displayed by the UI. Units must be UNITS_FAHRENHEIT. Not configurable.

- XML example:

<Sensor>

<System-Id>XX</System-Id>

<Name>Solar Temp</Name>

<Type>SENSOR_SOLAR_TEMP</Type>

<Units>UNITS_FAHRENHEIT</Units>



</Sensor>

- Body of Water

- Overview

- A BOW must have an associated filter and pump. Lack of a filter / pump makes the water containing device an accessory, not a BOW.

- There is no chlorination without filtration.

- There is no heating without filtration.

- The OmniLogic will recognize the same BOW types as were recognized by Pro Logic:

- Pool Only (one body of water)

- Spa Only (one body of water; differentiated based on size and controlled differently)

- Pool and Spa Single Equipment (two BOWs that share a filter and pump)

- Pool and Spa Dual Equipment with Separate Heaters (two BOWs that share nothing except during spillover)

- Pool and Spa Dual Equipment with Shared Heaters (two BOWs with separate filters and pumps but shared heaters)

- The configuration file must allow these BOW types to be exactly specified.

- Additional BOW types may be added in the future to support new pool configurations.

- Shared Equipment Types

- All allowed BOW types are defined below:



Table #15

- BOW_NO_EQUIPMENT_SHARED mean that NO equipment is shared. This BOW is completely independent (Pool Only or Spa Only)

- BOW_DUAL_EQUIPMENT mean separate filters but possible sharing of other equipment like heaters.

- BOW_SHARED_EQUIPMENT mean that all equipment is shared (Pool and Spa Single Equipment).

- BOW_STANDALONE_WITH_SPILLOVER means that NO equipment is shared, but has spillover with another BOW

- Shared Priority

When a BOW has its own filter pump but shares some equipment with another BOW, these 2 BOWs become a Combo. One of these 2 BOWs will have higher priority over the other on getting water treatments, for example, getting heat. The BOW with higher priority will have its <Shared_Priority> equals SHARED_EQUIPMENT_HIGH_PRIORITY, and the other BOW will have SHARED_EQUIPMENT_LOW_PRIORITY.

. When these 2 BOWs have spillover, only the BOW with SHARED_EQUIPMENT_LOW_PRIORITY can turn on/off spillover. Water flows from the SHARED_EQUIPMENT_HIGH_PRIORITY BOW to the SHARED_EQUIPMENT_LOW_PRIORITY BOW

- Common Requirements

- There are currently no operations defined for the body of water (not even init and teardown are used).

- The <Body-of-water> tag is used for this structure. Each body of water has an associated <Body-of-water> tag.

- Independent Pool (Pool Only)

- This is an independent pool (Pool Only). There is no sharing of this equipment. This has nothing to do with spillover.

- The required parameters are shown below:

- The <System-Id> must be unique and should be assigned sequentially.

- The default <Name> is Pool. A custom name may be substituted.

- The <Type> must be BOW_POOL. Not configurable.

- The <Shared-Type> must be BOW_NO_EQUIPMENT_SHARED. Not configurable.

- The <Shared-Equipment-System-ID> must be set to -1 since there is no sharing. Not configurable.

- <Supports-Spillover> must be no since there is no associated BOW to spillover into or from. Not configurable.

- <Use-Spillover-For-Filter-Operations> must be no since there can be no spillover. Not configurable.

- XML example:

<Body-of-water>

1

<System-Id>4</System-Id>

<Name>Family Pool</Name>

<Type>BOW_POOL</Type>

<Shared-Type>BOW_NO_EQUIPMENT_SHARED</Shared-Type>

<Shared-Equipment-System-ID>-1</Shared-Equipment-System-ID>

<Supports-Spillover>no</Supports-Spillover>

<Use-Spillover-For-Filter-Operations>

no

</Use-Spillover-For-Filter-Operations>

…

</Body-of-water>

- Independent Spa (Spa Only)

- This is an independent spa (Spa Only). There is no sharing of this equipment. This has nothing to do with spillover.

- The required parameters are shown below:

- The <System-Id> must be unique and should be assigned sequentially.

- The default <Name> is Spa. A custom name may be substituted.

- The <Type> must be BOW_SPA. Not configurable.

- The <Shared-Type> must be BOW_NO_EQUIPMENT_SHARED. Not configurable.

- The <Shared-Equipment-System-ID> must be set to -1 since there is no sharing. Not configurable.

- <Supports-Spillover> must be no since there is no associated BOW to spillover into or from. Not configurable.

- <Use-Spillover-For-Filter-Operations> must be no since there can be no spillover. Not configurable.

- XML example:

<Body-of-water>

1

<System-Id>4</System-Id>

<Name>Hot Tub</Name>

<Type>BOW_SPA</Type>

<Shared-Type>BOW_NO_EQUIPMENT_SHARED</Shared-Type>

<Shared-Equipment-System-ID>-1</Shared-Equipment-System-ID>

<Supports-Spillover>no</Supports-Spillover>

<Use-Spillover-For-Filter-Operations>

No

</Use-Spillover-For-Filter-Operations>

…

</Body-of-water>

- Shared Equipment (Pool and Spa SE)

- This is the configuration for a Pool and Spa Single Equipment (also known as Pool and Spa Shared Equipment also known as Pool and Spa Combo). This can be with or without spillover.

- The required parameters for each body of water are shown below:

- The <System-Id> must be unique and should be assigned sequentially.

- The default <Name>(s) are Pool and Spa. Custom names may be substituted.

- The <Type> must be BOW_POOL for the pool and BOW_SPA for the spa. Not configurable.

- <Shared-Type> for both bodies of water must be BOW_SHARED_EQUIPMENT. Not configurable.

- The <Shared-Equipment-System-ID> for each BOW must be set to the <System-Id> of the other BOW (that the equipment is being shared with).

- <Supports-Spillover> must be yes for both if spillover is present between these bodies of water or no for both if there is no spillover.

- <Use-Spillover-For-Filter-Operations> must be yes or no

- XML example:

<Body-of-water>

1

<System-Id>4</System-Id>

<Name>Family Pool</Name>

<Type>BOW_POOL</Type>

<Shared-Type>BOW_SHARED_EQUIPMENT</Shared-Type>

<Shared-Equipment-System-ID>5</Shared-Equipment-System-ID>

<Supports-Spillover>yes</Supports-Spillover>

<Use-Spillover-For-Filter-Operations>

yes

</Use-Spillover-For-Filter-Operations>

…

</Body-of-water>

<Body-of-water>

2

<System-Id>5</System-Id>

<Name>Therapy Spa</Name>

<Type>BOW_SPA</Type>

<Shared-Type>BOW_SHARED_EQUIPMENT</Shared-Type>

<Shared-Equipment-System-ID>4</Shared-Equipment-System-ID>

<Supports-Spillover>yes</Supports-Spillover>

<Use-Spillover-For-Filter-Operations>

yes

</Use-Spillover-For-Filter-Operations>

…

</Body-of-water>

- Pool and Spa DE with Separate Heaters

- This is an independent pool plus an independent spa. There is no sharing of any equipment. This has nothing to do with spillover.

- The required parameters are shown below:

- The <System-Id> must be unique and should be assigned sequentially.

- The default <Name>(s) are Pool and Spa. Custom names may be substituted.

- The <Type> must be BOW_POOL for the pool and BOW_SPA for the spa. Not configurable.

- The <Shared-Type> for both bodies of water must be BOW_DUAL_EQUIPMENT. Not configurable.

- <The Shared-Equipment-System-ID> must be set to -1 since there is no sharing.

- <Supports-Spillover> must be yes for both if spillover is present between these bodies of water or no for both if there is no spillover.

- <Use-Spillover-For-Filter-Operations> must be yes or no. 

- XML example:

<Body-of-water>

1

<System-Id>4</System-Id>

<Name>Family Pool</Name>

<Type>BOW_POOL</Type>

<Shared-Type>BOW_DUAL_EQUIPMENT</Shared-Type>

<Shared-Equipment-System-ID>-1</Shared-Equipment-System-ID>

<Supports-Spillover>no</Supports-Spillover>

<Use-Spillover-For-Filter-Operations>

no

</Use-Spillover-For-Filter-Operations>

…

<Heater>

<System-Id>16</System-Id>

<Shared-Type> BOW_NO_EQUIPMENT_SHARED</Shared-Type>

<Shared-Equipment-System-ID>-1</Shared-Equipment-System-ID>

…

</Heater>

</Body-of-water>

<Body-of-water>

2

<System-Id>9</System-Id>

<Name>Dads Hot Tub</Name>

<Type>BOW_SPA</Type>

<Shared-Type>BOW_DUAL_EQUIPMENT</Shared-Type>

<Shared-Equipment-System-ID>-1</Shared-Equipment-System-ID>

<Supports-Spillover>no</Supports-Spillover>

<Use-Spillover-For-Filter-Operations>

no

</Use-Spillover-For-Filter-Operations>

…

<Heater>

<System-Id>19</System-Id>

<Shared-Type>BOW_NO_EQUIPMENT_SHARED</Shared-Type>

<Shared-Equipment-System-ID>-1</Shared-Equipment-System-ID>

…

</Heater>

</Body-of-water>

- Pool and Spa DE with Shared Heaters

- This is an independent pool plus an independent spa (separate filters and chlorinators). There is, however, one or more shared heaters. This has nothing to do with spillover.

- The required parameters are shown below:

- The <System-Id> must be unique and should be assigned sequentially.

- The default <Name>(s) are Pool and Spa. Custom names may be substituted.

- The <Type> must be BOW_POOL for the pool and BOW_SPA for the spa. Not configurable.

- The <Shared-Type> for both bodies of water must be BOW_DUAL_EQUIPMENT. Not configurable.

- The <Shared-Priority> must be SHARED_EQUIPMENT_LOW_PRIORITY for one of the BOW and   SHARED_EQUIPMENT_HIGH_PRIORITY for the other BOW depending on which has higher priority and which is the lower

- <Shared-Equipment-System-ID> must be set to the System ID of the other body of water that you are sharing with. 

- <Supports-Spillover> must be yes for both if spillover is present between these bodies of water or no for both if there is no spillover.

- <Use-Spillover-For-Filter-Operations> must be yes or no

- XML example:

<Body-of-water>

1

<System-Id>4</System-Id>

<Name>Family Pool</Name>

<Type>BOW_POOL</Type>

<Shared-Type>BOW_DUAL_EQUIPMENT</Shared-Type>

<Shared-Equipment-System-ID>-1</Shared-Equipment-System-ID>

<Supports-Spillover>no</Supports-Spillover>

<Use-Spillover-For-Filter-Operations>

No

</Use-Spillover-For-Filter-Operations>

…

<Heater>

<System-Id>16</System-Id>

<Shared-Type>BOW_SHARED_EQUIPMENT</Shared-Type>

<Shared-Equipment-System-ID>23</Shared-Equipment-System-ID>

…

</Heater>

</Body-of-water>

<Body-of-water>

2

<System-Id>9</System-Id>

<Name>Spa</Name>

<Type>BOW_SPA</Type>

<Shared-Type>BOW_DUAL_EQUIPMENT</Shared-Type>

<Shared-Equipment-System-ID>-1</Shared-Equipment-System-ID>

<Supports-Spillover>no</Supports-Spillover>

<Use-Spillover-For-Filter-Operations>

No

</Use-Spillover-For-Filter-Operations>

…

<Heater>

<System-Id>23</System-Id>

<Shared-Type>BOW_SHARED_EQUIPMENT</Shared-Type>

<Shared-Equipment-System-ID>16</Shared-Equipment-System-ID>

…

</Heater>

</Body-of-water>

- Spillover

- Spillover is possible between any two bodies of water that are single equipment (shared equipment) or dual equipment. 

- Initially the OmniLogic system will support spillover from a spa into a pool only. Other types of spillover are possible (pool to pool, spa to spa, pool to spa), but they will not be supported by the initial release.

- At this time spillover is not supported on more than two bodies of water.

- Spillover is not supported on two independent bodies of water (a Spa Only cannot spill over into a Pool Only).

- When spillover is enabled and active it turns the two bodies of water into a single body of water: 

- Water is drawn by the filter pump from the pool and returned by the filter pump into the spa.

- The receiving body of water (spa) then overflows into a spillway or a waterfall leading into the first body of water (pool).

- As spillover results in a single body of water there can only be one active filter pump. In a single equipment / shared equipment system there is only one pump available. For a dual equipment system the initial release will always use the pool pump during spillover.

- Note that when spillover is terminated, it may leave the spa overly full depending on the relationship of the spillway to the rim. As there is a relatively small amount of water in the spa any excess water will simply splash out or evaporate, reducing the water level.

- In future releases, more generalized spillover may be supported. This includes:

- General spillover between any two bodies of water.

- General spillover between any three bodies of water.

- In the case of a pool spilling over, the amount of excess water when spillover is terminated will be much more significant. A properly designed spillover will partially mitigate this, but we may need to support a mechanism to automatically restore the pool level to what is normal.

- The spillover tags are included in every body of water:

<Supports-Spillover>yes</Supports-Spillover>

<Use-Spillover-For-Filter-Operations>

yes

</Use-Spillover-For-Filter-Operations>

- Filter Pumps

- Allowed Types

- All allowed filter pump types are defined below:

Table #16

- Suction and Return Valves

- The suction and return valves are required in every Pool and Spa or Pool and Spa with Spillover configuration. The operation of these valves is tied to the filter pump (the Operations that control these valves are contained in the filter pump section).

- If there is pool and spa shared equipment then the filter pump is included in the configuration file twice. 

- The first instance is associated with the primary BOW (pool).

- The second instance is associated with the secondary BOW (spa).

- When the filter pump is structured to the pool the valve Operation will set the suction and return valves to pool.

- When the filter pump is structured to the spa the valve Operation will set the suction and return valves with spa.

- If there is spillover, the valve setup for spillover will be included where the filter pump is structure to the primary BOW (pool). 

- Single Speed Pump

- The single speed pump has one winding that is controlled by an HV Relay (either powered or not powered). When powered the pump runs at a pre-defined speed. 

- There are multiple Operations required for a filter pump.

- The <Filter> tag is used for this structure since this is a filter pump.

- The required parameters are shown below:

- The <System-Id> must be unique and should be assigned sequentially.

- The default <Name> is Filter Pump. A custom name may be substituted.

- The <Shared-Type> must be BOW_NO_EQIPMENT_SHARED if the filter pump is exclusive to this body of water or BOW_SHARED_EQUIPMENT if the filter pump is shared with another body of water or BOW_DUAL_EQUIPMENT if the filter pump is exclusive to this BOW and the BOW shares other equipment with another BOW, or BOW_STANDALONE_WITH_SPILLOVER if the filter pump is exclusive to this BOW but the BOW has spillover with another BOW. This must match the setting at the body of water level that this filter pump reports to.

- The <Filter-Type> must be FMT_SINGLE_SPEED. Not configurable.

- <Max-Pump-Speed> must be 100(%) which is the high speed setting. Not configurable.

- <Min-Pump-Speed> must be 100(%) which is the low speed setting. Not configurable.

- <Filter-Valve-Position> is the current position of the pool /spa /spillover valves if there are pool /spa /spillover valves. Note: FLT_VALVES_POS_LOW_PRIO_HEAT/FLT_VALVES_POS_HIGH_PRIO_HEAT are only valid when BoW's Shared-Type = BOW_DUAL_EQUIPMENT. When both BoW's are running,  the SHARED_EQUIPMENT_HIGH_PRIORITY BoW always gets the heat (if heater is running), So that ValvePostion for the SHARED_EQUIPMENT_LOW_PRIORITY BoW will be FLT_VALVES_POS_POOL_ONLY. ValvePostion for the SHARED_EQUIPMENT_HIGH_PRIORITY BoW will be FLT_VALVES_POS_HIGH_PRIO_HEAT. When only the SHARED_EQUIPMENT_LOW_PRIORITY BoW's is running,  only the SHARED_EQUIPMENT_LOW_PRIORITY BoW gets the heat (if heater is running), So that ValvePostion for the SHARED_EQUIPMENT_LOW_PRIORITY BoW will be FLT_VALVES_POS_LOW_PRIO_HEAT ValvePostion for the SHARED_EQUIPMENT_HIGH_PRIORITY BoW will be FLT_VALVES_POS_POOL_ONLY (NOTE: NOT FLT_VALVES_POS_SPA_ONLY !!). If Spillover is supported for this BoW combo, Spillover can only be controlled by the SHARED_EQUIPMENT_LOW_PRIORITY BoW, into which water spillovers

- XML example:

<Filter>

<System-Id>9</System-Id>

<Name>Filter Pump</Name>

<Shared-Type>BOW_NO_EQUIPMENT_SHARED</Shared-Type>

<Filter-Type>FMT_SINGLE_SPEED</Filter-Type>

<Max-Pump-Speed>100</Max-Pump-Speed>

<Min-Pump-Speed>100</Min-Pump-Speed>

</Filter>

- Dual Speed Pump

- This dual speed pump has two different sets of windings (high speed and low speed) that are connected to two different HV Relays.

- Only one winding can be powered at once or else the pump will be damaged or destroyed.

- There are multiple operations required for a pump.

- The <Filter> tag is used for this structure since this is a filter pump.

- The required parameters are shown below:

- The <System-Id> must be unique and should be assigned sequentially.

- The default <Name> is Filter Pump. A custom name may be substituted.

- The <Shared-Type> must be BOW_NO_EQIPMENT_SHARED if the filter pump is exclusive to this body of water or BOW_SHARED_EQUIPMENT if the filter pump is shared with another body of water or BOW_DUAL_EQUIPMENT if the filter pump is exclusive to this BOW and the BOW shares other equipment with another BOW, or BOW_STANDALONE_WITH_SPILLOVER if the filter pump is exclusive to this BOW but the BOW has spillover with another BOW. This must match the setting at the body of water level that this filter pump reports to.

- The <Filter-Type> must be FMT_DUAL_SPEED. Not configurable.

- <Max-Pump-Speed> must be 100(%) which is the high speed setting. Not configurable.

- <Min-Pump-Speed> must be 50(%) which is the low speed setting. Not configurable.

- XML example:

<Filter>

<System-Id>9</System-Id>

<Name>Filter</Name>

<Shared-Type>BOW_SHARED_EQUIPMENT</Shared-Type>

<Filter-Type>FMT_DUAL_SPEED</Filter-Type>

<Max-Pump-Speed>100</Max-Pump-Speed>

<Min-Pump-Speed>50</Min-Pump-Speed>

</Filter>

- Variable Speed Pump

- This variable speed pump is a Smart Component in and of itself. It has a microprocessor based controller that is capable of varying the pump speed over a specified operating range.

- The Variable Speed Pump is controlled by HPN commands sent over a system bus.

- It is possible to connect a Variable Speed pump to an HV relay also, but this HV relay is ONLY used as a safety off. At the present time this will not be supported.

- There are multiple Operations required for a pump.

- The Filter tag is used for this structure since this is a filter pump.

- The required parameters are shown below:

- The <System-Id> must be unique and should be assigned sequentially.

- The default <Name> is Filter Pump. A custom name may be substituted.

- The <Shared-Type> must be BOW_NO_EQIPMENT_SHARED if the filter pump is exclusive to this body of water or BOW_SHARED_EQUIPMENT if the filter pump is shared with another body of water or BOW_DUAL_EQUIPMENT if the filter pump is exclusive to this BOW and the BOW shares other equipment with another BOW, or BOW_STANDALONE_WITH_SPILLOVER if the filter pump is exclusive to this BOW but the BOW has spillover with another BOW. This must match the setting at the body of water level that this filter pump reports to.

- The <Type> must be FMT_VARIABLE_ SPEED_PUMP. Not configurable.

- <Max-Pump-RPM> is the absolute maximum pump speed that the physical pump is capable of. For an EcoStar this must be 3450(RPM). This translates to 100%. Not configurable.

- <Min-Pump-RPM> is the absolute minimum pump speed that the physical pump is capable of. For a regular EcoStar this must be 600(RPM), which translates to 18%. For an EcoStar with SVRS this must be 1000(RPM), which translates to 29%. Not configurable.

- <Max-Pump-Speed> is a pool specific maximum pump speed that can further reduce the absolute maximum. Max-Pump-Speed must be ≥ 60(%) and ≤ 100(%).

- <Min-Pump-Speed> is a pool specific minimum pump speed that can further raise the absolute minimum. For a regular EcoStar pump the Min-Pump-Speed must be ≥ 18(%) and ≤ 40(%). For an EcoStar with SVRS the Min-Pump-Speed must be ≥ 29(%) and ≤ 40(%).

- <Filter-Valve-Position> is the current position of the pool /spa /spillover valves if there are pool /spa /spillover valves or FLT_DONT_CHANGE_VALVES if there are not. 

- <Vsp-Low-Pump-Speed> is the standard low speed setting that will always be available to the UI. Must be ≥ <Min-Pump-Speed> and ≤ <Max-Pump-Speed>.

- <Vsp-Medium-Pump-Speed> is the standard medium speed setting that will always be available to the UI. Must be ≥ <Min-Pump-Speed> and ≤ <Max-Pump-Speed>. Should be ≥ <Vsp-Low-Pump-Speed>.

- <Vsp-High-Pump-Speed> is the standard high speed setting that will always be available to the UI. Must be ≥ <Min-Pump-Speed> and ≤ <Max-Pump-Speed>. Should be ≥ <Vsp-Medium-Pump-Speed>.

- <Vsp-Custom-Pump-Speed> is the default custom speed setting; this is the speed that the UI custom speed setting menu will come up to. Must be ≥ <Min-Pump-Speed> and ≤ <Max-Pump-Speed>.

- XML example (EcoStar w/SVRS):

<Filter>

<System-Id>9</System-Id>

<Name>Filter</Name>

<Shared-Type>BOW_SHARED_EQUIPMENT</Shared-Type>

<Filter-Type>FMT_VARIABLE_SPEED_PUMP</Filter-Type>

<Max-Pump-RPM>3450</Max-Pump-RPM>

<Min-Pump-RPM>1000</Min-Pump-RPM>

<Max-Pump-Speed>90</Max-Pump-Speed>

<Min-Pump-Speed>30</Min-Pump-Speed>



<Filter-Valve-Position> 

FLT_VALVES_POS_POOL_ONLY

</Filter-Valve-Position>

<Vsp-Low-Pump-Speed>40</Vsp-Low-Pump-Speed>

<Vsp-Medium-Pump-Speed>60</Vsp-Medium-Pump-Speed>

<Vsp-High-Pump-Speed>85</Vsp-High-Pump-Speed>

<Vsp-Custom-Pump-Speed>50</Vsp-Custom-Pump-Speed>



</Filter>

- Heaters

- Overview

- This section will cover both heating and cooling. Just as pools must be heated in cooler climates, they must be cooled in warmer climates. While heating is a much more common application, pool cooling must be supported as well.

- Heaters are the first example of virtual devices in this document. There is a single temperature set point for a body of water; this set point is applied to the virtual heater and, by extension, to all of the physical heaters that compose the virtual heater.

- Virtual Heater

- It is common for multiple heaters to be associated with a single BOW or a single equipment set. The philosophy of the OmniLogic pool controller is to automate all of these heaters as a single heat source to simplify the user interface.

- There is a single temperature set point for each BOW.

- Whenever physical heaters are controlled, even if there is only a single physical heater on a body of water, there will also be a virtual heater defined that contains the physical heater(s).

- The required parameters are shown below (note that virtual components are NOT named):

- The <System-Id> must be unique and should be assigned sequentially. Note that the virtual heater gets a unique <System-Id>, as well as every physical heater associated with the virtual heater.

- The <Shared-Type> must be BOW_NO_EQIPMENT_SHARED if the virtual heater is exclusive to this body of water or BOW_SHARED_EQUIPMENT if the virtual heater is shared with another body of water.

- <Shared-Equipment-System-ID> is the <System-Id> of the virtual heater that this equipment is shared with or -1 if there is no sharing.

- <Enabled> is yes to enable the heater or no to disable the heater.

- <Current-Set-Point> is the set point for the body of water (must be in Fahrenheit).

- <Max-Water-Temp> is the maximum allowable water temperature for this body of water and must be set to 104(°F). Not configurable.

- <Min-Settable-Water-Temp> is the minimum temperature that the set point can go to (beyond this the heater is off). This must be set to 65(°F). Not configurable.

- <Max-Settable-Water-Temp> is the maximum temperature that the set point can go to. This must be ≤ <Max-Water-Temp>. This is currently fixed at 104(°F).

- XML example:

<Heater>

<!-- this is the virtual heater -->

<System-Id>16</System-Id>

<!-- the virtual heater does NOT get a name -->

<Shared-Type>BOW_NO_EQUIPMENT_SHARED</Shared-Type>

<Shared-Equipment-System-ID>-1</Shared-Equipment-System-ID>

<Enabled>yes</Enabled>

<Current-Set-Point>77</Current-Set-Point>

<Max-Water-Temp>104</Max-Water-Temp>

<Min-Settable-Water-Temp>64</Min-Settable-Water-Temp>

<Max-Settable-Water-Temp>104</Max-Settable-Water-Temp>

</Heater>

- Available physical heater types:

Table #19

- Gas Heater

- The gas heater is the workhorse heater on the pool pad. It generally has the greatest capacity and the least limitations, but it is usually the most expensive to operate.

- Gas heaters require a minimum water flow in order to function. They have internal flow sensors as a backup, but they also specify a minimum filter pump speed for safe operation.

- There are two Operations defined for a gas heater: Turn On and Turn Off.

- The required parameters are shown below:

- The <System-Id> must be unique and should be assigned sequentially.

- The default <Name> is Gas Heater. A custom name can be substituted.

- The <Type> must be PET_HEATER. Not configurable.

- The <Heater-Type> must be HTR_GAS. Not configurable.

- <Enabled> is yes to enable the gas heater or no to disable the gas heater.

- XML example:

<Operation>

PEO_HEATER_EQUIPMENT

<Heater-Equipment>

<System-Id>4</System-Id>

<Name>Gas Heater</Name>

<Type>PET_HEATER</Type>

<Heater-Type>HTR_GAS</Heater-Type>

<Enabled>yes</Enabled>

</Heater-Equipment>

</Operation>

- Heat Pump

- A heat pump is essentially an air conditioner used in reverse. It removes heat from the ambient air and transfers it to the pool water.

- Heat pumps have a minimum ambient temperature below which they cannot function.

- The required parameters are shown below:

- <System-Id>(s) are assigned sequentially.

- The default <Name> is Heat Pump. A custom name can be substituted.

- The <Type> must be PET_HEATER. Not configurable.

- The <Heater-Type> must be HTR_HEAT_PUMP. Not configurable.

- <Enabled> is yes to enable the heat pump or no to disable the heat pump.



- XML example:

<Operation>

PEO_HEATER_EQUIPMENT

<Heater-Equipment>

<System-Id>4</System-Id>

<Name>Heat Pump</Name>

<Type>PET_HEATER</Type>

<Heater-Type>HTR_HEAT_PUMP</Heater-Type>

<Enabled>yes</Enabled>

</Heater-Equipment>

</Operation>

- Solar Heater

- Solar heating involves pumping pool water up to solar panels, generally on a roof. It only makes sense during the day when the sun is out.

- There must be a solar temperature sensor, monitoring the temperature of the solar panels.

- The solar temperature is compared to the current pool water temperature. There is an initial minimum offset that must be met before it makes sense to pump water through the solar panels. There is another minimum (run time) offset that must be maintained otherwise solar heating will shut off automatically.

- Traditional solar panels have an 8°F turn on offset and a 2°F maintenance offset. This means that the solar panels have to be 8°F warmer than the pool before solar operation can commence. Once water is flowing through the solar panels they will be cooled, so the maintenance offset must be lower than the turn on offset.

- Newer solar panels that utilize vacuum insulation can operate successfully with a 0°F turn on offset and a 0°F maintenance offset (meaning that they can function during cold weather).

- The required parameters are shown below:

- <System-Id>(s) are assigned sequentially.

- The default <Name> is Solar Heat. A custom name may be substituted.

- The <Type> must be PET_HEATER. Not configurable.

- <Heater-Type> must be HTR_SOLAR. Not configurable.

- <Enabled> is yes to enable solar heating or no to disable solar heating.

Figure #4

- XML example with boost pump:

<Operation>

PEO_HEATER_EQUIPMENT

<Heater-Equipment>

<System-Id>4</System-Id>

<Type>PET_HEATER</Type>

<Name>Solar Heater</Name>

<Heater-Type>HTR_SOLAR</Heater-Type>

<Enabled>yes</Enabled>

</Heater-Equipment>

</Operation>



Figure #5

- XML example without boost pump:

<Operation>

PEO_HEATER_EQUIPMENT

<Heater-Equipment>

<System-Id>4</System-Id>

<Name>Solar Heat</Name>

<Type>PET_HEATER</Type>

<Heater-Type>HTR_SOLAR</Heater-Type>

<Enabled>yes</Enabled>

</Heater-Equipment>

</Operation>

- Chlorinators

- Virtual Chlorinator

- The virtual chlorinator coordinates the actions of multiple physical chlorinators. All physical chlorinators on the same body of water must function in the same operating mode (timed or ORP). Thus the operating mode is set once at the virtual level. 

- This requires a virtual chlorinator to be configured even if there is only one physical chlorinator.

- Electronic chlorination uses an electrolytic cell to separate salt molecules in the pool water into free chlorine, which is highly chemically reactive and destructive to bacteria and algae.

- The Hayward electrolytic cell is called a Turbo-Cell or T-Cell.

- There are four different sizes of T-Cell that are supported by the OmniLogic pool controller. Larger T-Cells draw more current and create more chlorine per unit time. T-Cells are generally selected based on the size of the body of water.

- The four supported T-Cells are defined below:

Table #20

- The allowed chlorinator types are defined below:

Table #21

- For electronic chlorination there are two possible operating modes that function quite differently.

- Allowed chlorinator operating modes are defined below:

Table #22

- Timed operation is open loop. It allows the user to select the percentage of filter pump on time in which the chlorinator will also be active. This operating mode is not adaptable to changing pool use patterns and is not recommended.

- ORP operation is closed loop meaning that the chlorine level (ORP) is continuously sensed and fed back to the chlorinator. In ORP mode a setpoint is required and the system works to maintain the ORP level at the set point.

- Timed mode uses an empirically determined duty cycle based on the on-time of the filter pump. For example, the chlorinator can be on 30% of the time that the filter pump is on.

- Since timed mode is a fixed approximation that is not readily adaptable to changing conditions, there is a Superchlorinate function associated with timed mode.

- Superchlorinate is a way of shocking the pool when the chlorine level has gotten too low or to raise the level in anticipation of heavy bather load.

- In ORP mode the current ORP (Oxidation Reduction Potential, which is easy to measure and which can be correlated to the free chlorine level) is measured and compared to the setpoint. So long as the current ORP level is less than the setpoint the chlorinator remains on.

- Superchlorinate is not needed in ORP mode.

- It is possible to enable or disable the individual physical chlorinators independently of one another.

- There is only one “operation” currently defined at the virtual chlorinator level which is used to group all of the physical chlorinators together.

- All other chlorinator operations are associated with the physical chlorinators.

- The associated physical chlorinator operations are nested within this virtual chlorinator operation.

- The required parameters depend on the operating mode:

- <System-Id>s are assigned sequentially and must be unique.

- Virtual components are NOT named, so there is no <Name> field.

- The <Shared-Type> must be BOW_NO_EQIPMENT_SHARED if the chlorinator is exclusive to this body of water or BOW_SHARED_EQUIPMENT if the chlorinator is shared with another body of water. This must match the setting at the body of water level that this chlorinator reports to.

- <Enabled> is either yes or no and must be yes for an operating chlorinator.

- <Mode> is one of the values in Table 22.

- <Timed-Percent> is required for timed mode and can be anything between 0 and 100(%) in units of 1%. This is ignored in auto mode.

- <SuperChlor-Timeout> is also required for timed mode and is the maximum amount of time that the chlorinator can be active in SuperChlorinate mode before it is automatically shut down. The default is 86400 seconds (24 hours). This is ignored in auto mode.

- <Cell-Type> is one of the values in Table 20. Note that by specifying the cell type at the virtual chlorinator level we are requiring that every physical chlorinator beneath this virtual chlorinator use the specified cell type. In order to allow different physical chlorinators to use different cell types, the specification of the cell type must be moved down to the physical chlorinator structure.

- <ORP-Timeout> defaults to 86400 seconds (24 hours) although it is not used for timed chlorination. Not configurable.

- <ORP-Sensor-ID> is the Node ID of the Chemistry Sense Module (CSM) which is controlling the ORP sensor for this BOW. Leave this out if there is no CSM.

- XML example of Timed Operation:

<Chlorinator>

<System-Id>7</System-Id>

<Shared-Type>BOW_SHARED_EQUIPMENT</Shared-Type>

<Enabled>yes</Enabled>

<Mode>CHLOR_OP_MODE_TIMED</Mode>

<Timed-Percent>30</Timed-Percent>

<SuperChlor-Timeout>86400</SuperChlor-Timeout>

<Cell-Type>CELL_TYPE_T3</Cell-Type>

</Chlorinator>

- XML example of ORP Operation:

<Chlorinator>

<System-Id>7</System-Id>

<Mode>CHLOR_OP_MODE_ORP_AUTO</Mode>

<Timed-Percent>30</Timed-Percent> 

<SuperChlor-Timeout>86400</SuperChlor-Timeout> 

<Cell-Type>CELL_TYPE_T9</Cell-Type>

</Chlorinator>

- Main Panel Chlorinator

- This is the first type of physical chlorinator.

- The required parameters as shown below:

- <System-Id>s are assigned sequentially and must be unique. In particular, this must be a different <System-Id> than the virtual chlorinator.

- The default <Name> is Chlorinator. A custom name may be substituted. There can only be one Main Panel chlorinator in any OmniLogic system.

- The <Type> must be PET_CHLORINATOR. Not configurable.

- The <Chlorinator-Type> must be CHLOR_TYPE_MAIN_PANEL. Not configurable.

- <Enabled> is either yes or no and must be yes for an operating chlorinator.

- XML example:

<Chlorinator>

<System-Id>7</System-Id>

<Mode>CHLOR_OP_MODE_TIMED</Mode>

<Timed-Percent>30</Timed-Percent>

<SuperChlor-Timeout>86400</SuperChlor-Timeout>

<Cell-Type>CELL_TYPE_T3</Cell-Type>

</Chlorinator>

- Expansion Panel Chlorinator

- The functionality is identical to the Main Panel chlorinator.

- Operation is identical to the Main Panel chlorinator.

- XML example:

<Chlorinator>

<System-Id>24</System-Id>

<Mode>CHLOR_OP_MODE_TIMED</Mode>

<Timed-Percent>30</Timed-Percent>

<SuperChlor-Timeout>86400</SuperChlor-Timeout>

<Cell-Type>CELL_TYPE_T15</Cell-Type>

</Chlorinator>

- Aqua Rite Chlorinator

- <explanation of Aqua Rite functionality> 

- <explanation of Aqua Rite operation>

- The required parameters are shown below (for each Aqua Rite assigned to this body of water):

- System IDs are assigned sequentially.

- Default name is Aqua Rite.

- <details>

- <details>

- XML example:

<Chlorinator>

<System-Id>24</System-Id>

<Mode>CHLOR_OP_MODE_TIMED</Mode>

<Timed-Percent>30</Timed-Percent>

<SuperChlor-Timeout>86400</SuperChlor-Timeout>

<Cell-Type>CELL_TYPE_T5</Cell-Type>

</Chlorinator>

- Chemistry Sense and Dispense (CSAD)

- Chemistry Sense Module

- There is a Smart Component called a Chemistry Sense Module (CSM) that contains a pH probe (or sensor) and an ORP probe (or sensor).

- The pH sensor reads the pH of the pool water. The pH sensor output is used by the CSAD function.

- The voltage output of the pH sensor (plus an offset voltage) correlates to the pH reading. The correlation is performed at the CSM.

- The Oxidation Reduction Potential (ORP) sensor reads the ORP of the pool water. The ORP sensor output is used by the chlorinator function.

- The voltage output of the ORP sensor is the ORP reading.

- pH is the acidity / alkalinity of the pool water.

- ORP correlates to free chlorine level, which is the true measure of chlorine sanitization effectiveness.

- It is possible to compute free chlorine based on ORP, pool temperature, salt level, and several other parameters. This is not currently being done in the OmniLogic controller.

- The pool controller allows for a slight calibration of the pH sensor. The calibration is done by the MSP. The ORP sensor is not calibrated.

- Chemistry Dispense

- Chemistry dispense involves using a relay to control the release of acid into the pool to lower the pH.

- One of the side effects of the salt chlorination electrolysis is that the pool water tends to become more basic (alkaline) the more the T-Cell is on.

- Chemistry dispense involves the release of acid into the pool to return the pH to neutral.

- Chemistry dispense is intended to maintain a pool balance, not to bring a pool to balance. The system is not capable of raising pH nor is it capable of a large rapid reduction of pH.

- There are two methods for releasing acid:

- Liquid (Muriatic) acid can be pumped into the pool from a reservoir. This is the most effective method, but there is concern about over feeding.

- CO2 gas can be pumped into the pool. This is a safer method, but less efficient, especially if the installation is not optimum. The problem is that the CO2 gas bubbles out of the pool and into the air before it can react with the pool water and turn into carbonic acid and effectively lower the pH.

- Virtual CSAD (Chemistry Sense and Dispense) System

- While there can only be one CSM per body of water, there can be multiple chemistry dispense systems on the same body of water. Therefore a virtual CSAD device is required.

- In the configuration file the CSAD structure is about maintaining the pH reading in a body of water. There can be only one pH set point per body of water, which must be at the virtual device level.

- CO2 and liquid acid feed cannot be mixed in a single body of water. One type or the other must be selected at the virtual level.

- There are Operations defined at the virtual level for the CSAD system.

- The required parameters are shown below:

- The <System-Id> must be unique and should be assigned sequentially.

- Virtual devices are NOT named.

- The <Mode> must be CSAD_AUTO.

- The <Type> must be either CO2 or ACID.

- <Enabled> is either yes or no and must be yes for an operating CSAD.

- The <TargetValue> is the pH set point and will default to 7.5.

- The <CalibrationValue> is the amount by which the pH probe is offset due to calibration. Must be 0 until the configuration procedure is performed. Not configurable.

- XML example:

<CSAD>

<System-Id>13</System-Id>

<Mode>CSAD_AUTO</Mode>

<Type>CO2</Type>

<Enabled>yes</Enabled>

<TargetValue>7.5</TargetValue>

<CalibrationValue>0.1</CalibrationValue>

</CSAD>



- Pool and Backyard Accessories

- Overview

- All of the special purpose pool equipment (filter pumps, heaters, chlorinators, CSAD) have already been covered in preceding sections. All remaining pool and backyard accessories are controlled by either a relay (HV, LV, or ACT) or a pump (or both together).

- Relay and pump configuration will be discussed in general, then specific examples will be given.

- Relays

- HV relays are used to control devices that run from AC current:

- Backyard and landscape lights

- Christmas lights

- Fire pits

- Misters, foggers, bug zappers

- ACT relays are used to control electronic pool valves that work by diverting water on either the suction or return side of the filter pump:

- Main drain or skimmer

- Jets

- Fountains

- Waterfalls

- Water slides

- Pressure and suction cleaners

- The required parameters are shown below:

- System IDs are assigned sequentially.

- There can be no default name. A name must be chosen.

- The Type must be either RLY_LOW_VOLTAGE_RELAY, RLY_HIGH_VOLTAGE_RELAY or RLY_VALVE_ACTUATOR

- The Function must be one of the following

RLY_WATER_FEATURE

RLY_LIGHT

RLY_BACKYARD_LIGHT

RLY_POOL_LIGHT

RLY_CLEANER

RLY_WATER_SLIDE

RLY_WATERFALL

RLY_LAMINARS

RLY_FOUNTAIN

RLY_FIREPIT

RLY_JETS

RLY_BLOWER

RLY_ACCESSORY

RLY_CLEANER_PRESSURE

RLY_CLEANER_SUCTION

RLY_CLEANER_ROBOTIC

RLY_CLEANER_IN_FLOOR 

- XML example:

<Relay>

<System-Id>40</System-Id>

<Name>Water Feature</Name>

<Type>RLY_ACT_VOLTAGE_RELAY</Type>

<Function>RLY_WATER_FEATURE</Function>

- Pumps

- Combination of Relays and Pumps

- Water features can be implemented using a boost pump. A boost pump creates a suction / return loop that is generally separate from the filter loop.

- Complex water features can be implemented using a combination of valve(s) and boost pump.

- There are many appropriate names for such features:

- Waterfall

- Fountain

- Laminar

- Main Drain

- Suction Clnr

- Pressure Cln

- Single Speed Boost Pump Driven

- The single speed boost pump has one winding that is either powered or not powered and controlled by an HV Relay

- Boost pumps cannot be shared. They must serve a single purpose. Thus no shared equipment parameter is required.

- Depending on how they are plumbed into the system, boost pumps may not need to be primed. The installer should be the ultimate judge of whether or not priming is required.

- The required parameters as shown below:

- System IDs are assigned sequentially.

- Default name is Wtr Feature1. Remember that multiple water features on the same body of water MUST have different and unique names.

- Type must be PMP_SINGLE_SPEED.

- XML example:

<Pump>

 <System-Id>12</System-Id>

 <Name>Water Feature</Name>

</Pump>

- Backyard Lights / Features

- Overview

- Backyard lights are standard, above ground, lights that are normally controlled by a wall switch. In this case an HV relay is used instead of the switch. This includes:

- Outdoor spot or flood lights

- Porch or Patio lights

- Cabana lights

- Deck lights

- Backyard features are anything other than a light that can be controlled by a relay. This includes:

- Fire pits

- Misters

- Foggers

- Lawn sprinklers

- Bug zappers

- HV Relay Driven

- There are 3 required parameters as shown below:

- System IDs are assigned sequentially.

- The default name is BY Light1 or BY Feature1.

- Type is RLY_HIGH_VOLTAGE_RELAY.

- XML example:

<Relay>

<System-Id>33</System-Id>

<Name>Porch Light</Name>

<Type>RLY_HIGH_VOLTAGE_RELAY</Type>

</Operation>

</Relay>

- MspConfig.xml Sample

<?xml version="1.0" encoding="UTF-8" ?>

<!--Settings for MSP-->

<Response>

  <MSPConfig>

    <System>

        <Msp-Vsp-Speed-Format>Percent</Msp-Vsp-Speed-Format>

        <Msp-Time-Format>12 Hour Format</Msp-Time-Format>

        <Units>Standard</Units>

        <Msp-Chlor-Display>Salt</Msp-Chlor-Display>

        <Msp-Language>English</Msp-Language>

    </System>

    <Backyard>MSP Configuration

        <System-Id>0</System-Id>

        <Name>Backyard</Name>

        <Service-Mode-Timeout>0</Service-Mode-Timeout>

        <Sensor>

            <System-Id>216</System-Id>

            <Name>AirSensor</Name>

            <Type>SENSOR_AIR_TEMP</Type>

            <Units>UNITS_FAHRENHEIT</Units>

        </Sensor>

        <Relay>

            <System-Id>221</System-Id>

            <Name>Landscape</Name>

            <Type>RLY_HIGH_VOLTAGE_RELAY</Type>

            <Function>RLY_BACKYARD_LIGHT</Function>

        </Relay>

        <Relay>

            <System-Id>222</System-Id>

            <Name>Step Lights</Name>

            <Type>RLY_HIGH_VOLTAGE_RELAY</Type>

            <Function>RLY_BACKYARD_LIGHT</Function>

        </Relay>

        <Relay>

            <System-Id>223</System-Id>

            <Name>Post Lights</Name>

            <Type>RLY_HIGH_VOLTAGE_RELAY</Type>

            <Function>RLY_BACKYARD_LIGHT</Function>

        </Relay>

        <Body-of-water>01

            <System-Id>183</System-Id>

            <Name>Pool</Name>

            <Type>BOW_POOL</Type>

            <Shared-Type>BOW_SHARED_EQUIPMENT</Shared-Type>

            <Shared-Equipment-System-ID>203</Shared-Equipment-System-ID>

            <Supports-Spillover>yes</Supports-Spillover>

            <Size-In-Gallons>0</Size-In-Gallons>

            <Filter>

                <System-Id>184</System-Id>

                <Name>Filter Pump</Name>

                <Shared-Type>BOW_SHARED_EQUIPMENT</Shared-Type>

                <Filter-Type>FMT_VARIABLE_SPEED_PUMP</Filter-Type>

                <Max-Pump-Speed>100</Max-Pump-Speed>

                <Min-Pump-Speed>18</Min-Pump-Speed>

                <Max-Pump-RPM>3450</Max-Pump-RPM>

                <Min-Pump-RPM>600</Min-Pump-RPM>

                <Vsp-Low-Pump-Speed>18</Vsp-Low-Pump-Speed>

                <Vsp-Medium-Pump-Speed>50</Vsp-Medium-Pump-Speed>

                <Vsp-High-Pump-Speed>100</Vsp-High-Pump-Speed>

                <Vsp-Custom-Pump-Speed>100</Vsp-Custom-Pump-Speed>

            </Filter>

            <Heater>

                <System-Id>185</System-Id>

                <Shared-Type>BOW_SHARED_EQUIPMENT</Shared-Type>

                <Enabled>yes</Enabled>

                <Current-Set-Point>70</Current-Set-Point>

                <Max-Water-Temp>104</Max-Water-Temp>

                <Min-Settable-Water-Temp>65</Min-Settable-Water-Temp>

                <Max-Settable-Water-Temp>104</Max-Settable-Water-Temp>

                <Operation>PEO_HEATER_EQUIPMENT

                    <Heater-Equipment>

                        <System-Id>186</System-Id>

                        <Name>Gas</Name>

                        <Type>PET_HEATER</Type>

                        <Heater-Type>HTR_GAS</Heater-Type>

                        <Enabled>yes</Enabled>

                    </Heater-Equipment>

                </Operation>

                <Operation>PEO_HEATER_EQUIPMENT

                    <Heater-Equipment>

                        <System-Id>187</System-Id>

                        <Name>Heat Pump</Name>

                        <Type>PET_HEATER</Type>

                        <Heater-Type>HTR_HEAT_PUMP</Heater-Type>

                        <Enabled>yes</Enabled>

                    </Heater-Equipment>

                </Operation>

                <Operation>PEO_HEATER_EQUIPMENT

                    <Heater-Equipment>

                        <System-Id>188</System-Id>

                        <Name>Solar</Name>

                        <Type>PET_HEATER</Type>

                        <Heater-Type>HTR_SOLAR</Heater-Type>

                        <Enabled>yes</Enabled>

                    </Heater-Equipment>

                </Operation>

            </Heater>

            <Sensor>

                <System-Id>189</System-Id>

                <Name>SolarSensor</Name>

                <Type>SENSOR_SOLAR_TEMP</Type>

                <Units>UNITS_FAHRENHEIT</Units>

            </Sensor>

            <CSAD>

                <System-Id>190</System-Id>

                <Name>pH</Name>

                <Mode>CSAD_AUTO</Mode>

                <Type>ACID</Type>

                <Enabled>yes</Enabled>

                <TargetValue>7.5</TargetValue>

                <CalibrationValue>0.0</CalibrationValue>

                <Timeout>7200</Timeout>

                <Extend-Enabled>no</Extend-Enabled>

                <ORP-Forced-On-Time>0</ORP-Forced-On-Time>

                <ORP-Forced-Enabled>no</ORP-Forced-Enabled>

            </CSAD>

            <Chlorinator>

                <System-Id>191</System-Id>

                <Name>Chlorinator</Name>

                <Shared-Type>BOW_SHARED_EQUIPMENT</Shared-Type>

                <Enabled>yes</Enabled>

                <Mode>CHLOR_OP_MODE_ORP_AUTO</Mode>

                <Timed-Percent>50</Timed-Percent>

                <SuperChlor-Timeout>24</SuperChlor-Timeout>

                <Cell-Type>CELL_TYPE_T15</Cell-Type>

                <ORP-Timeout>86400</ORP-Timeout>

            </Chlorinator>

            <Relay>

                <System-Id>194</System-Id>

                <Name>Cleaner</Name>

                <Type>RLY_HIGH_VOLTAGE_RELAY</Type>

                <Function>RLY_CLEANER_IN_FLOOR</Function>

            </Relay>

            <Relay>

                <System-Id>195</System-Id>

                <Name>Pool-Spill</Name>

                <Type>RLY_VALVE_ACTUATOR</Type>

                <Function>RLY_WATER_FEATURE</Function>

            </Relay>

            <Relay>

                <System-Id>196</System-Id>

                <Name>WaterSlide</Name>

                <Type>RLY_VALVE_ACTUATOR</Type>

                <Function>RLY_WATER_SLIDE</Function>

            </Relay>

            <Pump>

                <System-Id>197</System-Id>

                <Name>Waterfall</Name>

                <Type>PMP_VARIABLE_SPEED_PUMP</Type>

                <Function>PMP_WATERFALL</Function>

                <Max-Pump-RPM>3450</Max-Pump-RPM>

                <Min-Pump-RPM>600</Min-Pump-RPM>

                <Min-Pump-Speed>18</Min-Pump-Speed>

                <Max-Pump-Speed>100</Max-Pump-Speed>

                <Vsp-Medium-Pump-Speed>50</Vsp-Medium-Pump-Speed>

                <Vsp-Custom-Pump-Speed>50</Vsp-Custom-Pump-Speed>

                <Vsp-High-Pump-Speed>100</Vsp-High-Pump-Speed>

                <Vsp-Low-Pump-Speed>18</Vsp-Low-Pump-Speed>

            </Pump>

            <Relay>

                <System-Id>198</System-Id>

                <Name>RainCurtain</Name>

                <Type>RLY_VALVE_ACTUATOR</Type>

                <Function>RLY_WATERFALL</Function>

            </Relay>

            <ColorLogic-Light>

                <System-Id>199</System-Id>

                <Name>Pool Lights</Name>

                <Type>COLOR_LOGIC_UCL</Type>

            </ColorLogic-Light>

            <Relay>

                <System-Id>200</System-Id>

                <Name>Laminar</Name>

                <Type>RLY_HIGH_VOLTAGE_RELAY</Type>

                <Function>RLY_LAMINARS</Function>

            </Relay>

            <Pump>

                <System-Id>201</System-Id>

                <Name>Bubbler</Name>

                <Type>PMP_VARIABLE_SPEED_PUMP</Type>

                <Function>PMP_BLOWER</Function>

                <Max-Pump-RPM>3450</Max-Pump-RPM>

                <Min-Pump-RPM>600</Min-Pump-RPM>

                <Min-Pump-Speed>18</Min-Pump-Speed>

                <Max-Pump-Speed>100</Max-Pump-Speed>

                <Vsp-Medium-Pump-Speed>50</Vsp-Medium-Pump-Speed>

                <Vsp-Custom-Pump-Speed>50</Vsp-Custom-Pump-Speed>

                <Vsp-High-Pump-Speed>100</Vsp-High-Pump-Speed>

                <Vsp-Low-Pump-Speed>18</Vsp-Low-Pump-Speed>

            </Pump>

            <Relay>

                <System-Id>202</System-Id>

                <Name>SheerDescent</Name>

                <Type>RLY_HIGH_VOLTAGE_RELAY</Type>

                <Function>RLY_WATER_FEATURE</Function>

            </Relay>

            <Sensor>

                <System-Id>217</System-Id>

                <Name>WaterSensor</Name>

                <Type>SENSOR_WATER_TEMP</Type>

                <Units>UNITS_FAHRENHEIT</Units>

            </Sensor>

            <Sensor>

                <System-Id>219</System-Id>

                <Name>FlowSensor</Name>

                <Type>SENSOR_FLOW</Type>

                <Units>UNITS_ACTIVE_INACTIVE</Units>

            </Sensor>

        </Body-of-water>

        <Body-of-water>02

            <System-Id>203</System-Id>

            <Name>Spa</Name>

            <Type>BOW_SPA</Type>

            <Shared-Type>BOW_SHARED_EQUIPMENT</Shared-Type>

            <Shared-Equipment-System-ID>183</Shared-Equipment-System-ID>

            <Supports-Spillover>yes</Supports-Spillover>

            <Use-Spillover-For-Filter-Operations>no</Use-Spillover-For-Filter-Operations>

            <Size-In-Gallons>0</Size-In-Gallons>

            <Filter>

                <System-Id>204</System-Id>

                <Name>Filter Pump</Name>

                <Shared-Type>BOW_SHARED_EQUIPMENT</Shared-Type>

                <Filter-Type>FMT_VARIABLE_SPEED_PUMP</Filter-Type>

                <Max-Pump-Speed>100</Max-Pump-Speed>

                <Min-Pump-Speed>18</Min-Pump-Speed>

                <Max-Pump-RPM>3450</Max-Pump-RPM>

                <Min-Pump-RPM>600</Min-Pump-RPM>

                <Vsp-Low-Pump-Speed>18</Vsp-Low-Pump-Speed>

                <Vsp-Medium-Pump-Speed>50</Vsp-Medium-Pump-Speed>

                <Vsp-High-Pump-Speed>100</Vsp-High-Pump-Speed>

                <Vsp-Custom-Pump-Speed>100</Vsp-Custom-Pump-Speed>

            </Filter>

            <Heater>

                <System-Id>205</System-Id>

                <Shared-Type>BOW_SHARED_EQUIPMENT</Shared-Type>

                <Enabled>yes</Enabled>

                <Max-Water-Temp>104</Max-Water-Temp>

                <Min-Settable-Water-Temp>65</Min-Settable-Water-Temp>

                <Max-Settable-Water-Temp>104</Max-Settable-Water-Temp>

                <Cooldown-Enabled>no</Cooldown-Enabled>

                <Operation>PEO_HEATER_EQUIPMENT

                    <Heater-Equipment>

                        <System-Id>206</System-Id>

                        <Name>Gas</Name>

                        <Type>PET_HEATER</Type>

                        <Heater-Type>HTR_GAS</Heater-Type>

                        <Enabled>yes</Enabled>

                    </Heater-Equipment>

                </Operation>

                <Operation>PEO_HEATER_EQUIPMENT

                    <Heater-Equipment>

                        <System-Id>207</System-Id>

                        <Name>Heat Pump</Name>

                        <Type>PET_HEATER</Type>

                        <Heater-Type>HTR_HEAT_PUMP</Heater-Type>

                        <Enabled>yes</Enabled>

                    </Heater-Equipment>

                </Operation>

                <Operation>PEO_HEATER_EQUIPMENT

                    <Heater-Equipment>

                        <System-Id>208</System-Id>

                        <Name>Solar</Name>

                        <Type>PET_HEATER</Type>

                        <Heater-Type>HTR_SOLAR</Heater-Type>

                        <Enabled>yes</Enabled>

                    </Heater-Equipment>

                </Operation>

            </Heater>

            <Chlorinator>

                <System-Id>209</System-Id>

                <Name>Chlorinator</Name>

                <Shared-Type>BOW_SHARED_EQUIPMENT</Shared-Type>

                <Enabled>yes</Enabled>

                <Mode>CHLOR_OP_MODE_ORP_AUTO</Mode>

                <Timed-Percent>50</Timed-Percent>

                <SuperChlor-Timeout>24</SuperChlor-Timeout>

                <Cell-Type>CELL_TYPE_T15</Cell-Type>

                <ORP-Timeout>86400</ORP-Timeout>

            </Chlorinator>

            <CSAD>

                <System-Id>211</System-Id>

                <Name>pH</Name>

                <Mode>CSAD_AUTO</Mode>

                <Type>ACID</Type>

                <Enabled>yes</Enabled>

                <TargetValue>7.5</TargetValue>

                <CalibrationValue>0.0</CalibrationValue>

                <Timeout>7200</Timeout>

                <Extend-Enabled>no</Extend-Enabled>

                <ORP-Target-Level>650</ORP-Target-Level>

                <ORP-Forced-On-Time>0</ORP-Forced-On-Time>

                <ORP-Forced-Enabled>no</ORP-Forced-Enabled>

            </CSAD>

            <ColorLogic-Light>

                <System-Id>213</System-Id>

                <Name>Spa Lights</Name>

                <Type>COLOR_LOGIC_UCL</Type>

            </ColorLogic-Light>

            <Relay>

                <System-Id>214</System-Id>

                <Name>Blower</Name>

                <Type>RLY_HIGH_VOLTAGE_RELAY</Type>

                <Function>RLY_BLOWER</Function>

            </Relay>

            <Pump>

                <System-Id>215</System-Id>

                <Name>Spa Jets</Name>

                <Type>PMP_VARIABLE_SPEED_PUMP</Type>

                <Function>PMP_JETS</Function>

                <Max-Pump-RPM>3450</Max-Pump-RPM>

                <Min-Pump-RPM>600</Min-Pump-RPM>

                <Min-Pump-Speed>18</Min-Pump-Speed>

                <Max-Pump-Speed>100</Max-Pump-Speed>

                <Vsp-Medium-Pump-Speed>50</Vsp-Medium-Pump-Speed>

                <Vsp-Custom-Pump-Speed>50</Vsp-Custom-Pump-Speed>

                <Vsp-High-Pump-Speed>100</Vsp-High-Pump-Speed>

                <Vsp-Low-Pump-Speed>18</Vsp-Low-Pump-Speed>

            </Pump>

            <Sensor>

                <System-Id>218</System-Id>

                <Name>WaterSensor</Name>

                <Type>SENSOR_WATER_TEMP</Type>

                <Units>UNITS_FAHRENHEIT</Units>

            </Sensor>

            <Sensor>

                <System-Id>220</System-Id>

                <Name>FlowSensor</Name>

                <Type>SENSOR_FLOW</Type>

                <Units>UNITS_ACTIVE_INACTIVE</Units>

            </Sensor>

        </Body-of-water>

    </Backyard>

    <Schedules />

    <Favorites />

</MSPConfig>



Appendix E: Telemetry message enums

    <!-- 

      This <StatusEnums> defines ALL Enums used in OmniLogic status messages

     -->

    <StatusEnums> 

        <Backyard statusField="state" currentValueField="airTemp">

            <state>

                <Key name="0">off</Key>

                <Key name="1">on</Key>

                <Key name="2">service mode</Key>

                <Key name="3">config mode</Key>

                <Key name="4">timed service mode</Key>

            </state>

        </Backyard>



        <Relay statusField="relayState">

            <relayState>

                <Key name="0">off</Key>

                <Key name="1">on</Key>

            </relayState>

        </Relay>



        <Pump statusField="pumpState" currentValueField="pumpSpeed" setpointField="pumpSpeed">

            <pumpState>

                <Key name="0">off</Key>

                <Key name="1">on</Key>

            </pumpState>

        </Pump>



        <ValveActuator statusField="valveActuatorState">

            <valveActuatorState>

                <Key name="0">off</Key>

                <Key name="1">on</Key>

            </valveActuatorState>

        </ValveActuator>



        <BodyOfWater statusField="flow" currentValueField="waterTemp">

            <flow>

                <Key name="0">no flow</Key>

                <Key name="1">flow</Key>

            </flow>

        </BodyOfWater>



        <Filter statusField="filterState" currentValueField="filterSpeed" setpointField="filterSpeed">

            <filterState>

                <Key name="0">off</Key>

                <Key name="1">on</Key>

                <Key name="2">Priming</Key>

                <Key name="3">WAITING_TO_TURN_OFF</Key>

                <Key name="4">WAITING_TO_TURN_OFF_MANUAL</Key>

                <Key name="5">HEATER_EXTEND</Key>

                <Key name="6">COOL_DOWN_MODE</Key>

                <Key name="7">SUSPENDED</Key>

                <Key name="8">CSAD_EXTEND</Key>

                <Key name="9">FILTER_SUPERCHLORINATE</Key>

                <Key name="10">FILTER_FORCE_PRIMING</Key>

                <Key name="11">FILTER_WAITING_FOR_PUMP_TO_TURN_OFF</Key>

            </filterState>

            <valvePosition>

                <Key name="1">FLT_VALVES_POS_POOL_ONLY</Key>

                <Key name="2">FLT_VALVES_POS_SPA_ONLY</Key>

                <Key name="3">FLT_VALVES_POS_SPILLOVER</Key>

                <!--

                     . FLT_VALVES_POS_LOW_PRIO_HEAT/FLT_VALVES_POS_HIGH_PRIO_HEAT are only valid when BoW's Shared-Type = BOW_DUAL_EQUIPMENT.

                       BOW_DUAL_EQUIPMENT means 2 BoW's have separate filter pumps but they share one set of heaters.



                       One of the BoWs will have its Shared-Priority = SHARED_EQUIPMENT_LOW_PRIORITY, and the other one will have Shared-Priority = SHARED_EQUIPMENT_HIGH_PRIORITY,



                     . When both BoW's are running,  the SHARED_EQUIPMENT_HIGH_PRIORITY BoW always gets the heat (if heater is running),

                       So that 

                         - ValvePostion for the SHARED_EQUIPMENT_LOW_PRIORITY BoW will be FLT_VALVES_POS_POOL_ONLY 

                         - ValvePostion for the SHARED_EQUIPMENT_HIGH_PRIORITY BoW will be FLT_VALVES_POS_HIGH_PRIO_HEAT

                     . When only the SHARED_EQUIPMENT_LOW_PRIORITY BoW's is running,  only the SHARED_EQUIPMENT_LOW_PRIORITY BoW gets the heat (if heater is running),

                       So that 

                         - ValvePostion for the SHARED_EQUIPMENT_LOW_PRIORITY BoW will be FLT_VALVES_POS_LOW_PRIO_HEAT 

                         - ValvePostion for the SHARED_EQUIPMENT_HIGH_PRIORITY BoW will be FLT_VALVES_POS_POOL_ONLY (NOTE: NOT FLT_VALVES_POS_SPA_ONLY !!)



                     . If Spillover is supported for this BoW combo, Spillover can only be controlled by the SHARED_EQUIPMENT_LOW_PRIORITY BoW, into which water spillovers

                 -->

                <Key name="4">FLT_VALVES_POS_LOW_PRIO_HEAT</Key>

                <Key name="5">FLT_VALVES_POS_HIGH_PRIO_HEAT</Key>

            </valvePosition>

            <whyFilterIsOn>

                <Key name="0">Off</Key>

                <Key name="1">No water flow</Key>

                <Key name="2">Cool down</Key>

                <Key name="3">pH reduction extend</Key>

                <Key name="4">Heater extend</Key>

                <Key name="5">Paused</Key>

                <Key name="6">Valve changing</Key>

                <Key name="7">Forced high speed</Key>

                <Key name="8">Off for external interlock</Key>

                <Key name="9">Super chlorinate</Key>

                <Key name="10">Count down timer</Key>

                <Key name="11">Manual on</Key>

                <Key name="12">Manual spill over</Key>

                <Key name="13">Timed Spill over</Key>

                <Key name="14">Timed On</Key>

                <Key name="15">Freeze protect</Key>

            </whyFilterIsOn>

        </Filter>



        <!-- 

              *NOTE* 

               Heater is special, we have to scan through all its "child" physical heaters(defined in Config file) to tell if it's on or off.

               Currently Virtual Heater is on when any of its sub physical heaters are on.



               But Virtual Heater has another status, which tells if the Heater is enabled or not. This Enabled=yes/no field is in the MspConfig.xml,

                 <Heater>

                  <System-Id>185</System-Id>

                  <Shared-Type>BOW_SHARED_EQUIPMENT</Shared-Type>

                  <Enabled>yes</Enabled>                 <--------------------------- here!

                  ...

         -->

        <VirtualHeater statusField="API::NA" setpointField="Current-Set-Point">

        </VirtualHeater>



        <Heater statusField="heaterState" currentValueField="temp">

            <heaterState>

                <Key name="0">off</Key>

                <Key name="1">on</Key>

                <Key name="2">pause</Key>

            </heaterState>

        </Heater>



        <!--

             **IMPORTANT** 

             CSAD: API::CONFIG::TargetValue means the <TargetValue>7.5</TargetValue> under the corresponding <CSAD> node in the Configuration file

        -->

        <CSAD status="status" currentValueField="ph" setpointField="API::CONFIG::CSAD::TargetValue">

            <status>

                <Key name="0">not dispensing</Key>

                <Key name="1">dispensing</Key>

            </status>

            <mode>

                <Key name="0">CSAD off</Key>

                <Key name="1">CSAD auto</Key>

                <Key name="2">CSAD forced on</Key>

                <Key name="3">CSAD monitoring</Key>

                <Key name="4">CSAD dispensing off</Key>

            </mode>

        </CSAD>



        <!--

             **IMPORTANT** 

             CSAD: API::CONFIG::TargetValue means the <TargetValue>7.5</TargetValue> under the corresponding <CSAD> node in the Configuration file

             . If the Chlorinator is in Timed mode, CHLOR_OP_MODE_TIMED, 

                  <Chlorinator statusField="status" currentValueField="Timed-Percent" setpointField="Timed-Percent">



             . If the Chlorinator is in ORP mode, CHLOR_OP_MODE_ORP_AUTO, on/off status is from the "status" field. 

               But the currentValueField is the "orp" of the CSAD in the same BOW, this info can be found in the status/telemetry message. 

               And the setpointField is the <ORP-Target-Level> of the CSAD in the same BOW, and this <ORP-Target-Level> is in the Configuration File

                  <Chlorinator statusField="status" currentValueField="API::STATUS::CSAD::orp" setpointField="API::CONFIG::CSAD::ORP-Target-Level">

          -->

        <Chlorinator statusField="status" currentValueField="Timed-Percent" setpointField="Timed-Percent">

            <status>

                <Key name="API::BitAND::8">on</Key>

                <Key name="API::DEFAULT">off</Key>

            </status>

            <operatingMode>

                <Key name="1">timed</Key>

                <Key name="2">orp</Key>

            </operatingMode>

        </Chlorinator>



        <ColorLogic-Light statusField="lightState" currentValueField="currentShow">

            <!-- The currentShow below is generated dynamically based on this light's Type -->

            <lightState>

                <Key name="0">off</Key>

                <Key name="1">on</Key>

            </lightState>

            <!-- 

                 In the MspConfig.xml, look for a light's <Type>, below are the enums for each type.

             -->

            <currentShow Type="COLOR_LOGIC_2_5">

                <Key name="0">VoodooLounge</Key>

                <Key name="1">DeepBlueSea</Key>

                <Key name="2">AfternoonSkies</Key>

                <Key name="3">Emerald</Key>

                <Key name="4">Sangria</Key>

                <Key name="5">CloudWhite</Key>

                <Key name="6">Twilight</Key>

                <Key name="7">Tranquility</Key>

                <Key name="8">Gemstone</Key>

                <Key name="9">USA</Key>

                <Key name="10">MardiGras</Key>

                <Key name="11">CoolCabaret</Key>

            </currentShow>

            <currentShow Type="COLOR_LOGIC_4_0">        

                  <Key name="0">VoodooLounge</Key>

                  <Key name="1">DeepBlueSea</Key>

                  <Key name="2">AfternoonSkies</Key>

                  <Key name="3">Emerald</Key>

                  <Key name="4">Sangria</Key>

                  <Key name="5">CloudWhite</Key>

                  <Key name="6">Twilight</Key>

                  <Key name="7">Tranquility</Key>

                  <Key name="8">Gemstone</Key>

                  <Key name="9">USA</Key>

                  <Key name="10">MardiGras</Key>

                  <Key name="11">CoolCabaret</Key>

            </currentShow>

            <currentShow Type="COLOR_LOGIC_UCL">

                <Key name="0">Voodoo_Lounge</Key>

                <Key name="1">Deep_Blue_Sea</Key>

                <Key name="2">Royal_Blue</Key>

                <Key name="3">Afternoon_Skies</Key>

                <Key name="4">Aqua_Green</Key>

                <Key name="5">Emerald</Key>

                <Key name="6">Cloud_White</Key>

                <Key name="7">Warm_Red</Key>

                <Key name="8">Flamingo</Key>

                <Key name="9">Vivid_Violet</Key>

                <Key name="10">Sangria</Key>

                <Key name="11">Twilight</Key>

                <Key name="12">Tranquility</Key>

                <Key name="13">Gemstone</Key>

                <Key name="14">USA</Key>

                <Key name="15">Mardi_Gras</Key>

                <Key name="16">Cool_Cabaret</Key>

            </currentShow>

<currentShow Type="COLOR_LOGIC_UCL">    and additional Omni Direct light colors

                <Key name="0">Voodoo_Lounge</Key>

                <Key name="1">Deep_Blue_Sea</Key>

                <Key name="2">Royal_Blue</Key>

                <Key name="3">Afternoon_Skies</Key>

                <Key name="4">Aqua_Green</Key>

                <Key name="5">Emerald</Key>

                <Key name="6">Cloud_White</Key>

                <Key name="7">Warm_Red</Key>

                <Key name="8">Flamingo</Key>

                <Key name="9">Vivid_Violet</Key>

                <Key name="10">Sangria</Key>

                <Key name="11">Twilight</Key>

                <Key name="12">Tranquility</Key>

                <Key name="13">Gemstone</Key>

                <Key name="14">USA</Key>

                <Key name="15">Mardi_Gras</Key>

                <Key name="16">Cool_Cabaret</Key>        

10 Additional Omni Direct colors with configuration setting:  <V2-Active>yes</V2-Active>

    <Key name="17">Yellow</Key>

 <Key name="18">Orange</Key>

 <Key name="19">Gold</Key>

<Key name="20">Mint</Key>

<Key name="21">Teal</Key>

<Key name="22">Burnt_Orange</Key>

<Key name="23">Pure_White</Key>

<Key name="24">Crisp_White</Key>

<Key name="25">Warm_White</Key>

<Key name="26">Bright_Yellow</Key>

            </currentShow>

        </ColorLogic-Light>

    </StatusEnums> 












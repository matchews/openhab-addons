# ELK M1 Binding

The ELK M1 binding integrates the ELK M1  Alarm Panel into OpenHab.

The ELKM1 binding communicates with the ELKM1 Gold or ELKM1EZ8 using a TCP/IP socket connection.  Both systems require an ELK-M1XEP ethernet module.

## Supported Things

The table below lists the Hayward OmniLogic binding thing types:

| Things                       | Description                                                                     | Thing Type    |
|------------------------------|---------------------------------------------------------------------------------|---------------|
| Bridge 					   | Connection to ELKM1 M1XEP Module                                                | bridge        |
| Area                         | Area                                                                            | area      	 |
| Zone	                       | Zone                                                                            | zone          |

## Discovery

The binding will automatically discover the ELKM1 things from the alarm panel.

## Thing Configuration

ELKM1 Bridge Parameters:

| Property             | Default                      | Required | Description                  |
|----------------------|------------------------------|----------|------------------------------|
| IP Address           | None                         | Yes      | IP Address of the M1XEP      |
| Port                 | 2101                         | Yes      | Port of the M1XEP			|
| User Code            | None                         | Yes      | ELKM1 User Code              |
| Use Secure Port 	   | False                        | No       | Use Secure Port         		|
| Username    		   | None                         | No       | Secure Port Username			|
| Password    		   | None                         | No       | Secure Port Password			|

## Channels

### Area Channels

| Channel Type ID | Item Type          | Description                        | Read Write |
|-----------------|--------------------|------------------------------------|:----------:|
| State           | String             | The state of the area			    |      R     |
| Armed           | String             | The armed state of the area        |     R/W    |
| Armup           | String             | The armup state of the area        |      R     |
| Command		  | String             | Send ELK Commands                  |     R/W    |


### Zone Channels

| Channel Type ID | Item Type          | Description                     								        | Read Write |
|-----------------|--------------------|------------------------------------------------------------------------|:----------:|
| Area            | Number             | The area the zone is in.												|      R     |
| Definition      | String 			   | The type of zone (i.e. Fire Alarm, Burglar Entry 1, etc.)  		    |      R     |
| Status	      | String 			   | The current status of this zone (i.e. Normal, Violated, Trouble, etc.) |      R     |
| Config	      | String 			   | The physical config set for this zone (i.e. EOL, Open, Short, etc.)    |      R     |

## Full Example

After installing the binding, you will need to manually add the ELKM1 Bridge thing and enter your credentials.
All things can be autmatically discovered by scanning the bridge
Goto the inbox and add the things.

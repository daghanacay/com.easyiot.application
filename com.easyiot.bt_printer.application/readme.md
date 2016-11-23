# 

Provides access to bluetooth printer.  


## Installation:

### raspberry pi
In order to use this bundle on linux systems you need to install the following packages

libbluetooth-dev on Ubuntu,Raspian. it can be installed by sudo apt-get install libbluetooth-dev 
bluez-libs-devel on Fedora
bluez-devel on openSUSE



## Example

On the osgi console write the following to find the bluetooth devices around
to print on device
>printer:write 'hello application'

to use bluetooth protocol functions
> bt:search NOAUTHENTICATE_NOENCRYPT
> bt:write 2 hello

## Configuration
[
  {
    "service.factoryPid" : "com.easyiot.bluetooth.protocol",
    "service.pid" : "btprotocol",
    "id" : "btprotocol.1",
    "host" : "0F03E0C24A69"
  }
]
		
	
## References

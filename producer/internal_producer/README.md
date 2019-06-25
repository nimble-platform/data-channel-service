# Internal producer for DataChannel Service



<a name="getting-started"></a>
## Getting Started
This skel code wants to help end users to write iot messages into internal Datachannel service. This version connect directly to Kafka; it is in calendar refactoring for better integration in Nimble and new version of DataChannel Service in order to connect to identity service and use Data Pipe Service.

### - start
clone the project

if kafka works in SSL set environment variable
dc_ssl_login=login
dc_ssl_password=changeit (usually by using APIKEY)


- customize your properties files
internalproducer.properties 
	dc.custom.CustomProducer[0..n] - activate custom producer class
	dc.topic.producer.propertiesfile - define if you are working with Ibm Kafka (Ibm Producer) or Generic Kafka (DcProducer)
	
DcProducer - configure the client to work with generic Kafka
IbmProducer - configure the client to work with Ibm Kafka

other property files are for each example producer

- create your own producer

- compile
run maven on pom.xml


 ---
The project leading to this application has received funding from the European Union Horizon 2020 research and innovation programme under grant agreement No 723810.
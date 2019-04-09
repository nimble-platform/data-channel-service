package eu.nimble.service.datachannel.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import eu.nimble.common.rest.identity.IdentityResolver;
import eu.nimble.service.datachannel.entity.ChannelConfiguration;
import eu.nimble.service.datachannel.entity.Machine;
import eu.nimble.service.datachannel.entity.Sensor;
import eu.nimble.service.datachannel.kafka.KafkaDomainClient;
import eu.nimble.service.datachannel.repository.ChannelConfigurationRepository;
import eu.nimble.service.datachannel.repository.MachineRepository;
import eu.nimble.service.datachannel.repository.SensorRepository;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import request.CreateChannel;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * REST Controller for managing data channels.
 *
 * @author Johannes Innerbichler
 */
@Controller
@RequestMapping(path = "/channel")
@Api("Data Channel API")
public class ChannelController implements ChannelAPI{

    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(ChannelController.class);

    @Autowired
    private IdentityResolver identityResolver;

    @Autowired
    private KafkaDomainClient kafkaDomainClient;

    @Autowired
    private ChannelConfigurationRepository channelConfigurationRepository;

    @Autowired
    private SensorRepository sensorRepository;

    @Autowired
    private MachineRepository machineRepository;

    //--------------------------------------------------------------------------------------
    // createChannel
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> createChannel(
            @ApiParam(value = "Channel configuration", required = true)
            @RequestBody CreateChannel.Request createChannelRequest,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException {

        // check if company id matches
        String companyID = identityResolver.resolveCompanyId(bearer);
        if (createChannelRequest.getProducerCompanyID().equals(companyID) == false) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        // create channel configuration
        ChannelConfiguration config = new ChannelConfiguration(createChannelRequest.getProducerCompanyID(),
                createChannelRequest.getConsumerCompanyIDs(),
                createChannelRequest.getDescription(),
                createChannelRequest.getStartDateTime(),
                createChannelRequest.getEndDateTime(),
                createChannelRequest.getBusinessProcessID());

        // set up channel in the Kafka domain
        KafkaDomainClient.CreateChannelResponse response = kafkaDomainClient.createChannel(config);

        // update and save channel configuration
        config.setChannelID(response.getChannelId());
        config.setProducerTopic(response.getInputTopic());
        if (config.getConsumerCompanyIDs().stream().findFirst().isPresent() ) {
            Map<String, String> consumerTopics = new HashMap<>();
            consumerTopics.put(config.getConsumerCompanyIDs().stream().findFirst().get(), response.getOutputTopic());
            config.setConsumerTopics(consumerTopics);
        }
        config = channelConfigurationRepository.save(config);

        logger.info("Company {} opened channel with ID {}", companyID, config.getChannelID());
        return new ResponseEntity<>(new CreateChannel.Response(config.getChannelID()), HttpStatus.OK);
    }

    //--------------------------------------------------------------------------------------
    // getChannel
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> getChannel(
            @ApiParam(value = "channelID", required = true)
            @PathVariable String channelID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException {

        ChannelConfiguration channelConfiguration = channelConfigurationRepository.findOneByChannelID(channelID);
        if (channelConfiguration == null) {
            return ResponseEntity.notFound().build();
        }

        // check if request is authorized
        String companyID = identityResolver.resolveCompanyId(bearer);
        if (isAuthorized(channelConfiguration, companyID) == false) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("Company {} requested channel with ID {}", companyID, channelID);
        return new ResponseEntity<>(channelConfiguration, HttpStatus.OK);
    }

    //--------------------------------------------------------------------------------------
    // closeChannel
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> closeChannel(
            @ApiParam(value = "channelID", required = true)
            @PathVariable String channelID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException {

        ChannelConfiguration channelConfiguration = channelConfigurationRepository.findOneByChannelID(channelID);
        if (channelConfiguration == null) {
            return ResponseEntity.notFound().build();
        }

        // check if request is authorized
        String companyID = identityResolver.resolveCompanyId(bearer);
        if (isAuthorized(channelConfiguration, companyID) == false) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        kafkaDomainClient.deleteChannel(channelConfiguration.getChannelID()); // cleanup topics
        channelConfigurationRepository.delete(channelConfiguration); // delete configuration

        logger.info("Company {} requested closing of channel with ID {}", companyID, channelID);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //--------------------------------------------------------------------------------------
    // associatedChannels
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> associatedChannels(
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws UnirestException {

        // extract ID of company
        String companyID = identityResolver.resolveCompanyId(bearer);

        // get associated channels
        Set<ChannelConfiguration> producingChannels = channelConfigurationRepository.findByProducerCompanyID(companyID);
        Set<ChannelConfiguration> consumingChannels = channelConfigurationRepository.findByConsumerCompanyIDs(companyID);
        Set<ChannelConfiguration> allChannels = Stream.concat(producingChannels.stream(), consumingChannels.stream()).collect(Collectors.toSet());

        logger.info("Company {} requested associated channels", companyID);
        return new ResponseEntity<>(allChannels, HttpStatus.OK);
    }

    //--------------------------------------------------------------------------------------
    // getChannelsForBusinessProcessService (e.g. businessProcessID = "444")
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> getChannelsForBusinessProcessService(
            @ApiParam(value = "businessProcessID", required = true)
            @PathVariable String businessProcessID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException {

        String companyID = identityResolver.resolveCompanyId(bearer); // extract ID of company
        Set<ChannelConfiguration> channels = this.channelConfigurationRepository.findByBusinessProcessID(businessProcessID);

        logger.info("Company {} requested associated channels for business process with ID {}", companyID, businessProcessID);
        return new ResponseEntity<>(channels, HttpStatus.OK);
    }

    //--------------------------------------------------------------------------------------
    // getMessagesForChannel (e.g. channelID = "8d2599f4-e990-48dd-bcff-bc11e338196c")
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> getMessagesForChannel(
            @ApiParam(value = "channelID", required = true)
            @PathVariable String channelID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException {

        ChannelConfiguration channelConfiguration = channelConfigurationRepository.findOneByChannelID(channelID);
        if (channelConfiguration == null) {
            return ResponseEntity.notFound().build();
        }

        // check if request is authorized
        String companyID = identityResolver.resolveCompanyId(bearer);
        if (isAuthorized(channelConfiguration, companyID) == false) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Object> messages = kafkaDomainClient.getMessages(channelID);

        logger.info("Company {} requested messages of channel {}", companyID, channelID);
        logger.info("Returning {} messages for channel {}", messages.size(), channelID);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    //--------------------------------------------------------------------------------------
    // getSensorsForChannel
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> getSensorsForChannel(
            @ApiParam(value = "channelID", required = true) @PathVariable String channelID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer) throws IOException, UnirestException {

        ChannelConfiguration channelConfiguration = channelConfigurationRepository.findOneByChannelID(channelID);
        if (channelConfiguration == null) {
            return ResponseEntity.notFound().build();
        }

        // check if request is authorized
        String companyID = identityResolver.resolveCompanyId(bearer);
        if (isAuthorized(channelConfiguration, companyID) == false) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        List<Sensor> sortedSensors = channelConfiguration.getAssociatedSensors().stream()
                .sorted(Comparator.comparing(Sensor::getId))
                .collect(Collectors.toList());

        logger.info("Company {} requested messages of channel {}", companyID, channelID);
        return new ResponseEntity<>(sortedSensors, HttpStatus.OK);
    }


    //--------------------------------------------------------------------------------------
    // addSensorsForChannel
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> addSensorsForChannel(
            @ApiParam(value = "channelID", required = true) @PathVariable String channelID,
            @ApiParam(value = "Sensor to be added", required = true) @RequestBody Sensor sensor,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer) throws IOException, UnirestException {

        ChannelConfiguration channelConfiguration = channelConfigurationRepository.findOneByChannelID(channelID);
        if (channelConfiguration == null) {
            return ResponseEntity.notFound().build();
        }

        // check if request is authorized
        String companyID = identityResolver.resolveCompanyId(bearer);
        if (isAuthorized(channelConfiguration, companyID) == false) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // check if machine already exists
        Machine machineToStore = machineRepository.save(sensor.getMachine());

        // store sensor
        sensor.setMachine(machineToStore);
        sensorRepository.save(sensor);

        // add sensor to channel
        Set<Sensor> configuredSensors = channelConfiguration.getAssociatedSensors();
        configuredSensors.add(sensor);
        channelConfigurationRepository.save(channelConfiguration);

        logger.info("Company {} added sensor {}", companyID, sensor.getName());
        return new ResponseEntity<>(sensor, HttpStatus.OK);
    }

    //--------------------------------------------------------------------------------------
    // removeSensorForChannel
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> removeSensorForChannel(
            @ApiParam(value = "ID of channel", required = true) @PathVariable String channelID,
            @ApiParam(value = "SensorID to be removed", required = true) @PathVariable Long sensorID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer) throws IOException, UnirestException {

        ChannelConfiguration channelConfiguration = channelConfigurationRepository.findOneByChannelID(channelID);
        if (channelConfiguration == null) {
            return ResponseEntity.notFound().build();
        }

        // check if request is authorized
        String companyID = identityResolver.resolveCompanyId(bearer);
        if (isAuthorized(channelConfiguration, companyID) == false) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // remove sensor from channel
        Set<Sensor> associatedSensors = channelConfiguration.getAssociatedSensors().stream()
                .filter( s -> !s.getId().equals(sensorID))
                .collect(Collectors.toSet());
        channelConfiguration.setAssociatedSensors(associatedSensors);
        channelConfigurationRepository.save(channelConfiguration);

        for(Sensor sensor: associatedSensors) logger.info("Company {} removed sensor {}", companyID, sensor.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //--------------------------------------------------------------------------------------
    // isAuthorized
    //--------------------------------------------------------------------------------------
    private static Boolean isAuthorized(ChannelConfiguration channelConfiguration, String companyID) {
        return channelConfiguration.getProducerCompanyID().equals(companyID)
                || channelConfiguration.getConsumerCompanyIDs().contains(companyID);
    }
}

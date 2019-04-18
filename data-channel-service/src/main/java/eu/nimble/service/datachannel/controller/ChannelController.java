package eu.nimble.service.datachannel.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import eu.nimble.common.rest.identity.IdentityResolver;
import eu.nimble.service.datachannel.entity.ChannelConfiguration;
import eu.nimble.service.datachannel.entity.Machine;
import eu.nimble.service.datachannel.entity.Sensor;
import eu.nimble.service.datachannel.entity.Filter;
import eu.nimble.service.datachannel.entity.Server;
import eu.nimble.service.datachannel.kafka.KafkaDomainClient;
import eu.nimble.service.datachannel.repository.ChannelConfigurationRepository;
import eu.nimble.service.datachannel.repository.MachineRepository;
import eu.nimble.service.datachannel.repository.SensorRepository;
import eu.nimble.service.datachannel.repository.ServerRepository;
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
 * @author Andrea Musumeci
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
    private ServerRepository serverRepository;

    @Autowired
    private MachineRepository machineRepository;

    //--------------------------------------------------------------------------------------
    // createChannel
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> createChannel(
            @ApiParam(value = "Channel configuration",  required = true)
            @RequestBody CreateChannel.Request createChannelRequest,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException {

        // check if company id matches; TBD : solve Exception with partyID
        String companyID = identityResolver.resolveCompanyId(bearer);
        if (isAuthorized(createChannelRequest, companyID) == false) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //verify if yet present / it cannot be unique because of we have to permit to decide businessProcessID also in another step then the creation (need to be verified in BPM).
        ChannelConfiguration channelConfiguration = channelConfigurationRepository.findOneByBusinessProcessID( createChannelRequest.getBusinessProcessID() );
        if (channelConfiguration != null) {
            //duplicated businessProcessID
            if (isAuthorized(channelConfiguration, companyID) == true) {
                return new ResponseEntity<>(channelConfiguration, HttpStatus.BAD_REQUEST);
            } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        // create channel configuration
        ChannelConfiguration config = new ChannelConfiguration(
                createChannelRequest.getBusinessProcessID(),
                createChannelRequest.getSellerCompanyID(),
                createChannelRequest.getBuyerCompanyID(),
                createChannelRequest.getDescription());

        config = channelConfigurationRepository.save(config);
        logger.info("Company {} opened channel ", createChannelRequest.getSellerCompanyID());
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
    // associateChannel Business Process
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> associateChannelBusinessProcessID(
            @ApiParam(value = "channelID", required = true)
            @PathVariable String channelID,
            @ApiParam(value = "businessProcessID", required = true)
            @PathVariable String businessProcessID,
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

        channelConfiguration.setBusinessProcessID(businessProcessID);
        channelConfigurationRepository.save(channelConfiguration);
        
        logger.info("Company {} updated  channel {} with businessProcessID {}", companyID, channelID, businessProcessID);
        return new ResponseEntity<>(channelConfiguration, HttpStatus.OK);
    }

    //--------------------------------------------------------------------------------------
    // startChannel
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> startChannel(
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

        //$$ set Start Date and if internal create topics
        channelConfiguration.setStartDateTime( new java.util.Date() );
        channelConfigurationRepository.save(channelConfiguration);
        // set up channel in the Kafka domain -> this will be moved to Channel.start()
        //$$KafkaDomainClient.CreateChannelResponse response = kafkaDomainClient.createChannel(config);
        //$$DcfsClient.CreateFilteredChannelResponse response = dcfsClient.createFilteredChannel(config);


        logger.info("Company {} requested starting of channel with ID {}", companyID, channelID);
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

        //$$ set End Date but not delete all
        //kafkaDomainClient.deleteChannel(channelConfiguration.getChannelID()); // cleanup topics
        //channelConfigurationRepository.delete(channelConfiguration); // delete configuration

        channelConfiguration.setEndDateTime( new java.util.Date() );
        channelConfigurationRepository.save(channelConfiguration);
        logger.info("Company {} requested closing of channel with ID {}", companyID, channelID);
        return new ResponseEntity<>(channelConfiguration, HttpStatus.OK);
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
        Set<ChannelConfiguration> producingChannels = channelConfigurationRepository.findBySellerCompanyID(companyID);
        Set<ChannelConfiguration> consumingChannels = channelConfigurationRepository.findByBuyerCompanyID(companyID);
        Set<ChannelConfiguration> allChannels = Stream.concat(producingChannels.stream(), consumingChannels.stream()).collect(Collectors.toSet());

        logger.info("Company {} requested associated channels", companyID);
        return new ResponseEntity<>(allChannels, HttpStatus.OK);
    }

    //--------------------------------------------------------------------------------------
    // getChannelForBusinessProcessService (e.g. businessProcessID = "444")
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> getChannelForBusinessProcessService(
            @ApiParam(value = "businessProcessID", required = true)
            @PathVariable String businessProcessID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException {

        String companyID = identityResolver.resolveCompanyId(bearer); // extract ID of company
        ChannelConfiguration channel = this.channelConfigurationRepository.findOneByBusinessProcessID(businessProcessID);

        logger.info("Company {} requested associated channels for business process with ID {}", companyID, businessProcessID);
        return new ResponseEntity<>(channel, HttpStatus.OK);
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
    // getServersForChannel
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> getServersForChannel(
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

        List<Server> sortedServers = channelConfiguration.getAssociatedServers().stream()
                .sorted(Comparator.comparing(Server::getPriority))
                .collect(Collectors.toList());

        logger.info("Company {} requested servers of channel {}", companyID, channelID);
        return new ResponseEntity<>(sortedServers, HttpStatus.OK);
    }


    //--------------------------------------------------------------------------------------
    // addServersForChannel
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> addServersForChannel(
            @ApiParam(value = "channelID", required = true) @PathVariable String channelID,
            @ApiParam(value = "Server to be added", required = true) @RequestBody Server server,
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

        // store server
        serverRepository.save(server);

        // add sensor to channel
        Set<Server> configuredServers = channelConfiguration.getAssociatedServers();
        configuredServers.add(server);
        channelConfigurationRepository.save(channelConfiguration);

        logger.info("Company {} added server {}", companyID, server.getName());
        return new ResponseEntity<>(server, HttpStatus.OK);
    }

    //--------------------------------------------------------------------------------------
    // removeFilterForChannel
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> removeServerForChannel(
            @ApiParam(value = "ID of channel", required = true) @PathVariable String channelID,
            @ApiParam(value = "ServerID to be removed", required = true) @PathVariable Long serverID,
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

        // remove server from channel
        Set<Server> associatedServers = channelConfiguration.getAssociatedServers().stream()
                .filter( s -> !s.getId().equals(serverID))
                .collect(Collectors.toSet());
        channelConfiguration.setAssociatedServers(associatedServers);
        channelConfigurationRepository.save(channelConfiguration);

        for(Server server: associatedServers) logger.info("Company {} removed server {}", companyID, server.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }


    //--------------------------------------------------------------------------------------
    // getFiltersForChannel
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> getFiltersForChannel(
            @ApiParam(value = "channelID", required = true) @PathVariable String channelID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer) throws IOException, UnirestException {
            return ResponseEntity.notFound().build();
    }
    //--------------------------------------------------------------------------------------
    // addFiltersForChannel
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> addFiltersForChannel(
            @ApiParam(value = "channelID", required = true) @PathVariable String channelID,
            @ApiParam(value = "Filter to be added", required = true) @RequestBody Filter filter,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer) throws IOException, UnirestException {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    //--------------------------------------------------------------------------------------
    // removeFilterForChannel
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> removeFilterForChannel(
            @ApiParam(value = "ID of channel", required = true) @PathVariable String channelID,
            @ApiParam(value = "FilterID to be removed", required = true) @PathVariable Long filterID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer) throws IOException, UnirestException {

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    //--------------------------------------------------------------------------------------
    // isAuthorized
    //--------------------------------------------------------------------------------------
    private Boolean isAuthorized(CreateChannel.Request createChannelRequest, String companyID) {
        return createChannelRequest.getBuyerCompanyID().equals(companyID)
                || createChannelRequest.getSellerCompanyID().contains(companyID);
    }

    private Boolean isAuthorized(ChannelConfiguration channelConfiguration, String companyID) {
        return channelConfiguration.getBuyerCompanyID().equals(companyID)
                || channelConfiguration.getSellerCompanyID().contains(companyID);
    }
}

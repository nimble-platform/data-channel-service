package eu.nimble.service.datachannel.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import eu.nimble.service.datachannel.entity.ChannelConfiguration;
import eu.nimble.service.datachannel.identity.IdentityClient;
import eu.nimble.service.datachannel.kafka.KafkaDomainClient;
import eu.nimble.service.datachannel.repository.ChannelConfigurationRepository;
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
import java.util.HashMap;
import java.util.Set;
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
public class ChannelController {

    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(ChannelController.class);

    @Autowired
    private IdentityClient identityClient;

    @Autowired
    private KafkaDomainClient kafkaDomainClient;

    @Autowired
    private ChannelConfigurationRepository channelConfigurationRepository;

    /**
     * See API documentation
     *
     * @param createChannelRequest Configuration, which is used for opening the channel
     * @param bearer               OpenID Connect token storing requesting identity
     * @return ResponseEntity with ID of created data channel
     * @throws IOException      Error while communicating with the SensorThings Server
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Create new channel", notes = "Creates a new channel according to the provided contract. " +
            "It is checked that the requested user stored in the " +
            "Open ID Connect token is from the producer company (i.e. only producing companies are allowed to open channels).",
            response = CreateChannel.Response.class, nickname = "createChannel")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Channel created", response = CreateChannel.Response.class),
            @ApiResponse(code = 400, message = "Error while creating channel")})
    @RequestMapping(value = "/", produces = {"application/json"}, method = RequestMethod.POST)
    ResponseEntity<?> createChannel(
            @ApiParam(value = "Channel configuration", required = true) @RequestBody CreateChannel.Request createChannelRequest,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true) @RequestHeader(value = "Authorization") String bearer) throws IOException, UnirestException {

        // check if company id matches
        String companyID = identityClient.getCompanyId(bearer);
        if (createChannelRequest.getProducerCompanyID().equals(companyID) == false)
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

        // create channel configuration
        ChannelConfiguration config = new ChannelConfiguration(createChannelRequest.getProducerCompanyID(),
                createChannelRequest.getConsumerCompanyIDs(),
                createChannelRequest.getDescription(),
                createChannelRequest.getStartDateTime(),
                createChannelRequest.getEndDateTime(),
                createChannelRequest.getBusinessProcessID(),
                "dummyProducerTopic",  // Todo: determine input topic
                new HashMap<>());  // Todo: determine output topics

        // set up channel in the Kafka domain
        String channelID = kafkaDomainClient.createChannel(config);
        config.setChannelID(channelID);

        config = channelConfigurationRepository.save(config);

        logger.info("Company {} opened channel with ID {}", companyID, config.getChannelID());

        return new ResponseEntity<>(new CreateChannel.Response(config.getChannelID()), HttpStatus.OK);
    }

    /**
     * See API documentation
     *
     * @param channelID Identifier of requested channel.
     * @param bearer    OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Get channel with id", response = ChannelConfiguration.class, nickname = "getChannel")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Channel found", response = ChannelConfiguration.class),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Channel not found"),
            @ApiResponse(code = 400, message = "Error while fetching channel")})
    @RequestMapping(value = "/{channelID}", produces = {"application/json"}, method = RequestMethod.GET)
    ResponseEntity<?> getChannel(
            @ApiParam(value = "channelID", required = true) @PathVariable String channelID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true) @RequestHeader(value = "Authorization") String bearer) throws IOException, UnirestException {

        ChannelConfiguration channelConfiguration = channelConfigurationRepository.findOneByChannelID(channelID);
        if (channelConfiguration == null)
            return ResponseEntity.notFound().build();

        // check if request is authorized
        String companyID = identityClient.getCompanyId(bearer);
        if (isAuthorized(channelConfiguration, companyID) == false)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        logger.info("Company {} requested channel with ID {}", companyID, channelID);

        return new ResponseEntity<>(channelConfiguration, HttpStatus.OK);
    }

    /**
     * See API documentation
     *
     * @param channelID Identifier of channel to be closed.
     * @param bearer    OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Close channel with id", nickname = "closeChannel")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Channel closed"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Channel not found"),
            @ApiResponse(code = 400, message = "Error while closing channel")})
    @RequestMapping(value = "/{channelID}", method = RequestMethod.DELETE)
    ResponseEntity<?> closeChannel(
            @ApiParam(value = "channelID", required = true) @PathVariable String channelID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true) @RequestHeader(value = "Authorization") String bearer) throws IOException, UnirestException {

        ChannelConfiguration channelConfiguration = channelConfigurationRepository.findOneByChannelID(channelID);
        if (channelConfiguration == null)
            return ResponseEntity.notFound().build();

        // check if request is authorized
        String companyID = identityClient.getCompanyId(bearer);
        if (isAuthorized(channelConfiguration, companyID) == false)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        logger.info("Company {} requested closing of channel with ID {}", companyID, channelID);

        // cleanup topics
        kafkaDomainClient.deleteChannel(channelConfiguration.getChannelID());

        // delete configuration
        channelConfigurationRepository.delete(channelConfiguration);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * See API documentation
     *
     * @param bearer OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Get all associated channels", nickname = "getAllChannels",
            response = ChannelConfiguration.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Channels found", responseContainer = "List"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 400, message = "Error while fetching channels")})
    @RequestMapping(value = "/all", produces = {"application/json"}, method = RequestMethod.GET)
    ResponseEntity<?> associatedChannels(
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true) @RequestHeader(value = "Authorization") String bearer) throws IOException, UnirestException {

        // extract ID of company
        String companyID = identityClient.getCompanyId(bearer);

        // get associated channels
        Set<ChannelConfiguration> producingChannels = channelConfigurationRepository.findByProducerCompanyID(companyID);
        Set<ChannelConfiguration> consumingChannels = channelConfigurationRepository.findByConsumerCompanyIDs(companyID);
        Set<ChannelConfiguration> allChannels = Stream.concat(producingChannels.stream(), consumingChannels.stream()).collect(Collectors.toSet());

        logger.info("Company {} requested associated channels", companyID);

        return new ResponseEntity<>(allChannels, HttpStatus.OK);
    }

    @ApiOperation(value = "Get channels associated with business process", response = ChannelConfiguration.class,
            notes = "Returns empty list if no channel was found", nickname = "getChannelsForBusinessProcess", responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Channel found", response = ChannelConfiguration.class, responseContainer = "List"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 404, message = "Channel not found"),
            @ApiResponse(code = 400, message = "Error while fetching channel")})
    @RequestMapping(value = "/business-process/{businessProcessID}", produces = {"application/json"}, method = RequestMethod.GET)
    ResponseEntity<?> getChannelForBusinessProcess(
            @ApiParam(value = "businessProcessID", required = true) @PathVariable String businessProcessID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer) throws IOException, UnirestException {

        // query all relevant channels
        Set<ChannelConfiguration> channels = channelConfigurationRepository.findByBusinessProcessID(businessProcessID);

        // check if requester is authorized
        String companyID = identityClient.getCompanyId(bearer);
        if (channels.stream().allMatch(channelConfiguration -> isAuthorized(channelConfiguration, companyID)) == false) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("Company {} requested channel for business process with ID {}", companyID, businessProcessID);

        return new ResponseEntity<>(channels, HttpStatus.OK);
    }

    private static Boolean isAuthorized(ChannelConfiguration channelConfiguration, String companyID) {
        return channelConfiguration.getProducerCompanyID().equals(companyID)
                || channelConfiguration.getConsumerCompanyIDs().contains(companyID);
    }
}

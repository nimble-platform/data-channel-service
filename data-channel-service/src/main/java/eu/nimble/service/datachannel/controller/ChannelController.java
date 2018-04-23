package eu.nimble.service.datachannel.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import eu.nimble.service.datachannel.entity.ChannelContract;
import eu.nimble.service.datachannel.entity.CompanyChannels;
import eu.nimble.service.datachannel.identity.IdentityClient;
import eu.nimble.service.datachannel.sensorthings.SensorThingsContracts;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * REST Controller for endpoints for managing data channels.
 *
 * @author Johannes Innerbichler
 */
@Controller
@RequestMapping(path = "/channel")
@Api("Data Channel API")
public class ChannelController {

    private static Logger logger = LoggerFactory.getLogger(ChannelController.class);

    @Autowired
    private SensorThingsContracts sensorThingsContracts;

    @Autowired
    private IdentityClient identityClient;

    /**
     * See API documentation
     *
     * @param channelContract Contract, which is used for opening the channel
     * @param bearer          OpenID Connect token storing requesting identity
     * @return ResponseEntity with proper channel definition
     * @throws ServiceFailureException Error while communication with the Identity Service
     * @throws IOException             Error while communicating with the SensorThings Server
     * @throws UnirestException        Error while communication with the Identity Service
     */
    @ApiOperation(value = "Create new channel", notes = "Creates a new channel according to the provide contract. " +
            "It is checked that the requested user stored on the " +
            "Open ID Connect token is from the producer company (i.e. only producing companies can open channels). " +
            "The channel is stored in an SensorThings (see http://www.opengeospatial.org/standards/sensorthings) Thing object " +
            "(de.fraunhofer.iosb.ilt.sta.model.Thing), whereas the original contract " +
            "(eu.nimble.service.datachannel.entity.ChannelContract) is stored in the properties field under the key 'contract'.",
            response = Thing.class, nickname = "createChannel")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Channel created", response = Thing.class),
            @ApiResponse(code = 400, message = "Error while creating channel")})
    @RequestMapping(value = "/", produces = {"application/json"}, method = RequestMethod.POST)
    ResponseEntity<?> createChannel(
            @ApiParam(value = "Channel configuration", required = true) @RequestBody ChannelContract channelContract,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true) @RequestHeader(value = "Authorization") String bearer) throws ServiceFailureException, IOException, UnirestException {

        // check if company id matches
        String companyId = identityClient.getCompanyId(bearer);
        if (channelContract.getProducerCompanyID().equals(companyId) == false)
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

        Thing createdThing = sensorThingsContracts.createThingFromContract(channelContract);

        return new ResponseEntity<>(createdThing, HttpStatus.OK);
    }

    /**
     * See API documentation
     *
     * @param channelID Identifier of requested channel.
     * @param bearer    OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws ServiceFailureException Error while communication with the Identity Service
     * @throws IOException             Error while communicating with the SensorThings Server
     * @throws UnirestException        Error while communication with the Identity Service
     */
    @ApiOperation(value = "Get channel with id", notes = "The channel is stored in an SensorThings (see http://www.opengeospatial.org/standards/sensorthings) Thing object " +
            "(de.fraunhofer.iosb.ilt.sta.model.Thing), whereas the original contract " +
            "(eu.nimble.service.datachannel.entity.ChannelContract) is stored in the properties field under the key 'contract'.",
            response = Thing.class, nickname = "getChannel")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Channel found", response = Thing.class),
            @ApiResponse(code = 404, message = "Channel not found"),
            @ApiResponse(code = 400, message = "Error while fetching channel")})
    @RequestMapping(value = "/{channelID}", produces = {"application/json"}, method = RequestMethod.GET)
    ResponseEntity<?> getChannel(
            @ApiParam(value = "channelID", required = true) @PathVariable Long channelID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true) @RequestHeader(value = "Authorization") String bearer) throws IOException, UnirestException, ServiceFailureException {

        // get contract of companies
        String companyId = identityClient.getCompanyId(bearer);
        Set<Thing> thingsOfCompany = sensorThingsContracts.producerThingsForCompany(companyId);
        thingsOfCompany.addAll(sensorThingsContracts.consumerThingsForCompany(companyId));

        // check whether user is allowed to get channel
        Set<String> contractIDs = thingsOfCompany.stream().map(thing -> thing.getId().toString()).collect(Collectors.toSet());
        if (contractIDs.contains(String.valueOf(channelID)) == false)
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);

        Thing thing = null;
        try {
            thing = sensorThingsContracts.findThing(channelID);
        } catch (ServiceFailureException e) {
            logger.error("Error while fetching thing with ID {}", channelID);
        }

        if (thing == null)
            return ResponseEntity.notFound().build();

        return new ResponseEntity<>(thing, HttpStatus.OK);
    }

    /**
     * See API documentation
     *
     * @param bearer OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws ServiceFailureException Error while communication with the Identity Service
     * @throws IOException             Error while communicating with the SensorThings Server
     * @throws UnirestException        Error while communication with the Identity Service
     */
    @ApiOperation(value = "Get channels for company", notes = "Gets all channels (producer and consumer) associated with a company, whereas the " +
            "company is extracted from the identity stored in the OpenID Connect token.",
            response = CompanyChannels.class, nickname = "getChannelForCompany")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Channels found", response = CompanyChannels.class),
            @ApiResponse(code = 400, message = "Error while fetching channels")})
    @RequestMapping(value = "/company", produces = {"application/json"}, method = RequestMethod.GET)
    ResponseEntity<?> getChannelsForCompany(
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true) @RequestHeader(value = "Authorization") String bearer) throws ServiceFailureException, IOException, UnirestException {

        // ToDo: check if proper accesstoken (e.g. check role)

        // request company id
        String companyId = identityClient.getCompanyId(bearer);

        // get producer channels
        Set<Thing> producerThings = sensorThingsContracts.producerThingsForCompany(companyId);

        // get consumer channels
        Set<Thing> consumingThings = sensorThingsContracts.consumerThingsForCompany(companyId);

        return new ResponseEntity<>(new CompanyChannels(producerThings, consumingThings), HttpStatus.OK);
    }
}

package eu.nimble.service.datachannel.controller;

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


@Controller
@RequestMapping(path = "/channel")
@Api("Data Channel API")
public class ChannelController {

    private static Logger logger = LoggerFactory.getLogger(ChannelController.class);

    @Autowired
    private SensorThingsContracts sensorThingsContracts;

    @Autowired
    private  IdentityClient identityClient;

    @ApiOperation(value = "Create new channel", response = Thing.class, nickname = "createChannel")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Channel created", response = Thing.class),
            @ApiResponse(code = 400, message = "Error while creating channel")})
    @RequestMapping(value = "/", produces = {"application/json"}, method = RequestMethod.POST)
    ResponseEntity<Thing> createChannel(
            @ApiParam(value = "Channel configuration", required = true) @RequestBody ChannelContract channelContract) throws ServiceFailureException {

        Thing createdThing = sensorThingsContracts.createThingFromContract(channelContract);

        return new ResponseEntity<>(createdThing, HttpStatus.OK);
    }

    @ApiOperation(value = "Get channel with id", response = Thing.class, nickname = "getChannel")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Channel found", response = Thing.class),
            @ApiResponse(code = 404, message = "Channel not found"),
            @ApiResponse(code = 400, message = "Error while fetching channel")})
    @RequestMapping(value = "/{channelID}", produces = {"application/json"}, method = RequestMethod.GET)
    ResponseEntity<?> getChannel(
            @ApiParam(value = "channelID", required = true) @PathVariable Long channelID,
            @RequestHeader(value = "Authorization") String bearer) {

        // ToDo: check if proper accesstoken (related company is producer or consumer)

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

    @ApiOperation(value = "Get channels for company", notes = "Company is extracted from AccessToken",
            response = CompanyChannels.class, nickname = "getChannelForCompany")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Channels found", response = CompanyChannels.class),
            @ApiResponse(code = 400, message = "Error while fetching channels")})
    @RequestMapping(value = "/company", produces = {"application/json"}, method = RequestMethod.GET)
    ResponseEntity<?> getChannelForCompany(@RequestHeader(value = "Authorization") String bearer) throws ServiceFailureException, IOException {

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

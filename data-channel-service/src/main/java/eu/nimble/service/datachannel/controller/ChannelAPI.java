package eu.nimble.service.datachannel.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import eu.nimble.service.datachannel.entity.ChannelConfiguration;
import eu.nimble.service.datachannel.entity.Sensor;
import eu.nimble.service.datachannel.entity.Server;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import request.CreateChannel;

import java.io.IOException;

@Api(value = "channel", description = "the channel API")
public interface ChannelAPI {


    /**
     * See API documentation
     *
     * @param createChannelRequest Configuration, which is used for opening the channel
     * @param bearer               OpenID Connect token storing requesting identity
     * @return ResponseEntity with ID of created data channel
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Create new channel", notes = "Creates a new channel according to the provided contract. " +
            "It is checked that the requested user stored in the " +
            "Open ID Connect token is from the producer company (i.e. only producing companies are allowed to open channels).",
            response = CreateChannel.Response.class, nickname = "createChannel")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Channel created", response = CreateChannel.Response.class),
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Error while creating channel/duplicate businessProcessID"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found") })
    @RequestMapping(value = "/", produces = {"application/json"}, method = RequestMethod.POST)
    ResponseEntity<?> createChannel(
            @ApiParam(value = "Channel configuration", required = true)
            @RequestBody CreateChannel.Request createChannelRequest,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException;


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
            @ApiResponse(code = 400, message = "Error while fetching channel"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Channel not found") })
    @RequestMapping(value = "/{channelID}", produces = {"application/json"}, method = RequestMethod.GET)
    ResponseEntity<?> getChannel(
            @ApiParam(value = "channelID", required = true)
            @PathVariable String channelID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException;


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
            @ApiResponse(code = 200, message = "Channel closed", response = Object.class),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 400, message = "Error while closing channel"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Channel not found") })
    @RequestMapping(value = "/{channelID}/close", method = RequestMethod.POST)
    ResponseEntity<?> closeChannel(
            @ApiParam(value = "channelID", required = true)
            @PathVariable String channelID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException;

     /**
     * See API documentation
     *
     * @param channelID Identifier of channel to be closed.
     * @param bearer    OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Start channel with id", nickname = "startChannel")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Channel started", response = Object.class),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 400, message = "Error while closing channel"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Channel not found") })
    @RequestMapping(value = "/{channelID}/start", method = RequestMethod.POST)
    ResponseEntity<?> startChannel(
            @ApiParam(value = "channelID", required = true)
            @PathVariable String channelID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException;


    /**
     * See API documentation
     *
     * @param bearer OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Get all associated channels with a company", nickname = "getAllChannels",
            response = ChannelConfiguration.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Channels found", responseContainer = "List"),
            @ApiResponse(code = 400, message = "Error while closing channel"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not found") })
    @RequestMapping(value = "/all", produces = {"application/json"}, method = RequestMethod.GET)
    ResponseEntity<?> associatedChannels(
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws UnirestException;


    /**
     * See API documentation
     *
     * @param bearer OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Get associated channel for a business process", nickname = "getChannelForBusinessProcessService",
            response = ChannelConfiguration.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Channel found", response = ChannelConfiguration.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Error while fetching channel"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found") })
    @RequestMapping(value = "/business-process/{businessProcessID}", produces = {"application/json"}, method = RequestMethod.GET)
    ResponseEntity<?> getChannelForBusinessProcessService(
            @ApiParam(value = "businessProcessID", required = true)
            @PathVariable String businessProcessID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException;


    /**
     * See API documentation
     *
     * @param channelID Identifier of requested channel.
     * @param bearer    OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Get sensors of channel.", response = Sensor.class,
            notes = "Returns list of sensors sorted by ID", nickname = "getSensorsForChannel", responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Channel found", response = Sensor.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Error while fetching channel"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Channel not found") })
    @RequestMapping(value = "/{channelID}/sensors", produces = {"application/json"}, method = RequestMethod.GET)
    ResponseEntity<?> getSensorsForChannel(
            @ApiParam(value = "channelID", required = true)
            @PathVariable String channelID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException;

    /**
     * See API documentation
     *
     * @param channelID Identifier of requested channel.
     * @param bearer    OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Get servers of channel.", response = Server.class,
            notes = "Returns list of servers sorted by priority asc", nickname = "getServersForChannel", responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Channel found", response = Server.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Error while fetching channel"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Channel not found") })
    @RequestMapping(value = "/{channelID}/servers", produces = {"application/json"}, method = RequestMethod.GET)
    ResponseEntity<?> getServersForChannel(
            @ApiParam(value = "channelID", required = true)
            @PathVariable String channelID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException;


    /**
     * See API documentation
     *
     * @param channelID Identifier of requested channel.
     * @param sensor    sensor(s) to be added
     * @param bearer    OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Add sensor to channel.", response = Sensor.class,
            notes = "Add a sensor to a channel", nickname = "getSensorsForChannel")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sensor added", response = Sensor.class),
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Error while fetching channel"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Channel not found"),
            @ApiResponse(code = 409, message = "Sensor already exists") })
    @RequestMapping(value = "/{channelID}/sensor", produces = {"application/json"}, method = RequestMethod.POST)
    ResponseEntity<?> addSensorForChannel(
            @ApiParam(value = "channelID", required = true)
            @PathVariable String channelID,
            @ApiParam(value = "Sensor to be added", required = true)
            @RequestBody Sensor sensor,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException;


    /**
     * See API documentation
     *
     * @param channelID Identifier of requested channel.
     * @param server    server(s) to be added
     * @param bearer    OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Add server to channel.", response = Server.class,
            notes = "Add a server to a channel", nickname = "getServersForChannel")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Server added", response = Sensor.class),
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Error while fetching channel"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Channel not found"),
            @ApiResponse(code = 409, message = "Server already exists")
    })
    @RequestMapping(value = "/{channelID}/server", produces = {"application/json"}, method = RequestMethod.POST)
    ResponseEntity<?> addServerForChannel(
            @ApiParam(value = "channelID", required = true)
            @PathVariable String channelID,
            @ApiParam(value = "Server to be added", required = true)
            @RequestBody Server server,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException;


    /**
     * See API documentation
     *
     * @param channelID Identifier of requested channel.
     * @param sensorID  ID of sensor to be removed
     * @param bearer    OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Remove sensor from channel.",
            notes = "Remove a sensor from a channel", nickname = "removeSensorsForChannel")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sensor removed", response = Object.class),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 400, message = "Error while fetching channel"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Sensor or channel not found") })
    @RequestMapping(value = "/{channelID}/sensors/{sensorID}", method = RequestMethod.DELETE)
    ResponseEntity<?> removeSensorForChannel(
            @ApiParam(value = "ID of channel", required = true)
            @PathVariable String channelID,
            @ApiParam(value = "SensorID to be removed", required = true)
            @PathVariable Long sensorID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException;

    /**
     * See API documentation
     *
     * @param channelID Identifier of requested channel.
     * @param serverID  ID of server to be removed
     * @param bearer    OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Remove server from channel.",
            notes = "Remove a server from a channel", nickname = "removeServerForChannel")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Server removed", response = Object.class),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 400, message = "Error while fetching channel"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Server or channel not found") })
    @RequestMapping(value = "/{channelID}/server/{serverID}", method = RequestMethod.DELETE)
    ResponseEntity<?> removeServerForChannel(
            @ApiParam(value = "ID of channel", required = true)
            @PathVariable String channelID,
            @ApiParam(value = "ServerID to be removed", required = true)
            @PathVariable Long serverID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException;

 
    //--------------------------------------------------------------------------------------
    // hasInternalService
    //--------------------------------------------------------------------------------------
    /**
     * See API documentation
     *
     * @param bearer    OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Control if this Nimble Instance has internal server avaiable",
            notes = "Control if this Nimble Instance has internal server avaiable", nickname = "hasInternalService")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "", response = Boolean.class),
            @ApiResponse(code = 404, message = "Service not found", response = Boolean.class)
            } )
    @RequestMapping(value = "/hasInternalService", method = RequestMethod.GET)
    ResponseEntity<?> hasInternalService (
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException;

    //--------------------------------------------------------------------------------------
    // hasFilteringService
    //--------------------------------------------------------------------------------------
    @ApiOperation(value = "Control if this Nimble Instance has filtering server avaiable",
            notes = "Control if this Nimble Instance has filtering server avaiable", nickname = "hasFilteringService")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "", response = Boolean.class),
            @ApiResponse(code = 404, message = "Service not found", response = Boolean.class)
            } )
    @RequestMapping(value = "/hasFilteringService", method = RequestMethod.GET)
    ResponseEntity<?> hasFilteringService (
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException;

    //--------------------------------------------------------------------------------------
    // getFilteringServiceUrl
    //--------------------------------------------------------------------------------------
    @ApiOperation(value = "get filtering server url",
            notes = "get filtering server url", nickname = "getFilteringServiceUrl")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "", response = String.class),
            @ApiResponse(code = 404, message = "Service not found")
            } )
    @RequestMapping(value = "/getFilteringServiceUrl", method = RequestMethod.GET)
    ResponseEntity<?> getFilteringServiceUrl (
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException;

    //--------------------------------------------------------------------------------------
    // getProducerServiceUrl
    //--------------------------------------------------------------------------------------
    @ApiOperation(value = "get producer server url",
            notes = "get producer server url", nickname = "getProducerServiceUrl")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "", response = String.class),
            @ApiResponse(code = 404, message = "Service not found")
            } )
    @RequestMapping(value = "/getProducerServiceUrl", method = RequestMethod.GET)
    ResponseEntity<?> getProducerServiceUrl (
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws IOException, UnirestException;

}
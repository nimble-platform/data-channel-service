package eu.nimble.service.datachannel.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import eu.nimble.service.datachannel.entity.ERPData.SensorValue;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Api(value = "ERP", description = "ERP integration API")
public interface ErpAPI {

    /**
     * See API documentation
     *
     * @param bearer    OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Get all registered negotiation channels", nickname = "getAllActiveChannels")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Received all Nimble Channel IDs", response = Object.class),
            @ApiResponse(code = 400, message = "Error while getting Nimble Channel IDs"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found") })
    @RequestMapping(value = "/getAllActiveChannels", produces = {"application/json"}, method = RequestMethod.GET)
    ResponseEntity<?> getAllActiveChannels(
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws UnirestException;

    /**
     * See API documentation
     *
     * @param channelID  Nimble Channel ID
     * @param bearer   OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Get config data for channel negotiation", nickname = "getConfigDataForChannel")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Received specific Nimble Channel Data", response = Object.class),
            @ApiResponse(code = 400, message = "Error while getting Nimble Channel Data"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found") })
    @RequestMapping(value = "/getConfigData/{channelID}", produces = {"application/json"}, method = RequestMethod.GET)
    ResponseEntity<?> getConfigData(
            @ApiParam(value = "channelID", required = true)
            @PathVariable String channelID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws UnirestException;

    /**
     * See API documentation
     *
     * @param sensorValueMap  sensor data object
     * @param channelID  Nimble Channel ID
     * @param bearer   OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Produce sensor data for channel negotiation", nickname = "produceSensorData")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sensor Data produced", response = Object.class),
            @ApiResponse(code = 400, message = "Error while producing sensor data"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found") })
    @RequestMapping(value = "/produceSensorData/{channelID}", consumes = {"application/json"}, method = RequestMethod.POST)
    ResponseEntity<?> produceSensorData(
            @ApiParam(value = "erp data", required = true)
            @RequestBody Map<String, SensorValue> sensorValueMap,
            @ApiParam(value = "channelID", required = true)
            @PathVariable String channelID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws UnirestException;

    /**
     * See API documentation
     *
     * @param channelID  Nimble Channel ID
     * @param bearer   OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Consume sensor data for channel negotiation", nickname = "consumeSensorData", response = Map.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Sensor Data consumed", response = Object.class),
            @ApiResponse(code = 400, message = "Error while consuming sensor data"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found") })
    @RequestMapping(value = "/consumeSensorData/{channelID}", consumes = {"application/json"}, method = RequestMethod.GET)
    ResponseEntity<?> consumeSensorData(
            @ApiParam(value = "channelID", required = true)
            @PathVariable String channelID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws UnirestException;
}

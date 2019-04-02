package eu.nimble.service.datachannel.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import eu.nimble.service.datachannel.entity.tracing.EpcCodes;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "epc", description = "the epc API")
public interface EpcAPI {

    /**
     * See API documentation
     *
     * @param epcCodes  codes to register
     * @param bearer    OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Register EPC codes for an order.", nickname = "registerEpcCodes")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "EPC codes registered", response = Object.class),
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Error while registering the codes"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found") })
    @RequestMapping(value = "/", consumes = {"application/json"}, method = RequestMethod.POST)
    ResponseEntity<?> registerEpcCodes(
            @ApiParam(value = "Order Id with EPC codes", required = true)
            @RequestBody EpcCodes epcCodes,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws UnirestException;

    /**
     * See API documentation
     *
     * @param orderId   Identifier of a NIMBLE order
     * @param bearer    OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Get EPC codes for an order.", nickname = "getEpcCodes", response = EpcCodes.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "EPC codes found", response = EpcCodes.class),
            @ApiResponse(code = 400, message = "Error while querying the codes"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found") })
    @RequestMapping(value = "/{orderId}", method = RequestMethod.GET)
    ResponseEntity<?> getEpcCodes(
            @ApiParam(value = "orderId", required = true)
            @PathVariable String orderId,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws UnirestException;

    /**
     * See API documentation
     *
     * @param orderIds  order ids -> get epc codes
     * @param bearer    OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Get EPC codes for a list of orders.", nickname = "getEpcCodesByOrderIds", responseContainer = "List", response = EpcCodes.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "EPC codes found"),
            @ApiResponse(code = 400, message = "Error while querying the codes"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found") })
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<?> getMultipleEpcCodes(
            @ApiParam(value = "orders", required = true)
            @RequestParam("orders") List<String> orderIds,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws UnirestException;

    /**
     * See API documentation
     *
     * @param code    code to find epc
     * @param bearer  OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Get EPC objects for a specific code.", nickname = "getEpcCodesByCode", responseContainer = "Set", response = EpcCodes.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "EPC codes found"),
            @ApiResponse(code = 400, message = "Error while querying the codes"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden"),
            @ApiResponse(code = 404, message = "Not Found") })
    @RequestMapping(value = "/code/{code:.+}", method = RequestMethod.GET)
    ResponseEntity<?> getMultipleEpcCodesByCode(
            @ApiParam(value = "code", required = true)
            @PathVariable String code,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws UnirestException;

    /**
     * See API documentation
     *
     * @param epcCodes  Identifier(s) for removing epc code
     * @param bearer    OpenID Connect token storing requesting identity
     * @return See API documentation
     * @throws UnirestException Error while communication with the Identity Service
     */
    @ApiOperation(value = "Delete EPC codes for an order and returns updated object.", nickname = "deleteEpcCodes", response = EpcCodes.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "EPC codes deleted", response = EpcCodes.class),
            @ApiResponse(code = 204, message = "No Content"),
            @ApiResponse(code = 400, message = "Error while deleting the codes"),
            @ApiResponse(code = 401, message = "Unauthorized"),
            @ApiResponse(code = 403, message = "Forbidden") })
    @RequestMapping(value = "/", consumes = {"application/json"}, method = RequestMethod.DELETE)
    ResponseEntity<?> deleteEpcCodes(
            @ApiParam(value = "EPC codes object", required = true)
            @RequestBody EpcCodes epcCodes,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer)
            throws UnirestException;

}

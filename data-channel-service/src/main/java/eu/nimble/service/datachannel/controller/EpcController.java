package eu.nimble.service.datachannel.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import eu.nimble.service.datachannel.entity.ChannelConfiguration;
import eu.nimble.service.datachannel.entity.tracing.EpcCodes;
import eu.nimble.service.datachannel.identity.IdentityClient;
import eu.nimble.service.datachannel.kafka.KafkaDomainClient;
import eu.nimble.service.datachannel.repository.EpcCodesRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import request.CreateChannel;

import java.io.IOException;
import java.util.Set;

/**
 * REST Controller for EPC codes for an order.
 *
 * @author Johannes Innerbichler
 */
@Controller
@RequestMapping(path = "/epc")
@Api("EPC Code API")
public class EpcController {

    @Autowired
    private EpcCodesRepository epcCodesRepository;

    @Autowired
    private IdentityClient identityClient;

    @ApiOperation(value = "Register EPC codes for an order.", nickname = "registerEPCCodes")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "EPC codes registered"),
            @ApiResponse(code = 400, message = "Error while registering the codes")})
    @RequestMapping(value = "/", consumes = {"application/json"}, method = RequestMethod.POST)
    ResponseEntity<?> registerEPCCodes(
            @ApiParam(value = "Order Id with EPC codes", required = true) @RequestBody EpcCodes epcCodes,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true) @RequestHeader(value = "Authorization") String bearer) throws IOException, UnirestException {

        // check if company id matches
        // ToDo: verify access token and company
        identityClient.getCompanyId(bearer);

        // query existing or create new entity
        if (epcCodesRepository.findOneByOrderId(epcCodes.getOrderId()) != null) {
            Set<String> newCodes = epcCodes.getCodes();
            epcCodes = epcCodesRepository.findOneByOrderId(epcCodes.getOrderId());
            epcCodes.setCodes(newCodes);
        }

        epcCodes = epcCodesRepository.save(epcCodes);

        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Get EPC codes for an order.", nickname = "getEPCCodes", response = EpcCodes.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "EPC codes found"),
            @ApiResponse(code = 400, message = "Error while querying the codes")})
    @RequestMapping(value = "/{orderId}", consumes = {"application/json"}, method = RequestMethod.GET)
    ResponseEntity<?> getEPCCodes(
                    @ApiParam(value = "orderId", required = true) @PathVariable String orderId,
                    @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true) @RequestHeader(value = "Authorization") String bearer) throws IOException, UnirestException {

        // check if company id matches
        // ToDo: verify access token and company
        identityClient.getCompanyId(bearer);

        EpcCodes epcCodes = epcCodesRepository.findOneByOrderId(orderId);
        if (epcCodes == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(epcCodes);
    }

    @ApiOperation(value = "Delete EPC codes for an order and returns updated object.", nickname = "deleteEPCCodes", response = EpcCodes.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "EPC codes deleted"),
            @ApiResponse(code = 400, message = "Error while deleting the codes")})
    @RequestMapping(value = "/", consumes = {"application/json"}, method = RequestMethod.DELETE)
    ResponseEntity<?> deleteEPCCodes(
            @ApiParam(value = "EPC codes object", required = true) @RequestBody EpcCodes epcCodes,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true) @RequestHeader(value = "Authorization") String bearer) throws IOException, UnirestException {

        // check if company id matches
        // ToDo: verify access token and company
        identityClient.getCompanyId(bearer);

        // fetch existing codes
        Set<String> toBeDeleted = epcCodes.getCodes();
        epcCodes = epcCodesRepository.findOneByOrderId(epcCodes.getOrderId());
        if (epcCodes == null)
            return ResponseEntity.notFound().build();

        // update codes
        epcCodes.getCodes().removeAll(toBeDeleted);
        epcCodesRepository.save(epcCodes);

        return ResponseEntity.ok(epcCodes);
    }
}

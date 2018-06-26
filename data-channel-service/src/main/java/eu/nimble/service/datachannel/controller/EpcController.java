package eu.nimble.service.datachannel.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import eu.nimble.service.datachannel.entity.tracing.EpcCodes;
import eu.nimble.service.datachannel.identity.IdentityClient;
import eu.nimble.service.datachannel.repository.EpcCodesRepository;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    private static Logger logger = LoggerFactory.getLogger(EpcController.class);

    @Autowired
    private EpcCodesRepository epcCodesRepository;

    @Autowired
    private IdentityClient identityClient;

    @ApiOperation(value = "Register EPC codes for an order.", nickname = "registerEpcCodes")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "EPC codes registered"),
            @ApiResponse(code = 400, message = "Error while registering the codes")})
    @RequestMapping(value = "/", consumes = {"application/json"}, method = RequestMethod.POST)
    ResponseEntity<?> registerEpcCodes(
            @ApiParam(value = "Order Id with EPC codes", required = true) @RequestBody EpcCodes epcCodes,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true) @RequestHeader(value = "Authorization") String bearer) throws UnirestException {

        // check if company id matches
        // ToDo: verify access token and company
        identityClient.getCompanyId(bearer);

        // query existing or create new entity
        if (epcCodesRepository.findOneByOrderId(epcCodes.getOrderId()) != null) {
            Set<String> newCodes = epcCodes.getCodes();
            epcCodes = epcCodesRepository.findOneByOrderId(epcCodes.getOrderId());
            epcCodes.setCodes(newCodes);
        }

        logger.info("Registering EPC codes for order {}", epcCodes.getOrderId());

        epcCodesRepository.save(epcCodes);

        return ResponseEntity.ok().build();
    }

    @ApiOperation(value = "Get EPC codes for an order.", nickname = "getEpcCodes", response = EpcCodes.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "EPC codes found"),
            @ApiResponse(code = 400, message = "Error while querying the codes")})
    @RequestMapping(value = "/{orderId}", method = RequestMethod.GET)
    ResponseEntity<?> getEpcCodes(
                    @ApiParam(value = "orderId", required = true) @PathVariable String orderId,
                    @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true) @RequestHeader(value = "Authorization") String bearer) throws UnirestException {

        // check if company id matches
        // ToDo: verify access token and company
        identityClient.getCompanyId(bearer);

        EpcCodes epcCodes = epcCodesRepository.findOneByOrderId(orderId);
        if (epcCodes == null)
            return ResponseEntity.notFound().build();

        logger.info("Returning EPC codes for order {}", epcCodes.getOrderId());

        return ResponseEntity.ok(epcCodes);
    }

    @ApiOperation(value = "Get EPC codes for a list of orders.", nickname = "getEpcCodesByOrderIds", responseContainer = "List", response = EpcCodes.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "EPC codes found"),
            @ApiResponse(code = 400, message = "Error while querying the codes")})
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseEntity<?> getMultipleEpcCodes(
            @ApiParam(value = "orders", required = true) @RequestParam("orders") List<String> orderIds,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
                @RequestHeader(value = "Authorization") String bearer) throws UnirestException {

        // check if company id matches
        // ToDo: verify access token and company
        identityClient.getCompanyId(bearer);

        List<EpcCodes> epcCodes = epcCodesRepository.findByOrderIdIn(orderIds);

        logger.info("Returning EPC codes for orders {}", orderIds);

        return ResponseEntity.ok(epcCodes);
    }

    @ApiOperation(value = "Get EPC objects for a specific code.", nickname = "getEpcCodesByCode", responseContainer = "Set", response = EpcCodes.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "EPC codes found"),
            @ApiResponse(code = 400, message = "Error while querying the codes")})
    @RequestMapping(value = "/code/{code}", method = RequestMethod.GET)
    ResponseEntity<?> getMultipleEpcCodesByCode(
            @ApiParam(value = "code", required = true) @PathVariable String code,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer) throws UnirestException {

        // check if company id matches
        // ToDo: verify access token and company
        identityClient.getCompanyId(bearer);

        Set<EpcCodes> epcCodes = epcCodesRepository.findByCodes(code);

        logger.info("Returning EPC codes for code {}", code);

        return ResponseEntity.ok(epcCodes);
    }

    @ApiOperation(value = "Delete EPC codes for an order and returns updated object.", nickname = "deleteEpcCodes", response = EpcCodes.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "EPC codes deleted"),
            @ApiResponse(code = 400, message = "Error while deleting the codes")})
    @RequestMapping(value = "/", consumes = {"application/json"}, method = RequestMethod.DELETE)
    ResponseEntity<?> deleteEpcCodes(
            @ApiParam(value = "EPC codes object", required = true) @RequestBody EpcCodes epcCodes,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true) @RequestHeader(value = "Authorization") String bearer) throws UnirestException {

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

        logger.info("Removing EPC codes for order {}", epcCodes.getOrderId());

        return ResponseEntity.ok(epcCodes);
    }
}

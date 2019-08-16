package eu.nimble.service.datachannel.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import eu.nimble.common.rest.identity.IdentityResolver;
import eu.nimble.service.datachannel.entity.ChannelConfiguration;
import eu.nimble.service.datachannel.entity.ERPData.SensorValue;
import eu.nimble.service.datachannel.entity.Server;
import eu.nimble.service.datachannel.repository.ChannelConfigurationRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * REST Controller for managing calls from ERP.
 *
 * @author Mathias Schmoigl
 */
@Controller
@RequestMapping(path = "/erp")
@Api("ERP Integration API")
public class ErpController implements ErpAPI {

    @Autowired
    private IdentityResolver identityResolver;

    @Autowired
    private ChannelConfigurationRepository channelConfigurationRepository;

    //--------------------------------------------------------------------------------------
    // getAllActiveChannels
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> getAllActiveChannels(
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer) throws UnirestException {

        // extract ID of company
        String companyID = identityResolver.resolveCompanyId(bearer);

        // get associated channels
        Set<ChannelConfiguration> sellerChannels = channelConfigurationRepository.findBySellerCompanyID(companyID);
        Set<ChannelConfiguration> buyerChannels = channelConfigurationRepository.findByBuyerCompanyID(companyID);
        Map<String, String> resultChannels = new HashMap<String, String>();

        // return if no channels found
        if (sellerChannels == null || buyerChannels == null) {
            return ResponseEntity.notFound().build();
        }

        // search and group sellers
        for (ChannelConfiguration config : sellerChannels) {
            if (config.isOnLastPage() && config.isChannelOpened()) {
                resultChannels.put("seller", config.getChannelID());
            }
        }

        // search and group buyers
        for (ChannelConfiguration config : buyerChannels) {
            if (config.isOnLastPage() && config.isChannelOpened()) {
                resultChannels.put("buyer", config.getChannelID());
            }
        }

        // return all found buyer and seller datachannels that are ready to be streamed
        return ResponseEntity.ok(resultChannels);
    }

    //--------------------------------------------------------------------------------------
    // getConfigData (for ChannelID)
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> getConfigData(
            @ApiParam(value = "channelID", required = true)
            @PathVariable String channelID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer) throws UnirestException {

        ChannelConfiguration channelConfiguration = channelConfigurationRepository.findOneByChannelID(channelID);
        if (channelConfiguration == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(channelConfiguration);
    }

    //--------------------------------------------------------------------------------------
    // produceSensorData (for ChannelID)
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> produceSensorData(
            @ApiParam(value = "erp data", required = true)
            @RequestBody Map<String, SensorValue> sensorValueMap,
            @ApiParam(value = "channelID", required = true)
            @PathVariable String channelID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer) throws UnirestException {

        ChannelConfiguration channelConfiguration = channelConfigurationRepository.findOneByChannelID(channelID);
        if (channelConfiguration == null) {
            return ResponseEntity.notFound().build();
        }

        // produce data for every negotiated server
        for(Server server: channelConfiguration.getAssociatedServers())
        {
            // TODO: send SensorValues to all configured servers
            // TODO: URL = jdbc:postgresql://${DATACHANNEL_DB_HOST:localhost}:${DATACHANNEL_DB_HOST_PORT:5432}/${DATACHANNEL_DB_NAME:sensordatadb}
        }

        // if sending to all servers worked, return true
        return ResponseEntity.ok(true);
    }

    //--------------------------------------------------------------------------------------
    // consumeSensorData (for ChannelID)
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> consumeSensorData(
            @ApiParam(value = "channelID", required = true)
            @PathVariable String channelID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer) throws UnirestException {

        ChannelConfiguration channelConfiguration = channelConfigurationRepository.findOneByChannelID(channelID);
        if (channelConfiguration == null) {
            return ResponseEntity.notFound().build();
        }

        // try to consume data from a negotiated server, try next if failed
        for(Server server: channelConfiguration.getAssociatedServers()) {

            // TODO: receive SensorValues from first available server
            // TODO: URL = jdbc:postgresql://${DATACHANNEL_DB_HOST:localhost}:${DATACHANNEL_DB_HOST_PORT:5432}/${DATACHANNEL_DB_NAME:sensordatadb}
        }

        // if no server provided data, return false
        return ResponseEntity.ok(false);
    }
}

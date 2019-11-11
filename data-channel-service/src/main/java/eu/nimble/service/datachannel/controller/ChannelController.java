package eu.nimble.service.datachannel.controller;

import com.mashape.unirest.http.exceptions.UnirestException;
import eu.nimble.common.rest.identity.IdentityResolver;
import eu.nimble.service.datachannel.entity.ChannelConfiguration;
import eu.nimble.service.datachannel.entity.Machine;
import eu.nimble.service.datachannel.entity.Sensor;
import eu.nimble.service.datachannel.entity.Server;
import eu.nimble.service.datachannel.entity.NegotiationHistory;
import eu.nimble.service.datachannel.kafka.KafkaDomainClient;
import eu.nimble.service.datachannel.repository.ChannelConfigurationRepository;
import eu.nimble.service.datachannel.repository.MachineRepository;
import eu.nimble.service.datachannel.repository.SensorRepository;
import eu.nimble.service.datachannel.repository.ServerRepository;
import eu.nimble.service.datachannel.repository.NegotiationHistoryRepository;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import request.CreateChannel;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;


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
    private NegotiationHistoryRepository negotiationHistoryRepository;

    @Autowired
    private MachineRepository machineRepository;

    @Value("${nimble.datachannel.local-datapipe-service}")
    private String localDataPipe;

    @Value("${nimble.datachannel.local-filtering-service}")
    private String localDcfs;

    @Value("${nimble.datachannel.datapipe-url}")
    private String dataPipeUrl;

    @Value("${nimble.datachannel.filteringservice-url}")
    private String datachannelFilteringServicesUrl;

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
                createChannelRequest.getDescription(),
                createChannelRequest.getProductID());

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
    // setAdvancedConfig
    //--------------------------------------------------------------------------------------
    //public ResponseEntity<?> setAdvancedConfig (
    //        @ApiParam(value = "channelID", required = true)
    //        @PathVariable String channelID,
    //        @ApiParam(value = "usePrivateServers", required = true)
    //        @RequestParam boolean usePrivateServers,
    //        @ApiParam(value = "privateServersType", required = true)
    //        @RequestParam String privateServersType,
    //        @ApiParam(value = "hostRequest", required = true)
    //        @RequestParam boolean hostRequest,
    //        @ApiParam(value = "additionalNotes", required = true)
    //        @RequestParam String additionalNotes,
    //        @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
    //        @RequestHeader(value = "Authorization") String bearer)
    //        throws IOException, UnirestException {
    //
    //
    //    ChannelConfiguration channelConfiguration = channelConfigurationRepository.findOneByChannelID(channelID);
    //    if (channelConfiguration == null) {
    //        return ResponseEntity.notFound().build();
    //    }
    //
    //    // check if request is authorized
    //    String companyID = identityResolver.resolveCompanyId(bearer);
    //    if (isAuthorized(channelConfiguration, companyID) == false) {
    //        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    //    }
    //
    //    channelConfiguration.setPrivateServersType(privateServersType);
    //    channelConfiguration.setHostRequest(hostRequest);
    //    channelConfiguration.setAdditionalNotes(additionalNotes);
    //    channelConfiguration.setUsePrivateServers(usePrivateServers);
    //    channelConfigurationRepository.save(channelConfiguration);
    //
    //    logger.info("Company {} requested nextStep of Negotiation for channel with ID {}", companyID, channelID);
    //    return new ResponseEntity<>(channelConfiguration, HttpStatus.OK);
    //}


    //--------------------------------------------------------------------------------------
    // nextNegotiationStep
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> setNextNegotiationStepForChannel(
            @ApiParam(value = "channelID", required = true)
            @PathVariable String channelID,
            @ApiParam(value = "usePrivateServers", required = true)
            @RequestParam boolean usePrivateServers,
            @ApiParam(value = "sellerMessage", required = false)
            @RequestParam(required = false) String sellerMessage,
            @ApiParam(value = "buyerMessage", required = false)
            @RequestParam(required = false) String buyerMessage,
            @ApiParam(value = "sellerServerType", required = false)
            @RequestParam(required = false) String sellerServerType,
            @ApiParam(value = "buyerServerType", required = false)
            @RequestParam(required = false) String buyerServerType,
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

        if (sellerMessage!=null &&   !"".equals(sellerMessage) && sellerMessage.length()>0) {
            channelConfiguration.setNegotiationSellerMessages(sellerMessage);
        }
        
        if (buyerMessage!=null &&   !"".equals(buyerMessage) && buyerMessage.length()>0) {
            channelConfiguration.setNegotiationBuyerMessages(buyerMessage);
        }

        if (buyerServerType!=null &&   !"".equals(buyerServerType) && buyerServerType.length()>0) {
            channelConfiguration.setBuyerServersType(buyerServerType);
        }

        if (sellerServerType!=null &&   !"".equals(sellerServerType) && sellerServerType.length()>0) {
            channelConfiguration.setSellerServersType(sellerServerType);
        }

        channelConfiguration.setUsePrivateServers(usePrivateServers);

        
        NegotiationHistory negotiationHistory = new NegotiationHistory();
        negotiationHistory.setOwnership(companyID);
        negotiationHistory.setStep( channelConfiguration.getNegotiationStepcounter() );
        negotiationHistory.setJsonSnapshot(this.printJsonFormatChannel(channelConfiguration));
        negotiationHistory.setCreatedDateTime(new java.util.Date() );
        negotiationHistory = negotiationHistoryRepository.save(negotiationHistory);
        
        Set<NegotiationHistory> configuredNegotiationHistory = channelConfiguration.getAssociatedNegotiationHistory();
        configuredNegotiationHistory.add(negotiationHistory);
        
        channelConfiguration.setNextNegotiationStepcounter();

        channelConfigurationRepository.save(channelConfiguration);

        logger.info("Company {} requested nextStep of Negotiation for channel with ID {}", companyID, channelID);
        return new ResponseEntity<>(channelConfiguration, HttpStatus.OK);
    }

    //--------------------------------------------------------------------------------------
    // renegotiate
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> renegotiate(
            @ApiParam(value = "channelID", required = true)
            @PathVariable String channelID,
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer,
            @ApiParam(value = "steps", required = true)
            @PathVariable int steps)
            throws IOException, UnirestException
    {
        ChannelConfiguration channelConfiguration = channelConfigurationRepository.findOneByChannelID(channelID);
        if (channelConfiguration == null) {
            return ResponseEntity.notFound().build();
        }

        // check if request is authorized
        String companyID = identityResolver.resolveCompanyId(bearer);
        if (isAuthorized(channelConfiguration, companyID) == false) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String JsonChannel = this.printJsonFormatChannel(channelConfiguration);
        java.util.Date currentDate = new java.util.Date();
        Set<NegotiationHistory> configuredNegotiationHistory = channelConfiguration.getAssociatedNegotiationHistory();
        for (int num = 0; num < steps; num++)
        {
            NegotiationHistory negotiationHistory = new NegotiationHistory();
            negotiationHistory.setOwnership(companyID);
            negotiationHistory.setStep(channelConfiguration.getNegotiationStepcounter());
            negotiationHistory.setJsonSnapshot(JsonChannel);
            negotiationHistory.setCreatedDateTime(currentDate);
            negotiationHistory = negotiationHistoryRepository.save(negotiationHistory);

            configuredNegotiationHistory.add(negotiationHistory);

            channelConfiguration.setNextNegotiationStepcounter();
        }

        channelConfigurationRepository.save(channelConfiguration);

        logger.info("Company {} requested nextStep of Negotiation for channel with ID {}", companyID, channelID);
        return new ResponseEntity<>(channelConfiguration, HttpStatus.OK);

    }

    //--------------------------------------------------------------------------------------
    // getChannelFromNegotiationStep
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> getChannelFromNegotiationStep (
            @ApiParam(value = "channelID", required = true)
            @PathVariable String channelID,
            @ApiParam(value = "step", required = true)
            @PathVariable int step,
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

        NegotiationHistory negotiationHistory=null;
        Set<NegotiationHistory> configuredNegotiationHistory = channelConfiguration.getAssociatedNegotiationHistory();
        for(NegotiationHistory n : configuredNegotiationHistory) {
            if (n.getStep() == step) {
                negotiationHistory=n;
                break;
            }
        }
        
        ChannelConfiguration channelConfigurationFromHistory = null;
        if ( negotiationHistory!= null) {
            channelConfigurationFromHistory = getChannelfromJson( negotiationHistory.getJsonSnapshot() );
        }
        
        logger.info("Company {} requested channelConfiguration from history ID {} and step {}", companyID, channelID, step);
        return new ResponseEntity<>(channelConfigurationFromHistory, HttpStatus.OK);
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
        // if not private set up channel in the Kafka domain -> this will be moved to Channel.start()
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

        String companyID = identityResolver.resolveCompanyId(bearer);
        ChannelConfiguration channel = this.channelConfigurationRepository.findOneByBusinessProcessID(businessProcessID);
        printJsonFormatChannel(channel);

        
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
    public ResponseEntity<?> addSensorForChannel(
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

        //tbd: check if machine already exists
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

        try {
            sensorRepository.delete(sensorID);
        } catch (Exception ex) {
            //not able to delete due to double click for example, first delete ok, second give errors
            ex.printStackTrace();
        }

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
    public ResponseEntity<?> addServerForChannel(
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

        // add server to channel
        Set<Server> configuredServers = channelConfiguration.getAssociatedServers();
        configuredServers.add(server);
        channelConfigurationRepository.save(channelConfiguration);

        logger.info("Company {} added server {}", companyID, server.getName());
        return new ResponseEntity<>(server, HttpStatus.OK);
    }

    //--------------------------------------------------------------------------------------
    // removeServerForChannel
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

        try {
            serverRepository.delete(serverID);
        } catch (Exception ex) {
            //not able to delete due to double click for example, first delete ok, second give errors
            ex.printStackTrace();
        }


        for(Server server: associatedServers) logger.info("Company {} removed server {}", companyID, server.getName());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //--------------------------------------------------------------------------------------
    // hasInternalService
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> hasInternalService(
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer) throws IOException, UnirestException {
        boolean hasInternalService = false;
        hasInternalService = (localDataPipe!= null && !"".equalsIgnoreCase(localDataPipe));
        if (hasInternalService) return new ResponseEntity<>(new Boolean(hasInternalService), HttpStatus.OK);
        else return new ResponseEntity<>(new Boolean(hasInternalService), HttpStatus.NOT_FOUND);
    }

    //--------------------------------------------------------------------------------------
    // hasFilteringService
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> hasFilteringService(
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer) throws IOException, UnirestException {
        boolean hasFilteringService = false;
        hasFilteringService = (localDcfs!= null && !"".equalsIgnoreCase(localDcfs));
        if (hasFilteringService) return new ResponseEntity<>(new Boolean(hasFilteringService), HttpStatus.OK);
        else return new ResponseEntity<>(new Boolean(hasFilteringService), HttpStatus.NOT_FOUND);
    }

    //--------------------------------------------------------------------------------------
    // hasFilteringService
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> getProducerServiceUrl(
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer) throws IOException, UnirestException {
        boolean hasExternalProducerDomainUrl = false;
        hasExternalProducerDomainUrl = (dataPipeUrl!= null && !"".equalsIgnoreCase(dataPipeUrl));
        if (hasExternalProducerDomainUrl) return new ResponseEntity<>(dataPipeUrl, HttpStatus.OK);
        else return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    //--------------------------------------------------------------------------------------
    // getFilteringServiceUrl
    //--------------------------------------------------------------------------------------
    public ResponseEntity<?> getFilteringServiceUrl(
            @ApiParam(name = "Authorization", value = "OpenID Connect token containing identity of requester", required = true)
            @RequestHeader(value = "Authorization") String bearer) throws IOException, UnirestException {
        boolean hasExternalFilteringService = false;
        hasExternalFilteringService = (datachannelFilteringServicesUrl!= null && !"".equalsIgnoreCase(datachannelFilteringServicesUrl));
        if (hasExternalFilteringService) return new ResponseEntity<>(datachannelFilteringServicesUrl, HttpStatus.OK);
        else return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
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
    
    private String printJsonFormatChannel(ChannelConfiguration channelConfiguration) {
        String jsonStr="{}";
        ObjectMapper mapperObj = new ObjectMapper();
        try {
            // get Employee object as a json string
            jsonStr = mapperObj.writeValueAsString(channelConfiguration);
            System.out.println(jsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }    
        return jsonStr;
    }
    private ChannelConfiguration getChannelfromJson(String jsonStr) {
        ChannelConfiguration channelConfiguration = null;
        ObjectMapper mapperObj = new ObjectMapper();
        try {
            // get Employee object as a json string
            channelConfiguration = mapperObj.readValue(jsonStr, ChannelConfiguration.class);
            
        } catch (Exception e) {
            e.printStackTrace();
        }    
        return channelConfiguration;
    }
    
}

package eu.nimble.service.datachannel.controller;

import eu.nimble.service.datachannel.entity.ChannelConfiguration;
import eu.nimble.service.datachannel.repository.ChannelRepository;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


@Controller
@RequestMapping(path = "/channel")
@Api("Data Channel API")
public class ChannelController {

    private static Logger logger = LoggerFactory.getLogger(ChannelController.class);

    @Autowired
    private ChannelRepository channelRepository;

    @ApiOperation(value = "Create new channel", response = ChannelConfiguration.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Channel created", response = ChannelConfiguration.class),
            @ApiResponse(code = 400, message = "Error while creating channel")})
    @RequestMapping(value = "/", produces = {"application/json"}, method = RequestMethod.POST)
    ResponseEntity<ChannelConfiguration> createChannel(
            @ApiParam(value = "Channel configuration", required = true) @RequestBody ChannelConfiguration channelConfiguration,
            @RequestHeader(value = "Authorization") String bearer) {

        channelRepository.save(channelConfiguration);

        return new ResponseEntity<>(channelConfiguration, HttpStatus.OK);
    }

    @ApiOperation(value = "Get channel with id", response = ChannelConfiguration.class, nickname = "getChannel")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Channel found", response = ChannelConfiguration.class),
            @ApiResponse(code = 404, message = "Channel not found"),
            @ApiResponse(code = 400, message = "Error while fetching channel")})
    @RequestMapping(value = "/{channelID}", produces = {"application/json"}, method = RequestMethod.GET)
    ResponseEntity<?> getChannel(
            @ApiParam(value = "channelID", required = true) @PathVariable Long channelID,
            @RequestHeader(value = "Authorization") String bearer) {

        ChannelConfiguration channelConfiguration = channelRepository.findOneById(channelID);
        if (channelConfiguration == null)
            return ResponseEntity.notFound().build();
        return new ResponseEntity<>(channelConfiguration, HttpStatus.OK);
    }
}

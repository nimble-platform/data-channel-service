package eu.nimble.service.datachannel.entity.requests;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

/**
 * Request and response entities for creating a channel.
 *
 * @author Johannes Innerbichler
 */
public class CreateChannel {
    @ApiModel(value = "CreateChannelRequest", discriminator = "CCREQ")
    public static class Request {

        @NotNull
        @ApiModelProperty(value = "ID of creating company", required = true)
        private String producerCompanyID;

        @NotNull
        @ElementCollection(targetClass = String.class)
        @ApiModelProperty(value = "IDs of consuming companies", required = true)
        private Set<String> consumerCompanyIDs;

        @NotNull
        @ApiModelProperty(value = "Description and purpose of data channel", required = true)
        private String description;

        @NotNull
        @Temporal(TemporalType.TIMESTAMP)
        @ApiModelProperty(value = "Opening date/time of data channel", required = true)
        private java.util.Date startDateTime;

        @NotNull
        @Temporal(TemporalType.TIMESTAMP)
        @ApiModelProperty(value = "Closing date/time of data channel", required = true)
        private java.util.Date endDateTime;

        @ApiModelProperty(value = "ID of originating business process (optional)", required = false)
        private String businessProcessID;

        private Request() {
        }

        public Request(String producerCompanyID, Set<String> consumerCompanyIDs, String description, Date startDateTime, Date endDateTime, String businessProcessID) {
            this.producerCompanyID = producerCompanyID;
            this.consumerCompanyIDs = consumerCompanyIDs;
            this.description = description;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.businessProcessID = businessProcessID;
        }

        public String getProducerCompanyID() {
            return producerCompanyID;
        }

        public void setProducerCompanyID(String producerCompanyID) {
            this.producerCompanyID = producerCompanyID;
        }

        public Set<String> getConsumerCompanyIDs() {
            return consumerCompanyIDs;
        }

        public void setConsumerCompanyIDs(Set<String> consumerCompanyIDs) {
            this.consumerCompanyIDs = consumerCompanyIDs;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Date getStartDateTime() {
            return startDateTime;
        }

        public void setStartDateTime(Date startDateTime) {
            this.startDateTime = startDateTime;
        }

        public Date getEndDateTime() {
            return endDateTime;
        }

        public void setEndDateTime(Date endDateTime) {
            this.endDateTime = endDateTime;
        }

        public String getBusinessProcessID() {
            return businessProcessID;
        }

        public void setBusinessProcessID(String businessProcessID) {
            this.businessProcessID = businessProcessID;
        }
    }


    @ApiModel(value = "CreateChannelResponse", discriminator = "CCRES")
    public static class Response {

        @NotNull
        @ApiModelProperty(value = "ID of created channel", required = true)
        private String channelID;

        private Response() {
        }

        public Response(String channelID) {
            this.channelID = channelID;
        }

        public String getChannelID() {
            return channelID;
        }

        public void setChannelID(String channelID) {
            this.channelID = channelID;
        }
    }
}
package eu.nimble.service.datachannel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@ApiModel(value = "ChannelConfiguration")
public class ChannelConfiguration {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ApiModelProperty(value = "Unique identifier of channel", required = true)
    private String channelID;

    @NotNull
    @ApiModelProperty(value = "ID of seller company", required = true)
    private String sellerCompanyID;

    @NotNull
    @ApiModelProperty(value = "ID of buyer company", required = true)
    private String buyerCompanyID;


    @ApiModelProperty(value = "ID of originating business process")
    private String businessProcessID;

    @ApiModelProperty(value = "Description and purpose of data channel")
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty(value = "Opening date/time of data channel")
    private java.util.Date startDateTime;

    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty(value = "Closing date/time of data channel")
    private java.util.Date endDateTime;

    @ApiModelProperty(value = "private or internal; server - default true")
    private boolean usePrivateServers = true;

    @ApiModelProperty(value = "Type of private servers (internaldatachannel, mongodb, etc)")
    private String buyerServersType = "MongoDB";

    @ApiModelProperty(value = "Type of private servers (internaldatachannel, mongodb, etc)")
    private String sellerServersType = "MongoDB";



    @ApiModelProperty(value = "step conter negotiation")
    private int negotiationStepcounter = 0;

    @ApiModelProperty(value = "seller messages on negotiation")
    private String negotiationSellerMessages = "";

    @ApiModelProperty(value = "buyer messages on negotiation")
    private String negotiationBuyerMessages = "";

    
    @NotNull
    @ElementCollection(targetClass = Sensor.class)
    @ApiModelProperty(value = "Associated sensors")
    private Set<Sensor> associatedSensors = new HashSet<>();

    @NotNull
    @ElementCollection(targetClass = Server.class)
    @ApiModelProperty(value = "Associated private Server configurations")
    private Set<Server> associatedServers = new HashSet<>();

    @NotNull
    @ElementCollection(targetClass = NegotiationHistory.class)
    @ApiModelProperty(value = "Associated history of negotiation steps")
    private Set<NegotiationHistory> associatedNegotiationHistory = new HashSet<>();

    public ChannelConfiguration() {

    }
    public ChannelConfiguration(String businessProcessID, String sellerCompanyID, String buyerCompanyID, String description) {
        setSellerCompanyID(sellerCompanyID);
        setBuyerCompanyID(buyerCompanyID);
        setDescription(description);
        setBusinessProcessID(businessProcessID);
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getChannelID() {
        return channelID;
    }

    public String getSellerCompanyID() {
        return sellerCompanyID;
    }
    public void setSellerCompanyID(String sellerCompanyID) {
        this.sellerCompanyID = sellerCompanyID;
    }

    public String getBuyerCompanyID() {
        return buyerCompanyID;
    }
    public void setBuyerCompanyID(String buyerCompanyID) {
        this.buyerCompanyID = buyerCompanyID;
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
        if (businessProcessID == null || "".equals(businessProcessID))
            this.businessProcessID = System.currentTimeMillis()+"";
        this.channelID = this.businessProcessID+"-"+sellerCompanyID+"-"+buyerCompanyID;
    }

    public boolean isUsePrivateServers() {
        return usePrivateServers;
    }

    public void setUsePrivateServers(boolean usePrivateServers) {
        this.usePrivateServers = usePrivateServers;
    }

    public String getBuyerServersType() {
        return buyerServersType;
    }
    public void setBuyerServersType(String privateServersType) {
        this.buyerServersType = privateServersType;
    }

    public String getSellerServersType() {
        return sellerServersType;
    }
    public void setSellerServersType(String privateServersType) {
        this.sellerServersType = privateServersType;
    }

    public Set<Sensor> getAssociatedSensors() {
        return associatedSensors;
    }
    public void setAssociatedSensors(Set<Sensor> associatedSensors) {
        this.associatedSensors = associatedSensors;
    }

    public Set<Server> getAssociatedServers() {
        return associatedServers;
    }
    public void setAssociatedServers(Set<Server> associatedServers) {
        this.associatedServers = associatedServers;
    }


    public int getNegotiationStepcounter() {
        return negotiationStepcounter;
    }

    public void setNegotiationStepcounter(int negotiationStepcounter) {
        this.negotiationStepcounter = negotiationStepcounter;
    }

    public void setNextNegotiationStepcounter() {
        this.negotiationStepcounter++;
    }

    public String getNegotiationSellerMessages() {
        return negotiationSellerMessages;
    }

    public void setNegotiationSellerMessages(String negotiationSellerMessages) {
        this.negotiationSellerMessages = negotiationSellerMessages;
    }

    public String getNegotiationBuyerMessages() {
        return negotiationBuyerMessages;
    }

    public void setNegotiationBuyerMessages(String negotiationBuyerMessages) {
        this.negotiationBuyerMessages = negotiationBuyerMessages;
    }

    public Set<NegotiationHistory> getAssociatedNegotiationHistory() {
        return associatedNegotiationHistory;
    }

    public void setAssociatedNegotiationHistory(Set<NegotiationHistory> associatedNegotiationHistory) {
        this.associatedNegotiationHistory = associatedNegotiationHistory;
    }

    @JsonIgnore
    public boolean isOnLastPage() {
        return negotiationStepcounter % 5 == 3;
    }

    @JsonIgnore
    public boolean isChannelOpened()
    {
        if (startDateTime == null) {
            return false;
        }

        if (endDateTime == null) {
            return true;
        }

        if (startDateTime.after(endDateTime)) {
            return true;
        }
        else {
            return false;
        }
    }

}

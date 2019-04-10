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


    @ApiModelProperty(value = "ID of originating business process (optional)")
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

    @ApiModelProperty(value = "Type of private servers (kafka, mongodb, etc)")
    private String privateServersType;

    @NotNull
    @ElementCollection(targetClass = Sensor.class)
    @ApiModelProperty(value = "Associated sensors")
    private Set<Sensor> associatedSensors = new HashSet<>();

    @NotNull
    @ElementCollection(targetClass = Server.class)
    @ApiModelProperty(value = "Associated private Server configurations")
    private Set<Server> associatedServers = new HashSet<>();

    @NotNull
    @ElementCollection(targetClass = Filter.class)
    @ApiModelProperty(value = "Associated filters")
    private Set<Filter> associatedFilters = new HashSet<>();


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

    public String getPrivateServersType() {
        return privateServersType;
    }

    public void setPrivateServersType(String privateServersType) {
        this.privateServersType = privateServersType;
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

    public Set<Filter> getAssociatedFilters() {
        return associatedFilters;
    }
    public void setAssociatedFilters(Set<Filter> associatedFilters) {
        this.associatedFilters = associatedFilters;
    }
}

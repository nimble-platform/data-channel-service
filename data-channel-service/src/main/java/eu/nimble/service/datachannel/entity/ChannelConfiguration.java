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
    @ApiModelProperty(value = "ID of producing company", required = true)
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

    @ApiModelProperty(value = "ID of originating business process (optional)")
    private String businessProcessID;

    @NotNull
    @ApiModelProperty(value = "Topic of producer")
    private String producerTopic;

    @NotNull
    @ElementCollection
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "consumer_topics", joinColumns = @JoinColumn(name = "company_id"))
    @ApiModelProperty(value = "Map for mapping producer company IDs to associated Kafka topics.")
    private Map<String, String> consumerTopics;

    @NotNull
    @ElementCollection(targetClass = Sensor.class)
    @ApiModelProperty(value = "Associated sensors")
    private Set<Sensor> associatedSensors = new HashSet<>();

    public ChannelConfiguration() {
    }

    public ChannelConfiguration(String producerCompanyID, Set<String> consumerCompanyIDs, String description,
                                Date startDateTime, Date endDateTime, String businessProcessID) {
        this.producerCompanyID = producerCompanyID;
        this.consumerCompanyIDs = consumerCompanyIDs;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.businessProcessID = businessProcessID;
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

    public void setChannelID(String channelID) {
        this.channelID = channelID;
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

    public String getProducerTopic() {
        return producerTopic;
    }

    public void setProducerTopic(String producerTopic) {
        this.producerTopic = producerTopic;
    }

    public Map<String, String> getConsumerTopics() {
        return consumerTopics;
    }

    public void setConsumerTopics(Map<String, String> consumerTopics) {
        this.consumerTopics = consumerTopics;
    }

    public Set<Sensor> getAssociatedSensors() {
        return associatedSensors;
    }

    public void setAssociatedSensors(Set<Sensor> associatedSensors) {
        this.associatedSensors = associatedSensors;
    }
}

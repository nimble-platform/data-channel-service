package eu.nimble.service.datachannel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Entity
@ApiModel(value = "ChannelConfiguration")
public class ChannelConfiguration {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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

    @ApiModelProperty(value = "ID of originating business process (optional)", required = false)
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

    public ChannelConfiguration() {
    }

    public ChannelConfiguration(String producerCompanyID, Set<String> consumerCompanyIDs, String description,
                                Date startDateTime, Date endDateTime, String businessProcessID, String producerTopic,
                                Map<String, String> consumerTopics) {
        this.producerCompanyID = producerCompanyID;
        this.consumerCompanyIDs = consumerCompanyIDs;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.businessProcessID = businessProcessID;
        this.producerTopic = producerTopic;
        this.consumerTopics = consumerTopics;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}

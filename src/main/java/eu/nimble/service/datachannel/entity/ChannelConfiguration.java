package eu.nimble.service.datachannel.entity;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

@Entity
public class ChannelConfiguration {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @NotNull
    private String description;

    @NotNull
    @ElementCollection(targetClass=String.class)
    private Set<String> dataStreamIDs;

    @NotNull
    private String producerCompanyID;

    @NotNull
    @ElementCollection(targetClass=String.class)
    private Set<String> consumerCompanyIDs;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date startDateTime;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date endDateTime;

    @NotNull
    private String messageSchema;

    @NotNull
    private String technology;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<String> getDataStreamIDs() {
        return dataStreamIDs;
    }

    public void setDataStreamIDs(Set<String> dataStreamIDs) {
        this.dataStreamIDs = dataStreamIDs;
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

    public String getMessageSchema() {
        return messageSchema;
    }

    public void setMessageSchema(String messageSchema) {
        this.messageSchema = messageSchema;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }
}

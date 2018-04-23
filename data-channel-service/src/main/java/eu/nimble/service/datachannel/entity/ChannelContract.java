package eu.nimble.service.datachannel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * Contract for defining a data channel.
 *
 * @author Johannes Innerbichler
 */
@Entity
@ApiModel(value = "ChannelContract", discriminator = "CC")
public class ChannelContract {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @NotNull
    @ApiModelProperty(value = "Description and purpose of data channel", required = true)
    private String description;

    @NotNull
    @ApiModelProperty(value = "Name of data channel", required = true)
    private String name;

    @NotNull
    @ElementCollection(targetClass=String.class)
    @ApiModelProperty(value = "List of datastream id associated with data channel", required = true)
    private Set<String> dataStreamIDs;

    @NotNull
    @ApiModelProperty(value = "ID of producing company", required = true)
    private String producerCompanyID;

    @NotNull
    @ElementCollection(targetClass=String.class)
    @ApiModelProperty(value = "IDs of consuming companies", required = true)
    private Set<String> consumerCompanyIDs;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty(value = "Opening date/time of data channel", required = true)
    private java.util.Date startDateTime;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty(value = "Closing date/time of data channel", required = true)
    private java.util.Date endDateTime;

    @NotNull
    @ApiModelProperty(value = "Schema of messages exchanged via data channel", required = true, allowableValues = "sensorthings")
    private String messageSchema;

    @NotNull
    @ApiModelProperty(value = "Used technology for exchanging messages", required = true, allowableValues = "kafka")
    private String technology;

    @NotNull
    @ElementCollection
    @ApiModelProperty(value = "Meta information of used technology")
    private Map<String, String> technologyMeta;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Map<String, String> getTechnologyMeta() {
        return technologyMeta;
    }

    public void setTechnologyMeta(Map<String, String> technologyMeta) {
        this.technologyMeta = technologyMeta;
    }
}

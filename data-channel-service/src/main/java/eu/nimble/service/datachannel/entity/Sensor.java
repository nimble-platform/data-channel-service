package eu.nimble.service.datachannel.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@ApiModel(value = "Sensor")
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ApiModelProperty(value = "Name of sensor")
    private String name;

    @ApiModelProperty(value = "Description of sensor")
    private String description;

    @ApiModelProperty(value = "data interval of sensor")
    private long interval;

    @ApiModelProperty(value = "Key of sensor data (optional) - example READINGID")
    private String dataKey;

    @ApiModelProperty(value = "Metadata of sensor (optional) - example READINGID BIGINT, LATITUDE DOUBLE,  LONGITUDE DOUBLE, TEMPERATURE DOUBLE, UNITS VARCHAR, LOCATION.NAME VARCHAR, LOCATION.ADRESS VARCHAR, LOCATION.CONTACTS.TELEPHONE VARCHAR, LOCATION.CONTACTS.WEB VARCHAR")
    private String metadata;

    @ApiModelProperty(value = "Advanced Filtering (optional, only if filtering server is enabled) - in order to create views or subset for this sensor")
    private String advancedFiltering;


    @NotNull
    @ManyToOne
    @JoinColumn(name = "machineID")
    @ApiModelProperty(value = "Machine of sensor")
    private Machine machine;

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

    public Machine getMachine() {
        return machine;
    }
    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getDataKey() {
        return dataKey;
    }

    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
    }

    public String getAdvancedFiltering() {
        return advancedFiltering;
    }

    public void setAdvancedFiltering(String advancedFiltering) {
        this.advancedFiltering = advancedFiltering;
    }

}
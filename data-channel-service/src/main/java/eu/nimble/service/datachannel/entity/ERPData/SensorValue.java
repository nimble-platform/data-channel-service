package eu.nimble.service.datachannel.entity.ERPData;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@ApiModel(value = "SensorValue")
public class SensorValue {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ApiModelProperty(value = "Value of sensor datum")
    private Integer sensorValue;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty(value = "Timestamp of sensor datum")
    private java.util.Date sensorTime;


    public Integer getSensorValue() {
        return sensorValue;
    }
    public void setSensorValue(Integer value) {
        this.sensorValue = value;
    }

    public java.util.Date getSensorTime() {
        return sensorTime;
    }
    public void setSensorTime(java.util.Date time) {
        this.sensorTime = time;
    }
}

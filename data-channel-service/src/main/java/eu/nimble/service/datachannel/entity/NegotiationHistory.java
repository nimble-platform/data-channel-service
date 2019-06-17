package eu.nimble.service.datachannel.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@ApiModel(value = "Negotiationhistory")
public class NegotiationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ApiModelProperty(value = "who does changes")
    private String ownership;

    @NotNull
    @ApiModelProperty(value = "step count in the negotiation")
    private int step;

    @ApiModelProperty(value = "json snapshot of channel")
    @Column(columnDefinition = "TEXT")
    private String jsonSnapshot;

    @Temporal(TemporalType.TIMESTAMP)
    @ApiModelProperty(value = "Created date/time")
    private java.util.Date createdDateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnership() {
        return ownership;
    }

    public void setOwnership(String ownership) {
        this.ownership = ownership;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getJsonSnapshot() {
        return jsonSnapshot;
    }

    public void setJsonSnapshot(String jsonSnapshot) {
        this.jsonSnapshot = jsonSnapshot;
    }

    public Date getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(Date createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    
    
}
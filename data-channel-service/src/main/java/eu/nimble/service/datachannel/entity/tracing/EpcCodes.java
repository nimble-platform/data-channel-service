package eu.nimble.service.datachannel.entity.tracing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@ApiModel(value = "EpcCodes")
public class EpcCodes {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(unique = true)
    @ApiModelProperty(value = "Id of order", required = true)
    private String orderId;

    @NotNull
    @ElementCollection(targetClass = String.class)
    @ApiModelProperty(value = "EPC codes of order", required = true)
    private Set<String> codes = new HashSet<>();

    private EpcCodes() {
    }

    public EpcCodes(String orderId, Set<String> codes) {
        this.orderId = orderId;
        this.codes = codes;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Set<String> getCodes() {
        return codes;
    }
    public void setCodes(Set<String> codes) {
        this.codes = codes;
    }
}

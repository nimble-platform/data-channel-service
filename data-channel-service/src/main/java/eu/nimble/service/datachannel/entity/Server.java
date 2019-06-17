package eu.nimble.service.datachannel.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
@ApiModel(value = "Server")
public class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ApiModelProperty(value = "Priority order")
    private int priority=1;

    @NotNull
    @ApiModelProperty(value = "server name")
    private String name;

    @NotNull
    @ApiModelProperty(value = "Datasource url address")
    private String address;

    @NotNull
    @ApiModelProperty(value = "Storage duration")
    private String duration="12";

    @NotNull
    @ApiModelProperty(value = "Owner company ID")
    private String ownership;

    @ApiModelProperty(value = "login")
    private String login;

    @ApiModelProperty(value = "loginPW")
    private String loginPW;

    @NotNull
    @ApiModelProperty(value = "description")
    private String description;





    //@ApiModelProperty(value = "additional config parameters")
    //private String additionalParameters;


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnership() {
        return ownership;
    }

    public void setOwnership(String ownerID) {
        this.ownership = ownerID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String url) {
        this.address = url;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }


    public String getloginPW() {
        return loginPW;
    }

    public void setLoginPW(String loginPW) {
        this.loginPW = loginPW;
    }


    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

package eu.nimble.service.datachannel.entity;

import de.fraunhofer.iosb.ilt.sta.model.Thing;

import javax.validation.constraints.NotNull;
import java.util.Set;

public class CompanyChannels {

    @NotNull
    private Set<Thing> producer;

    @NotNull
    private Set<Thing> consumer;

    public CompanyChannels(Set<Thing> producer, Set<Thing> consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }

    public Set<Thing> getProducer() {
        return producer;
    }

    public void setProducer(Set<Thing> producer) {
        this.producer = producer;
    }

    public Set<Thing> getConsumer() {
        return consumer;
    }

    public void setConsumer(Set<Thing> consumer) {
        this.consumer = consumer;
    }
}

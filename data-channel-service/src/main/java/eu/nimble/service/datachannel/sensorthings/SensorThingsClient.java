package eu.nimble.service.datachannel.sensorthings;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iosb.ilt.sta.ServiceFailureException;
import de.fraunhofer.iosb.ilt.sta.model.Thing;
import de.fraunhofer.iosb.ilt.sta.model.builder.ThingBuilder;
import de.fraunhofer.iosb.ilt.sta.service.SensorThingsService;
import eu.nimble.service.datachannel.entity.ChannelContract;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

@Component
public class SensorThingsClient {

    @Value("${nimble.data-channel.sensorthings.url}")
    private String sensorThingsServerUrl;

    private SensorThingsService service;

    @PostConstruct
    private void init() throws URISyntaxException, MalformedURLException {
        this.service = new SensorThingsService(new URL(sensorThingsServerUrl));
    }

    public Thing createThingFromContract(ChannelContract channelContract) throws ServiceFailureException {

        // build thing
        Thing thing = ThingBuilder.builder()
                .name(channelContract.getName())
                .description(channelContract.getDescription())
                .properties(adaptContract(channelContract))
                .build();

        // create on SensorThings server
        service.create(thing);

        return thing;
    }

    public Thing findThing(Long identifier) throws ServiceFailureException {
        return service.things().find(identifier);
    }

    public Set<Thing> producerThingsForCompany(String companyID) throws ServiceFailureException, IOException {

        Set<Thing> foundThings = new HashSet<>();
        Iterator<Thing> thingsIter = service.things().query().count().orderBy("description")
                .select("properties", "name", "id", "description").skip(5).top(10).list().fullIterator();
        while (thingsIter.hasNext()) {
            Thing thing = thingsIter.next();
            ChannelContract contract = extractContract(thing);
            if (contract != null && contract.getProducerCompanyID().equals(companyID))
                foundThings.add(thing);
        }

        return foundThings;
    }

    public Set<Thing> consumerThingsForCompany(String companyID) throws ServiceFailureException, IOException {

        Set<Thing> foundThings = new HashSet<>();
        Iterator<Thing> thingsIter = service.things().query().count().orderBy("description")
                .select("properties", "name", "id", "description").skip(5).top(10).list().fullIterator();
        while (thingsIter.hasNext()) {
            Thing thing = thingsIter.next();
            ChannelContract contract = extractContract(thing);
            if (contract != null && contract.getConsumerCompanyIDs().contains(companyID))
                foundThings.add(thing);
        }

        return foundThings;
    }

    private static Map<String, Object> adaptContract(ChannelContract channelContract) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("contract", channelContract);
        return properties;
    }

    private static ChannelContract extractContract(Thing thing) throws IOException {
        Map<String, Object> properties = thing.getProperties();
        final String jsonContract = new ObjectMapper().writeValueAsString(properties.get("contract"));
        return new ObjectMapper().readValue(jsonContract, ChannelContract.class);
    }
}

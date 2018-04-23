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

/**
 * Client of the SensorThings server.
 *
 * @author Johannes Innerbichler
 */
@Component
public class SensorThingsContracts {

    @Value("${nimble.data-channel.sensorthings.url}")
    private String sensorThingsServerUrl;

    private SensorThingsService service;

    /**
     * Initialized the underlying SensorThings service (see de.fraunhofer.iosb.ilt.sta.service.SensorThingsService)
     *
     * @throws URISyntaxException    Invalid URL of SensorThings server
     * @throws MalformedURLException Invalid URL of SensorThings server
     */
    @PostConstruct
    private void init() throws URISyntaxException, MalformedURLException {
        this.service = new SensorThingsService(new URL(sensorThingsServerUrl));
    }

    /**
     * Creates a SensorThing thing based on a channel contract, whereas the original contract is stored in the properties.
     *
     * @param channelContract Original contract of data channel
     * @return Thing representing channel.
     * @throws ServiceFailureException Error while communicating with the SensorThings server.
     */
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

    /**
     * Searches a specific thing on the SensorThings server based on its identifier
     *
     * @param identifier Identifier to search for
     * @return Found things
     * @throws ServiceFailureException Thrown if thing cannot be found.
     */
    public Thing findThing(Long identifier) throws ServiceFailureException {
        return service.things().find(identifier);
    }

    /**
     * Get all things which store where the company is the producing party in the data channel.
     *
     * @param companyID Identifier of company
     * @return Found things. Empty if no thing was found
     * @throws ServiceFailureException Error while communicating with SensorThings server
     * @throws IOException             Error while communicating with SensorThings server
     */
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

    /**
     * Get all things which store where the company is the consuming party in the data channel.
     *
     * @param companyID Identifier of company
     * @return Found things. Empty if no thing was found
     * @throws ServiceFailureException Error while communicating with SensorThings server
     * @throws IOException             Error while communicating with SensorThings server
     */
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

    /**
     * Converting channel contract to properties object of a SensorThing thing.
     *
     * @param channelContract Contract of channel
     * @return Created properties
     */
    private static Map<String, Object> adaptContract(ChannelContract channelContract) {
        Map<String, Object> properties = new HashMap<>();
        properties.put("contract", channelContract);
        return properties;
    }

    /**
     * Extract contract from thing, which is stored in the properties.
     *
     * @param thing Thing representing contract
     * @return Contract of channel
     * @throws IOException Channel contract could not be extracted
     */
    private static ChannelContract extractContract(Thing thing) throws IOException {
        Map<String, Object> properties = thing.getProperties();
        final String jsonContract = new ObjectMapper().writeValueAsString(properties.get("contract"));
        return new ObjectMapper().readValue(jsonContract, ChannelContract.class);
    }
}

package eu.nimble.service.datachannel.kafka;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import eu.nimble.service.datachannel.entity.ChannelConfiguration;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Client for communications with services in the Kafka domain.
 *
 * @author Johannes Innerbichler
 */
@Component
public class KafkaDomainClient {

    @Value("${nimble.kafka-domain.service-url}")
    private String kafkaDomainUrl;

    private static Logger logger = LoggerFactory.getLogger(KafkaDomainClient.class);

    public String createChannel(ChannelConfiguration channelConfig) throws UnirestException {

        String sourceID = channelConfig.getProducerCompanyID();
        String targetID = channelConfig.getConsumerCompanyIDs().stream().findFirst().get();

        // create filter json
        Map<String, String> map = new HashMap<>();
        map.put("producerCompanyID", channelConfig.getProducerCompanyID());
        JSONObject jsonFilter = new JSONObject(map);

        // create channel in Kafka domain
        HttpResponse<String> response = Unirest.post(kafkaDomainUrl + "/start-new")
                .queryString("source", sourceID)
                .queryString("target", targetID)
                .queryString("filter", jsonFilter.toString())
                .asString();

        logger.debug("{} {} {}", response.getStatus(), response.getStatusText(), response.getBody());

        return response.getBody();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void deleteChannel(String channelID){
        Unirest.delete(kafkaDomainUrl + "/" + channelID);
    }
}

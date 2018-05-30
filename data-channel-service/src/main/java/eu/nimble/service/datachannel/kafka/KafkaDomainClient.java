package eu.nimble.service.datachannel.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
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

    public CreateChannelResponse createChannel(ChannelConfiguration channelConfig) throws UnirestException {

        String sourceID = channelConfig.getProducerCompanyID();
        String targetID = channelConfig.getConsumerCompanyIDs().stream().findFirst().get();

        // create filter json
        Map<String, String> map = new HashMap<>();
        map.put("producerCompanyID", channelConfig.getProducerCompanyID());
        JSONObject jsonFilter = new JSONObject(map);

        JSONObject body = new JSONObject();
        body.accumulate("source", sourceID);
        body.accumulate("target", targetID);
        body.accumulate("filter", jsonFilter);

        // create channel in Kafka domain
        HttpResponse<JsonNode> response = Unirest.post(kafkaDomainUrl + "/start-new")
                .header("Content-Type", "application/json")
                .body(body)
                .asJson();

        logger.debug("{} {} {}", response.getStatus(), response.getStatusText(), response.getBody());

        ObjectMapper mapper = new ObjectMapper().registerModule(new JsonOrgModule());
        return mapper.convertValue(response.getBody().getObject(), CreateChannelResponse.class);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void deleteChannel(String channelID) {
        Unirest.delete(kafkaDomainUrl + "/" + channelID);
    }

    @SuppressWarnings("unused")
    public static class CreateChannelResponse {

        private String channelId;
        private String inputTopic;
        private String outputTopic;

        public String getChannelId() {
            return channelId;
        }

        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public String getInputTopic() {
            return inputTopic;
        }

        public void setInputTopic(String inputTopic) {
            this.inputTopic = inputTopic;
        }

        public String getOutputTopic() {
            return outputTopic;
        }

        public void setOutputTopic(String outputTopic) {
            this.outputTopic = outputTopic;
        }
    }
}

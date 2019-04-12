package eu.nimble.service.datachannel.dcfs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.RequestBodyEntity;
import eu.nimble.service.datachannel.entity.ChannelConfiguration;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Client for communications with services in the Kafka domain.
 *
 * @author Johannes Innerbichler
 */
@Service
public class DcfsClient {

    @Value("${nimble.dcfs.service-url}")
    private String DcfsUrl;

    private static Logger logger = LoggerFactory.getLogger(DcfsClient.class);

    public CreateFilteredChannelResponse createChannel(ChannelConfiguration channelConfig) throws UnirestException {
 /*$$TBD for each Sensor
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
        HttpResponse<String> response = Unirest.post(dcfsDomainUrl + "/start-new-filtered")
                .header("Content-Type", "application/json")
                .body(body)
                .asString();

        logger.debug("{} {} {}", response.getStatus(), response.getStatusText(), response.getBody());

        JSONObject jsonResponse = new JSONObject(response.getBody());
        return new CreateChannelResponse(jsonResponse.getString("channelId"), jsonResponse.getString("inputTopic"),
                jsonResponse.getString("outputTopic"));
*/   
        return new CreateFilteredChannelResponse("", true);
}

    @SuppressWarnings("unused")
    public static class CreateFilteredChannelResponse {

        private String channelId;
        private boolean hasErrors;

        public CreateFilteredChannelResponse(String channelId, boolean hasErrors) {
            this.channelId = channelId;
            this.hasErrors = hasErrors;
        }

        public String getChannelId() {
            return channelId;
        }
        public void setChannelId(String channelId) {
            this.channelId = channelId;
        }

        public boolean isHasErrors() {
            return hasErrors;
        }

        public void setHasErrors(boolean hasErrors) {
            this.hasErrors = hasErrors;
        }

    }
}

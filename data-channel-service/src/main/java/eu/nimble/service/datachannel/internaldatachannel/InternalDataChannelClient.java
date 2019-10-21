package eu.nimble.service.datachannel.internaldatachannel;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import eu.nimble.service.datachannel.entity.ChannelConfiguration;
import eu.nimble.service.datachannel.entity.Sensor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Set;

/**
 * InternalDataChannel client
 *
 * @author Andrea Musumeci
 */
@Service
public class InternalDataChannelClient {

    @Value("${nimble.datachannel.local-datapipe-service}")
    private String localDataPipe;

    private static Logger logger = LoggerFactory.getLogger(InternalDataChannelClient.class);

    public boolean createChannel(ChannelConfiguration channelConfig) throws UnirestException {
        Set<Sensor> sensors =  channelConfig.getAssociatedSensors();
        Iterator<Sensor> iSensor = sensors.iterator();
        boolean hasErrors = false;

        String createChannelUrl = localDataPipe+"/manage/createInternalChannelTopic?idDataChannel="+channelConfig.getChannelID();

        HttpResponse<String> response = Unirest.post(createChannelUrl)
                .header("Content-Type", "application/json")
                .asString();

        hasErrors = hasErrors || response.getStatus()!=200;

        if (hasErrors)
            return hasErrors;

        while (iSensor.hasNext()) {

            String createSensorUrl = localDataPipe+"/manage/createInternalSensorTopic?idDataChannel="+channelConfig.getChannelID()+"&idSensor="+iSensor.next().getId();
            HttpResponse<String> responseSensor = Unirest.post(createSensorUrl)
                    .header("Content-Type", "application/json")
                    .asString();

            hasErrors = hasErrors || responseSensor.getStatus()!=200;

        }

        return hasErrors;
}

}

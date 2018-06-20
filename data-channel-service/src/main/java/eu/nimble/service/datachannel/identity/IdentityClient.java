package eu.nimble.service.datachannel.identity;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * REST Client for communication with the Identity Service.
 * @author Johannes Innerbichler
 */
@Service
public class IdentityClient {

    @Value("${nimble.identity.service-url}")
    private String identityServiceUrl;

    /**
     * Extracts the identity from an OpenID Connect token and fetches the associated company from the Identity Service.
     * @param accessToken OpenID Connect token storing identity.
     * @return Identifier of associated company
     * @throws UnirestException Error while communicating with the Identity Service.
     */
    public String getCompanyId(String accessToken) throws UnirestException {

        // obtain extended user information
        HttpResponse<JsonNode> response = Unirest.get(identityServiceUrl + "/user-info")
                .header("Authorization", accessToken)
                .asJson();

        return (String) response.getBody().getObject().get("ublPartyID");
    }
}

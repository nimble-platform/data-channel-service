package eu.nimble.service.datachannel.identity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * REST Client for communication with the Identity Service.
 * @author Johannes Innerbichler
 */
@Component
public class IdentityClient {

    @Value("${nimble.identity.service-url}")
    private String identityServiceUrl;

    /**
     * Extract the identity from an OpenID Connect token and fetches the associated company from the Identity Service.
     * @param accessToken OpenID Connect token storing identity.
     * @return Identifier of associated company
     * @throws IOException Token cannot be read
     * @throws UnirestException Error while communicating with the Identity Service.
     */
    public String getCompanyId(String accessToken) throws IOException, UnirestException {
        String rawAccessToken = accessToken.replace("Bearer ", "");

        // decode token
//        Jwt tokenDecoded = JwtHelper.decodeAndVerify(idToken, verifier(kid));
        Jwt tokenDecoded = JwtHelper.decode(rawAccessToken);
        Map<String, String> authInfo = new ObjectMapper().readValue(tokenDecoded.getClaims(), Map.class);

        // obtain extended user information
        HttpResponse<JsonNode> response = Unirest.get(identityServiceUrl + "/user-info")
                .header("Authorization", accessToken)
                .asJson();

        return (String) response.getBody().getObject().get("ublPartyID");
    }
}

package eu.nimble.service.datachannel.identity;

import org.springframework.stereotype.Component;

@Component
public class IdentityClient {

    public String getCompanyId(String accessToken) {
        return "9999";
    }
}

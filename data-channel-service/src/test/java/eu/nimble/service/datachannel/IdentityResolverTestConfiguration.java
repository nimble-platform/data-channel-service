package eu.nimble.service.datachannel;

import eu.nimble.common.rest.identity.IdentityResolver;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@Configuration
public class IdentityResolverTestConfiguration {

    @Bean
    @Primary
    public IdentityResolver identityResolver() {
        return Mockito.mock(IdentityResolver.class, (Answer) invocation -> "444");
    }
}

package eu.nimble.service.datachannel.repository;

import eu.nimble.service.datachannel.entity.ChannelConfiguration;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ChannelConfigurationRepository extends CrudRepository<ChannelConfiguration, Long> {
    ChannelConfiguration findOneByChannelID(String channelID);

    ChannelConfiguration findOneByBusinessProcessID(String businessProcessID);

    Set<ChannelConfiguration> findBySellerCompanyID(String companyId);

    Set<ChannelConfiguration> findByBuyerCompanyID(String companyId);
}

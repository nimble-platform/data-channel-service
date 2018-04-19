package eu.nimble.service.datachannel.repository;

import eu.nimble.service.datachannel.entity.ChannelConfiguration;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ChannelRepository extends PagingAndSortingRepository<ChannelConfiguration, Long> {

    ChannelConfiguration findOneById(Long id);

}

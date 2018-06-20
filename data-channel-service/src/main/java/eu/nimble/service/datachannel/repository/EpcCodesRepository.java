package eu.nimble.service.datachannel.repository;

import eu.nimble.service.datachannel.entity.tracing.EpcCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EpcCodesRepository extends JpaRepository<EpcCodes, Long> {

    EpcCodes findOneByOrderId(String orderId);

}
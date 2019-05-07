package eu.nimble.service.datachannel.repository;

import eu.nimble.service.datachannel.entity.NegotiationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface NegotiationHistoryRepository extends JpaRepository<NegotiationHistory, Long> {
    Set<NegotiationHistory> findByStep(int step);
}


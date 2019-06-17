package eu.nimble.service.datachannel.repository;

import eu.nimble.service.datachannel.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long> {
    Server findOneById(Long id);
}


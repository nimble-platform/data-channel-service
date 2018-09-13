package eu.nimble.service.datachannel.repository;

import eu.nimble.service.datachannel.entity.Machine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineRepository extends JpaRepository<Machine, Long> {
    Machine findOneByName(String name);
}

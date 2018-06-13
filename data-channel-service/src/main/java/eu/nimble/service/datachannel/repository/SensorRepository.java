package eu.nimble.service.datachannel.repository;

import eu.nimble.service.datachannel.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    Sensor findOneByName(String name);
}


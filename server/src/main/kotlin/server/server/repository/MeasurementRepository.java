package server.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.server.models.Measurement;

import java.util.Optional;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    Optional<Measurement> findByMeasurementId(long measurement_id);
}

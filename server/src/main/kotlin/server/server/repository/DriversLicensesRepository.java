package server.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import server.server.models.DriversLicenses;
import server.server.models.User;
import server.server.models.compositeKeys.DriverLicensesKey;

import java.util.List;

@Repository
public interface DriversLicensesRepository extends JpaRepository<DriversLicenses, DriverLicensesKey> {

    List<DriversLicenses> getDriversLicensesByUser(User user);

}

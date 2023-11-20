package cloudFileStorage.repositories;

import cloudFileStorage.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUsersRepository extends JpaRepository<AppUser, Integer> {

}

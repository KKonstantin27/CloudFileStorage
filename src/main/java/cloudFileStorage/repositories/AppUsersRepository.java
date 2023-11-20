package cloudFileStorage.repositories;

import cloudFileStorage.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppUsersRepository extends JpaRepository<AppUser, Integer> {
    List<AppUser> findAppUserByUsername(String username);

}

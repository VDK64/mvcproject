package com.mvcproject.mvcproject.repositories;

import com.mvcproject.mvcproject.entities.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends PagingAndSortingRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByActivationCode(String code);
    Optional<User> findBySteamId(String steamId);

    @Query(value = "select * from usr u where u.id in (select uf.user_id from usr_fr uf where friend_id = ?1)",
            nativeQuery = true)
    List<User> whomFriend(Long id);
}

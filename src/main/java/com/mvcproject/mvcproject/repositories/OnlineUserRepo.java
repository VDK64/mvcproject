package com.mvcproject.mvcproject.repositories;

import com.mvcproject.mvcproject.entities.OnlineUser;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OnlineUserRepo extends PagingAndSortingRepository<OnlineUser, Long> {
    Optional<OnlineUser> findByUsername(String username);
    boolean existsByUsername(String username);
    void deleteByUsername(String username);
}

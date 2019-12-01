package com.mvcproject.mvcproject.repositories;

import com.mvcproject.mvcproject.entities.Game;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepo extends PagingAndSortingRepository<Game, Long> {
}

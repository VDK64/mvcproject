package com.mvcproject.mvcproject.repositories;

import com.mvcproject.mvcproject.entities.Game;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface GameRepo extends PagingAndSortingRepository<Game, Long> {
}

package com.mvcproject.mvcproject.repositories;

import com.mvcproject.mvcproject.entities.ShowStatus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowStatusRepo extends PagingAndSortingRepository<ShowStatus, Long> {
}

package com.mvcproject.mvcproject.repositories;

import com.mvcproject.mvcproject.entities.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepo extends PagingAndSortingRepository<Message, Long> {
}

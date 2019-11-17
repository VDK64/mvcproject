package com.mvcproject.mvcproject.repositories;

import com.mvcproject.mvcproject.entities.Message;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends PagingAndSortingRepository<Message, Long> {
    List<Message> findByFromIdAndToId(Long fromId, Long toId);
}

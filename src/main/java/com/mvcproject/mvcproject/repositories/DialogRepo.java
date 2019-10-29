package com.mvcproject.mvcproject.repositories;

import com.mvcproject.mvcproject.entities.Dialog;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DialogRepo extends PagingAndSortingRepository<Dialog, Long> {
}

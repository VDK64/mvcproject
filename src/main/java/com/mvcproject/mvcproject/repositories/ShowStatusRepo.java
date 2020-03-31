package com.mvcproject.mvcproject.repositories;

import com.mvcproject.mvcproject.entities.Dialog;
import com.mvcproject.mvcproject.entities.ShowStatus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShowStatusRepo extends PagingAndSortingRepository<ShowStatus, Long> {
    List<ShowStatus> findByDialog(Dialog dialog);
}

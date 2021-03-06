package com.mvcproject.mvcproject.repositories;

import com.mvcproject.mvcproject.entities.Dialog;
import com.mvcproject.mvcproject.entities.ShowStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShowStatusRepo extends PagingAndSortingRepository<ShowStatus, Long> {
    List<ShowStatus> findByDialog(Dialog dialog);

    Optional<ShowStatus> findByDialogAndUsername(Dialog dialog, String username);

    @Query(value = "select * from shw_status where dlg_id = ?1", nativeQuery = true)
    List<ShowStatus> findByDialogId(Long id);
}

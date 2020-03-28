package com.mvcproject.mvcproject.repositories;

import com.mvcproject.mvcproject.entities.Dialog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DialogRepo extends PagingAndSortingRepository<Dialog, Long> {

    @Query(value = "select * from dlg d inner join dlg_usr du on (d.id = du.dlg_id and du.usr_id = ?1)",
            nativeQuery = true)
    List<Dialog> findDialogByContainingUserNative(Long id);
}

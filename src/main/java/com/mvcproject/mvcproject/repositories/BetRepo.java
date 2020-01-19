package com.mvcproject.mvcproject.repositories;

import com.mvcproject.mvcproject.entities.Bet;
import com.mvcproject.mvcproject.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BetRepo extends PagingAndSortingRepository<Bet, Long> {
    List<Bet> findByUser(User user);
    List<Bet> findByOpponent(User opponent);
    List<Bet> findByUserAndIsConfirm(User user, Boolean isConfirm);
    List<Bet> findByOpponentAndIsConfirm(User opponent, Boolean isConfirm);
    Page<Bet> findByUser(User user, Pageable pageable);
    Page<Bet> findByOpponent(User opponent, Pageable pageable);
    List<Bet> findByOpponentAndIsNew(User Opponent, Boolean isNew);
    Optional<Bet> findByUserAndOpponentAndWhoWin(User user, User opponent, String whoWin);
}

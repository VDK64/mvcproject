package com.mvcproject.mvcproject.repositories;

import com.mvcproject.mvcproject.entities.Bet;
import com.mvcproject.mvcproject.entities.User;
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
}

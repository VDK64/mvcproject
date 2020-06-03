package com.mvcproject.mvcproject.repositories;

import com.mvcproject.mvcproject.entities.Game;
import com.mvcproject.mvcproject.entities.GameStatus;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepo extends PagingAndSortingRepository<Game, Long> {

    List<Game> findByStatus(GameStatus status);
    Optional<Game> findByLobbyName(String lobbyName);
    List<Game> findByUserSteamId64AndIsUserReady(String userSteamId64, Boolean isUserReady);
    List<Game> findByOpponentSteamId64AndIsOpponentReady(String opponentSteamId64, Boolean isOpponentReady);
}

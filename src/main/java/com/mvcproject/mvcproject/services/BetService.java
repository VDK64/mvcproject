package com.mvcproject.mvcproject.services;

import com.mvcproject.mvcproject.entities.Bet;
import com.mvcproject.mvcproject.entities.User;
import com.mvcproject.mvcproject.repositories.BetRepo;
import com.mvcproject.mvcproject.repositories.UserRepo;
import com.mvcproject.mvcproject.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BetService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BetRepo betRepo;
    @Autowired
    private Validator validator;

    public List<List<Bet>> getBetInfo(User user) {
        List<List<Bet>> response = new ArrayList<>();
        List<Bet> byUser = betRepo.findByUser(user);
        List<Bet> byOpponent = betRepo.findByOpponent(user);
        response.add(byUser);
        response.add(byOpponent);
        return response;
    }

}

package com.mvcproject.mvcproject.dto.matchResult;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Match {
    private String start_time;

    private Player[] players;

    private String match_id;
}

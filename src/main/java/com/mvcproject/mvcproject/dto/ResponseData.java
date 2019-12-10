package com.mvcproject.mvcproject.dto;

import lombok.Data;

@Data
public class ResponseData {
    private String token;
    private String user;
    private String opponent;
}

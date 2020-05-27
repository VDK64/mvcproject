package com.mvcproject.mvcproject.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestData {
    private String token;
    private String user;
    private String opponent;
    private String errorCode;
    private String port;
}
